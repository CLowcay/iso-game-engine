package isogame;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import java.util.HashMap;
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
	 * Get the upper left hand coordinate of a tile in iso space, accounting for
	 * its elevation.
	 * */
	public Point2D correctedIsoCoord(MapPoint p, CameraAngle a) {
		return toIsoCoord(p, a).add(0, ELEVATION_H * terrain.getTile(p).elevation);
	}

	/**
	 * Render the entire stage (skipping the invisible bits for efficiency).
	 * */
	public void render(GraphicsContext cx, CameraAngle angle, BoundingBox visible) {
		double[] xs = new double[4];
		double[] ys = new double[4];
		terrain.iterateTiles(angle).forEachRemaining(tile -> {
			Point2D p = correctedIsoCoord(tile.pos, angle);
			double x = p.getX();
			double y = p.getY();
			xs[0] = x + (TILEW / 2);
			ys[0] = y;
			xs[1] = x + TILEW;
			ys[1] = y + (TILEH / 2);
			xs[2] = x + (TILEW / 2);
			ys[2] = y + TILEH;
			xs[3] = x;
			ys[3] = y + (TILEH / 2);
			cx.setFill(tile.texture);
			cx.fillPolygon(xs, ys, 4);
		});
	}
}

