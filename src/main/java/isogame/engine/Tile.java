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
import org.json.JSONException;
import org.json.JSONObject;
import static isogame.engine.TilePrerenderer.OFFSETX;
import static isogame.engine.TilePrerenderer.OFFSETY;
import static isogame.GlobalConstants.TILEH;

/**
 * Represents a single tile in a stage.
 * */
public class Tile implements HasJSONRepresentation {
	public final int elevation;
	public final TerrainTexture tex;
	public final CliffTexture cliffTexture;
	public final SlopeType slope;
	public final boolean isManaZone;
	public final StartZoneType startZone;
	public final MapPoint pos;

	private final boolean even;

	public Tile(final MapPoint p, final TerrainTexture texture) {
		this(p, 0, SlopeType.NONE, false, StartZoneType.NONE, texture, null);
	}

	public Tile(
		final int elevation,
		final SlopeType slope,
		final TerrainTexture texture,
		final CliffTexture cliffTexture
	) {
		this(new MapPoint(0, 0), elevation, slope,
			false, StartZoneType.NONE, texture, cliffTexture);
	}

	public Tile(
		final MapPoint pos,
		final int elevation,
		final SlopeType slope,
		final boolean isManaZone,
		final StartZoneType startZone,
		final TerrainTexture texture,
		final CliffTexture cliffTexture
	) {
		this.elevation = elevation;
		this.pos = pos;

		tex = texture;
		even = (pos.x + pos.y) % 2 == 0;

		this.cliffTexture = cliffTexture;

		this.slope = slope;
		this.isManaZone = isManaZone;
		this.startZone = startZone;
	}

	public static Tile fromJSON(final JSONObject json, final Library lib)
		throws CorruptDataException
	{
		try {
			final JSONObject p = json.getJSONObject("p");
			final int elevation = json.getInt("elevation");
			final String slope = json.getString("slope");
			final boolean isManaZone = json.getBoolean("isManaZone");
			final String startZone = json.getString("startZone");
			final String texture = json.getString("texture");
			final String cliffTexture = json.optString("cliffTexture", null);

			return new Tile(
				MapPoint.fromJSON(p), elevation,
				SlopeType.valueOf(slope), isManaZone,
				StartZoneType.valueOf(startZone),
				lib.getTerrain(texture),
				cliffTexture == null? null : lib.getCliffTexture(cliffTexture));
		} catch (JSONException e) {
			throw new CorruptDataException("Error parsing tile, " + e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new CorruptDataException("Type error in tile", e);
		}
	}

	@Override
	public JSONObject getJSON() {
		final JSONObject r = new JSONObject();
		r.put("p", pos.getJSON());
		r.put("elevation", new Integer(elevation));
		r.put("slope", slope.name());
		r.put("isManaZone", new Boolean(isManaZone));
		r.put("startZone", startZone.name());
		r.put("texture", tex.id);
		if (cliffTexture != null) r.put("cliffTexture", cliffTexture.id);
		return r;
	}

	/**
	 * Get a string describing the special properties of this tile
	 * @return May be null
	 * */
	public String specialStatusString() {
		String r;
		switch (startZone) {
			case PLAYER: r = "1"; break;
			case AI: r = "2"; break;
			default: r = null;
		}

		if (isManaZone) {
			if (r == null) r = "M"; else r += "M"; 
		}

		return r;
	}

	/**
	 * Make a new tile with a different texture
	 * */
	public Tile newTexture(final TerrainTexture tex) {
		return new Tile(pos, elevation, slope, isManaZone, startZone, tex, cliffTexture);
	}

	/**
	 * Make a new tile with different elevation characteristics
	 * */
	public Tile newElevation(
		final int elevation,
		final SlopeType slope,
		final CliffTexture cliffTexture
	) {
		return new Tile(pos, elevation, slope, isManaZone, startZone, tex, cliffTexture);
	}

	/**
	 * Make a new tile with a different mana zone property
	 * */
	public Tile newManaZone(final boolean isManaZone) {
		return new Tile(pos, elevation, slope, isManaZone, startZone, tex, cliffTexture);
	}

	/**
	 * Make a new tile with a different start zone type
	 * */
	public Tile newStartZone(final StartZoneType startZone) {
		return new Tile(pos, elevation, slope, isManaZone, startZone, tex, cliffTexture);
	}

	public Tile clearSpecialProperties() {
		return new Tile(pos, elevation, slope, false, StartZoneType.NONE, tex, cliffTexture);
	}

	public SlopeType adjustSlopeForCameraAngle(final CameraAngle angle) {
		int s;
		int d;

		switch (slope) {
			case N: s = 0; break;
			case E: s = 1; break;
			case S: s = 2; break;
			case W: s = 3; break;
			case NONE: return SlopeType.NONE;
			default: throw new RuntimeException(
				"Invalid slope type, this cannot happen");
		}

		switch (angle) {
			case UL: d = 0; break;
			case LL: d = 1; break;
			case LR: d = 2; break;
			case UR: d = 3; break;
			default: throw new RuntimeException(
				"Invalid camera angle, this cannot happen");
		}

		switch ((s + d) % 4) {
			case 0: return SlopeType.N;
			case 1: return SlopeType.E;
			case 2: return SlopeType.S;
			case 3: return SlopeType.W;
			default: throw new RuntimeException(
				"Computed invalid slope type, this cannot happen");
		}
	}

	private final double[] xs = new double[6];
	private final double[] ys = new double[6];

	/**
	 * Render this tile at (0,0).  If you need to draw the tile somewhere else,
	 * do a translation before calling this method.
	 * */
	public void render(
		final GraphicsContext cx,
		final Highlighter highlighter,
		final CameraAngle angle
	) {
		final SlopeType slope = adjustSlopeForCameraAngle(angle);

		cx.drawImage(tex.getTexture(even, slope), -OFFSETX, -OFFSETY);
		if (highlighter != null) highlighter.renderTop(cx, slope);
		if (slope != SlopeType.NONE) {
			cx.drawImage(cliffTexture.getPreTexture(slope), -OFFSETX, -OFFSETY);
			if (highlighter != null) highlighter.renderCliff(cx, slope);
		}

		if (elevation != 0) {
			final Image epaint = cliffTexture.getPreTexture(SlopeType.NONE);
			for (int i = 0; i < elevation; i++) {
				cx.translate(0, TILEH / 2);
				cx.drawImage(epaint, -OFFSETX, -OFFSETY);
				if (highlighter != null) highlighter.renderElevation(cx);
			}
		}
	}

	@Override
	public String toString() {
		return pos.toString();
	}
}

