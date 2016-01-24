package isogame.battle.commands;

import isogame.battle.Turn;

public abstract class Command {
	public abstract void doCmd(Turn turn) throws CommandException;
}

