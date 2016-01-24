package isogame.battle.commands;

import java.util.Collection;

import isogame.battle.Ability;
import isogame.battle.DamageToTarget;
import isogame.battle.Turn;
import isogame.engine.MapPoint;

public class UseAbilityCommand extends Command {
	private final MapPoint agent;
	private final Ability ability;
	private final Collection<DamageToTarget> targets;

	public UseAbilityCommand(
		MapPoint agent, Ability ability, Collection<DamageToTarget> targets
	) {
		this.agent = agent;
		this.ability = ability;
		this.targets = targets;
	}

	@Override
	public void doCmd(Turn turn) throws CommandException {
		if (!turn.canDoAbility(agent, ability, targets))
			throw new CommandException("Invalid ability command");
		turn.doAbility(agent, ability, targets);
	}
}

