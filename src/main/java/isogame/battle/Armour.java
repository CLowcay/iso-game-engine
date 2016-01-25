package isogame.battle;

public class Armour {
	public final int physicalDefence;
	public final int magicalDefence;
	public final Stats buff;

	public Armour(int physicalDefence, int magicalDefence, Stats buff) {
		this.physicalDefence = physicalDefence;
		this.magicalDefence = magicalDefence;
		this.buff = buff;
	}
}

