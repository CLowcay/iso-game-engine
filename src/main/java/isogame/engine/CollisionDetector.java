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

import javafx.geometry.Point2D;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import static isogame.GlobalConstants.TILEH;
import static isogame.GlobalConstants.TILEW;

public class CollisionDetector {
	private final Stage stage;

	public CollisionDetector(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Determine which map tile the mouse is currently over, correcting for
	 * elevation.
	 * @return null if there is no tile at the mouse position.
	 * */
	public MapPoint mouseTileCollision(Point2D in, CameraAngle a) {
		MapPoint p = stage.fromIsoCoord(in, a);
		Iterator<Tile> it = stage.terrain.iterateCollisionDetection(p, a);

		Tile tile;
		while (it.hasNext()) {
			tile = it.next();

			// compute a polygon representing the position and shape of the tile.
			// Then we will do a test to see if the mouse point is inside the
			// polygon.
			double[] xs = new double[6];
			double[] ys = new double[6];
			int pts;

			double extension = (TILEH * ((double) tile.elevation)) / 2;
			switch (tile.adjustSlopeForCameraAngle(a)) {
				case NONE:
					if (tile.elevation == 0) {
						xs[0] = TILEW / 2; ys[0] = -2;
						xs[1] = TILEW + 4; ys[1] = TILEH / 2;
						xs[2] = TILEW / 2; ys[2] = TILEH + 2;
						xs[3] = -4;        ys[3] = TILEH / 2;
						pts = 4;
					} else if (tile.elevation > 0) {
						xs[0] = TILEW / 2; ys[0] = -2;
						xs[1] = TILEW + 4; ys[1] = TILEH / 2;
						xs[2] = TILEW + 4; ys[2] = (TILEH / 2) + extension + 2;
						xs[3] = TILEW / 2; ys[3] = TILEH + extension + 2;
						xs[4] = -4;        ys[4] = (TILEH / 2) + extension + 2;
						xs[5] = -4;        ys[5] = TILEH / 2;
						pts = 6;
					} else {
						throw new RuntimeException("Negative elevation not supported");
					}
					break;
				case N:
					xs[0] = -4;        ys[0] = (TILEH / 2) + 2;
					xs[1] = TILEW / 2; ys[1] = 0 - (TILEH / 2) - 2;
					xs[2] = TILEW + 4; ys[2] = 0;
					xs[3] = TILEW + 4; ys[3] = (TILEH / 2) + extension + 2;
					xs[4] = TILEW / 2; ys[4] = TILEH + extension + 4;
					xs[5] = -4;        ys[5] = (TILEH / 2) + extension + 2;
					pts = 6;
					break;
				case E:
					xs[0] = -4;        ys[0] = (TILEH / 2) + 2;
					xs[1] = TILEW / 2; ys[1] = -2;
					xs[2] = TILEW + 4; ys[2] = -2;
					xs[3] = TILEW + 4; ys[3] = (TILEH / 2) + extension + 2;
					xs[4] = TILEW / 2; ys[4] = TILEH + extension + 2;
					xs[5] = -4;        ys[5] = (TILEH / 2) + extension + 2;
					pts = 6;
					break;
				case S:
					xs[0] = -4;        ys[0] = -2;
					xs[1] = TILEW / 2; ys[1] = -2;
					xs[2] = TILEW + 4; ys[2] = (TILEH / 2) + 2;
					xs[3] = TILEW + 4; ys[3] = (TILEH / 2) + extension + 2;
					xs[4] = TILEW / 2; ys[4] = TILEH + extension + 2;
					xs[5] = -4;        ys[5] = (TILEH / 2) + extension + 2;
					pts = 6;
					break;
				case W:
					xs[0] = -4;        ys[0] = 0;
					xs[1] = TILEW / 2; ys[1] = 0 - (TILEH / 2) - 2;
					xs[2] = TILEW + 4; ys[2] = (TILEH / 2) + 2;
					xs[3] = TILEW + 4; ys[3] = (TILEH / 2) + extension + 2;
					xs[4] = TILEW / 2; ys[4] = TILEH + extension + 4;
					xs[5] = -4;        ys[5] = (TILEH / 2) + extension + 2;
					pts = 6;
					break;
				default: throw new RuntimeException(
					"Invalid slope type. This cannot happen");
			}

			Point2D cp = stage.correctedIsoCoord(tile.pos, a);
			if (isPointInPolygon(xs, ys, pts, in.getX() - cp.getX(), in.getY() - cp.getY())) {
				return tile.pos;
			}
		}

		return null;
	}

	/**
	 * Determine which sprite the mouse is currently over, correcting for
	 * elevation etc.  Does a pixel perfect hit test.
	 * @return null if there is no sprite at the mouse position
	 * */
	public MapPoint mouseSpriteCollision(Point2D in, CameraAngle a) {
		MapPoint p = stage.fromIsoCoord(in, a);
		Iterator<Tile> it = stage.terrain.iterateCollisionDetection(p, a);

		Tile tile;
		while (it.hasNext()) {
			tile = it.next();
			List<Sprite> l = stage.getSpritesByTile(tile.pos);

			if (l != null) {
				l = l.subList(0, l.size());
				Collections.reverse(l);
				for (Sprite s : l) {
					Point2D sp = in.subtract(stage.correctedIsoCoord(tile.pos, a));
					if (s.hitTest(sp.getX(), sp.getY(), a)) return tile.pos;
				}
			}
		}

		return null;
	}

	/**
	 * Determine if a point lies inside a convex polygon.
	 * */
	private boolean isPointInPolygon(
		double[] xs, double[] ys, int pts, double x, double y
	) {
		double t = 0;

		// for a convex polygon, we can determine if a point lies inside the
		// polygon by checking it lies on the same side of each line on the
		// perimeter of the polygon.
		for (int i = 0; i < pts; i++) {
			double lx0 = xs[i];
			double ly0 = ys[i];
			double lx1 = xs[(i + 1) % pts];
			double ly1 = ys[(i + 1) % pts];

			// the sign of this cross product determines which side the point is on.
			double det = ((lx1 - lx0) * (y - ly0)) - ((ly1 - ly0) * (x - lx0));
			if (det > 0 && t < 0 || det < 0 && t > 0) {
				return false;
			} else if (det != 0) {
				t = det;
			}
		}

		return true;
	}
}

