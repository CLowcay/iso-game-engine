package isogame.comptroller;

import isogame.battle.Ability;
import isogame.battle.InstantEffect;
import isogame.battle.StatusEffect;
import isogame.battle.Turn;
import isogame.engine.MapPoint;

public class UseAbilityCommand extends Command {
	private final MapPoint agent;
	private final MapPoint target;
	private final Ability ability;
	private final long damage;
	private final StatusEffect statusEffect;
	private final InstantEffect pre;
	private final InstantEffect post;

	public UseAbilityCommand(
		MapPoint agent, MapPoint target, Ability ability, long damage,
		StatusEffect statusEffect, InstantEffect pre, InstantEffect post
	) {
		this.agent = agent;
		this.target = target;
		this.ability = ability;
		this.damage = damage;
		this.statusEffect = statusEffect;
		this.pre = pre;
		this.post = post;
	}

	@Override
	public void docmd(Turn turn) {
		turn.doAbility(agent, target, ability, damage, statusEffect, pre, post);
	}
}

