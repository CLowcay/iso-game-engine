package isogame.battle.commands;

import java.util.Collection;

import isogame.battle.Battle;
import isogame.battle.DamageToTarget;
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
	public void doCmd(Battle battle) throws CommandException {
		if (!battle.battleState.canAttack(agent, targets))
			throw new CommandException("Invalid attack");
		battle.doAttack(agent, targets);
	}
}

