package isogame.battle;

public class Weapon extends InventoryItem {
	public final int physicalAttack;
	public final int magicalAttack;
	public final Ability attack;

	public Weapon(int physicalAttack, int magicalAttack, Ability attack) {
		this.physicalAttack = physicalAttack;
		this.magicalAttack = magicalAttack;
		this.attack = attack;
	}
}

