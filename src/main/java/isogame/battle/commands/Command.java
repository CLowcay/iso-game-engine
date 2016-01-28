package isogame.battle.commands;

import isogame.battle.Battle;

public abstract class Command {
	public abstract void doCmd(Battle turn) throws CommandException;
}

