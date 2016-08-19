package isogame.engine;

import isogame.GlobalConstants;
import javafx.scene.canvas.GraphicsContext;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class AnimationChain {
	private Optional<MoveSpriteAnimation> activeAnimation = Optional.empty();
	private final Queue<MoveSpriteAnimation> queuedAnimations = new LinkedList<>();
	private final Sprite sprite;

	public AnimationChain(Sprite sprite) {
		this.sprite = sprite;
		sprite.setAnimationChain(this);
	}

	public Optional<MoveSpriteAnimation> getActiveAnimation() {
		return activeAnimation;
	}

	/**
	 * Shut down the animation chain now.
	 * */
	public void terminateChain() {
		sprite.setAnimationChain(null);
	}

	/**
	 * @return true if the entire animation chain is finished
	 * */
	public boolean updateAnimation(long t) {
		if (!activeAnimation.isPresent()) {
			activeAnimation = Optional.ofNullable(queuedAnimations.poll());
			activeAnimation.ifPresent(a -> a.start());
		}

		return activeAnimation.map(a -> {
			if (a.updateAnimation(t)) activeAnimation = Optional.empty();
			return false;
		}).orElse(true);
	}

	/**
	 * Render a sprite taking into account any animation effects.
	 * */
	public void renderSprite(
		GraphicsContext gx,
		CameraAngle angle,
		Sprite s,
		long t,
		boolean isTargetSlice
	) {
		if (activeAnimation.isPresent()) {
			activeAnimation.get().renderSprite(gx, angle, s, t, isTargetSlice);
		} else {
			gx.save();
			s.renderFrame(gx, 0, (int) GlobalConstants.TILEW, t, angle);
			gx.restore();
		}
	}

	public void queueAnimation(MoveSpriteAnimation a) {
		queuedAnimations.add(a);
	}
}

