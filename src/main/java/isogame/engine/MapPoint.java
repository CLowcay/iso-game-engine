package isogame.engine;

import org.json.simple.JSONObject;

/**
 * A map coordinate.  See coordinates.txt for more information.
 * */
public class MapPoint implements HasJSONRepresentation {
	public final int x;
	public final int y;

	public MapPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "MAP:(" + x + ", " + y + ")";
	}

	@Override
	@SuppressWarnings("unchecked")
	public JSONObject getJSON() {
		JSONObject r = new JSONObject();
		r.put("x", new Integer(x));
		r.put("y", new Integer(y));
		return r;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MapPoint) {
			MapPoint p = (MapPoint) obj;
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

