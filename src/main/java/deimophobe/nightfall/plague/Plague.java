package deimophobe.nightfall.plague;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterManager;

import java.util.Set;

/**
 * Created by Deimophobe on 15/04/17.
 */
public abstract class Plague {
	public abstract void startPlague();
	
	public void endPlague(GameDamageType reason) {
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
	
	public static final int getAmountToKill(boolean includePlagueds) {
		int desiredMonsterAmt = (Game.getGame().getNumPlayers()+2)/3;
		int dwarvesToKill = desiredMonsterAmt - MonsterManager.getManager().getNumberOfPlayers();
		int numPlagueds = getPlagueds().size();
		
		if (includePlagueds) {
			// If we're counting plagued dwarves, then number to kill must be at least the number of plagueds.
			dwarvesToKill = Math.max(dwarvesToKill, numPlagueds);
		} else {
			// If we're not counting number of plagueds, then subtract from total.
			dwarvesToKill -= numPlagueds;
		}
		return Math.max(dwarvesToKill, 0);
	}

	public void onDwarfDeath(Dwarf dwarf) {

	}
}
