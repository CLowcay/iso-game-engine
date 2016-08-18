package isogame.engine;

import javafx.scene.canvas.GraphicsContext;

@FunctionalInterface
public interface SpriteDecalRenderer {
	public void render(
		GraphicsContext cx, Sprite s, long t, CameraAngle angle);
}

