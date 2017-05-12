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
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import java.io.IOException;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class CliffTexture implements HasJSONRepresentation {
	// a paint for every direction the slope could be going.
	private final Paint ul;
	private final Paint ur;
	private final Paint flat;

	public final String id;
	private final String urlWide;
	private final String urlNarrow;

	private final Map<SlopeType, Image> prerendered;

	public CliffTexture(
		ResourceLocator loc,
		String id, String urlWide, String urlNarrow, boolean nofx
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
				Image imgWide = new Image(loc.gfx(urlWide));
				Image imgNarrow = new Image(loc.gfx(urlNarrow));

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

	public static CliffTexture fromJSON(
		JSONObject json,
		ResourceLocator loc, boolean nofx
	) throws CorruptDataException
	{
		try {
			String id = json.getString("id");
			String urlWide = json.getString("urlWide");
			String urlNarrow = json.getString("urlNarrow");

			return new CliffTexture(loc, id, urlWide, urlNarrow, nofx);
		} catch (IllegalArgumentException e) {
			throw new CorruptDataException("Bad filename in cliff texture", e);
		} catch (JSONException e) {
			throw new CorruptDataException("Error parsing cliff, " + e.getMessage(), e);
		}
	}

	@Override
	public JSONObject getJSON() {
		final JSONObject r = new JSONObject();
		r.put("id", id);
		r.put("urlWide", urlWide);
		r.put("urlNarrow", urlNarrow);

		return r;
	}

	public Paint getTexture(SlopeType s) {
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

	public Paint getFlatTexture() {
		return flat;
	}

	/**
	 * Get an appropriate prerendered texture.
	 * */
	public Image getPreTexture(SlopeType slope) {
		return prerendered.get(slope);
	}
}

