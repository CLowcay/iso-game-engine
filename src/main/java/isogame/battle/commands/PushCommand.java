package isogame.battle.commands;

import isogame.battle.Turn;
import isogame.engine.MapPoint;

public class PushCommand extends Command {
	private final MapPoint agent;
	private final MapPoint target;
	private final boolean effective; // determines if the push is effective

	public PushCommand(MapPoint agent, MapPoint target, boolean effective) {
		this.agent = agent;
		this.target = target;
		this.effective = effective;
	}

	@Override
	public void doCmd(Turn turn) throws CommandException {
		if (!turn.canPush(agent, target, effective))
			throw new CommandException("Invalid push command");
		turn.doPush(agent, target, effective);
	}
}

