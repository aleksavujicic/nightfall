package deimophobe.nightfall.cooldown;

/**
 * Created by Deimophobe on 19/01/18.
 */
@FunctionalInterface
public interface Updateable {
	void update();
	
	default LifetimeExpireable butLasts(int lifetime) {
		return new LifetimeExpireable(lifetime) {
			@Override
			public void update() {
				super.update();
				Updateable.this.update();
			}
		};
	}
}
