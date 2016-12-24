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

import javafx.geometry.Point2D;

/**
 * Manages continuous (smooth) animations that can be started, stopped, and
 * changed from outside the animation loop.
 * */
public class ContinuousAnimator {
	private Point2D vector;
	private double speed;  // number of times per second we traverse the vector

	private Point2D origin;
	private long t0;
	private boolean running = false;
	private boolean stopping = false;
	private boolean t0IsNow = true;

	// deal with changes to an animation in progress
	private boolean animationChanging = false;
	private Point2D vector1;
	private double speed1;

	public ContinuousAnimator() {
		vector = new Point2D(0, 0);
		origin = new Point2D(0, 0);
		t0 = 0;
	}

	/**
	 * Change the speed and direction of the animation.
	 * @param vector The direction to move
	 * @param speed The number of times per second we traverse the entire vector
	 * */
	public void setAnimation(Point2D vector, double speed) {
		if (running && (!vector.equals(this.vector) || speed != this.speed)) {
			animationChanging = true;
			this.vector1 = vector;
			this.speed1 = speed;
		} else {
			this.vector = vector;
			this.speed = speed;
		}
	}

	/**
	 * Reset the initial position
	 * */
	public void reset(Point2D origin) {
		this.origin = origin;
	}

	public void start() {
		if (!running) t0IsNow = true;
		running = true;
	}

	public void stop() {
		if (running) {
			running = false;
			stopping = true;
		}
	}

	/**
	 * Compute the position at time t.
	 * @param t The time at which to compute the position.
	 * */
	public Point2D valueAt(long t) {
		if (t0IsNow) {
			t0 = t;
			t0IsNow = false;
		}

		Point2D r;

		if (running || stopping) {
			r = origin.add(vector.multiply(
				((double) (t - t0)) * (speed / 1000000000.0d)));
		} else {
			r = origin;
		}

		if (stopping) {
			origin = r;
			stopping = false;
		}

		if (animationChanging) {
			vector = vector1;
			speed = speed1;
			origin = r;
			t0 = t;
			animationChanging = false;
		}

		return r;
	}
}

