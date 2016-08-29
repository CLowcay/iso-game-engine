package isogame.engine;

import javafx.scene.canvas.GraphicsContext;
import static isogame.GlobalConstants.TILEW;

public abstract class Animation {
	public abstract void start(Sprite s);

	/**
	 * @return true if the animation is now complete.
	 * */
	public abstract boolean updateAnimation(long t);

	/**
	 * Render a sprite taking into account this movement animation.
	 * @param isTargetSlice true if we are rendering onto the tile the sprite is
	 * moving into.  False if we are rendering onto the tile where the sprite is
	 * moving from.
	 * */
	public void renderSprite(
		GraphicsContext gx,
		CameraAngle angle,
		Sprite s,
		long t,
		boolean isTargetSlice
	) {
		gx.save();
		s.renderFrame(gx, 0, (int) TILEW, t, angle);
		gx.restore();
	}
}

