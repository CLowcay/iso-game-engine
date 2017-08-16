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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.geometry.Point2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static isogame.GlobalConstants.ELEVATION_H;
import static isogame.GlobalConstants.TILEH;
import static isogame.GlobalConstants.TILEW;

public class StageInfo implements HasJSONRepresentation {
	public final int w;
	public final int h;
	private final Tile[] data;

	// transformation from map coordinates to iso coordinates
	private final Affine isoTransform;

	private final Rotate rUL;
	private final Rotate rLL;
	private final Rotate rLR;
	private final Rotate rUR;

	public StageInfo(
		final int w, final int h, final Tile[] data
	) throws CorruptDataException {
		this.w = w;
		this.h = h;
		this.data = data;
		if (data.length != w * h)
			throw new CorruptDataException("Incorrect number of tiles in stage");

		// set the camera angle rotations
		final double xPivot = ((double) this.w) / 2.0d;
		final double yPivot = ((double) this.h) / 2.0d;
		rUL = new Rotate();
		rLL = new Rotate(90, xPivot, yPivot);
		rLR = new Rotate(180, xPivot, yPivot);
		rUR = new Rotate(270, xPivot, yPivot);

		// compute the iso coordinate transformation
		// note that javafx transformations appear to compose backwards
		isoTransform = new Affine();
		isoTransform.appendTranslation((0 - TILEW) / 2, 0);
		isoTransform.appendScale(TILEW / Math.sqrt(2), TILEH / Math.sqrt(2));
		isoTransform.appendRotation(45, 0, 0);
	}

	public static StageInfo fromJSON(final JSONObject json, final Library lib)
		throws CorruptDataException
	{
		try {
			final int w = json.getInt("w");
			final int h =  json.getInt("h");
			final JSONArray jsonData = json.getJSONArray("data");

			final Tile[] data = new Tile[w * h];
			int i = 0;
			for (final Object t : jsonData) {
				data[i] = Tile.fromJSON((JSONObject) t, lib);
				i += 1;
			}

			return new StageInfo(w, h, data);
		} catch (ClassCastException e) {
			throw new CorruptDataException("Type error in stage info", e);
		} catch (JSONException e) {
			throw new CorruptDataException("Error parsing stage info, " + e.getMessage(), e);
		}
	}

	/**
	 * Get start tiles belonging to the human player (or player 1 in PVP)
	 * */
	public Collection<MapPoint> getPlayerStartTiles() {
		return Arrays.stream(data)
			.filter(t -> t.startZone == StartZoneType.PLAYER)
			.map(t -> t.pos)
			.collect(Collectors.toList());
	}

	/**
	 * Get start tiles belonging to the ai player (or player 2 in PVP)
	 * */
	public Collection<MapPoint> getAIStartTiles() {
		return Arrays.stream(data)
			.filter(t -> t.startZone == StartZoneType.AI)
			.map(t -> t.pos)
			.collect(Collectors.toList());
	}

	public boolean usesTerrainTexture(final TerrainTexture tex) {
		return Arrays.stream(data).anyMatch(t -> t.tex == tex);
	}

	public boolean usesCliffTexture(final CliffTexture tex) {
		return Arrays.stream(data).anyMatch(t -> t.cliffTexture == tex);
	}

	@Override
	public JSONObject getJSON() {
		final JSONArray a = new JSONArray();
		int ndata = w * h;
		for (int i = 0; i < ndata; i++) a.put(data[i].getJSON());

		final JSONObject r = new JSONObject();
		r.put("w", new Integer(w));
		r.put("h", new Integer(h));
		r.put("data", a);
		return r;
	}

	public Tile getTile(final MapPoint pos) throws IndexOutOfBoundsException {
		if (pos.x < 0 || pos.y < 0 || pos.x >= w || pos.y >= h)
			throw new IndexOutOfBoundsException();
		return data[(pos.y * w) + pos.x];
	}

	private final Set<MapPoint> updated = new HashSet<>();

	/**
	 * Get all the tiles updated since the last call to getUpdatedTiles
	 * */
	public List<Tile> getUpdatedTiles() {
		final List<Tile> r = updated.stream()
			.map(p -> data[(p.y * w) + p.x]).collect(Collectors.toList());
		updated.clear();
		return r;
	}

	public void setTile(final Tile tile)
		throws IndexOutOfBoundsException
	{
		if (tile.pos.x < 0 || tile.pos.y < 0 || tile.pos.x >= w || tile.pos.y >= h)
			throw new IndexOutOfBoundsException();
		updated.add(tile.pos);
		data[(tile.pos.y * w) + tile.pos.x] = tile;
	}

	public boolean hasTile(final MapPoint pos) {
		return pos.x >= 0 && pos.y >= 0 && pos.x < w && pos.y < h;
	}

	/**
	 * Get the position of the tile that renders at the top of the map.
	 * */
	public MapPoint getTop(final CameraAngle a) {
		switch (a) {
			case UL: return new MapPoint(0,     0);
			case LL: return new MapPoint(0,     h - 1);
			case LR: return new MapPoint(w - 1, h - 1);
			case UR: return new MapPoint(w - 1, 0);
			default: throw new RuntimeException("Invalid camera angle, this cannot happen");
		}
	}

	/**
	 * Get the position of the tile that renders at the bottom of the map.
	 * */
	public MapPoint getBottom(final CameraAngle a) {
		switch (a) {
			case UL: return new MapPoint(w - 1, h - 1);
			case LL: return new MapPoint(w - 1, 0);
			case LR: return new MapPoint(0,     0);
			case UR: return new MapPoint(0,     h - 1);
			default: throw new RuntimeException("Invalid camera angle, this cannot happen");
		}
	}

	/**
	 * Get the position of the tile that renders at the left of the map.
	 * */
	public MapPoint getLeft(final CameraAngle a) {
		switch (a) {
			case UL: return new MapPoint(0,     h - 1);
			case LL: return new MapPoint(w - 1, h - 1);
			case LR: return new MapPoint(w - 1, 0);
			case UR: return new MapPoint(0,     0);
			default: throw new RuntimeException("Invalid camera angle, this cannot happen");
		}
	}

	/**
	 * Get the position of the tile that renders at the right of the map.
	 * */
	public MapPoint getRight(final CameraAngle a) {
		switch (a) {
			case UL: return new MapPoint(w - 1, 0);
			case LL: return new MapPoint(0,     0);
			case LR: return new MapPoint(0,     h - 1);
			case UR: return new MapPoint(w - 1, h - 1);
			default: throw new RuntimeException("Invalid camera angle, this cannot happen");
		}
	}

	/**
	 * Get the upper left hand coordinate of a tile in iso space,
	 * assuming no elevation.
	 * */
	public Point2D toIsoCoord(final MapPoint p, final CameraAngle a) {
		final Point2D in = new Point2D(p.x, p.y);
		switch (a) {
			case UL: return isoTransform.transform(rUL.transform(in));
			case LL: return isoTransform.transform(rLL.transform(in));
			case LR: return isoTransform.transform(rLR.transform(in));
			case UR: return isoTransform.transform(rUR.transform(in));
			default: throw new RuntimeException(
				"Invalid camera angle.  This cannot happen");
		}
	}

	/**
	 * Convert an iso coordinate to the (uncorrected) map tile that lives there.
	 * */
	public MapPoint fromIsoCoord(final Point2D in, final CameraAngle a) {
		Point2D t;
		try {
			switch (a) {
				case UL:
					t = rUL.inverseTransform(isoTransform.inverseTransform(in));
					return new MapPoint((int) (t.getX() - 0.5), (int) t.getY());
				case LL:
					t = rLL.inverseTransform(isoTransform.inverseTransform(in));
					return new MapPoint((int) (t.getX() + 0.5), (int) (t.getY() + 1.5));
				case LR:
					t = rLR.inverseTransform(isoTransform.inverseTransform(in));
					return new MapPoint((int) (t.getX() + 1.5), (int) t.getY());
				case UR:
					t = rUR.inverseTransform(isoTransform.inverseTransform(in));
					return new MapPoint((int) (t.getX() - 1.5), (int) (t.getY() + 0.5));
				default: throw new RuntimeException(
					"Invalid camera angle.  This cannot happen");
			}
		} catch (NonInvertibleTransformException e) {
			throw new RuntimeException("This cannot happen", e);
		}
	}

	/**
	 * Get the upper left hand coordinate of a tile in iso space, accounting for
	 * its elevation.
	 * */
	public Point2D correctedIsoCoord(final MapPoint p, final CameraAngle a) {
		final Tile tile = getTile(p);
		return toIsoCoord(p, a).add(0d, ELEVATION_H * tile.elevation);
	}

	public Point2D correctedSpriteIsoCoord(final MapPoint p, final CameraAngle a) {
		final Tile tile = getTile(p);
		return toIsoCoord(p, a).add(0d, ELEVATION_H * tile.elevation +
			(tile.slope != SlopeType.NONE? 0.5d * ELEVATION_H : 0d));
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
	public Iterator<Tile> iterateTiles(final CameraAngle a) {
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
	public Iterator<Tile> iterateCollisionDetection(
		final MapPoint p, final CameraAngle a
	) {
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

