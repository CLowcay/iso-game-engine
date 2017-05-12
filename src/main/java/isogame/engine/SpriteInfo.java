/* © Callum Lowcay 2015, 2016

This file is part of iso-game-engine.

iso-game-engine is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

iso-game-engine is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with iso-game-engine.  If not, see <http://www.gnu.org/licenses/>.

*/
package isogame.engine;

import isogame.resource.ResourceLocator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SpriteInfo implements HasJSONRepresentation {
	public final Map<String, SpriteAnimation> animations;
	private final List<SpriteAnimation> animationsOrdered;

	public final SpriteAnimation defaultAnimation;
	public final String id;

	// sprites are rendered in order of priority starting with 0
	public final int priority;

	@Override
	public String toString() {
		return id;
	}

	public String debugString() {
		return "Sprite:" + id + ":" + priority;
	}

	public SpriteInfo(String id, int priority, SpriteAnimation defaultAnimation) {
		this.id = id;
		this.priority = priority;
		this.defaultAnimation = defaultAnimation;
		animations = new HashMap<>();
		animationsOrdered = new ArrayList<>();
		if (defaultAnimation != null) addAnimation(defaultAnimation);
	}

	public static SpriteInfo fromJSON(
		JSONObject json, ResourceLocator loc
	) throws CorruptDataException
	{
		try {
			final String id = json.getString("id");
			final JSONArray animations = json.getJSONArray("animations");
			final int priority = json.optInt("priority", 0);

			@SuppressWarnings("unchecked")
			final Iterator<Object> i = animations.iterator();
			// Hazard: Editor must make sure that every sprite has at least one sprite
			if (!i.hasNext()) throw new CorruptDataException("No animations defined for sprite");

			final SpriteInfo info = new SpriteInfo(id, priority,
				SpriteAnimation.fromJSON((JSONObject) i.next(), loc));

			while (i.hasNext()) {
				info.addAnimation(
					SpriteAnimation.fromJSON((JSONObject) i.next(), loc));
			}
			return info;
		} catch (ClassCastException e) {
			throw new CorruptDataException("Type error in sprite", e);
		} catch (JSONException e) {
			throw new CorruptDataException("Error parsing sprite info, " + e.getMessage(), e);
		}
	}

	public Collection<SpriteAnimation> getAllAnimations() {
		return animations.values();
	}

	public void addAnimation(SpriteAnimation animation) {
		animations.put(animation.id, animation);
		animationsOrdered.add(animation);
	}

	@Override
	public JSONObject getJSON() {
		final JSONArray a = new JSONArray();
		animationsOrdered.forEach(x -> a.put(x.getJSON()));

		JSONObject r = new JSONObject();
		r.put("id", id);
		r.put("priority", priority);
		r.put("animations", a);

		return r;
	}
}

