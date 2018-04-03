package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import me.libraryaddict.disguise.disguisetypes.watchers.PolarBearWatcher;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Random;

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
		Random random = new Random();
		double dx = random.nextDouble()*6 - 3;
		double dy = random.nextDouble()*6 - 3;
		double dz = random.nextDouble()*6 - 3;
		Block block = monster.getLocation().add(dx, dy, dz).getBlock();
		if (block.getType().isSolid()) {
			TimedBlock.placeTimedBlock(new TimedBlock(block, Material.PACKED_ICE, 10 * 20, monster));
		}
	}
}
