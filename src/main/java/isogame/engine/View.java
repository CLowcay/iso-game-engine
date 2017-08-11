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

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Translate;
import static isogame.GlobalConstants.ISO_VIEWPORTH;
import static isogame.GlobalConstants.ISO_VIEWPORTW;
import static isogame.GlobalConstants.TILEH;
import static isogame.GlobalConstants.TILEW;

public class View extends Pane {
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

	final private Pane innerPane = new Pane();

	/**
	 * Create a view with an initial viewport.
	 * */
	public View(final int w, final int h) {
		this.getChildren().add(innerPane);

		innerPane.setFocusTraversable(false);
		innerPane.setMouseTransparent(true);

		viewportW = w;
		viewportH = h;
		angle = CameraAngle.UL;
		x = 0;
		y = 0;
		lx = 0;
		ly = 0;

		widthProperty().addListener((obs, w0, w1) -> {
			setViewport(w1.intValue(), (int) getHeight());
		});
		heightProperty().addListener((obs, h0, h1) -> {
			setViewport((int) getWidth(), h1.intValue());
		});

		updateClipping();
		updateScreenTransform();
	}

	/**
	 * Calculate the viewport, adding letterboxing (black bars) to ensure the
	 * aspect ratio is 16:9.
	 * */
	private void setViewport(final int w, final int h) {
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

		updateClipping();
		updateScreenTransform();
	}

	private void updateClipping() {
		Rectangle r1 = null;
		Rectangle r2 = null;

		if (ly > 0) {
			r1 = new Rectangle(0, 0, viewportW, ly);
			r2 = new Rectangle(0, ly + viewportH, viewportW, ly);
		} else if (lx > 0) {
			r1 = new Rectangle(0, 0, lx, viewportH);
			r2 = new Rectangle(lx + viewportW, 0, lx, viewportH);
		}

		if (r1 != null && r2 != null) {
			r1.setFill(Color.BLACK);
			r2.setFill(Color.BLACK);
			this.getChildren().clear();
			this.getChildren().addAll(innerPane, r1, r2);
		} else {
			this.getChildren().clear();
			this.getChildren().add(innerPane);
		}
	}

	public void centreOnTile(final Stage stage, final MapPoint pos) {
		final Point2D centre = stage.toIsoCoord(pos, angle);
		x = centre.getX() - ((ISO_VIEWPORTW - TILEW) / 2.0);
		y = centre.getY() - ((ISO_VIEWPORTH - TILEH) / 2.0);
		updateScreenTransform();
	}

	public Point2D getViewportCentre() {
		return new Point2D(viewportW / 2.0, viewportH / 2.0);
	}

	public void rotateLeft() {
		angle = angle.nextClockwise();
	}

	public void rotateRight() {
		angle = angle.nextAnticlockwise();
	}

	private final static int overscroll = 3;

	public Rectangle2D getScrollBounds(final Stage stage) {
		final Point2D pt = stage.toIsoCoord(stage.terrain.getTop(angle), angle);
		final Point2D pb = stage.toIsoCoord(stage.terrain.getBottom(angle), angle);
		final Point2D pl = stage.toIsoCoord(stage.terrain.getLeft(angle), angle);
		final Point2D pr = stage.toIsoCoord(stage.terrain.getRight(angle), angle);
		return new Rectangle2D(
			pl.getX() - (overscroll * TILEW),
			pt.getY() - (overscroll * TILEH),
			pr.subtract(pl).getX() + (overscroll * TILEW * 2) - ISO_VIEWPORTW,
			pb.subtract(pt).getY() + (overscroll * TILEH * 2) - ISO_VIEWPORTH);
	}

	public CameraAngle getCameraAngle() {
		return angle;
	}

	public void setScrollPos(final Point2D p) {
		this.x = p.getX();
		this.y = p.getY();

		scrollTransform.setX(-x);
		scrollTransform.setY(-y);
	}

	public Point2D getScrollPos() {
		return new Point2D(x, y);
	}

	/**
	 * Get the tile at the mouse position.
	 * @return null if there is no tile under the mouse.
	 * */
	public MapPoint tileAtMouse(final Point2D mouse, final Stage stage) {
		try {
			return stage.collisions.mouseTileCollision(
				screenTransform.inverseTransform(mouse), angle);
		} catch (NonInvertibleTransformException e) {
			throw new RuntimeException("This cannot happen", e);
		}
	}

	/**
	 * Get the sprite at the mouse position.
	 * @return null if there is no sprite under the mouse.
	 * */
	public MapPoint spriteAtMouse(final Point2D mouse, final Stage stage) {
		try {
			return stage.collisions.mouseSpriteCollision(
				screenTransform.inverseTransform(mouse), angle);
		} catch (NonInvertibleTransformException e) {
			throw new RuntimeException("This cannot happen", e);
		}
	}

	private final Translate scrollTransform = new Translate();
	private Affine screenTransform = null;

	private void updateScreenTransform() {
		screenTransform = new Affine();
		screenTransform.appendTranslation(lx, ly);
		screenTransform.appendScale(
			viewportW / ISO_VIEWPORTW, viewportH / ISO_VIEWPORTH);

		innerPane.getTransforms().clear();
		innerPane.getTransforms().addAll(screenTransform, scrollTransform);
		scrollTransform.setX(-x);
		scrollTransform.setY(-y);
	}

	public void update(final long t, final Stage stage) {
		stage.update(innerPane.getChildren(), t, angle);
	}

	/**
	 * Render a single complete frame.
	 * @param cx The graphics context
	 * @param stage The stage to render
	 * @param renderDebug Render debug information
	 * */
	public void renderFrame(
		final GraphicsContext cx,
		final long t,
		final Stage stage,
		final boolean renderDebug
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

