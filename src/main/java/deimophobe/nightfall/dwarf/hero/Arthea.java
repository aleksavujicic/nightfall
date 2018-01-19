package deimophobe.nightfall.dwarf.hero;

import deimophobe.nightfall.PlayerSkin;
import deimophobe.nightfall.SkinManager;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 7/05/17.
 */
public class Arthea extends Hero {
	protected Arthea(Player player, HeroType type) {
		super(player, type);
	}
	
	@Override
	public boolean givePotionEffect(PotionEffectType type, int duration, int amplifier, boolean showAbove, boolean colourBlue, boolean force) {
		if (!isEnraged())
			return super.givePotionEffect(type, duration, amplifier, showAbove, colourBlue, force);
		else
			return false;
	}
	
	@Override
	public void removePotionEffect(PotionEffectType type) {
		if (!isEnraged())
			super.removePotionEffect(type);
	}
	
	@Override
	public void updateManaBar() {
		if (!isEnraged())
			super.updateManaBar();
		else
			player.setLevel(enrageTimer/20);
	}
	
	@Override
	public void updateCooldownBar() {
		if (!isEnraged()) {
			super.updateCooldownBar();
		} else {
			float frac;
			if (enrageTimer >= ENRAGE_DURATION)
				frac = 1 - (float) (enrageTimer - ENRAGE_DURATION)/ENRAGE_TRANSITION_DURATION;
			else
				frac = (float) enrageTimer/ENRAGE_DURATION;
			player.setExp(frac);
		}
	}
	
	
	@Override
	public void update(boolean a, boolean b, boolean c, boolean d, boolean quadSec) {
		super.update(a,b,c,d,quadSec);
		
		if (isEnraged()) {
			if (enrageTimer > 0)
				enrageTimer--;
			
			if (enrageTimer > ENRAGE_DURATION) {
				Location location = getLocation().add(0,1,0);
				World world = location.getWorld();
				world.spawnParticle(Particle.SPELL_INSTANT, location, 1 ,0.2, 0.2, 0.2);
			} else {
				Location location = getLocation().add(0,0.7,0);
				World world = location.getWorld();
				world.spawnParticle(Particle.REDSTONE, location, 5 ,0.5, 0.5, 0.5, 0);
			}
			
			//if (quadSec)
			//	updateMobList();
			
			if (enrageTimer == ENRAGE_DURATION)
				startEnrage();
				
			if (enrageTimer == 0)
				forceKill();
		}
	}
	
	/* TODO
	@Override
	protected void mobspawnDamage() {
		if (!isEnraged())
			super.mobspawnDamage();
	}
	*/
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (isEnraged()) {
			damage.setProc(true);
		}
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		if (damage.getType() == NaturalDamageType.VOID) {
			return;
		}
			
		if (isEnraged() && enrageTimer != 0) {
			GameEntity monster = damage.getAttacker();
			if (monster instanceof AIEntity)
				monster.doDamage(this, CustomDamageType.BLOOD_MAGIC, 1000, true, true);
			
			damage.cancel();
		} else {
			super.onDamageReceive(damage);
		}
		
		if (damage.willKill() && !isEnraged()) {
			startTransition();
			damage.softCancel();
		}
	}
	
	@Override
	public void notifyDeath(Dwarf dwarf) {
		super.notifyDeath(dwarf);
		if (dwarf == this) {
			player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
			//cancelGlow();
		}
	}
	
	/* TODO
	@Override
	public String generateDeathMessage() {
		if (isEnraged())
			return getDisplayName() + " died from her injuries.";
		else
			return super.generateDeathMessage();
	}
	*/
	
	private static final int ENRAGE_TRANSITION_DURATION = 10*20;
	private static final int ENRAGE_DURATION = 60*20;
	private int enrageTimer = -1;
	private boolean isEnraged() {
		return enrageTimer != -1;
	}
	
	private void startTransition() {
		
		setTitle(ChatColor.LIGHT_PURPLE, "Arthea", true);
		PlayerSkin newSkin = new PlayerSkin(ChatColor.LIGHT_PURPLE + "Arthea", "arthea");
		SkinManager.getManager().addSkinChange(this, newSkin);
		type.getData().getTeam().addEntry(ChatColor.LIGHT_PURPLE + "Arthea");
		
		
		player.getInventory().clear();
		player.setHealth(3);
		
		super.clearEffects();
		Bukkit.broadcastMessage(getDisplayName() + " has been fatally wounded!");
		enrageTimer = ENRAGE_TRANSITION_DURATION + ENRAGE_DURATION;
		super.givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, ENRAGE_DURATION + ENRAGE_TRANSITION_DURATION, 5, true, true, true);
		super.givePotionEffect(PotionEffectType.REGENERATION, ENRAGE_DURATION + ENRAGE_TRANSITION_DURATION, 3, true, true, true);
		super.givePotionEffect(PotionEffectType.SLOW, ENRAGE_TRANSITION_DURATION, 100, true, true, true);
		super.givePotionEffect(PotionEffectType.JUMP, ENRAGE_TRANSITION_DURATION, -50, true, true, true);
		super.givePotionEffect(PotionEffectType.CONFUSION, ENRAGE_TRANSITION_DURATION + 20, -100, true, true, true);
		super.givePotionEffect(PotionEffectType.WEAKNESS, ENRAGE_TRANSITION_DURATION + 20, 100, true, true, true);
		//super.givePotionEffect(PotionEffectType.SLOW_DIGGING, ENRAGE_TRANSITION_DURATION + 20, 100, false, false, true);
		super.givePotionEffect(PotionEffectType.BLINDNESS, ENRAGE_TRANSITION_DURATION + 20, 100, true, true, true);
	}
	
	private void startEnrage() {
		doDamage(null, CustomDamageType.BLOOD_MAGIC, 10, true);
		
		PlayerInventory inv = player.getInventory();
		inv.clear();
		
		giveKitItems(KitGiveType.ARTHEA_SPECIAL);
		inv.setHeldItemSlot(0);
		
		player.updateInventory();
		getArmour().updateEquipment();
		
		super.clearEffects();
		
		super.givePotionEffect(PotionEffectType.HEAL, 5, 20, false, false, true);
		super.givePotionEffect(PotionEffectType.SPEED, ENRAGE_DURATION, 5, true, true, true);
		super.givePotionEffect(PotionEffectType.INCREASE_DAMAGE, ENRAGE_DURATION, 5, true, true, true);
		super.givePotionEffect(PotionEffectType.NIGHT_VISION, ENRAGE_DURATION, 1, true, true, true);
		super.givePotionEffect(PotionEffectType.FIRE_RESISTANCE, ENRAGE_DURATION, 1, true, true, true);
		super.givePotionEffect(PotionEffectType.JUMP, ENRAGE_DURATION, 3, true, true, true);
		super.givePotionEffect(PotionEffectType.GLOWING, ENRAGE_DURATION, 3, true, true, true);
		
		//makeMobsGlow();
	}
	
//	private PacketListener glower;
//	private Set<Integer> mobIDs = new HashSet<>();
//	private void makeMobsGlow() {
//		updateMobList();
//
//		// https://www.spigotmc.org/threads/simulating-potion-effect-glowing-with-protocollib.218828/#post-2246160
//		// http://wiki.vg/Entities#Entity
//		// https://bukkit.org/threads/glowing-for-one-person.446790/
//		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
//
//		glower = new PacketAdapter(NightfallPlugin.getPlugin(), ListenerPriority.HIGHEST, PacketType.Play.Server.ENTITY_METADATA) {
//			@Override
//			public void onPacketSending(PacketEvent event) {
//				if (event.getPlayer() != player) return;
//				PacketContainer packet = event.getPacket();
//				int id = packet.getIntegers().read(0);
//				if (!mobIDs.contains(id)) return;
//
//				List<WrappedWatchableObject> objects = packet.getWatchableCollectionModifier().read(0);
//				for (WrappedWatchableObject object : objects) {
//					if (object.getIndex() != 0) continue;
//					byte b = (byte) object.getValue();
//					b = (byte) (b | 0b01000000);
//					object.setValue(b);
//				}
//			}
//		};
//
//		protocolManager.addPacketListener(glower);
//	}
//	private void updateMobList() {
//		for (MonsterPlayer monster : MonsterManager.getManager().getAlivePlayerMobs()) {
//			Entity visibleEntity = monster.getDisguiseEntity();
//			if (visibleEntity == null)
//				visibleEntity = monster.getEntity();
//
//			mobIDs.add(visibleEntity.getEntityId());
//		}
//	}
//
//	private void cancelGlow() {
//		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
//		protocolManager.removePacketListener(glower);
//	}
}
