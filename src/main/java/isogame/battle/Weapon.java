package isogame.battle;

public class Weapon extends InventoryItem {
	public final WeaponType type;
	public final Stats buff;
	public final Ability attack;

	public Weapon(
		WeaponType type, Stats buff, Ability attack
	) {
		this.type = type;
		this.buff = buff;
		this.attack = attack;
	}
}

