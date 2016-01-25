package isogame.battle;

public class Weapon extends InventoryItem {
	public final int physicalAttack;
	public final int magicalAttack;
	public final Stats buff;
	public final Ability attack;

	public Weapon(
		int physicalAttack, int magicalAttack, Stats buff, Ability attack
	) {
		this.physicalAttack = physicalAttack;
		this.magicalAttack = magicalAttack;
		this.buff = buff;
		this.attack = attack;
	}
}

