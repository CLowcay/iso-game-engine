package isogame;

import javafx.geometry.BoundingBox;
import javafx.scene.canvas.GraphicsContext;
import java.util.HashMap;
import java.util.Map;

public class Stage {
	public final StageInfo info;
	public final Map<MapPoint, Sprite> sprites;

	public Stage(StageInfo info) {
		this.info = info;
		sprites = new HashMap();
	}

	public void addSprite(Sprite sprite) {
		sprites.put(sprite.pos, sprite);
	}

	/**
	 * Render the entire stage (skipping the invisible bits for efficiency).
	 * */
	public void render(GraphicsContext cx, BoundingBox visible) {
	}
}

