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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A map coordinate.  See coordinates.txt for more information.
 * */
public class MapPoint implements HasJSONRepresentation {
	public final int x;
	public final int y;

	public MapPoint(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	public static MapPoint fromJSON(final JSONObject json)
		throws CorruptDataException
	{
		try {
			final int x = json.getInt("x");
			final int y = json.getInt("y");

			return new MapPoint(x, y);
		} catch (JSONException e) {
			throw new CorruptDataException("Error in map point, " + e.getMessage(), e);
		}
	}

	public MapPoint add(final MapPoint p) {
		return new MapPoint(x + p.x, y + p.y);
	}

	/**
	 * Add a map point to this one, scaling the other map point first.
	 * @param p The point to add
	 * @param s The scale factor to apply to p
	 * */
	public MapPoint addScale(final MapPoint p, final int s) {
		return new MapPoint(x + s * p.x, y + s * p.y);
	}

	/**
	 * compute this - p.
	 * */
	public MapPoint subtract(final MapPoint p) {
		return new MapPoint(x - p.x, y - p.y);
	}

	/**
	 * Compute the manhattan distance
	 * */
	public int distance(final MapPoint p) {
		return Math.abs(p.x - x) + Math.abs(p.y - y);
	}

	/**
	 * Normalise a point so that x and y are in the range [-1, 1]
	 * */
	public MapPoint normalise() {
		return new MapPoint(
			x == 0? 0 : x / Math.abs(x),
			y == 0? 0 : y / Math.abs(y));
	}

	@Override
	public String toString() {
		return "MAP:(" + x + ", " + y + ")";
	}

	@Override
	public JSONObject getJSON() {
		final JSONObject r = new JSONObject();
		r.put("x", new Integer(x));
		r.put("y", new Integer(y));
		return r;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof MapPoint) {
			final MapPoint p = (MapPoint) obj;
			return p.x == x && p.y == y;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return x + y;
	}
}

