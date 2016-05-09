package isogame.battle;

import isogame.battle.data.AbilityInfo;
import isogame.battle.data.AbilityType;
import isogame.battle.data.Range;
import isogame.battle.data.Stats;

public class Ability {
	public final AbilityInfo info;

	public Ability(AbilityInfo info) {
		this.info = info;
	}

	private final double const_a = 3;
	private final double const_b = 4;
	private final double const_h = 12;
	private final double const_i = 15;

	public double damageFormula(
		double attackBuff, double defenceBuff, Stats a, Stats t
	) {
		double q = info.type == AbilityType.WEAPON? 1 : info.eff;
		return
			q * (1 + attackBuff - defenceBuff) *
			(0.9 + (0.2 * Math.random())) *
			(((double) a.attack) - ((double) t.defence)) *
			((const_b * ((double) a.power)) / const_a);
	}

	public double healingFormula(
		double attackBuff, double defenceBuff, Stats a, Stats t
	) {
		return (info.eff * const_h *
			(0.9 + (0.2 * Math.random())) *
			(double) t.vitality) / const_i;
	}

	public DamageToTarget computeDamageToTarget(
		Character a, Targetable t
	) {
		Stats aStats = a.getStats();
		Stats tStats = t.getStats();

		double damage = info.heal?
			healingFormula(a.getAttackBuff(), t.getDefenceBuff(), aStats, tStats) :
			damageFormula(a.getAttackBuff(), t.getDefenceBuff(), aStats, tStats);

		return new DamageToTarget(t.getPos(), (int) damage,
			imposeEffect(info.chance, info.statusEffect.orElse(null)),
			imposeEffect(info.chance, info.instantBefore.orElse(null)),
			imposeEffect(info.chance, info.instantAfter.orElse(null)));
	}

	public <T> T imposeEffect(double p, T effect) {
		return Math.random() < p ? effect : null;
	}
}

