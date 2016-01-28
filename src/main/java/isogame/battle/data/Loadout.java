package isogame.battle.data;

import java.util.Collection;

import isogame.battle.Character;
import isogame.battle.InventoryItem;

public class Loadout {
	public final Character c1;
	public final Character c2;
	public final Character c3;
	public final Character c4;
	public final Collection<InventoryItem> items;

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

