package isogame.battle.commands;

import isogame.battle.Battle;
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
	public void doCmd(Battle battle) throws CommandException {
		if (!battle.battleState.canPush(agent, target, effective))
			throw new CommandException("Invalid push command");
		battle.doPush(agent, target, effective);
	}
}

