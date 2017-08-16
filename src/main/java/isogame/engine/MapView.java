/* © Callum Lowcay 2015, 2016, 2017

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import static isogame.GlobalConstants.SCROLL_SPEED;
import static isogame.GlobalConstants.TILEH;

/**
 * Draws a scrolling, rotating map.
 * */
public class MapView extends View {
	private Stage stage = null;
	private boolean enableAnimations = false;
	private BiConsumer<SelectionInfo, MouseButton> onSelection = (x, b) -> {};
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

	private final Paint[] highlightColors;

	private final boolean debugMode;

	final public KeyBindingTable keyBindings = new KeyBindingTable();

	public MapView(
		final Node root,
		final Stage stage,
		final boolean enableAnimations,
		final boolean debugMode,
		final Paint[] highlightColors
	) {
		super(960, 400);

		this.stage = stage;
		this.enableAnimations = enableAnimations;
		this.debugMode = debugMode;
		this.highlightColors = highlightColors;

		if (stage != null) stage.setHighlightColors(highlightColors);

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
					final Point2D centre = getViewportCentre();
					final MapPoint centreP = tileAtMouse(centre, this.stage);
					if (action == KeyBinding.rotateLeft) rotateLeft();
						else rotateRight();
					scrolling.setClamp(getScrollBounds(this.stage));
					centreOnTile(this.stage, centreP);
					scrolling.reset(getScrollPos());
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

		if (stage != null) scrolling.setClamp(getScrollBounds(stage));
		centreView();
	}

	private void centreView() {
		if (stage == null) return;
		centreOnTile(new MapPoint(stage.terrain.w/2, stage.terrain.h/2));
	}

	public void centreOnTile(final MapPoint tile) {
		if (stage == null) return;
		this.centreOnTile(stage, tile);
		scrolling.reset(this.getScrollPos());
	}

	private AnimationTimer animateCanvas = new AnimationTimer() {
		long count = 1;
		long target = 0;

		private final long targetDuration = 16666666l;

		@Override
		public void handle(final long now) {
			if (target == 0) target = now;

			if (stage != null && (target - now) < targetDuration) {
				setScrollPos(scrolling.valueAt(target));
				update(enableAnimations? target : 0, stage);
				while (target <= now) target = target + targetDuration;
			}
		}
	};

	MapPoint p0 = null;
	MapPoint sprite0 = null;

	public void resetMouseHandlers() {
		p0 = null;
		sprite0 = null;
	}

	private EventHandler<MouseEvent> mouseHandler =
		new EventHandler<MouseEvent>() {

			@Override
			public void handle(final MouseEvent event) {
				if (stage == null) return;

				final EventType etype = event.getEventType();
				if (etype == MouseEvent.MOUSE_MOVED ||
					etype == MouseEvent.MOUSE_DRAGGED
				) {
					final MapPoint p = tileAtMouse(
						new Point2D(event.getX(), event.getY()), stage);

					if (p != p0) {
						p0 = p;
						if (p == null) {
							onMouseOut.run();
						} else {
							onMouseOver.accept(p);
							if (event.isPrimaryButtonDown() && selectable.contains(p)) {
								onSelection.accept(new SelectionInfo(Optional.of(p), Optional.empty()), event.getButton());
							}
						}
					}

					final MapPoint sprite = spriteAtMouse(
						new Point2D(event.getX(), event.getY()), stage);

					if (sprite0 != sprite) {
						sprite0 = sprite;
						if (sprite == null) {
							onMouseOutSprite.run();
						} else {
							if (mouseOverSprites.stream().anyMatch(s -> s.getPos().equals(sprite))) {
								onMouseOverSprite.accept(sprite);
							}
							if (event.isPrimaryButtonDown() && selectableSprites.contains(sprite)) {
								onSelection.accept(new SelectionInfo(Optional.empty(), Optional.of(sprite)), event.getButton());
							}
						}
					}

				} else if (etype == MouseEvent.MOUSE_PRESSED) {
					final MapPoint p = tileAtMouse(
						new Point2D(event.getX(), event.getY()), stage);

					final MapPoint ps = spriteAtMouse(
						new Point2D(event.getX(), event.getY()), stage);

					final boolean hasPoint = p != null && (selectable.contains(p) ||
						selectableSprites.stream().anyMatch(s -> s.getPos().equals(p)));

					final boolean hasSprite = ps != null &&
						selectableSprites.stream().anyMatch(s -> s.getPos().equals(ps));

					onSelection.accept(new SelectionInfo(
						Optional.ofNullable(hasPoint? p : null),
						Optional.ofNullable(hasSprite? ps : null)), event.getButton());
				}
			}
		};

	boolean kup = false;
	boolean kdown = false;
	boolean kleft = false;
	boolean kright = false;
	private void setScrollKey(final KeyBinding action, final boolean v) {
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

	public void setStage(final Stage stage) {
		this.stage = stage;

		if (stage == null) return;

		stage.invalidate();
		setSelectable(new ArrayList<>());
		stage.setHighlightColors(highlightColors);
		scrolling.setClamp(getScrollBounds(stage));
		centreView();
	}

	public Stage getStage() {
		return stage;
	}

	public void setHighlight(
		final Collection<MapPoint> pts, final int priority
	) {
		if (stage == null) return;

		stage.clearHighlighting(priority);
		for (final MapPoint p : pts) stage.setHighlight(p, priority);
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

	public void setSelectable(final Collection<MapPoint> pts) {
		if (stage == null) return;

		selectable.clear();
		selectable.addAll(pts);
	}

	public void setSelectableSprites(final Collection<Sprite> pts) {
		if (stage == null) return;

		selectableSprites.clear();
		selectableSprites.addAll(pts);
	}

	public void setMouseOverSprites(final Collection<Sprite> sprites) {
		if (stage == null) return;
		
		mouseOverSprites.clear();
		mouseOverSprites.addAll(sprites);
	}

	public boolean isSelectable(final MapPoint p) {
		return selectable.contains(p);
	}

	public void doOnSelection(final BiConsumer<SelectionInfo, MouseButton> c) {
		this.onSelection = c;
	}

	public void doOnMouseOver(final Consumer<MapPoint> c) {
		this.onMouseOver = c;
	}

	public void doOnMouseOverSprite(final Consumer<MapPoint> c) {
		this.onMouseOverSprite = c;
	}

	public void doOnKeyPressed(final Consumer<KeyBinding> c) {
		this.onKeyPressed = c;
	}

	public void doOnKeyReleased(final Consumer<KeyBinding> c) {
		this.onKeyReleased = c;
	}

	public void doOnMouseOutSprite(final Runnable c) {
		this.onMouseOutSprite = c;
	}

	public void doOnMouseOut(final Runnable r) {
		this.onMouseOut = r;
	}

	public void enableAnimations(final boolean enableAnimations) {
		this.enableAnimations = enableAnimations;
	}
}
