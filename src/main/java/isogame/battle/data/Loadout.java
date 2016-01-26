package isogame.battle.data;

import isogame.battle.Character;

public class Loadout {
	private final Character c1;
	private final Character c2;
	private final Character c3;
	private final Character c4;
	private final Collection<InventoryItem> items;

	public Loadout(
		Character c1, Character c2, Character c3, Character c4,
		Collection<InventoryItem> items
	) {
		this.c1 = c1;
		this.c2 = c2;
		this.c3 = c3;
		this.c4 = c4;
		this.items = items;
	}
}

