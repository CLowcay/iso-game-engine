package isogame.comptroller;

import isogame.battle.commands.Command;
import isogame.battle.commands.CommandException;

public interface BattleListener {
	public void startTurn();
	public void endTurn();
	public void endBattle(boolean playerWins);
	public void badCommand(CommandException e);
	public void command(Command cmd);
}

