package isogame.battle;

import isogame.engine.StageInfo;

public class Battle {
	public final StageInfo terrain;
	public final Collection<Character> characters;

	public Battle(StageInfo terrain, Collection<Character> characters) {
		this.terrain = terrain;
		this.characters = characters;
	}
}

