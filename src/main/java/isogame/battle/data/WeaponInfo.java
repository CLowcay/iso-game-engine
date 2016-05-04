package isogame.battle.data;

import isogame.engine.CorruptDataException;
import org.json.simple.JSONObject;

public class WeaponInfo {
	public final String name;
	public final int range;
	public final String character;
	public final AbilityInfo attack;

	public WeaponInfo(
		String name, int range, String character, AbilityInfo attack
	) {
		this.name = name;
		this.range = range;
		this.character = character;
		this.attack = attack;
	}


	public static WeaponInfo fromJSON(JSONObject json)
		throws CorruptDataException
	{
		Object rname = json.get("name");
		Object rrange = json.get("range");
		Object rcharacter = json.get("character");
		Object rattack = json.get("attack");

		try {
			if (rname == null) throw new CorruptDataException("Missing weapon name");
			String name = (String) rname;
			if (rrange == null) throw new CorruptDataException("Missing weapon range in " + name);
			if (rcharacter == null) throw new CorruptDataException("Missing weapon character in " + name);
			if (rattack == null) throw new CorruptDataException("Missing weapon attack in " + name);

			Number range = (Number) rrange;
			String character = (String) rcharacter;
			AbilityInfo attack = AbilityInfo.fromJSON((JSONObject) rattack);

			return new WeaponInfo(name, range.intValue(), character, attack);
		} catch (ClassCastException e) {
			throw new CorruptDataException("Type error in weapon info", e);
		}

	}
}

