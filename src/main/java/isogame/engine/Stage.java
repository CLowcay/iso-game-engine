package isogame.engine;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import static isogame.GlobalConstants.ELEVATION_H;
import static isogame.GlobalConstants.TILEH;
import static isogame.GlobalConstants.TILEW;

public class Stage {
	public final StageInfo terrain;
	public final Map<MapPoint, Sprite> sprites;
	// transformation from map coordinates to iso coordinates
	private final Affine isoTransform;

	private final Rotate rUL;
	private final Rotate rLL;
	private final Rotate rLR;
	private final Rotate rUR;

	public Stage(StageInfo terrain) {
		this.terrain = terrain;
		sprites = new HashMap<MapPoint, Sprite>();

		// set the camera angle rotations
		double xPivot = ((double) terrain.w) / 2.0d;
		double yPivot = ((double) terrain.h) / 2.0d;
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

	public void addSprite(Sprite sprite) {
		sprites.put(sprite.pos, sprite);
	}

	/**
	 * Get the upper left hand coordinate of a tile in iso space,
	 * assuming no elevation.
	 * */
	public Point2D toIsoCoord(MapPoint p, CameraAngle a) {
		Point2D in = new Point2D(p.x, p.y);
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
	public MapPoint fromIsoCoord(Point2D in, CameraAngle a) {
		Point2D t;
		try {
			switch (a) {
				case UL:
					t = rUL.inverseTransform(isoTransform.inverseTransform(in));
					break;
				case LL:
					t = rLL.inverseTransform(isoTransform.inverseTransform(in));
					break;
				case LR:
					t = rLR.inverseTransform(isoTransform.inverseTransform(in));
					break;
				case UR:
					t = rUR.inverseTransform(isoTransform.inverseTransform(in));
					break;
				default: throw new RuntimeException(
					"Invalid camera angle.  This cannot happen");
			}
		} catch (NonInvertibleTransformException e) {
			throw new RuntimeException("This cannot happen", e);
		}

		return new MapPoint(
			(int) Math.floor(t.getX() - 0.5), (int) Math.floor(t.getY() + 0.5));
	}

	/**
	 * Determine which map tile the mouse is currently over, correcting for
	 * elevation.
	 * @return Null if there is no tile at the mouse position.
	 * */
	public MapPoint mouseTileCollision(Point2D in, CameraAngle a) {
		MapPoint p = fromIsoCoord(in, a);
		Iterator<Tile> it = terrain.iterateCollisionDetection(p, a);

		Tile tile;
		while (it.hasNext()) {
			tile = it.next();

			// compute a polygon representing the position and shape of the tile.
			// Then we will do a test to see if the mouse point is inside the
			// polygon.
			double[] xs = new double[6];
			double[] ys = new double[6];
			int pts;

			switch (tile.adjustSlopeForCameraAngle(a)) {
				case NONE:
					xs[0] = TILEW / 2; ys[0] = -2;
					xs[1] = TILEW + 4; ys[1] = TILEH / 2;
					xs[2] = TILEW / 2; ys[2] = TILEH + 2;
					xs[3] = -4;        ys[3] = TILEH / 2;
					pts = 4;
					break;
				case N:
					xs[0] = -4;        ys[0] = (TILEH / 2) + 2;
					xs[1] = TILEW / 2; ys[1] = 0 - (TILEH / 2) - 2;
					xs[2] = TILEW + 4; ys[2] = 0;
					xs[3] = TILEW + 4; ys[3] = (TILEH / 2) + 2;
					xs[4] = TILEW / 2; ys[4] = TILEH + 4;
					pts = 5;
					break;
				case E:
					xs[0] = -4;        ys[0] = (TILEH / 2) + 2;
					xs[1] = TILEW / 2; ys[1] = -2;
					xs[2] = TILEW + 4; ys[2] = -2;
					xs[3] = TILEW + 4; ys[3] = (TILEH / 2) + 2;
					xs[4] = TILEW / 2; ys[4] = TILEH + 2;
					pts = 5;
					break;
				case S:
					xs[0] = -4;        ys[0] = -2;
					xs[1] = TILEW / 2; ys[1] = -2;
					xs[2] = TILEW + 4; ys[2] = (TILEH / 2) + 2;
					xs[3] = TILEW / 2; ys[3] = TILEH  + 2;
					xs[4] = -4;        ys[4] = (TILEH / 2) + 2;
					pts = 5;
					break;
				case W:
					xs[0] = -4;        ys[0] = 0;
					xs[1] = TILEW / 2; ys[1] = 0 - (TILEH / 2) - 2;
					xs[2] = TILEW + 4; ys[2] = (TILEH / 2) + 2;
					xs[3] = TILEW / 2; ys[3] = TILEH + 4;
					xs[4] = -4;        ys[4] = (TILEH / 2) + 2;
					pts = 5;
					break;
				default: throw new RuntimeException(
					"Invalid slope type. This cannot happen");
			}

			Point2D cp = correctedIsoCoord(tile.pos, a);
			if (isPointInPolygon(xs, ys, pts, in.getX() - cp.getX(), in.getY() - cp.getY())) {
				return tile.pos;
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

	/**
	 * Get the upper left hand coordinate of a tile in iso space, accounting for
	 * its elevation.
	 * */
	public Point2D correctedIsoCoord(MapPoint p, CameraAngle a) {
		return toIsoCoord(p, a).add(0, ELEVATION_H * terrain.getTile(p).elevation);
	}

	private Map<MapPoint, LinkedList<Integer>> highlighting = new HashMap<>();
	private Paint[] highlightColors = {};

	public void setHighlightColors(Paint[] highlightColors) {
		this.highlightColors = highlightColors;
	}

	/**
	 * Highlight a tile.
	 * @param p The tile to highlight
	 * @param priority The highlighter to use.  A tile map be under several
	 * highlights at once, but only the highest priority highlight is actually
	 * rendered.
	 * */
	public void setHighlight(MapPoint p, int priority) {
		if (priority < 0 || priority >= highlightColors.length) {
			throw new RuntimeException("Invalid highlight priority " + priority);
		}

		LinkedList<Integer> highlights = highlighting.get(p);

		// insert new highlight value into the list, keeping the list sorted.  Ugly
		// but it works
		if (highlights == null) {
			highlights = new LinkedList<>();
			highlights.add(priority);
			highlighting.put(p, highlights);
		} else {
			int i = 0;
			for (int h : highlights) {
				if (priority > h) {
					highlights.add(i, priority);
					break;
				} else {
					i++;
				}
			}
		}
	}

	/**
	 * Clear a highlighting level.
	 * */
	public void clearHighlighting(int priority) {
		highlighting.values().forEach(h -> h.remove(priority));
	}

	/**
	 * Clear all highlighting.
	 * */
	public void clearAllHighlighting() {
		highlighting.clear();
	}

	public boolean isHighlighted(MapPoint p) {
		return highlighting.containsKey(p);
	}

	private void doHighlight(
		GraphicsContext cx, MapPoint p, double[] xs, double[] ys
	) {
		LinkedList<Integer> h = highlighting.get(p);
		if (h != null) {
			Integer i = h.peekFirst();
			if (i != null) {
				cx.setFill(highlightColors[i]);
				cx.fillPolygon(xs, ys, 4);
			}
		}
	}

	/**
	 * Render the entire stage (skipping the invisible bits for efficiency).
	 * */
	public void render(GraphicsContext cx, CameraAngle angle, BoundingBox visible) {
		double[] xs = new double[6];
		double[] ys = new double[6];
		terrain.iterateTiles(angle).forEachRemaining(tile -> {
			Point2D p = correctedIsoCoord(tile.pos, angle);
			double x = p.getX();
			double y = p.getY();

			if (visible.intersects(x, y, TILEW, TILEH)) {
				cx.save();
				cx.translate(x, y);

				switch (tile.adjustSlopeForCameraAngle(angle)) {
					case NONE:
						xs[0] = TILEW / 2; ys[0] = -2;
						xs[1] = TILEW + 4; ys[1] = TILEH / 2;
						xs[2] = TILEW / 2; ys[2] = TILEH + 2;
						xs[3] = -4;        ys[3] = TILEH / 2;
						cx.setFill(tile.texture);
						cx.fillPolygon(xs, ys, 4);
						doHighlight(cx, tile.pos, xs, ys);
						break;
					case N:
						xs[0] = -4;        ys[0] = (TILEH / 2) + 2;
						xs[1] = TILEW / 2; ys[1] = 0 - (TILEH / 2) - 2;
						xs[2] = TILEW + 4; ys[2] = 0;
						xs[3] = TILEW / 2; ys[3] = TILEH + 4;
						cx.setFill(tile.texture);
						cx.fillPolygon(xs, ys, 4);
						doHighlight(cx, tile.pos, xs, ys);

						xs[0] = TILEW / 2; ys[0] = TILEH;
						xs[1] = TILEW;     ys[1] = 0;
						xs[2] = TILEW;     ys[2] = (TILEH / 2) + 2;
						xs[3] = TILEW / 2; ys[3] = TILEH + 2;
						cx.setFill(tile.getCliffTexture(angle));
						cx.fillPolygon(xs, ys, 4);
						break;
					case E:
						xs[0] = -4;        ys[0] = (TILEH / 2) + 2;
						xs[1] = TILEW / 2; ys[1] = -2;
						xs[2] = TILEW + 4; ys[2] = -2;
						xs[3] = TILEW / 2; ys[3] = (TILEH / 2) + 2;
						cx.setFill(tile.texture);
						cx.fillPolygon(xs, ys, 4);
						doHighlight(cx, tile.pos, xs, ys);

						xs[0] = 0;         ys[0] = TILEH / 2;
						xs[1] = TILEW / 2; ys[1] = TILEH / 2;
						xs[2] = TILEW;     ys[2] = 0;
						xs[3] = TILEW;     ys[3] = (TILEH / 2) + 2;
						xs[4] = TILEW / 2; ys[4] = TILEH + 2;
						xs[5] = 0;         ys[5] = (TILEH / 2) + 2;
						cx.setFill(tile.getCliffTexture(angle));
						cx.fillPolygon(xs, ys, 6);
						break;
					case S:
						xs[0] = -4;        ys[0] = -2;
						xs[1] = TILEW / 2; ys[1] = -2;
						xs[2] = TILEW + 4; ys[2] = (TILEH / 2) + 2;
						xs[3] = TILEW / 2; ys[3] = (TILEH / 2) + 2;
						cx.setFill(tile.texture);
						cx.fillPolygon(xs, ys, 4);
						doHighlight(cx, tile.pos, xs, ys);

						xs[0] = 0;         ys[0] = 0;
						xs[1] = TILEW / 2; ys[1] = TILEH / 2;
						xs[2] = TILEW;     ys[2] = TILEH / 2;
						xs[3] = TILEW;     ys[3] = (TILEH / 2) + 2;
						xs[4] = TILEW / 2; ys[4] = TILEH + 2;
						xs[5] = 0;         ys[5] = (TILEH / 2) + 2;
						cx.setFill(tile.getCliffTexture(angle));
						cx.fillPolygon(xs, ys, 6);
						break;
					case W:
						xs[0] = -4;        ys[0] = 0;
						xs[1] = TILEW / 2; ys[1] = 0 - (TILEH / 2) - 2;
						xs[2] = TILEW + 4; ys[2] = (TILEH / 2) + 2;
						xs[3] = TILEW / 2; ys[3] = TILEH + 4;
						cx.setFill(tile.texture);
						cx.fillPolygon(xs, ys, 4);
						doHighlight(cx, tile.pos, xs, ys);

						xs[0] = 0;         ys[0] = 0;
						xs[1] = TILEW / 2; ys[1] = TILEH;
						xs[2] = TILEW / 2; ys[2] = TILEH + 2;
						xs[3] = 0;         ys[3] = (TILEH / 2) + 2;
						cx.setFill(tile.getCliffTexture(angle));
						cx.fillPolygon(xs, ys, 4);
						break;
				}

				cx.setFill(tile.getCliffTexture(angle));
				for (int i = 0; i < tile.elevation; i++) {
					cx.translate(0, TILEH / 2);
					xs[0] = 0;         ys[0] = 0;
					xs[1] = 0;         ys[1] = (TILEH / 2) + 2;
					xs[2] = TILEW / 2; ys[2] = TILEH + 2;
					xs[3] = TILEW;     ys[3] = (TILEH / 2) + 2;
					xs[4] = TILEW;     ys[4] = 0;
					xs[5] = TILEW / 2; ys[5] = TILEH / 2;
					cx.fillPolygon(xs, ys, 6);
				}
				cx.restore();
			}
		});
	}
}
