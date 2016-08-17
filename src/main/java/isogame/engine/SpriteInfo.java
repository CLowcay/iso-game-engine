package isogame.engine;

import isogame.resource.ResourceLocator;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SpriteInfo implements HasJSONRepresentation {
	public final Map<String, SpriteAnimation> animations;
	private final List<SpriteAnimation> animationsOrdered;

	public final SpriteAnimation defaultAnimation;
	public final String id;

	@Override
	public String toString() {
		return id;
	}

	public SpriteInfo(String id, SpriteAnimation defaultAnimation) {
		this.id = id;
		this.defaultAnimation = defaultAnimation;
		animations = new HashMap<>();
		animationsOrdered = new ArrayList<>();
		if (defaultAnimation != null) addAnimation(defaultAnimation);
	}

	public static SpriteInfo fromJSON(
		JSONObject json, ResourceLocator loc
	) throws CorruptDataException
	{
		Object rId = json.get("id");
		Object rAnimations = json.get("animations");

		if (rId == null) throw new CorruptDataException("Error in sprite, missing id");
		if (rAnimations == null) throw new CorruptDataException("Error in sprite, missing animations");

		try {
			JSONArray animations = (JSONArray) rAnimations;
			@SuppressWarnings("unchecked")
			Iterator<Object> i = animations.iterator();
			// Hazard: Editor must make sure that every sprite has at least one sprite
			if (!i.hasNext()) throw new CorruptDataException("No animations defined for sprite");
			SpriteInfo info = new SpriteInfo((String) rId,
				SpriteAnimation.fromJSON((JSONObject) i.next(), loc));
			while (i.hasNext()) {
				info.addAnimation(
					SpriteAnimation.fromJSON((JSONObject) i.next(), loc));
			}
			return info;
		} catch (ClassCastException e) {
			throw new CorruptDataException("Type error in sprite", e);
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
	@SuppressWarnings("unchecked")
	public JSONObject getJSON() {
		JSONArray a = new JSONArray();
		animationsOrdered.forEach(x -> a.add(x.getJSON()));

		JSONObject r = new JSONObject();
		r.put("id", id);
		r.put("animations", a);

		return r;
	}
}

