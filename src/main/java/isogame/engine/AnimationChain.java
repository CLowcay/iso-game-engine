package isogame.engine;

import isogame.GlobalConstants;
import javafx.scene.canvas.GraphicsContext;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class AnimationChain {
	private Runnable onFinished = () -> {};
	private Optional<Animation> activeAnimation = Optional.empty();
	private final Queue<Animation> queuedAnimations = new LinkedList<>();
	private final Sprite sprite;

	public AnimationChain(Sprite sprite) {
		this.sprite = sprite;
		sprite.setAnimationChain(this);
	}

	private boolean chainRunning = false;
	public void doOnFinished(Runnable onFinished) {
		this.onFinished = onFinished;
	}

	public Optional<Animation> getActiveAnimation() {
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
			activeAnimation.ifPresent(a -> {
				a.start(sprite);
				chainRunning = true;
			});
		}

		return activeAnimation.map(a -> {
			if (a.updateAnimation(t)) activeAnimation = Optional.empty();
			return false;
		}).orElseGet(() -> {
			if (chainRunning) {
				chainRunning = false;
				onFinished.run();
			}
			return true;
		});
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

	public void queueAnimation(Animation a) {
		queuedAnimations.add(a);
	}
}

