package deimophobe.nightfall;

import deimophobe.nightfall.game.Game;

import static deimophobe.nightfall.common.Misc.DO_NOTHING;

/**
 * Created by Deimophobe on 16/10/18.
 */
public enum VoteType {
	MAP_CHANGE(0.75, Game::createNewGame, DO_NOTHING);
	
	;
	
	
	private final double passRatio;
	private final Runnable onPass;
	private final Runnable onFail;
	
	VoteType(double passRatio, Runnable onPass, Runnable onFail) {
		this.passRatio = passRatio;
		this.onPass = onPass;
		this.onFail = onFail;
	}
	
	public void finishedVote(double actualRatio) {
		if (actualRatio >= passRatio) {
			onPass.run();
		} else {
			onFail.run();
		}
	}
}
