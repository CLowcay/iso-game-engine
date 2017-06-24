/* © Callum Lowcay 2017

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

import java.util.Optional;

/**
 * Information about at Mapview selection.
 * */
public class SelectionInfo {
	public final Optional<MapPoint> mouseTile;
	public final Optional<MapPoint> spriteTile;

	public SelectionInfo(
		final Optional<MapPoint> mouseTile,
		final Optional<MapPoint> spriteTile
	) {
		this.mouseTile = mouseTile;
		this.spriteTile = spriteTile;
	}

	public Optional<MapPoint> pointPriority() {
		if (mouseTile.isPresent()) return mouseTile; else return spriteTile;
	}

	public Optional<MapPoint> spritePriority() {
		if (mouseTile.isPresent()) return spriteTile; else return mouseTile;
	}
}

