package isogame.engine;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import static isogame.GlobalConstants.ISO_VIEWPORTH;
import static isogame.GlobalConstants.ISO_VIEWPORTW;
import static isogame.GlobalConstants.TILEH;
import static isogame.GlobalConstants.TILEW;

public class View {
	private CameraAngle angle;

	// These are in iso space coordinates.  Please refer to coordinates.txt for
	// more information.
	private double x;
	private double y;

	// letterboxing coordinates
	private double lx;
	private double ly;

	// These are in screen coordinates
	private double viewportW;
	private double viewportH;

	/**
	 * Create a view with an initial viewport.
	 * */
	public View(int w, int h) {
		viewportW = w;
		viewportH = h;
		angle = CameraAngle.UL;
		x = 0;
		y = 0;
		lx = 0;
		ly = 0;

		updateScreenTransform();
	}

	/**
	 * Calculate the viewport, adding letterboxing (black bars) to ensure the
	 * aspect ratio is 16:9.
	 * */
	public void setViewport(int w, int h) {
		lx = 0;
		ly = 0;

		double ph = w * (9.0d / 16.0d);
		double pw = h * (16.0d / 9.0d);

		if (pw > w) {
			viewportW = w;
			viewportH = ph;
			ly = (h - ph) / 2;
		} else if (ph > h) {
			viewportH = h;
			viewportW = pw;
			lx = (w - pw) / 2;
		} else {
			viewportW = w;
			viewportH = h;
		}

		updateScreenTransform();
	}

	public void centreOnTile(Stage stage, MapPoint pos) {
		Point2D centre = stage.toIsoCoord(pos, angle);
		x = centre.getX() - ((ISO_VIEWPORTW - TILEW) / 2.0);
		y = centre.getY() - ((ISO_VIEWPORTH - TILEH) / 2.0);
		updateScreenTransform();
	}

	public void rotateLeft() {
		angle = angle.nextClockwise();
	}

	public void rotateRight() {
		angle = angle.nextAnticlockwise();
	}

	public CameraAngle getCameraAngle() {
		return angle;
	}

	public void setScrollPos(Point2D p) {
		this.x = p.getX();
		this.y = p.getY();
		updateScreenTransform();
	}

	public Point2D getScrollPos() {
		return new Point2D(x, y);
	}

	/**
	 * Get the tile at the mouse position.
	 * @return null if there is no tile under the mouse.
	 * */
	public MapPoint tileAtMouse(Point2D mouse, Stage stage) {
		try {
			return stage.mouseTileCollision(
				screenTransform.inverseTransform(mouse), angle);
		} catch (NonInvertibleTransformException e) {
			throw new RuntimeException("This cannot happen", e);
		}
	}

	/**
	 * Get the sprite at the mouse position.
	 * @return null if there is no sprite under the mouse.
	 * */
	public MapPoint spriteAtMouse(Point2D mouse, Stage stage) {
		try {
			return stage.mouseSpriteCollision(
				screenTransform.inverseTransform(mouse), angle);
		} catch (NonInvertibleTransformException e) {
			throw new RuntimeException("This cannot happen", e);
		}
	}

	Affine screenTransform;

	private void updateScreenTransform() {
		screenTransform = new Affine();
		screenTransform.appendTranslation(lx, ly);
		screenTransform.appendScale(
			viewportW / ISO_VIEWPORTW, viewportH / ISO_VIEWPORTH);
		screenTransform.appendTranslation(-x, -y);
	}

	/**
	 * Render a single complete frame.
	 * @param cx The graphics context
	 * @param stage The stage to render
	 * @param renderDebug Render debug information
	 * */
	public void renderFrame(
		GraphicsContext cx, long t, Stage stage, boolean renderDebug
	) {
		cx.setFill(Color.WHITE);
		cx.fillRect(lx, ly, viewportW, viewportH);

		cx.save();
		cx.setTransform(screenTransform);

		stage.render(cx, angle, t, new BoundingBox(x, y - TILEH,
				ISO_VIEWPORTW, ISO_VIEWPORTH + (2 * TILEH)), renderDebug);

		cx.restore();

		// draw black bars if we need them.
		// Performance note: clipping may be more efficient
		if (ly > 0) {
			cx.setFill(Color.BLACK);
			cx.fillRect(0, 0, viewportW, ly);
			cx.fillRect(0, ly + viewportH, viewportW, ly);
		} else if (lx > 0) {
			cx.setFill(Color.BLACK);
			cx.fillRect(0, 0, lx, viewportH);
			cx.fillRect(lx + viewportW, 0, lx, viewportH);
		}
	}
}

