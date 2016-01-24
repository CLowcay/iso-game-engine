package isogame.battle.commands;

import java.util.Collection;

import isogame.battle.DamageToTarget;
import isogame.battle.Turn;
import isogame.engine.MapPoint;

public class AttackCommand extends Command {
	private final MapPoint agent;
	private final Collection<DamageToTarget> targets;


	public AttackCommand(
		MapPoint agent, Collection<DamageToTarget> targets
	) {
		this.agent = agent;
		this.targets = targets;
	}

	@Override
	public void doCmd(Turn turn) throws CommandException {
		if (!turn.canAttack(agent, targets))
			throw new CommandException("Invalid attack");
		turn.doAttack(agent, targets);
	}
}

