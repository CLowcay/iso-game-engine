package isogame.comptroller;

import isogame.battle.InstantEffect;
import isogame.battle.StatusEffect;
import isogame.battle.Turn;
import isogame.engine.MapPoint;

public class AttackCommand extends Command {
	private final MapPoint agent;
	private final MapPoint target;
	private final long damage;
	private final StatusEffect statusEffect;
	private final InstantEffect pre;
	private final InstantEffect post;


	public AttackCommand(
		MapPoint agent, MapPoint target, long damage,
		StatusEffect statusEffect, InstantEffect pre, InstantEffect post
	) {
		this.agent = agent;
		this.target = target;
		this.damage = damage;
		this.statusEffect = statusEffect;
		this.pre = pre;
		this.post = post;
	}

	@Override
	public void docmd(Turn turn) {
		turn.doAttack(agent, target, damage, statusEffect, pre, post);
	}
}

