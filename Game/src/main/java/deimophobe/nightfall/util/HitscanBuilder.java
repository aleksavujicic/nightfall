package deimophobe.nightfall.util;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 8/05/18.
 */
public final class HitscanBuilder {
	private double thickness;
	private double particlePeriod;
	private Consumer<Location> particlePlacer;
	private Consumer<Dwarf> dwarfConsumer;
	private Consumer<MonsterEntity> mobConsumer;
	private Consumer<Block> hitBlockConsumer;
	
	private HitscanBuilder() {
	}
	
	public static HitscanBuilder aHitscan() {
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
	
	public HitscanBuilder withHitBlockConsumer(Consumer<Block> hitBlockConsumer) {
		this.hitBlockConsumer = hitBlockConsumer;
		return this;
	}
	
	public HitscanBuilder but() {
		return clone();
	}
	
	public HitscanBuilder clone() {
		return aHitscan()
				.withThickness(thickness)
				.withParticlePeriod(particlePeriod)
				.withParticlePlacer(particlePlacer)
				.withDwarfConsumer(dwarfConsumer)
				.withMobConsumer(mobConsumer)
				.withHitBlockConsumer(hitBlockConsumer);
	}
	
	public Hitscan build() {
		return new Hitscan(thickness, particlePeriod, particlePlacer, dwarfConsumer, mobConsumer, hitBlockConsumer);
	}
}
