package deimophobe.dvz.plague;

/**
 * Created by Deimophobe on 9/03/17.
 */
class InstaPlague extends Plague {
	@Override
	protected void onStart() {
		forceEnd();
	}
}
