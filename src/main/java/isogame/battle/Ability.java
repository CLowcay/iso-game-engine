package isogame.battle;

public class Ability {
	public final String name;
	public final AbilityType type;
	public final int ap;
	public final int pp;
	public final double effFactor;
	public final double chance;
	public final boolean heal;
	public final Range range;
	public final boolean isMana;
	public final Ability replacement;
	public final InstantEffect instantBefore;
	public final InstantEffect instantAfter;
	public final StatusEffect statusEffect;

	public Ability(
		String name,
		AbilityType type,
		int ap,
		int pp,
		double effFactor,
		double chance,
		boolean heal,
		Range range,
		boolean isMana,
		Ability replacement,
		InstantEffect instantBefore,
		InstantEffect instantAfter,
		StatusEffect statusEffect
	) {
		this.name = name;
		this.type = type;
		this.ap = ap;
		this.pp = pp;
		this.effFactor = effFactor;
		this.chance = chance;
		this.heal = heal;
		this.range = range;
		this.isMana = isMana;
		this.replacement = replacement;
		this.instantBefore = instantBefore;
		this.instantAfter = instantAfter;
		this.statusEffect = statusEffect;
	}

	private final double const_a = 3;
	private final double const_b = 4;
	private final double const_h = 12;
	private final double const_i = 15;

	public double damageFormula(
		double attackBuff, double defenceBuff, Stats a, Stats t
	) {
		double q = type == AbilityType.ATTACK? 1 : effFactor;
		return
			q * (1 + attackBuff - defenceBuff) *
			(0.9 + (0.2 * Math.random())) *
			(((double) a.attack) - ((double) t.defence)) *
			((const_b * ((double) a.power)) / const_a);
	}

	public double healingFormula(
		double attackBuff, double defenceBuff, Stats a, Stats t
	) {
		return (effFactor * const_h *
			(0.9 + (0.2 * Math.random())) *
			(double) t.vitality) / const_i;
	}

	public DamageToTarget computeDamageToTarget(
		Character a, Targetable t
	) {
		Stats aStats = a.getStats();
		Stats tStats = t.getStats();

		double damage = heal?
			healingFormula(a.getAttackBuff(), t.getDefenceBuff(), aStats, tStats) :
			damageFormula(a.getAttackBuff(), t.getDefenceBuff(), aStats, tStats);

		return new DamageToTarget(t.getPos(), (int) damage,
			imposeEffect(chance, statusEffect),
			imposeEffect(chance, instantBefore),
			imposeEffect(chance, instantAfter));
	}

	public <T> T imposeEffect(double p, T effect) {
		return Math.random() < p ? effect : null;
	}
}

