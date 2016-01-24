package isogame.battle;

import isogame.engine.MapPoint;

public class DamageToTarget {
	public final MapPoint target;
	public final int damage;
	public final StatusEffect statusEffect;
	public final InstantEffect pre;
	public final InstantEffect post;

	public DamageToTarget(
		MapPoint target,
		int damage,
		StatusEffect statusEffect,
		InstantEffect pre,
		InstantEffect post
	) {
		this.target = target;
		this.damage = damage;
		this.statusEffect = statusEffect;
		this.pre = pre;
		this.post = post;
	}
}

