package isogame.engine;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
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
	}

	public void centreOnTile(Stage stage, MapPoint pos) {
		Point2D centre = stage.toIsoCoord(pos, angle);
		x = centre.getX() - ((ISO_VIEWPORTW - TILEW) / 2.0);
		y = centre.getY() - ((ISO_VIEWPORTH - TILEH) / 2.0);
	}

	public void rotateLeft() {
		angle = angle.nextClockwise();
	}

	public void rotateRight() {
		angle = angle.nextAnticlockwise();
	}

	public void setScrollPos(Point2D p) {
		this.x = p.getX();
		this.y = p.getY();
	}

	public Point2D getScrollPos() {
		return new Point2D(x, y);
	}

	/**
	 * Render a single complete frame.
	 * */
	public void renderFrame(GraphicsContext cx, Stage stage) {
		Affine t = new Affine();
		cx.setTransform(t);

		cx.setFill(Color.WHITE);
		cx.fillRect(lx, ly, viewportW, viewportH);

		cx.save();

		t.appendTranslation(lx, ly);
		t.appendScale(viewportW / ISO_VIEWPORTW, viewportH / ISO_VIEWPORTH);
		t.appendTranslation(-x, -y);
		cx.setTransform(t);

		Point2D test1 = stage.toIsoCoord(new MapPoint(0, 0), angle);
		Point2D test2 = t.transform(test1);
		stage.render(cx, angle, new BoundingBox(x, y - TILEH,
				ISO_VIEWPORTW, ISO_VIEWPORTH + (2 * TILEH)));

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

