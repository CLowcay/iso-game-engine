package isogame.battle.commands;

import java.util.List;

import isogame.battle.Turn;
import isogame.engine.MapPoint;

public class MoveCommandRequest extends CommandRequest {
	private final MapPoint start;
	private final MapPoint target;

	public MoveCommandRequest(MapPoint start, MapPoint target) {
		this.start = start;
		this.target = target;
	}
	
	@Override
	public Command makeCommand(Turn turn) throws CommandException {
		List<MapPoint> path = turn.findPath(start, target);
		if (turn.canMove(path)) {
			return new MoveCommand(path);
		} else {
			throw new CommandException("Bad path command request");
		}
	}
}
