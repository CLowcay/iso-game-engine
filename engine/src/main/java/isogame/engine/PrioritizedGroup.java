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

import javafx.scene.Group;

/**
 * A nasty ugly hack that allows us to navigate the scene graph to insert
 * sprites in the correct z-order based on their priorities
 * */
public class PrioritizedGroup extends Group {
	public final int priority;

	public static final int TILE = -1;

	/**
	 * @param priority the priority level of this group
	 * */
	public PrioritizedGroup(final int priority) {
		this.priority = priority;
	}
}

