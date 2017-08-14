/* © Callum Lowcay 2015, 2016

This file is part of iso-game-engine.

iso-game-engine is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

iso-game-engine is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with iso-game-engine.  If not, see <http://www.gnu.org/licenses/>.

*/
package isogame.engine;

import isogame.GlobalConstants;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import javafx.scene.canvas.GraphicsContext;

public class AnimationChain {
	private Runnable onFinished = () -> {};
	private Optional<Animation> activeAnimation = Optional.empty();
	private final Queue<Animation> queuedAnimations = new LinkedList<>();
	private final Sprite sprite;

	public AnimationChain(final Sprite sprite) {
		this.sprite = sprite;
		sprite.setAnimationChain(Optional.of(this));
	}

	private boolean chainRunning = false;
	public void doOnFinished(final Runnable onFinished) {
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
	 * Update the animation
	 * TODO: extend this so that it also updates the scene graph
	 * @return true if the entire animation chain is finished
	 * */
	public boolean updateAnimation(final long t) {
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
		final GraphicsContext gx,
		final CameraAngle angle,
		final Sprite s,
		final long t,
		final boolean isTargetSlice
	) {
		if (activeAnimation.isPresent()) {
			activeAnimation.get().renderSprite(gx, angle, s, t, isTargetSlice);
		} else {
			gx.save();
			s.renderFrame(gx, 0, (int) GlobalConstants.TILEW, t, angle);
			gx.restore();
		}
	}

	public void queueAnimation(final Animation a) {
		queuedAnimations.add(a);
	}
}

