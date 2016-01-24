package isogame.battle.commands;

import isogame.battle.Turn;

public abstract class CommandRequest {
	public abstract Command makeCommand(Turn turn) throws CommandException;
}

