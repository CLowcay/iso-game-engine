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

/**
 * A texture for use on terrain.  This could be embellished later to make more
 * sophisticated terrain.
 * */
public class TerrainTexture implements HasJSONRepresentation {
	public final String id;
	private final String url;

	public final Paint samplePaint;

	private final Map<SlopeType, Image> evenPrerendered;
	private final Map<SlopeType, Image> oddPrerendered;

	public TerrainTexture(
		final ResourceLocator loc,
		final String id,
		final String url,
		final boolean nofx
	) throws CorruptDataException {
		this.id = id;
		this.url = url;

		if (nofx) {
			samplePaint = null;
			evenPrerendered = null;
			oddPrerendered = null;

		} else {
			try {
				final Image texture = new Image(loc.gfx(url));
				int w = (int) texture.getWidth();
				int h = (int) texture.getHeight();

				final Paint evenPaint = new ImagePattern(texture, 0, 0, 1, 1, true);
				final Paint oddPaint = new ImagePattern(texture, -0.5, -0.5, 1, 1, true);
				samplePaint = evenPaint;

				evenPrerendered = TilePrerenderer.prerenderTile(evenPaint);
				oddPrerendered = TilePrerenderer.prerenderTile(oddPaint);
			} catch (IOException e) {
				throw new CorruptDataException(
					"Cannot locate resource " + url, e);
			}
		}
	}

	/**
	 * Get an appropriate prerendered texture.
	 * */
	public Image getTexture(final boolean even, final SlopeType slope) {
		if (even) return evenPrerendered.get(slope);
		else return oddPrerendered.get(slope);
	}

	/**
	 * @param nofx True if we are running in an environment where we cannot use
	 * JavaFX
	 * */
	public static TerrainTexture fromJSON(
		final JSONObject json,
		final ResourceLocator loc,
		final boolean nofx
	) throws CorruptDataException
	{
		try {
			final String id = json.getString("id");
			final String url = json.getString("url");

			return new TerrainTexture(loc, id, url, nofx);
		} catch (JSONException e) {
			throw new CorruptDataException("Error parsing terrain texture, " + e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new CorruptDataException("Bad filename in texture", e);
		}
	}

	@Override
	public JSONObject getJSON() {
		final JSONObject r = new JSONObject();
		r.put("id", id);
		r.put("url", url);

		return r;
	}

}

