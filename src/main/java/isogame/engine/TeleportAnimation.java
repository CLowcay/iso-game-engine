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

import java.util.function.BiConsumer;


/**
 * An animation to teleport a sprite instantly from one square to another.
 * */
public class TeleportAnimation extends Animation {
	private final MapPoint target;

	public TeleportAnimation(final Sprite sprite, final MapPoint target) {
		super(sprite);
		this.target = target;
	}

	/**
	 * @return true if the animation is now complete.
	 * */
	@Override public boolean updateAnimation(
		final StageInfo terrain, final long t
	) {
		sprite.setPos(target);
		return true;
	}
}

