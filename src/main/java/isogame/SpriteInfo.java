package isogame;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public class SpriteInfo {
	public final Map<String, SpriteAnimation> animations;

	public SpriteInfo() {
		animations = new HashMap<String, SpriteAnimation>();
	}
}

