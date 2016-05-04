package isogame.battle.data;

import isogame.engine.CorruptDataException;
import java.util.Collection;
import java.util.LinkedList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CharacterInfo {
	public final String name;
	public final Stats stats;
	public final Collection<AbilityInfo> abilities;


	public CharacterInfo(
		String name, Stats stats, Collection<AbilityInfo> abilities
	) {
		this.name = name;
		this.stats = stats;
		this.abilities = abilities;
	}

	public static CharacterInfo fromJSON(JSONObject json)
		throws CorruptDataException
	{
		Object rname = json.get("name");
		Object rstats = json.get("stats");
		Object rabilities = json.get("abilities");

		try {
			if (rname == null) throw new CorruptDataException("Missing character name");
			String name = (String) rname;

			if (rstats == null)
				throw new CorruptDataException("Missing character stats");
			Stats stats = Stats.fromJSON((JSONObject) rstats);

			if (rabilities == null)
				throw new CorruptDataException("No abilities defined for character " + name);
			JSONArray abilities = (JSONArray) rabilities;

			Collection<AbilityInfo> allAbilities = new LinkedList<>();
			for (Object a : abilities) {
				allAbilities.add(AbilityInfo.fromJSON((JSONObject) a));
			}

			return new CharacterInfo(name, stats, allAbilities);
		} catch(ClassCastException e) {
			throw new CorruptDataException("Type error in character", e);
		}
	}

}

