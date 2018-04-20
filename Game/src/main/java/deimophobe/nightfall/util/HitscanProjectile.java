package deimophobe.nightfall.util;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterEntity;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 29/12/17.
 */
public class HitscanProjectile extends CustomProjectile {
	
	private final double thickness;
	private final double particlePeriod;
	private final Consumer<Location> particlePlacer;
	private final Consumer<Dwarf> dwarfConsumer;
	private final Consumer<MonsterEntity> mobConsumer;
	
	public HitscanProjectile(
			Location location,
			Vector velocity,
			double thickness,
			double range,
			double particlePeriod,
			Consumer<Location> particlePlacer,
			Consumer<Dwarf> dwarfConsumer,
			Consumer<MonsterEntity> mobConsumer
	) {
		super((int) (range/velocity.length()), location, velocity, 0, 1);
		this.particlePeriod = particlePeriod;
		this.thickness = thickness;
		this.particlePlacer = particlePlacer;
		this.dwarfConsumer = dwarfConsumer;
		this.mobConsumer = mobConsumer;
	}
	
	@Override
	public void run() {
		boolean success = Util.fireHitscan(location, velocity, velocity.length(), thickness,  particlePeriod, particlePlacer, dwarfConsumer, mobConsumer);
		if (!success) {
			this.cancel();
		}
		
		super.run();
	}
}
