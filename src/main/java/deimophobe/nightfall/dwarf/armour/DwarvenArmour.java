package deimophobe.nightfall.dwarf.armour;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import deimophobe.nightfall.Game;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.effects.GameEffect;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Deimophobe on 5/05/17.
 */
public class DwarvenArmour implements Armour {
	private final Dwarf dwarf;
	
	private boolean armoured = false;
	
	private static final double DEFAULT_MAX = 1000;
	private double armourValue = DEFAULT_MAX;
	private double durability = 100;
	
	private boolean invisible = false;
	
	private ArmourLevel currentLevel = ArmourLevel.SHINY;
	private Map<ArmourLevel, ArmourSet> setMap = new HashMap<>();
	
	
	
	
	public DwarvenArmour(Dwarf dwarf) {
		this.dwarf = dwarf;
		
		for (ArmourLevel level : ArmourLevel.values()) {
			ArmourSet set = level.getSet();
			setMap.put(level, set);
		}
		addModifier(ItemModifierType.QUIVER, 20);
	}
	
	@Override
	public boolean isArmoured() { return armoured; }
	public void putOn() {
		if (!armoured) {
			armoured = true;
			setMap.get(currentLevel).equip(dwarf);
			GameEffect.DWARF_ARMOURED.playEffect(dwarf);
			dwarf.onArmourEquip();
		} else {
			Bukkit.getLogger().warning("Tried to equip armour on dwarf which is already equipped!\nDwarf: " + dwarf.getName());
		}
	}
	
	
	public double getValue() {
		return armourValue;
	}
	public void changeDurability(double amt, String reason) {
		addModifier(ItemModifierType.ARMOUR_DURABILITY, (int) amt, reason);
		durability += amt;
	}
	
	
	@Override
	public void addModifier(ItemModifierType type, int value, String reason) {
		for (ArmourSet set : setMap.values()) {
			set.chest.addModifier(type, value, reason);
		}
		updateArmour(true);
	}
	@Override
	public void updateEquipment() {
		updateArmour(true);
	}
	
	
	@Override
	public boolean canPickRepair() {
		return armourFraction() <= 0.65;
	}
	@Override
	public boolean canShrineRepair() {
		return !isAtMax();
	}
	
	@Override
	public void damage(double damage) {
		if (Game.getGame().getPhase() == Phase.BUILD) return;
		
		armourValue -= damage/(durability/100);
		if (armourValue <= 0) armourValue = 0;
		updateArmour();
	}
	@Override
	public void repair(double amount) {
		armourValue += amount;
		if (armourValue >= DEFAULT_MAX) armourValue = DEFAULT_MAX;
		updateArmour();
	}
	
	
	@Override
	public double getResistance() {
		if (isArmoured()) {
			double x = armourFraction();
			int n = DwarfManager.getManager().getNumberOfPlayers();
			return (0.75 + 0.075 * x + 0.1d/(n+1));
		} else {
			return 0.6;
		}
	}
	@Override
	public int getManaRegenRate() {
		if (!isArmoured()) return 0;
		
		if (isAtMax()) return 10; // Otherwise formula below would give 11 only when full (which is kinda weird).
		return (int) Math.floor(Math.atan(2 * armourFraction()) * 10/Math.atan(2)) + 1;
	}
	
	
	public double armourFraction() { return armourValue/DEFAULT_MAX; }
	private boolean isAtMax() { return armourFraction() >= 1; }
	private void updateArmour() { updateArmour(false);	}
	private void updateArmour(boolean force) {
		if (isArmoured() && (force ||!currentLevel.isValid(this))) {
			currentLevel = ArmourLevel.getLevel(this);
			setMap.get(currentLevel).equip(dwarf);
		}
		
		dwarf.getPlayer().setFoodLevel((int) Math.ceil(20f * armourFraction()));
	}
	
	
	// Item Invis stuff
	public void hideArmour() {
		if (!invisible) {
			invisible = true;
			updateArmourVisibility();
		}
	}
	public void showArmour() {
		if (invisible) {
			invisible = false;
			updateArmourVisibility();
		}
	}
	
	private static final Map<EnumWrappers.ItemSlot, Function<PlayerInventory, ItemStack>> slotToItemGetter = new HashMap<>();
	static {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(NightfallPlugin.getPlugin(), PacketType.Play.Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				setupEntityEquipmentPacket(event.getPacket());
			}
		});
		
		slotToItemGetter.put(EnumWrappers.ItemSlot.HEAD, PlayerInventory::getHelmet);
		slotToItemGetter.put(EnumWrappers.ItemSlot.CHEST, PlayerInventory::getChestplate);
		slotToItemGetter.put(EnumWrappers.ItemSlot.LEGS, PlayerInventory::getLeggings);
		slotToItemGetter.put(EnumWrappers.ItemSlot.FEET, PlayerInventory::getBoots);
	}
	
	private void updateArmourVisibility() {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		
		for (EnumWrappers.ItemSlot slot : slotToItemGetter.keySet()) {
			PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
			packet.getIntegers().write(0, dwarf.getEntity().getEntityId());
			packet.getItemSlots().write(0, slot);
			packet.getItemModifier().write(0, slotToItemGetter.get(slot).apply(dwarf.getPlayer().getInventory()));
			protocolManager.broadcastServerPacket(packet);
		}
	}
	
	private static void setupEntityEquipmentPacket(PacketContainer packet) {
		EnumWrappers.ItemSlot slot = packet.getItemSlots().read(0);
		
		switch (slot) {
			case HEAD:
			case CHEST:
			case LEGS:
			case FEET: {
				int id = packet.getIntegers().read(0);
				Dwarf dwarf = DwarfManager.getManager().getGamePlayer(id);
				if (dwarf == null) return;
				if (!(dwarf.getArmour() instanceof DwarvenArmour)) return;
				
				DwarvenArmour armour = (DwarvenArmour) dwarf.getArmour();
				if (armour.invisible) {
					packet.getItemModifier().write(0, null);
				}
			}
		}
	}
	
	
	// Helper classes
	
	private enum ArmourLevel {
		SHINY("shiny", 0.8, 1),
		HIGH("high", 0.6, 0.8),
		MED("med", 0.3, 0.6),
		LOW("low", 0, 0.3)
		;
		
		private final String setName;
		private final double minArmour;
		private final double maxArmour;
		ArmourLevel(String sectionName, double minArmour, double maxArmour) {
			this.setName = sectionName;
			this.minArmour = minArmour;
			this.maxArmour = maxArmour;
		}
		
		private ArmourSet getSet() {
			return new ArmourSet(setName);
		}
		
		private boolean isValid(DwarvenArmour armour) {
			double frac = armour.armourFraction();
			return  (minArmour <= frac && frac <= maxArmour);
		}
		
		private static ArmourLevel getLevel(DwarvenArmour armour) {
			double frac = armour.armourFraction();
			for (ArmourLevel level : values()) {
				if (frac >= level.minArmour)
					return level;
			}
			return LOW;
		}
	}
	
	private static class ArmourSet {
		private final CustomItem chest;
		private final CustomItem legs;
		private final CustomItem boots;
		
		private ArmourSet(String section) {
			chest = DwarvenItems.getItem("armour", section + ".chest", Slot.CHEST);
			legs = DwarvenItems.getItem("armour", section + ".legs", Slot.LEGS);
			boots = DwarvenItems.getItem("armour", section + ".boots", Slot.FEET);
		}
		
		private void equip(Dwarf dwarf) {
			PlayerInventory inv = dwarf.getPlayer().getInventory();
			inv.setChestplate(chest.createItemStack());
			inv.setLeggings(legs.createItemStack());
			inv.setBoots(boots.createItemStack());
		}
	}
}
