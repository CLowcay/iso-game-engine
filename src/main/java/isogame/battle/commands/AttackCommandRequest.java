package isogame.battle.commands;

import java.util.Collection;
import java.util.stream.Collectors;

import isogame.battle.Ability;
import isogame.battle.Character;
import isogame.battle.DamageToTarget;
import isogame.battle.Turn;
import isogame.engine.MapPoint;

public class AttackCommandRequest extends CommandRequest {
	private final MapPoint agent;
	private final MapPoint target;

	public AttackCommandRequest(MapPoint agent, MapPoint target) {
		this.agent = agent;
		this.target = target;
	}

	@Override
	public Command makeCommand(Turn turn) throws CommandException {
		Character a = turn.getCharacterAt(agent);
		Ability ability = a.getWeapon().attack;
		Collection<DamageToTarget> targets =
			turn.getAbilityTargets(agent, ability, target).stream()
			.map(t -> ability.computeDamageToTarget(a, t))
			.collect(Collectors.toList());

		if (turn.canAttack(agent, targets)) {
			return new AttackCommand(agent, targets);
		} else {
			throw new CommandException("Invalid ability command request");
		}
	}
}

