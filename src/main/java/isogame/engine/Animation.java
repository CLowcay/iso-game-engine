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

import javafx.scene.canvas.GraphicsContext;
import static isogame.GlobalConstants.TILEW;

public abstract class Animation {
	public abstract void start(final Sprite s);

	/**
	 * @return true if the animation is now complete.
	 * */
	public abstract boolean updateAnimation(final long t);

	/**
	 * Render a sprite taking into account this movement animation.
	 * @param isTargetSlice true if we are rendering onto the tile the sprite is
	 * moving into.  False if we are rendering onto the tile where the sprite is
	 * moving from.
	 * */
	public void renderSprite(
		final GraphicsContext gx,
		final CameraAngle angle,
		final Sprite s,
		final long t,
		final boolean isTargetSlice
	) {
		gx.save();
		s.renderFrame(gx, 0, (int) TILEW, t, angle);
		gx.restore();
	}
}

