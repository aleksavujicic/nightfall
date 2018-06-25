package deimophobe.nightfall.cooldown;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Deimophobe on 28/04/18.
 */
public class CooldownHolder implements Updateable {
	
	private final Set<Updateable> updateables = new HashSet<>();
	private final Set<Expirable> expirables = new HashSet<>();
	
	// Only used to temporary hold any new items to prevent concurrent modifications
	// from updateables creating more updateables (note this is not a multithread issue).
	private final Set<Updateable> queuedUpdateables = new HashSet<>();
	private boolean updating;
	
	@Override
	public void update() {
		updating = true;
		
		updateables.forEach(Updateable::update);
		removeExpireables();
		expirables.forEach(Expirable::update);
		removeExpireables();
		
		updating = false;
		
		queuedUpdateables.forEach(this::addUpdateable);
		queuedUpdateables.clear();
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
		checkNotNull(updateable, "Updateable must not be null");
		if (updating) {
			queuedUpdateables.add(updateable);
		} else {
			if (updateable instanceof Expirable) {
				expirables.add((Expirable) updateable);
			} else {
				updateables.add(updateable);
			}
		}
	}
}
