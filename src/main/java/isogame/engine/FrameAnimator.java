package isogame.engine;

public class FrameAnimator {
	int frame0 = 0;
	final long nframes;
	final long framePeriod;
	long t0 = 0;

	/**
	 * @param nframes Number of frames in the animation
	 * @param fps Frames per second
	 * */
	public FrameAnimator(int nframes, int fps) {
		this.nframes = nframes;
		framePeriod = 1000000000l / (long) fps;
	}

	/**
	 * Reset the initial frame
	 * */
	public void reset(int frame0, long now) {
		this.frame0 = frame0;
		this.t0 = now;
	}

	/**
	 * Compute the frame to use at time t
	 * @param t The time at which to get the current frame
	 * */
	public int frameAt(long t) {
		return (int) ((frame0 + ((t - t0) / framePeriod)) % nframes);
	}
}

