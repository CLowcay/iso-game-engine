package isogame.engine;

import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SpriteInfo implements HasJSONRepresentation {
	public final Map<String, SpriteAnimation> animations;
	private final List<SpriteAnimation> animationsOrdered;

	public SpriteAnimation defaultAnimation = null;
	public final String id;

	public SpriteInfo(String id) {
		this.id = id;
		animations = new HashMap<>();
		animationsOrdered = new ArrayList<>();
	}

	public SpriteAnimation getDefaultAnimation() throws CorruptDataException {
		if (defaultAnimation == null)
			throw new CorruptDataException("No animations defined for sprite " + id);
		else return defaultAnimation;
	}

	public void addAnimation(String id, SpriteAnimation animation) {
		if (defaultAnimation == null) defaultAnimation = animation;
		animations.put(id, animation);
		animationsOrdered.add(animation);
	}

	@Override
	@SuppressWarnings("unchecked")
	public JSONObject getJSON() {
		JSONArray a = new JSONArray();
		for (SpriteAnimation animation : animationsOrdered) {
			a.add(animation.getJSON());
		}

		JSONObject r = new JSONObject();
		r.put("id", id);
		r.put("animations", a);

		return r;
	}
}

