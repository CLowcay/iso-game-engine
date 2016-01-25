package isogame.battle;

public class Stats {
	public final int ap;
	public final int mp;
	public final int strength;
	public final int intelligence;
	public final int vitality;

	public final double physicalBuff;
	public final double magicalBuff;

	public Stats() {
		this.ap = 0;
		this.mp = 0;
		this.strength = 0;
		this.intelligence = 0;
		this.vitality = 0;
		this.physicalBuff = 0;
		this.magicalBuff = 0;
	}

	public Stats(
		int ap, int mp,
		int strength, int intelligence, int vitality,
		double physicalBuff, double magicalBuff
	) {
		this.ap = ap;
		this.mp = mp;
		this.strength = strength;
		this.intelligence = intelligence;
		this.vitality = vitality;
		this.physicalBuff = physicalBuff;
		this.magicalBuff = magicalBuff;
	}

	public Stats add(Stats stats) {
		return new Stats(
			ap + stats.ap,
			mp + stats.mp,
			strength + stats.strength,
			intelligence + stats.intelligence,
			vitality + stats.vitality,
			physicalBuff + stats.physicalBuff,
			magicalBuff + stats.magicalBuff
		);
	}
}

