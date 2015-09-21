package isogame;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public class SpriteInfo {
	public final Map<String, SpriteAnimation> animations;
	public SpriteAnimation defaultAnimation = null;
	public final String id;

	public SpriteInfo(String id) {
		this.id = id;
		animations = new HashMap<>();
	}

	public SpriteAnimation getDefaultAnimation() throws CorruptDataException {
		if (defaultAnimation == null)
			throw new CorruptDataException("No animations defined for sprite " + id);
		else return defaultAnimation;
	}

	public void addAnimation(String id, SpriteAnimation animation) {
		if (defaultAnimation == null) defaultAnimation = animation;
		animations.put(id, animation);
	}
}

