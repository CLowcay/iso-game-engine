package isogame.battle.data;

import isogame.engine.CorruptDataException;

public enum AbilityType {
	WEAPON, SPECIAL, SKILL, SPELL;

	public static AbilityType parse(String s)
		throws CorruptDataException
	{
		switch (s.toLowerCase()) {
			case "weapon": return WEAPON;
			case "special": return SPECIAL;
			case "skill": return SKILL;
			case "spell": return SPELL;
			default: throw new CorruptDataException("Unknown ability type " + s);
		}
	}
}

