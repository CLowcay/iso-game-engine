package isogame.battle.commands;

import java.util.List;

import isogame.battle.Turn;
import isogame.engine.MapPoint;

public class MoveCommand extends Command {
	private final List<MapPoint> path;

	public MoveCommand(List<MapPoint> path) throws CommandException {
		if (path.size() < 2) throw new CommandException("Bad path in move command");
		this.path = path;
	}

	@Override
	public void doCmd(Turn turn) throws CommandException {
		if (!turn.canMove(path)) throw new CommandException("Invalid move command");
		turn.doMove(path);
	}
}

