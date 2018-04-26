package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.blocks.timedblock.DataTimedBlock;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import me.libraryaddict.disguise.disguisetypes.watchers.PolarBearWatcher;
import org.bukkit.Material;
import org.bukkit.block.Block;

class PolarBabes extends AbstractMob {

	PolarBabes(MonsterPlayer monster){
		super(monster, MobType.POLARBABE);
	}

	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		changeDisguiseWatcher(PolarBearWatcher.class, pw -> pw.setBaby(true));
	}

	@Override
	public void update() {
		super.update();
		if(everyNthTick(20)) {
			tryPlaceIce();
		}
	}

	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		damage.setManaDrain(5);
		damage.addPostDamageHandler(this::tryPlaceIce);
	}

	private void tryPlaceIce() {
		Block block = Misc.randomLocation(monster.getLocation(), 5, 3, 5).getBlock();
		
		if (block.getType().isSolid()) {
			BlockManager.getManager().placeTimedBlock(new DataTimedBlock(10*20, block, monster, Material.PACKED_ICE));
		}
	}
}
