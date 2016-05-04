package isogame.battle.data;

import isogame.engine.CorruptDataException;
import org.json.simple.JSONObject;

public class Stats {
	public final int ap;
	public final int mp;
	public final int power;
	public final int vitality;
	public final int attack;
	public final int defence;

	public Stats() {
		this.ap = 0;
		this.mp = 0;
		this.power = 0;
		this.vitality = 0;
		this.attack = 0;
		this.defence = 0;
	}

	public Stats(
		int ap, int mp,
		int power, int vitality,
		int attack, int defence
	) {
		this.ap = ap;
		this.mp = mp;
		this.power = power;
		this.vitality = vitality;
		this.attack = attack;
		this.defence = defence;
	}

	public Stats add(Stats stats) {
		return new Stats(
			ap + stats.ap,
			mp + stats.mp,
			power + stats.power,
			vitality + stats.vitality,
			attack + stats.attack,
			defence + stats.defence);
	}

	public static Stats fromJSON(JSONObject json)
		throws CorruptDataException
	{
		Object rap       = json.get("ap");
		Object rmp       = json.get("mp");
		Object rpower    = json.get("power");
		Object rvitality = json.get("vitality");
		Object rattack   = json.get("attack");
		Object rdefence  = json.get("defence");

		if (rap       == null) throw new CorruptDataException("Missing ap");
		if (rmp       == null) throw new CorruptDataException("Missing mp");
		if (rpower    == null) throw new CorruptDataException("Missing power");
		if (rvitality == null) throw new CorruptDataException("Missing vitality");
		if (rattack   == null) throw new CorruptDataException("Missing attack");
		if (rdefence  == null) throw new CorruptDataException("Missing defence");

		try {
			Number ap = (Number) rap;
			Number mp = (Number) rmp;
			Number power = (Number) rpower;
			Number vitality = (Number) rvitality;
			Number attack = (Number) rattack;
			Number defence = (Number) rdefence;

			return new Stats(
				ap.intValue(),
				mp.intValue(),
				power.intValue(),
				vitality.intValue(),
				attack.intValue(),
				defence.intValue());
		} catch (ClassCastException e) {
			throw new CorruptDataException("Type error in stats", e);
		}
	}
}

