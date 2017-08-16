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

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import javafx.collections.ObservableList;
import javafx.scene.Node;

public class AnimationChain {
	private Runnable onFinished = () -> {};
	private Optional<Animation> activeAnimation = Optional.empty();
	private final Queue<Animation> queuedAnimations = new LinkedList<>();
	private final Sprite sprite;

	AnimationChain(final Sprite sprite) {
		this.sprite = sprite;
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
		chainRunning = false;
		onFinished.run();
	}

	/**
	 * Update the animation
	 * @return true if the entire animation chain is finished
	 * */
	private boolean updateAnimation(
		final StageInfo terrain, final long t
	) {
		if (!activeAnimation.isPresent()) {
			activeAnimation = Optional.ofNullable(queuedAnimations.poll());
			activeAnimation.ifPresent(a -> {
				a.start();
				chainRunning = true;
			});
		}

		return activeAnimation.map(a -> {
			if (a.updateAnimation(terrain, t)) activeAnimation = Optional.empty();
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
	 * Update the scene graph taking into account all animations
	 * */
	public void updateSceneGraph(
		final ObservableList<Node> graph,
		final StageInfo terrain,
		final CameraAngle angle,
		final long t
	) {
		if (updateAnimation(terrain, t)) {
			sprite.updateSceneGraph(graph, terrain, angle, t);
		} else {
			activeAnimation.ifPresent(a ->
				a.updateSceneGraph(graph, terrain, angle, t));
		}
	}

	public void queueAnimation(final Animation a) {
		queuedAnimations.add(a);
	}
}

