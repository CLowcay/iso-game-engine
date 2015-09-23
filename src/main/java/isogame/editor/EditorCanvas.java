package isogame.editor;

import isogame.engine.CliffTexture;
import isogame.engine.ContinuousAnimator;
import isogame.engine.CorruptDataException;
import isogame.engine.MapPoint;
import isogame.engine.SlopeType;
import isogame.engine.Stage;
import isogame.engine.StageInfo;
import isogame.engine.StartZoneType;
import isogame.engine.TerrainTexture;
import isogame.engine.Tile;
import isogame.engine.View;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import java.util.EnumSet;
import java.util.Set;
import static isogame.GlobalConstants.SCROLL_SPEED;
import static isogame.GlobalConstants.TILEH;

public class EditorCanvas extends Pane {
	private final AnimationTimer animateCanvas;

	public EditorCanvas(Node root) throws CorruptDataException {
		super();
		this.setFocusTraversable(true);

		Canvas canvas = new Canvas();
		this.getChildren().add(canvas);
		canvas.widthProperty().bind(this.widthProperty());
		canvas.heightProperty().bind(this.heightProperty());

		Stage stage = exampleStage();
		View view = new View(960, 540);
		view.centreOnTile(stage, new MapPoint(3, 3));

		final ContinuousAnimator scrolling = new ContinuousAnimator();
		scrolling.reset(view.getScrollPos());

		final GraphicsContext cx = canvas.getGraphicsContext2D();

		animateCanvas = new AnimationTimer() {
			int count0 = 0;
			int count = 0;
			long now0 = 0;

			@Override
			public void handle(long now) {
				count++;
				if (now0 == 0) now0 = now;
				if ((now - now0) >= 5000000000l) {
					System.err.println("fps: " + ((count - count0) / 5));
					now0 = now;
					count0 = count;
				}

				view.setScrollPos(scrolling.valueAt(now));
				view.renderFrame(cx, stage);
			}
		};

		// Listen for window resize events
		this.widthProperty().addListener((obs, w0, w) -> {
			view.setViewport(w.intValue(), (int) canvas.getHeight());
		});
		this.heightProperty().addListener((obs, h0, h) -> {
			view.setViewport((int) canvas.getWidth(), h.intValue());
		});

		// Listen for keyboard events
		final Set<KeyCode> keys = EnumSet.noneOf(KeyCode.class);
		root.setOnKeyPressed(event -> {
			KeyCode k = event.getCode();
			keys.add(k);
			setScrollingAnimation(scrolling, keys);
			switch (k) {
				case A: view.rotateLeft(); break;
				case D: view.rotateRight(); break;
			}
		});
		root.setOnKeyReleased(event -> {
			keys.remove(event.getCode());
			setScrollingAnimation(scrolling, keys);
		});

		// Listen for mouse events
		root.setOnMouseMoved(new EventHandler<MouseEvent>() {
			MapPoint p0 = null;
			@Override
			public void handle(MouseEvent event) {
				MapPoint p = view.tileAtMouse(new Point2D(event.getX(), event.getY()), stage);
				if (p != p0 && stage.terrain.hasTile(p)) {
					p0 = p;
					stage.clearAllHighlighting();
					stage.setHighlight(p, 0);
				}
			}
		});
	}

	/**
	 * Make a simple example stage for testing purposes.
	 * */
	private Stage exampleStage() throws CorruptDataException {
		TerrainTexture black = new TerrainTexture("black", "/black.jpg");
		TerrainTexture white = new TerrainTexture("white", "/white.jpg");
		CliffTexture cliff = new CliffTexture(
			"cliffs",
			"/cliff_texture.png",
			"/cliff_texture_narrow.png");
		Tile[] data = new Tile[8 * 8];
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				TerrainTexture t;
				if ((x + y) % 2 == 0) t = black; else t = white;
				int elevation = 0;
				SlopeType slope = SlopeType.NONE;
				if (x == 0 && y == 1) slope = SlopeType.N;
				if (x == 1 && y == 1) slope = SlopeType.N;
				if (x == 0 && y == 0) elevation = 1;
				if (x == 1 && y == 0) elevation = 1;

				if (x == 0 && y == 6) slope = SlopeType.S;
				if (x == 1 && y == 6) slope = SlopeType.S;
				if (x == 0 && y == 7) elevation = 1;
				if (x == 1 && y == 7) elevation = 1;

				if (x == 6 && y == 0) slope = SlopeType.E;
				if (x == 6 && y == 1) slope = SlopeType.E;
				if (x == 7 && y == 0) elevation = 1;
				if (x == 7 && y == 1) elevation = 1;

				if (x == 7 && y == 6) slope = SlopeType.W;
				if (x == 7 && y == 7) slope = SlopeType.W;
				if (x == 6 && y == 6) elevation = 1;
				if (x == 6 && y == 7) elevation = 1;

				if (x == 4 && y == 3) slope = SlopeType.S;
				if (x == 3 && y == 4) slope = SlopeType.E;
				if (x == 4 && y == 5) slope = SlopeType.N;
				if (x == 5 && y == 4) slope = SlopeType.W;

				data[(y * 8) + x] = new Tile(
					new MapPoint(x, y), elevation,
					slope, false,
					StartZoneType.NONE, t, cliff);
			}
		}
		StageInfo terrain = new StageInfo(8, 8, data);
		return new Stage(terrain);
	}

	private void setScrollingAnimation(
		ContinuousAnimator scrolling, Set<KeyCode> keys
	) {
		boolean kup = keys.contains(KeyCode.UP);
		boolean kdown = keys.contains(KeyCode.DOWN);
		boolean kleft = keys.contains(KeyCode.LEFT);
		boolean kright = keys.contains(KeyCode.RIGHT);

		if (kup && !kdown) {
			if (kleft && !kright) {
				scrolling.setAnimation(new Point2D(-TILEH, -TILEH), SCROLL_SPEED);
				scrolling.start();
			} else if (kright && !kleft) {
				scrolling.setAnimation(new Point2D(TILEH, -TILEH), SCROLL_SPEED);
				scrolling.start();
			} else if (!kleft && !kright) {
				scrolling.setAnimation(new Point2D(0, -TILEH), SCROLL_SPEED);
				scrolling.start();
			}
		} else if (kdown && !kup) {
			if (kleft && !kright) {
				scrolling.setAnimation(new Point2D(-TILEH, TILEH), SCROLL_SPEED);
				scrolling.start();
			} else if (kright && !kleft) {
				scrolling.setAnimation(new Point2D(TILEH, TILEH), SCROLL_SPEED);
				scrolling.start();
			} else if (!kleft && !kright) {
				scrolling.setAnimation(new Point2D(0, TILEH), SCROLL_SPEED);
				scrolling.start();
			}
		} else if (!kdown && !kup) {
			if (kleft && !kright) {
				scrolling.setAnimation(new Point2D(-TILEH, 0), SCROLL_SPEED);
				scrolling.start();
			} else if (kright && !kleft) {
				scrolling.setAnimation(new Point2D(TILEH, 0), SCROLL_SPEED);
				scrolling.start();
			} else {
				scrolling.stop();
			}
		} else {
			scrolling.stop();
		}
	}

	public void startAnimating() {
		animateCanvas.start();
	}

	public void stopAnimating() {
		animateCanvas.stop();
	}
}

