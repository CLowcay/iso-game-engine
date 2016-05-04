package isogame.battle.data;

import isogame.engine.CorruptDataException;

public enum StatusEffectType {
	ACCELERATED, DAZED, DEBILITATED, ENERGIZED, FEARED,
	IMPRISONED, PRECISE, RESISTANT, REVERBERATING, VULNERABLE,
	ONGOING, PANICKED, REGENERATION, SILENCED, SLOWED, STRENGTHENED,
	STUNNED, VAMPIRISM, WEAKENED;

	public StatusEffectKind getEffectKind() {
		switch(this) {
			case ACCELERATED: return StatusEffectKind.BUFF;
			case DAZED: return StatusEffectKind.DEBUFF;
			case DEBILITATED: return StatusEffectKind.DEBUFF;
			case ENERGIZED: return StatusEffectKind.BUFF;
			case FEARED: return StatusEffectKind.DEBUFF;
			case IMPRISONED: return StatusEffectKind.DEBUFF;
			case PRECISE: return StatusEffectKind.BUFF;
			case RESISTANT: return StatusEffectKind.BUFF;
			case REVERBERATING: return StatusEffectKind.BUFF;
			case VULNERABLE: return StatusEffectKind.DEBUFF;
			case ONGOING: return StatusEffectKind.DEBUFF;
			case PANICKED: return StatusEffectKind.DEBUFF;
			case REGENERATION: return StatusEffectKind.BUFF;
			case SILENCED: return StatusEffectKind.DEBUFF;
			case SLOWED: return StatusEffectKind.DEBUFF;
			case STRENGTHENED: return StatusEffectKind.BUFF;
			case STUNNED: return StatusEffectKind.DEBUFF;
			case VAMPIRISM: return StatusEffectKind.BUFF;
			case WEAKENED: return StatusEffectKind.DEBUFF;
			default:
				throw new RuntimeException("Invalid status effect kind, this cannot happen");
		}
	}

	public static StatusEffectType parse(String s)
		throws CorruptDataException
	{
		switch(s.toLowerCase()) {
			case "accelerated": return ACCELERATED;
			case "dazed": return DAZED;
			case "debilitated": return DEBILITATED;
			case "energized": return ENERGIZED;
			case "feared": return FEARED;
			case "imprisoned": return IMPRISONED;
			case "precise": return PRECISE;
			case "resistant": return RESISTANT;
			case "reverberating": return REVERBERATING;
			case "vulnerable": return VULNERABLE;
			case "ongoing": return ONGOING;
			case "panicked": return PANICKED;
			case "regeneration": return REGENERATION;
			case "silenced": return SILENCED;
			case "slowed": return SLOWED;
			case "strengthened": return STRENGTHENED;
			case "stunned": return STUNNED;
			case "vampirism": return VAMPIRISM;
			case "weakened": return WEAKENED;
			default:
				throw new CorruptDataException("Invalid status effect " + s);
		}
	}
}

