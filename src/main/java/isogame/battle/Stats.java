package isogame.battle;

public class Stats {
	public final int ap;
	public final int hp;
	public final int mp;
	public final int strength;
	public final int intelligence;
	public final int vitality;

	public final double physicalBuff;
	public final double magicalBuff;

	public Stats(
		int ap, int hp, int mp,
		int strength, int intelligence, int vitality,
		double physicalBuff, double magicalBuff
	) {
		this.ap = ap;
		this.hp = hp;
		this.mp = mp;
		this.strength = strength;
		this.intelligence = intelligence;
		this.vitality = vitality;
		this.physicalBuff = physicalBuff;
		this.magicalBuff = magicalBuff;
	}
}

