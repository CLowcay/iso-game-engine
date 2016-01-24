package isogame.battle;

public class Ability {
	public final String name;
	public final AbilityType type;
	public final Range range;
	public final boolean isMana;
	public final Ability replacement;
	public final double effFactor;
	public final InstantEffect instantBefore;
	public final InstantEffect instantAfter;
	public final StatusEffect statusEffect;

	public Ability(
		String name,
		AbilityType type,
		Range range,
		boolean isMana,
		Ability replacement,
		double effFactor,
		InstantEffect instantBefore,
		InstantEffect instantAfter,
		StatusEffect statusEffect
	) {
		this.name = name;
		this.type = type;
		this.range = range;
		this.isMana = isMana;
		this.replacement = replacement;
		this.effFactor = effFactor;
		this.instantBefore = instantBefore;
		this.instantAfter = instantAfter;
		this.statusEffect = statusEffect;
	}

	private final double const_a = 3;
	private final double const_b = 4;

	public DamageToTarget computeDamageToTarget(
		TurnCharacter a, Targetable t
	) {
		double q = effFactor;
		double buff;
		double attack;
		double defence;
		double attackPower;
		double defencePower;

		switch (type) {
			case ATTACK:
				q = 1;
				if (a.weapon.physicalAttack > a.weapon.magicalAttack) {
					buff = 1 + a.stats.physicalBuff - t.getStats().physicalBuff;
					attack = a.weapon.physicalAttack;
					defence = t.getPhysicalDefence();
					attackPower = a.stats.strength;
					defencePower = t.getStats().strength;
				} else {
					buff = 1 + a.stats.magicalBuff - t.getStats().magicalBuff;
					attack = a.weapon.magicalAttack;
					defence = t.getMagicalDefence();
					attackPower = a.stats.intelligence;
					defencePower = t.getStats().intelligence;
				}
			case SPECIAL:
				throw new RuntimeException("Not implemented");
			case SKILL:
				buff = 1 + a.stats.physicalBuff - t.getStats().physicalBuff;
				attack = a.weapon.physicalAttack;
				defence = t.getPhysicalDefence();
				attackPower = a.stats.strength;
				defencePower = t.getStats().strength;
				break;
			case MAGIC:
				buff = 1 + a.stats.magicalBuff - t.getStats().magicalBuff;
				attack = a.weapon.magicalAttack;
				defence = t.getMagicalDefence();
				attackPower = a.stats.intelligence;
				defencePower = t.getStats().intelligence;
				break;
			default:
				throw new RuntimeException("This cannot happen");
		}

		double damage = q * buff *
			(0.9 + (0.2 * Math.random())) *
			(attack - defence) *
			((const_b * attackPower) / const_a);

		double pEffect = q * (attackPower / defencePower) *
			(Math.cbrt(attack) / Math.cbrt(defence)) * 0.5;

		return new DamageToTarget(t.getPos(), (int) damage,
			imposeEffect(pEffect, statusEffect),
			imposeEffect(pEffect, instantBefore),
			imposeEffect(pEffect, instantAfter));
	}

	public <T> T imposeEffect(double p, T effect) {
		return Math.random() < p ? effect : null;
	}
}

