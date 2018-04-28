package deimophobe.nightfall.cooldown;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 28/04/18.
 */
public class CooldownHolder implements Updateable {
	
	private final Set<Updateable> updateables = new HashSet<>();
	private final Set<Expirable> expirables = new HashSet<>();
	
	@Override
	public void update() {
		updateables.forEach(Updateable::update);
		removeExpireables();
		expirables.forEach(Expirable::update);
		removeExpireables();
	}
	
	private void removeExpireables() {
		expirables.removeIf(expirable -> {
			if (expirable.hasExpired()) {
				expirable.onExpiry();
				return true;
			} else {
				return false;
			}
		});
	}
	
	public void addUpdateable(Updateable updateable) {
		if (updateable instanceof Expirable) {
			expirables.add((Expirable) updateable);
		} else {
			updateables.add(updateable);
		}
	}
}
