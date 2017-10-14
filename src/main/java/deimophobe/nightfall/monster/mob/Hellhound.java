package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.WolfWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Random;

/**
 * Created by Deimophobe on 8/07/17.
 */
public class Hellhound extends Wolf {
	
	Hellhound(MonsterPlayer monster) {
		super(monster, MobType.HELLHOUND);
		getWeapon().addModifier(ItemModifierType.BURNING, 1, "Breath of Hell");
	}
	
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		
		Disguise disguise = getDisguise();
		FlagWatcher watcher = disguise.getWatcher();
		if (watcher instanceof WolfWatcher) {
			((WolfWatcher) watcher).setAngry(true);
		} else {
			Bukkit.getLogger().severe("Hellhound not disguised as wolf?");
		}
		
	}
	
	@Override
	public void update(boolean a, boolean b, boolean sec, boolean d, boolean e) {
		super.update(a,b,sec,d,e);
		//if (sec)
			//tryPlaceMagmaBlock();
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		//tryPlaceMagmaBlock();
	}
	
	private void tryPlaceMagmaBlock() {
		Random random = new Random();
		double dx = random.nextDouble()*6 - 3;
		double dy = random.nextDouble()*6 - 3;
		double dz = random.nextDouble()*6 - 3;
		Block block = monster.getLocation().add(dx, dy, dz).getBlock();
		if (block.getType().isSolid())
			TimedBlock.placeTimedBlock(new TimedBlock(block, Material.MAGMA, 140, monster));
	}
}
