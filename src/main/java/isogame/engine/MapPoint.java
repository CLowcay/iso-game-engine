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

	public static MapPoint fromJSON(JSONObject json)
		throws CorruptDataException
	{
		Object rX = json.get("x");
		Object rY = json.get("y");

		if (rX == null) throw new CorruptDataException("Error in point, missing x");
		if (rY == null) throw new CorruptDataException("Error in point, missing y");

		try {
			return new MapPoint(
				((Number) rX).intValue(),
				((Number) rY).intValue());
		} catch (ClassCastException e) {
			throw new CorruptDataException("Type error in point", e);
		}
	}

	public MapPoint add(MapPoint p) {
		return new MapPoint(x + p.x, y + p.y);
	}

	/**
	 * Add a map point to this one, scaling the other map point first.
	 * @param p The point to add
	 * @param s The scale factor to apply to p
	 * */
	public MapPoint addScale(MapPoint p, int s) {
		return new MapPoint(x + s * p.x, y + s * p.y);
	}

	/**
	 * compute this - p.
	 * */
	public MapPoint subtract(MapPoint p) {
		return new MapPoint(x - p.x, y - p.y);
	}

	/**
	 * Compute the manhattan distance
	 * */
	public int distance(MapPoint p) {
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

