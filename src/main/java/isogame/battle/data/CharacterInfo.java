package isogame.battle.data;

import isogame.engine.CorruptDataException;
import isogame.engine.HasJSONRepresentation;
import java.util.Collection;
import java.util.LinkedList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CharacterInfo implements HasJSONRepresentation {
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

	@Override
	@SuppressWarnings("unchecked")
	public JSONObject getJSON() {
		JSONObject r = new JSONObject();
		r.put("name", name);
		r.put("stats", stats.getJSON());
		JSONArray as = new JSONArray();
		for (AbilityInfo a : abilities) {
			as.add(a.getJSON());
		}
		r.put("abilities", as);
		return r;
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

