package isogame.engine;

import javafx.scene.canvas.GraphicsContext;
import java.util.function.BiConsumer;

/**
 * An animation to teleport a sprite instantly from one square to another.
 * */
public class TeleportAnimation extends Animation {
	private final MapPoint start;
	private final MapPoint target;
	private final BiConsumer<MapPoint, MapPoint> crossBoundary;

	public TeleportAnimation(
		MapPoint start, MapPoint target,
		BiConsumer<MapPoint, MapPoint> crossBoundary
	) {
		this.start = start;
		this.target = target;
		this.crossBoundary = crossBoundary;
	}


	@Override public void start(Sprite s) {
		return;
	}

	/**
	 * @return true if the animation is now complete.
	 * */
	@Override public boolean updateAnimation(long t) {
		crossBoundary.accept(start, target);
		return true;
	}
}

