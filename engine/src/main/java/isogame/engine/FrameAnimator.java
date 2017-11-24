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

public class FrameAnimator {
	int frame0 = 0;
	final long nframes;
	final long framePeriod;
	long t0 = 0;

	/**
	 * @param nframes Number of frames in the animation
	 * @param fps Frames per second
	 * */
	public FrameAnimator(final int nframes, final int fps) {
		this.nframes = nframes;
		framePeriod = 1000000000l / (long) fps;
	}

	/**
	 * Reset the initial frame
	 * */
	public void reset(final int frame0, final long now) {
		this.frame0 = frame0;
		this.t0 = now;
	}

	/**
	 * Compute the frame to use at time t
	 * @param t The time at which to get the current frame
	 * */
	public int frameAt(final long t) {
		return (int) ((frame0 + ((t - t0) / framePeriod)) % nframes);
	}
}

