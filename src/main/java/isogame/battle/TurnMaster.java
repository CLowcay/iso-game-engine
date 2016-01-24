package isogame.battle;

import isogame.battle.commands.Command;
import isogame.battle.commands.CommandException;
import isogame.battle.commands.CommandRequest;

public class TurnMaster extends Turn {
	public Command doCommand(CommandRequest cmd) throws CommandException {
		return cmd.makeCommand(this);
	}
}

