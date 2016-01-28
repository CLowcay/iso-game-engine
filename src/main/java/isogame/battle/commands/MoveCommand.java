package isogame.battle.commands;

import java.util.List;

import isogame.battle.Battle;
import isogame.engine.MapPoint;

public class MoveCommand extends Command {
	private final List<MapPoint> path;

	public MoveCommand(List<MapPoint> path) throws CommandException {
		if (path.size() < 2) throw new CommandException("Bad path in move command");
		this.path = path;
	}

	@Override
	public void doCmd(Battle battle) throws CommandException {
		if (!battle.battleState.canMove(path)) throw new CommandException("Invalid move command");
		battle.doMove(path);
	}
}

