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
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.Map;
import static isogame.engine.TilePrerenderer.OFFSETX;
import static isogame.engine.TilePrerenderer.OFFSETY;

/**
 * A class for rendering highlighted tiles.
 * */
public class Highlighter {
	public final Color color;

	private final Map<SlopeType, Image> tileTexture;
	private final Map<SlopeType, Image> cliffTexture;

	public Highlighter(final Color color) {
		this.color = color;

		tileTexture = TilePrerenderer.prerenderTile(color);
		cliffTexture = TilePrerenderer.prerenderCliff(s -> color);
	}

	public void renderTop(final GraphicsContext cx, final SlopeType slope) {
		cx.drawImage(tileTexture.get(slope), -OFFSETX, -OFFSETY);
	}

	public void renderCliff(final GraphicsContext cx, final SlopeType slope) {
		cx.drawImage(cliffTexture.get(slope), -OFFSETX, -OFFSETY);
	}

	public void renderElevation(final GraphicsContext cx) {
		cx.drawImage(cliffTexture.get(SlopeType.NONE), -OFFSETX, -OFFSETY);
	}
}

