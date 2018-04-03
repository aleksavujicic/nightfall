package deimophobe.nightfall.dwarf.kit.ranged;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by Deimophobe on 16/06/17.
 */
public abstract class AbstractToggleBow extends AbstractBow {
	public AbstractToggleBow(Dwarf dwarf) {super(dwarf);}
	private boolean active = false;
	private final static String ARROW_METADATA_KEY = "active";
	
	private final ComplexCooldown toggler = new ComplexCooldown(4, this::onToggle);
	
	@Override
	public void update() {
		super.update();
		toggler.update();
	}
	
	@Override
	public Projectile onBowFire(Projectile arrow, float force) {
		arrow = super.onBowFire(arrow, force);
		if (active) {
			arrow.setMetadata(ARROW_METADATA_KEY, new FixedMetadataValue(NightfallPlugin.getPlugin(), true));
		}
		return arrow;
	}
	
	protected void removeArrow(Projectile arrow) {
		arrow.removeMetadata(ARROW_METADATA_KEY, NightfallPlugin.getPlugin());
	}
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		if (click.isLeftClick()) {
			return toggler.tryUse();
		}
		return false;
	}
	
	protected void updateActive() {
		setActive(active);
	}
	
	protected void setActive(boolean setActive) {
		// Force false if disabled
		if (!canActivate()) setActive = false;
		
		// Don't do anything if not changed
		if (setActive == active) return;
		
		// Replace all instances in inv with shiny bow
		setShiny(setActive);
		active = setActive;
	}
	
	protected boolean isActive() {
		return active;
	}
	
	protected boolean isActiveProjectile(Projectile proj) {
		return proj.hasMetadata(ARROW_METADATA_KEY);
	}
	
	protected abstract boolean canActivate();
	protected void onToggle() {
		setActive(!active);
	}
}
