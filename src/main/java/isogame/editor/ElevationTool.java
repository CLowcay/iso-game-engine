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
package isogame.editor;

import isogame.engine.CliffTexture;
import isogame.engine.MapPoint;
import isogame.engine.SlopeType;
import isogame.engine.Stage;
import isogame.engine.Tile;
import isogame.engine.View;

public class ElevationTool extends Tool {
	private final CliffTexture texture;
	private final int de;
	private final SlopeType slope;

	public ElevationTool(CliffTexture texture, int de, SlopeType slope) {
		this.texture = texture;
		this.de = de;
		this.slope = slope;
	}

	@Override
	public void apply(MapPoint p, Stage stage, View view) {
		if (stage.terrain.hasTile(p)) {
			Tile t = stage.terrain.getTile(p);
			if (de < 0 && t.slope != SlopeType.NONE) {
				stage.terrain.setTile(t.newElevation(
					t.elevation, SlopeType.NONE, texture));
			} else {
				if (t.elevation + de >= 0) {
					stage.terrain.setTile(t.newElevation(
						t.elevation + de, slope, texture));
				}
			}
		}
	}
}

