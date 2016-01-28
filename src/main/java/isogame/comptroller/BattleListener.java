package isogame.comptroller;

import java.util.Collection;

import isogame.battle.commands.Command;
import isogame.engine.MapPoint;

public interface BattleListener {
	public void startTurn();
	public void endTurn();
	public void endBattle(boolean playerWins);
	public void moveRange(Collection<MapPoint> destinations);
	public void targetingInfo(Collection<MapPoint> targets);
	public void command(Command cmd);
}

