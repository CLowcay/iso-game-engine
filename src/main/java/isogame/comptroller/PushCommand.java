package isogame.comptroller;

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
	public void docmd(Turn turn) {
		turn.doPush(agent, target, effective);
	}
}

