package deimophobe.nightfall.util;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterEntity;
import org.bukkit.Location;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 2/05/18.
 */
public final class HitscanBuilder {
	private double thickness;
	private double particlePeriod = Hitscan.DEFAULT_PARTICLE_PERIOD;
	private Consumer<Location> particlePlacer = null;
	private Consumer<Dwarf> dwarfConsumer = null;
	private Consumer<MonsterEntity> mobConsumer = null;
	
	private HitscanBuilder() {}
	
	public static HitscanBuilder builder() {
		return new HitscanBuilder();
	}
	
	public HitscanBuilder withThickness(double thickness) {
		this.thickness = thickness;
		return this;
	}
	
	public HitscanBuilder withParticlePeriod(double particlePeriod) {
		this.particlePeriod = particlePeriod;
		return this;
	}
	
	public HitscanBuilder withParticlePlacer(Consumer<Location> particlePlacer) {
		this.particlePlacer = particlePlacer;
		return this;
	}
	
	public HitscanBuilder withDwarfConsumer(Consumer<Dwarf> dwarfConsumer) {
		this.dwarfConsumer = dwarfConsumer;
		return this;
	}
	
	public HitscanBuilder withMobConsumer(Consumer<MonsterEntity> mobConsumer) {
		this.mobConsumer = mobConsumer;
		return this;
	}
	
	public HitscanBuilder but() {
		return builder()
				.withThickness(thickness)
				.withParticlePeriod(particlePeriod)
				.withParticlePlacer(particlePlacer)
				.withDwarfConsumer(dwarfConsumer)
				.withMobConsumer(mobConsumer);
	}
	
	public Hitscan build() {
		return new Hitscan(thickness, particlePeriod, particlePlacer, dwarfConsumer, mobConsumer);
	}
}
