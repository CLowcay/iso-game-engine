package isogame.battle;

public class Stats {
	public final int ap;
	public final int mp;
	public final int power;
	public final int vitality;
	public final int attack;
	public final int defence;
	public final WeaponType weaponType;

	public final double buff;

	public Stats() {
		this.ap = 0;
		this.mp = 0;
		this.power = 0;
		this.vitality = 0;
		this.attack = 0;
		this.defence = 0;
		this.buff = 0;
		this.weaponType = WeaponType.SWORD;
	}

	public Stats(
		int ap, int mp,
		int power, int vitality,
		int attack, int defence,
		double buff, WeaponType weaponType
	) {
		this.ap = ap;
		this.mp = mp;
		this.power = power;
		this.vitality = vitality;
		this.attack = attack;
		this.defence = defence;
		this.buff = buff;
		this.weaponType = weaponType;
	}

	public Stats(
		int ap, int mp,
		int power, int vitality,
		int attack, int defence,
		double buff
	) {
		this(
			ap, mp, power,
			vitality, attack,
			defence, buff,
			WeaponType.SWORD);
	}

	public Stats add(Stats stats) {
		return new Stats(
			ap + stats.ap,
			mp + stats.mp,
			power + stats.power,
			vitality + stats.vitality,
			attack + stats.attack,
			defence + stats.defence,
			buff + stats.buff,
			weaponType
		);
	}
}

