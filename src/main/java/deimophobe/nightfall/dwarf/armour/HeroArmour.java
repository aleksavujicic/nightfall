package deimophobe.nightfall.dwarf.armour;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.game.Curse;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.hero.Hero;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.region.Region;
import deimophobe.nightfall.util.ArmourSlot;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 6/05/17.
 */
public class HeroArmour extends StaticArmour {
	private final Hero hero;
	private final ArmourSlot slot;
	private final CustomItem armour;
	
	public HeroArmour(Hero hero, CustomItem armour) {
		this(hero, armour, ArmourSlot.HEAD);
	}
	
	public HeroArmour(Hero hero, CustomItem armour, ArmourSlot slot) {
		this.hero = hero;
		this.slot = slot;
		this.armour = armour;
		updateEquipment();
	}
	
	@Override
	public void addModifier(ItemModifierType type, int value, String reason, ArmourSlot slot) {
		addModifier(type, value, reason);
		if (Game.getGame().isCurseActive(Curse.BLIZZARD)){
			for(Player player : Bukkit.getOnlinePlayers()){
				player.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 25, 10, 10, 10, 0);
			}
			for(Dwarf dwarf : DwarfManager.getManager().getDwarves()){
				dwarf.givePotionEffect(PotionEffectType.SLOW,15*20,1,true,true,true);
			}
		}
	}
	
	@Override
	public void addModifier(ItemModifierType type, int value, String reason) {
		armour.addModifier(type, value, reason);
		updateEquipment();
	}
	
	@Override
	public void updateEquipment() {
		slot.equipArmour(hero, armour);
	}
	
	@Override
	public double getResistance() {
		GameMap map = GameMap.getCurrentMap();
		Region shrine = map.getCurrentShrineRegion();
		
		double goldboost = 0;
		if (shrine != null && shrine.containsPlayer(hero))
			goldboost = 0.05;
		
		double dwarfBoost = 0.02 * Math.sqrt(DwarfManager.getManager().getNumberOfPlayers());
		return Math.min(0.7 + goldboost + dwarfBoost, 0.85);
	}
	
	@Override
	public int getManaRegenRate() {
		int mana = 5;
		if (Game.getGame().isCurseActive(Curse.DOOM)) mana = mana - 2;
		if (Game.getGame().isCurseActive(Curse.SUPER_DOOM)) mana = mana - 58;
		return mana;
	}
}
