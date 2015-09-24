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

