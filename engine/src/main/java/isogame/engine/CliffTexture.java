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

import isogame.resource.ResourceLocator;

import java.io.IOException;
import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;

import org.json.JSONException;
import org.json.JSONObject;
import ssjsjs.annotations.Field;
import ssjsjs.annotations.Implicit;
import ssjsjs.annotations.JSONConstructor;
import ssjsjs.JSONable;

/**
 * A texture for drawing cliffs.
 * */
public class CliffTexture implements JSONable {
	// a paint for every direction the slope could be going.
	private final Paint ul;
	private final Paint ur;
	private final Paint flat;

	public final String id;
	private final String urlWide;
	private final String urlNarrow;

	private final Map<SlopeType, Image> prerendered;

	@JSONConstructor
	public CliffTexture(
		@Implicit("locator") final ResourceLocator loc,
		@Field("id") final String id,
		@Field("urlWide") final String urlWide,
		@Field("urlNarrow") final String urlNarrow,
		@Implicit("nofx") final boolean nofx
	) throws CorruptDataException {
		this.id = id;
		this.urlWide = urlWide;
		this.urlNarrow = urlNarrow;

		if (nofx) {
			prerendered = null;
			ul = null;
			ur = null;
			flat = null;
		} else {
			try {
				final Image imgWide = new Image(loc.gfx(urlWide));
				final Image imgNarrow = new Image(loc.gfx(urlNarrow));

				ul = new ImagePattern(imgNarrow, -1, 0, 2, 1, true);
				ur = new ImagePattern(imgNarrow,  0, 0, 2, 1, true);
				flat = new ImagePattern(imgWide,  0, 0, 1, 1, true);

				prerendered = TilePrerenderer.prerenderCliff(this::getTexture);
			} catch (IOException e) {
				throw new CorruptDataException(
					"Cannot locate resource " + urlWide + " or " + urlNarrow, e);
			}
		}
	}

	/**
	 * Get the texture for a slope tile.
	 * @param s the slope type
	 * @return the texture as a tiling Paint
	 * */
	public Paint getTexture(final SlopeType s) {
		switch (s) {
			case N: return ur;
			case S: return flat;
			case E: return flat;
			case W: return ul;
			case NONE: return flat;
			default:
				throw new RuntimeException("Invalid slope type, this cannot happen");
		}
	}

	/**
	 * Get the texture for a non-slope tile.
	 * @return the texture as a tiling Paint
	 * */
	public Paint getFlatTexture() {
		return flat;
	}

	/**
	 * Get an appropriate prerendered texture.
	 * @param slope the slope type
	 * @return the prerendered texture as an image
	 * */
	public Image getPreTexture(final SlopeType slope) {
		return prerendered.get(slope);
	}
}

