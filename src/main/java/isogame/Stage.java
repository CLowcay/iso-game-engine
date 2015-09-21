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
						break;
					case N:
						xs[0] = -4;        ys[0] = (TILEH / 2) + 2;
						xs[1] = TILEW / 2; ys[1] = 0 - (TILEH / 2) - 2;
						xs[2] = TILEW + 4; ys[2] = 0;
						xs[3] = TILEW / 2; ys[3] = TILEH + 4;
						cx.setFill(tile.texture);
						cx.fillPolygon(xs, ys, 4);
						break;
					case E:
						xs[0] = -4;        ys[0] = (TILEH / 2) + 2;
						xs[1] = TILEW / 2; ys[1] = -2;
						xs[2] = TILEW + 4; ys[2] = -2;
						xs[3] = TILEW / 2; ys[3] = (TILEH / 2) + 2;
						cx.setFill(tile.texture);
						cx.fillPolygon(xs, ys, 4);
						break;
					case S:
						xs[0] = -4;        ys[0] = -2;
						xs[1] = TILEW / 2; ys[1] = -2;
						xs[2] = TILEW + 4; ys[2] = (TILEH / 2) + 2;
						xs[3] = TILEW / 2; ys[3] = (TILEH / 2) + 2;
						cx.setFill(tile.texture);
						cx.fillPolygon(xs, ys, 4);
						break;
					case W:
						xs[0] = -4;        ys[0] = 0;
						xs[1] = TILEW / 2; ys[1] = 0 - (TILEH / 2) - 2;
						xs[2] = TILEW + 4; ys[2] = (TILEH / 2) + 2;
						xs[3] = TILEW / 2; ys[3] = TILEH + 4;
						cx.setFill(tile.texture);
						cx.fillPolygon(xs, ys, 4);
						break;
				}

				for (int i = 0; i < tile.elevation; i++) {
					cx.translate(0, TILEH / 2);
					xs[0] = 0;         ys[0] = 0;
					xs[1] = 0;         ys[1] = (TILEH / 2) + 2;
					xs[2] = TILEW / 2; ys[2] = TILEH + 2;
					xs[3] = TILEW;     ys[3] = (TILEH / 2) + 2;
					xs[4] = TILEW;     ys[4] = 0;
					xs[5] = TILEW / 2; ys[5] = TILEH / 2;
					cx.setFill(tile.getCliffTexture(angle));
					cx.fillPolygon(xs, ys, 6);
				}
				cx.restore();
			}
		});
	}
}

