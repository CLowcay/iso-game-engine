package isogame;

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
	public CameraAngle angle;

	// These are in iso space coordinates.  Please refer to coordinates.txt for
	// more information.
	public double x;
	public double y;

	// These are in screen coordinates
	public double viewportW;
	public double viewportH;

	/**
	 * Create a view with an initial viewport.
	 * */
	public View(int w, int h) {
		viewportW = w;
		viewportH = h;
		angle = CameraAngle.UL;
		x = 0;
		y = 0;
	}

	public void setViewport(int w, int h) {
		viewportW = w;
		viewportH = h;
	}

	public void centreOnTile(Stage stage, MapPoint pos) {
		Point2D centre = stage.toIsoCoord(pos, angle);
		x = centre.getX() - ((ISO_VIEWPORTW - TILEW) / 2.0);
		y = centre.getY() - ((ISO_VIEWPORTH - TILEH) / 2.0);
	}

	/**
	 * Render a single complete frame.
	 * */
	public void renderFrame(GraphicsContext cx, Stage stage) {
		Affine t = new Affine();
		t.appendScale(viewportW / ISO_VIEWPORTW, viewportH / ISO_VIEWPORTH);
		t.appendTranslation(-x, -y);
		cx.setTransform(t);

		Point2D test1 = stage.toIsoCoord(new MapPoint(0, 0), angle);
		Point2D test2 = t.transform(test1);
		stage.render(cx, angle, new BoundingBox(x, y, ISO_VIEWPORTW, ISO_VIEWPORTH));
	}
}

