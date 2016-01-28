package isogame.battle.commands;

import isogame.battle.BattleState;

public abstract class CommandRequest {
	public abstract Command makeCommand(BattleState turn) throws CommandException;
}

