package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Deimophobe on 29/03/18.
 */
abstract class AnnotatedDoom implements Doom {
	private Title title;
	private MonsterSpawner spawner;
	
	protected void setTitle(Title title) {
		this.title = title;
	}
	
	protected void setSpawner(MonsterSpawner spawner) {
		this.spawner = spawner;
	}
	
	@Override
	public void showTitle() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			title.playTitle(player);
		}
	}
	
	@Override
	public void spawnMobs() {
		List<MonsterPlayer> monsterList = new ArrayList<>(MonsterManager.getManager().getDeadPlayers());
		Collections.shuffle(monsterList);
		
		for (MonsterPlayer monster : monsterList) {
			spawner.spawnMonster(monster);
		}
	}
}
