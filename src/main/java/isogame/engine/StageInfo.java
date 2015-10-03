package isogame.engine;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class StageInfo implements HasJSONRepresentation {
	public final int w;
	public final int h;
	private final Tile[] data;

	public StageInfo(int w, int h, Tile[] data) throws CorruptDataException {
		this.w = w;
		this.h = h;
		this.data = data;
		if (data.length != w * h)
			throw new CorruptDataException("Incorrect number of tiles in stage");
	}

	public static StageInfo fromJSON(JSONObject json, Library lib)
		throws CorruptDataException
	{
		Object rW = json.get("w");
		Object rH =  json.get("h");
		Object rData = json.get("data");

		if (rW == null) throw new CorruptDataException("Error in stage, missing w");
		if (rH == null) throw new CorruptDataException("Error in stage, missing h");
		if (rData == null) throw new CorruptDataException("Error in stage, missing data");

		try {
			JSONArray jsonData = (JSONArray) rData;
			int w = ((Number) rW).intValue();
			int h = ((Number) rH).intValue();
			Tile[] data = new Tile[w * h];
			int i = 0;
			for (Object t : jsonData) {
				data[i] = Tile.fromJSON((JSONObject) t, lib);
				i += 1;
			}

			return new StageInfo(w, h, data);
		} catch (ClassCastException e) {
			throw new CorruptDataException("Type error in stage info", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public JSONObject getJSON() {
		JSONArray a = new JSONArray();
		int ndata = w * h;
		for (int i = 0; i < ndata; i++) {
			a.add(data[i].getJSON());
		}

		JSONObject r = new JSONObject();
		r.put("w", new Integer(w));
		r.put("h", new Integer(h));
		r.put("data", a);
		return r;
	}

	public Tile getTile(MapPoint pos) throws IndexOutOfBoundsException {
		if (pos.x < 0 || pos.y < 0 || pos.x >= w || pos.y >= h)
			throw new IndexOutOfBoundsException();
		return data[(pos.y * w) + pos.x];
	}

	public void setTile(Tile tile)
		throws IndexOutOfBoundsException
	{
		if (tile.pos.x < 0 || tile.pos.y < 0 || tile.pos.x >= w || tile.pos.y >= h)
			throw new IndexOutOfBoundsException();
		data[(tile.pos.y * w) + tile.pos.x] = tile;
	}

	public boolean hasTile(MapPoint pos) {
		return pos.x >= 0 && pos.y >= 0 && pos.x < w && pos.y < h;
	}

	/**
	 * Iterate over the tiles in this sort of order:
	 * 0 2 5
	 * 1 4 7
	 * 3 6 8
	 *
	 * The starting corner is determined by the camera angle.
	 *
	 * Iterating over the tiles in this order guarantees that we draw them from
	 * the back to the front, so objects closer to the camera properly obscure
	 * objects that are further away.
	 * */
	public Iterator<Tile> iterateTiles(CameraAngle a) {
		// coordinates of the next tile to return
		final int x0;
		final int y0;

		// coordinates of the last tile to return
		final int x1;
		final int y1;

		switch (a) {
			case UL: x0 =     0; y0 =     0; x1 = w - 1; y1 = h - 1; break;
			case LL: x0 =     0; y0 = h - 1; x1 = w - 1; y1 =     0; break;
			case LR: x0 = w - 1; y0 = h - 1; x1 =     0; y1 =     0; break;
			case UR: x0 = w - 1; y0 =     0; x1 =     0; y1 = h - 1; break;
			default: throw new RuntimeException("Invalid camera angle, this cannot happen");
		}

		// vector to move across rows of tiles
		final int dx;
		final int dy;

		// vector to move down rows of tiles
		final int vx1;
		final int vy1;

		// auxiliary vector to move down rows of tiles
		final int vx2;
		final int vy2;

		switch (a) {
			case UL: dx =  1; dy = -1; vx1 =  0; vy1 =  1; vx2 =  1; vy2 =  0; break;
			case LL: dx = -1; dy = -1; vx1 =  1; vy1 =  0; vx2 =  0; vy2 = -1; break;
			case LR: dx = -1; dy =  1; vx1 =  0; vy1 = -1; vx2 = -1; vy2 =  0; break;
			case UR: dx =  1; dy =  1; vx1 = -1; vy1 =  0; vx2 =  0; vy2 =  1; break;
			default: throw new RuntimeException("Invalid camera angle, this cannot happen");
		}

		return new Iterator<Tile>() {
			// current coordinates
			private int x = x0;
			private int y = y0;

			// coordinates of the first tile on the current row
			private int rx = x0;
			private int ry = y0;

			private boolean done = false;
			private boolean useAuxVector = false;

			@Override
			public boolean hasNext() {
				return !done;
			}

			@Override
			public Tile next() {
				if (done) {
					throw new NoSuchElementException();
				} else {
					Tile r = data[(y * w) + x];
					x += dx;
					y += dy;
					if (x < 0 || x >= w || y < 0 || y >= h) {
						if (useAuxVector) {
							rx += vx2;
							ry += vy2;
							if (rx < 0 || rx >= w || ry < 0 || ry >= h) done = true;
						} else {
							rx += vx1;
							ry += vy1;
							if (rx < 0 || rx >= w || ry < 0 || ry >= h) {
								useAuxVector = true;
								rx += vx2 - vx1;
								ry += vy2 - vy1;
							}
						}
						x = rx;
						y = ry;
					}
					return r;
				}
			}
		};
	}

	/**
	 * Iterate over the tiles in the correct order to do mouse collision
	 * detection when the mouse is at point p with elevation 0.
	 * */
	public Iterator<Tile> iterateCollisionDetection(MapPoint p, CameraAngle a) {
		/* WARNING: This method is complicated and subtle, but absolutely essential
		 * for reliable collision detection.  Think very carefully before modifying
		 * this code.
		 * */

		// vector to move down columns of tiles
		final int dx;
		final int dy;
		final int tx;
		final int ty;

		switch (a) {
			case UL: dx = +1; dy = +1; tx = w - 1; ty = h - 1; break;
			case LL: dx = +1; dy = -1; tx = w - 1; ty =     0; break;
			case LR: dx = -1; dy = -1; tx =     0; ty =     0; break;
			case UR: dx = -1; dy = +1; tx =     0; ty = h - 1; break;
			default: throw new RuntimeException("Invalid camera angle, this cannot happen");
		}

		int ny = (ty - p.y) / dy;
		int nx = (tx - p.x) / dx;

		int startx;
		int starty;

		// now we basically have to determine which edge of the board intersects a
		// line drawn along the vector (dx, dy).
		int xcheck = p.x + (ny * dx);
		int ycheck = p.y + (nx * dy);
		if (xcheck >= 0 && xcheck < w) {
			// it intercepts the y axis.
			startx = xcheck;
			starty = p.y + (ny * dy);
		} else if (ycheck >= 0 && ycheck < h) {
			// it intercepts the x axis.
			startx = p.x + (nx * dx);
			starty = ycheck;
		} else {
			// it does not intercept, but we might be looking at one of the two
			// corner diamonds on either side of the map.  Shift the start point from
			// side to side and see if we can find an intercept starting at the
			// shifted point.
			int x = p.x - dx;
			int y = p.y;
			ny = (ty - y) / dy;
			nx = (tx - x) / dx;

			xcheck = x + (ny * dx);
			ycheck = y + (nx * dy);
			if (xcheck >= 0 && xcheck < w) {
				startx = xcheck;
				starty = y + (ny * dy);
			} else if (ycheck >= 0 && ycheck < h) {
				startx = x + (nx * dx);
				starty = ycheck;
			} else {
				x = p.x;
				y = p.y - dy;
				ny = (ty - y) / dy;
				nx = (tx - x) / dx;

				xcheck = x + (ny * dx);
				ycheck = y + (nx * dy);
				if (xcheck >= 0 && xcheck < w) {
					startx = xcheck;
					starty = y + (ny * dy);
				} else if (ycheck >= 0 && ycheck < h) {
					startx = x + (nx * dx);
					starty = ycheck;
				} else {
					// no, there really is no intercept, so just set the starting point
					// to something arbitrary.  The iterator will not that this is not a
					// valid point and no tiles will be iterated over in this case.
					startx = p.x;
					starty = p.y;
				}
			}
		}

		return new Iterator<Tile>() {
			int x = startx;
			int y = starty;

			int xa = startx + dx;
			int ya = starty;

			int xb = startx;
			int yb = starty + dy;

			int untilMove = 2;

			@Override
			public boolean hasNext() {
				return
					(x  >= 0 &&  x < w &&  y >= 0 &&  y < h) ||
					(xa >= 0 && xa < w && ya >= 0 && ya < h) ||
					(xb >= 0 && xb < w && yb >= 0 && yb < h);
			}

			public Tile next() {
				if (!hasNext()) throw new NoSuchElementException();

				int rx;
				int ry;

				switch (untilMove) {
					case 2:
						untilMove = 1;
						rx = xa; ry = ya;
						xa -= dx; ya -= dy;
						if (rx >= 0 && rx < w && ry >= 0 && ry < h) break;
					case 1:
						untilMove = 0;
						rx = xb; ry = yb;
						xb -= dx; yb -= dy;
						if (rx >= 0 && rx < w && ry >= 0 && ry < h) break;
					case 0:
						untilMove = 2;
						rx = x; ry = y;
						x  -= dx; y  -= dy;
						break;
					default: throw new RuntimeException(
						"Invalid move counter.  This cannot happen");
				}

				return data[(ry * w) + rx];
			}
		};
	}
}

