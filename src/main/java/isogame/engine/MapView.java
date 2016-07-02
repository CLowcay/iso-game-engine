package isogame.engine;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.HashSet;
import java.util.Set;
import static isogame.GlobalConstants.SCROLL_SPEED;
import static isogame.GlobalConstants.TILEH;

/**
 * Draws a scrolling, rotating map.  (Shouldn't this be part of the game engine?)
 * */
public class MapView extends Canvas {
	private Stage stage = null;
	private boolean enableAnimations = false;
	private Consumer<MapPoint> onSelection = x -> {};
	private Consumer<MapPoint> onMouseOver = x -> {};
	private Runnable onMouseOut = () -> {};

	private int selectionHighlighter = 0;

	private final ContinuousAnimator scrolling = new ContinuousAnimator();
	private final Set<MapPoint> selectable = new HashSet<>();

	private final Paint[] highlightColors;

	private final GraphicsContext cx;

	public final View view = new View(960, 400);

	public MapView(
		Node root,
		Stage stage,
		boolean enableAnimations,
		Paint[] highlightColors
	) {
		super();
		cx = this.getGraphicsContext2D();

		this.stage = stage;
		this.enableAnimations = enableAnimations;
		this.highlightColors = highlightColors;

		this.widthProperty().addListener((obs, w0, w) -> {
			view.setViewport(w.intValue(), (int) this.getHeight());
		});
		this.heightProperty().addListener((obs, h0, h) -> {
			view.setViewport((int) this.getWidth(), h.intValue());
		});

		// Listen for events
		root.addEventHandler(MouseEvent.ANY, mouseHandler);
		root.setOnKeyPressed(event -> {
			KeyCode k = event.getCode();
			setScrollKey(k, true);
			setScrollingAnimation();
			switch (k) {
				case A: view.rotateLeft(); break;
				case D: view.rotateRight(); break;
			}
		});
		root.setOnKeyReleased(event -> {
			setScrollKey(event.getCode(), false);
			setScrollingAnimation();
		});

		centreView();
		scrolling.reset(view.getScrollPos());
	}

	private void centreView() {
		if (stage == null) return;

		view.centreOnTile(stage, new MapPoint(
			stage.terrain.w/2, stage.terrain.h/2));
	}

	private AnimationTimer animateCanvas = new AnimationTimer() {
		int count0 = 0;
		int count = 0;
		long now0 = 0;

		@Override
		public void handle(long now) {
			count++;
			if (now0 == 0) now0 = now;
			if ((now - now0) >= 5000000000l) {
				System.err.println("fps: " + ((count - count0) / 5));
				now0 = now0 + 5000000000l;
				count0 = count;
			}

			if (stage != null) {
				view.setScrollPos(scrolling.valueAt(now));
				view.renderFrame(cx, enableAnimations? now : 0, stage, true);
			}
		}
	};

	private EventHandler<MouseEvent> mouseHandler =
		new EventHandler<MouseEvent>() {
			MapPoint p0 = null;

			@Override
			public void handle(MouseEvent event) {
				if (stage == null) return;

				EventType etype = event.getEventType();
				if (etype == MouseEvent.MOUSE_MOVED ||
					etype == MouseEvent.MOUSE_DRAGGED
				) {
					MapPoint p = view.tileAtMouse(
						new Point2D(event.getX(), event.getY()), stage);

					if (p != p0) {
						p0 = p;
						if (p == null) {
							onMouseOut.run();
						} else {
							onMouseOver.accept(p);
							if (event.isPrimaryButtonDown() && selectable.contains(p)) {
								onSelection.accept(p);
							}
						}
					}
				} else if (etype == MouseEvent.MOUSE_PRESSED) {
					MapPoint p = view.tileAtMouse(
						new Point2D(event.getX(), event.getY()), stage);

					if (p != null && selectable.contains(p)) onSelection.accept(p);
				}
			}
		};

	boolean kup = false;
	boolean kdown = false;
	boolean kleft = false;
	boolean kright = false;
	private void setScrollKey(KeyCode key, boolean v) {
		switch (key) {
			case UP: kup = v; break;
			case DOWN: kdown = v; break;
			case LEFT: kleft = v; break;
			case RIGHT: kright = v; break;
		}
	}

	private void setScrollingAnimation() {
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

	public void setStage(Stage stage) {
		this.stage = stage;

		if (stage == null) return;
		
		setSelectable(new ArrayList<>());
		stage.setHighlightColors(highlightColors);
		centreView();
	}

	public Stage getStage() {
		return stage;
	}

	public void setHighlight(Collection<MapPoint> pts, int priority) {
		if (stage == null) return;

		stage.clearHighlighting(priority);
		for (MapPoint p : pts) stage.setHighlight(p, priority);
	}

	public void setAllSelectable() {
		if (stage == null) return;

		selectable.clear();
		for (int x = 0; x < stage.terrain.w; x++) {
			for (int y = 0; y < stage.terrain.h; y++) {
				selectable.add(new MapPoint(x, y));
			}
		}
	}

	public void setSelectable(Collection<MapPoint> pts) {
		if (stage == null) return;

		selectable.clear();
		selectable.addAll(pts);
	}

	public void doOnSelection(Consumer<MapPoint> c) {
		this.onSelection = c;
	}

	public void doOnMouseOver(Consumer<MapPoint> c) {
		this.onMouseOver = c;
	}

	public void doOnMouseOut(Runnable r) {
		this.onMouseOut = r;
	}

	public void enableAnimations(boolean enableAnimations) {
		this.enableAnimations = enableAnimations;
	}
}


