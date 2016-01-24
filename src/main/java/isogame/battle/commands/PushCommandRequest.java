package isogame.battle.commands;

import isogame.battle.Stats;
import isogame.battle.Targetable;
import isogame.battle.Turn;
import isogame.battle.TurnCharacter;
import isogame.engine.MapPoint;

public class PushCommandRequest extends CommandRequest {
	private final MapPoint agent;
	private final MapPoint target;

	public PushCommandRequest(MapPoint agent, MapPoint target) {
		this.agent = agent;
		this.target = target;
	}

	@Override
	public Command makeCommand(Turn turn) throws CommandException {
		TurnCharacter a = turn.getTurnCharacterAt(agent);
		Targetable t = turn.getTargetableAt(target);

		if (a != null && t != null) {
			boolean effective;
			if (!t.canPush()) effective = false;
			else if (a.player == t.getPlayer()) effective = true;
			else effective = Math.random() <= chanceOfSuccess(a.stats, t.getStats());

			if (turn.canPush(agent, target, effective)) {
				return new PushCommand(agent, target, effective);
			}
		}

		throw new CommandException("Invalid push command request");
	}

	public double chanceOfSuccess(Stats a, Stats t) {
		return a.strength / t.strength * 0.7;
	}
}

