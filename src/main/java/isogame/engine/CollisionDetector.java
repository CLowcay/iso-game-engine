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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javafx.geometry.Point2D;

public class CollisionDetector {
	private final Stage stage;

	public CollisionDetector(final Stage stage) {
		this.stage = stage;
	}

	/**
	 * Determine which map tile the mouse is currently over, correcting for
	 * elevation.
	 * @return null if there is no tile at the mouse position.
	 * */
	public MapPoint mouseTileCollision(
		final Point2D in, final CameraAngle a
	) {
		final MapPoint p = stage.terrain.fromIsoCoord(in, a);
		final Iterator<Tile> it = stage.terrain.iterateCollisionDetection(p, a);

		while (it.hasNext()) {
			final Tile tile = it.next();
			final Point2D cp = stage.terrain.correctedIsoCoord(tile.pos, a);
			final List<Point2D> shape;

			switch (a) {
				case UL: shape = tile.shapeUL; break;
				case UR: shape = tile.shapeUR; break;
				case LL: shape = tile.shapeLL; break;
				case LR: shape = tile.shapeLR; break;
				default:
					throw new RuntimeException("Invalid camera angle, this cannot happen");
			}

			if (isPointInPolygon(
				shape, in.getX() - cp.getX(), in.getY() - cp.getY())
			) {
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
	public MapPoint mouseSpriteCollision(
		final Point2D in, final CameraAngle a
	) {
		final MapPoint p = stage.terrain.fromIsoCoord(in, a);
		final Iterator<Tile> it = stage.terrain.iterateCollisionDetection(p, a);

		while (it.hasNext()) {
			final Tile tile = it.next();
			final List<Sprite> l = stage.getSpritesByTile(tile.pos);

			for (final Sprite s : l) {
				final Point2D sp = in.subtract(
					stage.terrain.correctedSpriteIsoCoord(tile.pos, a));
				if (s.hitTest(sp.getX(), sp.getY(), a)) return tile.pos;
			}
		}

		return null;
	}

	/**
	 * Determine if a point lies inside a convex polygon.
	 * */
	private boolean isPointInPolygon(
		final List<Point2D> shape,
		final double x,
		final double y
	) {
		double t = 0;

		// for a convex polygon, we can determine if a point lies inside the
		// polygon by checking it lies on the same side of each line on the
		// perimeter of the polygon.
		final int pts = shape.size();
		for (int i = 0; i < pts; i++) {
			final double lx0 = shape.get(i).getX();
			final double ly0 = shape.get(i).getY();
			final double lx1 = shape.get((i + 1) % pts).getX();
			final double ly1 = shape.get((i + 1) % pts).getY();

			// the sign of this cross product determines which side the point is on.
			final double det = ((lx1 - lx0) * (y - ly0)) - ((ly1 - ly0) * (x - lx0));
			if (det > 0 && t < 0 || det < 0 && t > 0) {
				return false;
			} else if (det != 0) {
				t = det;
			}
		}

		return true;
	}
}

