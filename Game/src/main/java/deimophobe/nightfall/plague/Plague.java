package deimophobe.nightfall.plague;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.monster.MonsterManager;

import java.util.Set;

/**
 * Created by Deimophobe on 15/04/17.
 */
public abstract class Plague {
	public abstract void startPlague();
	
	protected final void endPlague(GameDamageType reason) {
		if (hasEnded()) {
			NightfallPlugin.logger().warning("Tried to end plague but has already ended?");
			new RuntimeException("Tried to end plague but has already ended?").printStackTrace();
			
			return;
		}
		
		for (Dwarf dwarf : getPlagueds()) {
			killDwarf(dwarf, reason);
		}
		
		final int toKill = getAmountToKill(false);
		for (int i=0; i<toKill; i++) {
			killDwarf(getRandomPlagueable(), reason);
		}
		
		Game.getGame().notifyPlagueFinish();
	}

	public void endPlague() {
		endPlague(GameDamageType.FORCE_PLAGUED);
	}
	
	protected boolean checkEnd() {
		if (hasEnded()) return true;
		
		if (getAmountToKill(true) == 0) {
			endPlague();
			return true;
		} else {
			return false;
		}
	}


	protected final boolean hasEnded() {
		return Game.getGame().getPhase() != Phase.PLAGUE;
	}
	
	protected void killDwarf(Dwarf dwarf, GameDamageType reason) {
		dwarf.instaKill(null, reason);
	}
	protected void killDwarf(Dwarf dwarf) {
		killDwarf(dwarf, GameDamageType.FORCE_PLAGUED);
	}

	protected static Dwarf getRandomPlagueable() {
		return Misc.getRandom(getPlagueables());
	}
	protected static Dwarf getRandomPlagued() {
		return Misc.getRandom(getPlagueds());
	}
	protected static Set<Dwarf> getPlagueables() {
		return DwarfManager.getManager().getPlagueables();
	}
	protected static Set<Dwarf> getPlagueds() {
		return DwarfManager.getManager().getPlagued();
	}
	protected static boolean isPlagueable(Dwarf dwarf) {
		return dwarf.getPlagueStatus() == Dwarf.PlagueStatus.NORMAL || dwarf.getPlagueStatus() == Dwarf.PlagueStatus.PLAGUED;
	}
	protected static boolean isPlagued(Dwarf dwarf) {
		return dwarf.getPlagueStatus() == Dwarf.PlagueStatus.PLAGUED;
	}
	
	public static int getAmountToKill(boolean includePlagueds) {
		// Get numbers of various player sets
		int numGamePlayers = Game.getGame().getNumberOfPlayers();
		int numDwarves = DwarfManager.getManager().getNumberOfPlayers();
		int numMonsters = MonsterManager.getManager().getNumberOfPlayers();
		int numPlagueables = getPlagueables().size();
		int numPlagueds = getPlagueds().size();
		
		int desiredMonsterAmt = (numGamePlayers + 2)/3; // How many mobs we want
		int dwarvesToKill = desiredMonsterAmt - numMonsters; // How many more mobs we need
		dwarvesToKill = Math.min(dwarvesToKill, numPlagueables); // We can't kill more dwarves than there are plagueables
		
		if (includePlagueds) {
			// If we're counting plagued dwarves, then number to kill must be at least the number of plagueds.
			dwarvesToKill = Math.max(dwarvesToKill, numPlagueds);
		} else {
			// If we're not counting number of plagueds, then subtract from total.
			dwarvesToKill -= numPlagueds;
		}
		// Can't kill negative dwarves
		return Math.max(dwarvesToKill, 0);
	}

	public void onDwarfDeath(Dwarf dwarf) {

	}
}
