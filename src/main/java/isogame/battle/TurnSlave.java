package isogame.battle;

import isogame.battle.commands.Command;
import isogame.battle.commands.CommandException;

public class TurnSlave extends Turn {
	public void doCmd(Command cmd) throws CommandException {
		cmd.doCmd(this);
	}
}

