package deimophobe.nightfall.damage;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.event.DwarfDamageEvent;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.game.GameEntity;
import deimophobe.nightfall.monster.MonsterEntity;
import org.bukkit.ChatColor;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;

/**
 * Created by Deimophobe on 29/08/17.
 */
public class DwarfDamage extends GameDamage<GameEntity<?>, Dwarf> {
	private double armourShred;
	public double getArmourShred() {return armourShred;}
	public void setArmourShred(double armourShred) {this.armourShred = armourShred;}
	public void addArmourShred(double amt) {this.armourShred += amt;}
	public void multiplyArmourShred(double multiply) {this.armourShred *= multiply;}
	
	private int manaDrain;
	public int getManaDrain() {return manaDrain;}
	public void setManaDrain(int manaDrain) {this.manaDrain = manaDrain;}
	public void addManaDrain(int manaDrain) {this.manaDrain += manaDrain;}
	public void multiplyManaDrain(double multiply) {this.manaDrain *= multiply;}
	
	public DwarfDamage(GameEntity attacker, Dwarf receiver, GameDamageType type, double damage, Projectile arrow) {
		super(attacker, receiver, type, damage, arrow);
		addHandlers();
	}
	
	public Dwarf getDwarf() {
		return getReceiver();
	}
	
	@Override
	void notifyEntities() {
		if (attacker instanceof MonsterEntity)
			((MonsterEntity) attacker).onDamageAttack(this);
		receiver.onDamageReceive(this);
	}
	
	@Override
	void callEvents() {
		DwarfDamageEvent event = new DwarfDamageEvent(this);
		Misc.dispatchEvent(event);
	}
	
	private void addHandlers() {
		if (Game.getGame().getPhase() == Phase.BUILD) {
			addPreDamageHandler(PreDamagePriority.DWARF_BUILD_PHASE_SAVER, () -> {
				if (willKill()) {
					this.forceSoftCancel();
					
					receiver.respawn();
					new BukkitRunnable() {
						@Override public void run() { receiver.respawn(); }
					}.runTaskLater(NightfallPlugin.getPlugin(), 1);
					receiver.sendTitleMessage(ChatColor.GOLD + "It is not your time to die yet");
				}
			});
		}
		
		addPostDamageHandler(() -> {
			receiver.getArmour().damage(armourShred);
			receiver.useMana(manaDrain);
			if (manaDrain > 0) receiver.playSound("entity.zombie_villager.converted", 1f, 1.5f, true);
		});
	}
	
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.####");
		return super.toString()
				+ "  Armour Shred: " + df.format(armourShred) + "\n"
				+ "  Mana Drain: " + manaDrain;
	}
}
