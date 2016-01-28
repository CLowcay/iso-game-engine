package isogame.battle.commands;

import java.util.Collection;
import java.util.stream.Collectors;

import isogame.battle.Ability;
import isogame.battle.BattleState;
import isogame.battle.Character;
import isogame.battle.DamageToTarget;
import isogame.engine.MapPoint;

public class UseAbilityCommandRequest extends CommandRequest {
	private final MapPoint agent;
	private final MapPoint target;
	private final Ability ability;

	public UseAbilityCommandRequest(
		MapPoint agent, MapPoint target, Ability ability
	) {
		this.agent = agent;
		this.target = target;
		this.ability = ability;
	}

	@Override
	public Command makeCommand(BattleState battleState) throws CommandException {
		Character a = battleState.getCharacterAt(agent);
		Collection<DamageToTarget> targets =
			battleState.getAbilityTargets(agent, ability, target).stream()
			.map(t -> ability.computeDamageToTarget(a, t))
			.collect(Collectors.toList());

		if (battleState.canDoAbility(agent, ability, targets)) {
			return new UseAbilityCommand(agent, ability, targets);
		} else {
			throw new CommandException("Invalid ability command request");
		}
	}
}

