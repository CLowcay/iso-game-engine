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

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.HashSet;
import java.util.Set;
import static isogame.GlobalConstants.SCROLL_SPEED;
import static isogame.GlobalConstants.TILEH;

/**
 * Draws a scrolling, rotating map.
 * */
public class MapView extends Canvas {
	private Stage stage = null;
	private boolean enableAnimations = false;
	private BiConsumer<MapPoint, MouseButton> onSelection = (x, b) -> {};
	private Consumer<MapPoint> onMouseOver = x -> {};
	private Consumer<MapPoint> onMouseOverSprite = x -> {};
	private Consumer<KeyBinding> onKeyPressed = x -> {};
	private Consumer<KeyBinding> onKeyReleased = x -> {};
	private Runnable onMouseOutSprite = () -> {};
	private Runnable onMouseOut = () -> {};

	private int selectionHighlighter = 0;

	private final ContinuousAnimator scrolling = new ContinuousAnimator();
	private final Set<MapPoint> selectable = new HashSet<>();
	private final Set<Sprite> selectableSprites = new HashSet<>();
	private final Set<Sprite> mouseOverSprites = new HashSet<>();

	private final Highlighter[] highlightColors;

	private final GraphicsContext cx;

	public final View view = new View(960, 400);

	private final boolean debugMode;

	final public KeyBindingTable keyBindings = new KeyBindingTable();

	public MapView(
		Node root,
		Stage stage,
		boolean enableAnimations,
		boolean debugMode,
		Highlighter[] highlightColors
	) {
		super();
		cx = this.getGraphicsContext2D();

		this.stage = stage;
		this.enableAnimations = enableAnimations;
		this.debugMode = debugMode;
		this.highlightColors = highlightColors;

		if (stage != null) stage.setHighlightColors(highlightColors);

		this.widthProperty().addListener((obs, w0, w) -> {
			view.setViewport(w.intValue(), (int) this.getHeight());
		});
		this.heightProperty().addListener((obs, h0, h) -> {
			view.setViewport((int) this.getWidth(), h.intValue());
		});

		// set the default keys
		keyBindings.keys.put(new KeyCodeCombination(KeyCode.UP), KeyBinding.scrollUp);
		keyBindings.keys.put(new KeyCodeCombination(KeyCode.DOWN), KeyBinding.scrollDown);
		keyBindings.keys.put(new KeyCodeCombination(KeyCode.LEFT), KeyBinding.scrollLeft);
		keyBindings.keys.put(new KeyCodeCombination(KeyCode.RIGHT), KeyBinding.scrollRight);
		keyBindings.keys.put(new KeyCodeCombination(KeyCode.Q), KeyBinding.rotateLeft);
		keyBindings.keys.put(new KeyCodeCombination(KeyCode.E), KeyBinding.rotateRight);

		// Listen for events
		root.addEventHandler(MouseEvent.ANY, mouseHandler);
		root.setOnKeyPressed(event -> {
			keyBindings.getKeyAction(event).ifPresent(action -> {
				onKeyPressed.accept(action);
				setScrollKey(action, true);
				setScrollingAnimation();
				if (action == KeyBinding.rotateLeft || action == KeyBinding.rotateRight) {
					if (this.stage == null) return;
					final Point2D centre = view.getViewportCentre();
					final MapPoint centreP = view.tileAtMouse(centre, this.stage);
					if (action == KeyBinding.rotateLeft) view.rotateLeft(); else view.rotateRight();
					view.centreOnTile(this.stage, centreP);
					scrolling.reset(view.getScrollPos());
				}
			});
		});
		root.setOnKeyReleased(event -> {
			keyBindings.getKeyAction(event).ifPresent(action -> {
				onKeyReleased.accept(action);
				setScrollKey(action, false);
				setScrollingAnimation();
			});
		});

		centreView();
	}

	private void centreView() {
		if (stage == null) return;
		centreOnTile(new MapPoint(stage.terrain.w/2, stage.terrain.h/2));
	}

	public void centreOnTile(MapPoint tile) {
		if (stage == null) return;
		view.centreOnTile(stage, tile);
		scrolling.reset(view.getScrollPos());
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
				view.renderFrame(cx, enableAnimations? now : 0, stage, debugMode);
			}
		}
	};

	private EventHandler<MouseEvent> mouseHandler =
		new EventHandler<MouseEvent>() {
			MapPoint p0 = null;
			MapPoint sprite0 = null;

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
								onSelection.accept(p, event.getButton());
							}
						}
					}

					MapPoint sprite = view.spriteAtMouse(
						new Point2D(event.getX(), event.getY()), stage);

					if (sprite0 != sprite) {
						sprite0 = sprite;
						if (sprite == null) {
							onMouseOutSprite.run();
						} else {
							if (mouseOverSprites.stream().anyMatch(s -> s.pos.equals(sprite))) {
								onMouseOverSprite.accept(sprite);
							}
							if (event.isPrimaryButtonDown() && selectableSprites.contains(sprite)) {
								onSelection.accept(sprite, event.getButton());
							}
						}
					}

				} else if (etype == MouseEvent.MOUSE_PRESSED) {
					MapPoint p = view.tileAtMouse(
						new Point2D(event.getX(), event.getY()), stage);

					MapPoint ps = view.spriteAtMouse(
						new Point2D(event.getX(), event.getY()), stage);

					if (ps != null &&
						selectableSprites.stream().anyMatch(s -> s.pos.equals(ps))) {
						onSelection.accept(ps, event.getButton());

					} else if (p != null && (selectable.contains(p) ||
						selectableSprites.stream().anyMatch(s -> s.pos.equals(p)))) {
						onSelection.accept(p, event.getButton());

					} else {
						onSelection.accept(null, event.getButton());
					}
				}
			}
		};

	boolean kup = false;
	boolean kdown = false;
	boolean kleft = false;
	boolean kright = false;
	private void setScrollKey(KeyBinding action, boolean v) {
		if (action == KeyBinding.scrollUp) kup = v;
		else if (action == KeyBinding.scrollDown) kdown = v;
		else if (action == KeyBinding.scrollLeft) kleft = v;
		else if (action == KeyBinding.scrollRight) kright = v;
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

	public void setSelectableSprites(Collection<Sprite> pts) {
		if (stage == null) return;

		selectableSprites.clear();
		selectableSprites.addAll(pts);
	}

	public void setMouseOverSprites(Collection<Sprite> sprites) {
		if (stage == null) return;
		
		mouseOverSprites.clear();
		mouseOverSprites.addAll(sprites);
	}

	public boolean isSelectable(MapPoint p) {
		return selectable.contains(p);
	}

	public void doOnSelection(BiConsumer<MapPoint, MouseButton> c) {
		this.onSelection = c;
	}

	public void doOnMouseOver(Consumer<MapPoint> c) {
		this.onMouseOver = c;
	}

	public void doOnMouseOverSprite(Consumer<MapPoint> c) {
		this.onMouseOverSprite = c;
	}

	public void doOnKeyPressed(Consumer<KeyBinding> c) {
		this.onKeyPressed = c;
	}

	public void doOnKeyReleased(Consumer<KeyBinding> c) {
		this.onKeyReleased = c;
	}

	public void doOnMouseOutSprite(Runnable c) {
		this.onMouseOutSprite = c;
	}

	public void doOnMouseOut(Runnable r) {
		this.onMouseOut = r;
	}

	public void enableAnimations(boolean enableAnimations) {
		this.enableAnimations = enableAnimations;
	}
}
