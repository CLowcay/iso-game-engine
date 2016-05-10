package isogame.engine;

import isogame.resource.ResourceLocator;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SpriteInfo implements HasJSONRepresentation {
	public final Map<String, SpriteAnimation> animations;
	private final List<SpriteAnimation> animationsOrdered;

	private SpriteAnimation defaultAnimation = null;
	public final String id;

	public SpriteInfo(String id) {
		this.id = id;
		animations = new HashMap<>();
		animationsOrdered = new ArrayList<>();
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
			SpriteInfo info = new SpriteInfo((String) rId);
			JSONArray animations = (JSONArray) rAnimations;
			for (Object a : animations) {
				info.addAnimation(SpriteAnimation.fromJSON(
					(JSONObject) a, loc));
			}
			return info;
		} catch (ClassCastException e) {
			throw new CorruptDataException("Type error in sprite", e);
		}
	}

	public SpriteAnimation getDefaultAnimation() throws CorruptDataException {
		if (defaultAnimation == null)
			throw new CorruptDataException("No animations defined for sprite " + id);
		else return defaultAnimation;
	}

	public Collection<SpriteAnimation> getAllAnimations() {
		return animations.values();
	}

	public void addAnimation(SpriteAnimation animation) {
		if (defaultAnimation == null) defaultAnimation = animation;
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

