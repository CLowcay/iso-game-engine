package isogame.editor;

import isogame.engine.CliffTexture;
import isogame.engine.ContinuousAnimator;
import isogame.engine.CorruptDataException;
import isogame.engine.Library;
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
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import static isogame.GlobalConstants.SCROLL_SPEED;
import static isogame.GlobalConstants.TILEH;

public class EditorCanvas extends Pane {
	private final AnimationTimer animateCanvas;
	private Tool tool = null;
	private final View view;
	private final Window window;
	private final Paint[] highlightColors =
		new Paint[] {Color.rgb(0x00, 0x00, 0xFF, 0.2)};

	boolean saved = true;
	Stage stage = null;
	File stageFile = null;
	Library localLibrary = null;

	/**
	 * Load a stage from a file.
	 * */
	public void loadStage(LibraryPane library, File dataDir) {
		if (promptSaveContinue(library, dataDir)) {
			FileChooser fc = new FileChooser();
			fc.setTitle("Open map file");
			fc.setInitialDirectory(dataDir);
			fc.getExtensionFilters().addAll(new ExtensionFilter("Map Files", "*.map"));
			File r = fc.showOpenDialog(window);
			if (r != null) {
				try {
					Library lib = library.loadLocalLibrary(r);
					try (BufferedReader in =
						new BufferedReader(
						new InputStreamReader(
						new FileInputStream(r), "UTF-8")))
					{
						JSONParser parser = new JSONParser();
						JSONObject json = (JSONObject) parser.parse(in);
						Object stagejson = json.get("stage");
						if (stagejson == null) throw new CorruptDataException(
							"Error in map file, missing stage");
						stage = Stage.fromJSON((JSONObject) stagejson, lib);
						stage.setHighlightColors(highlightColors);
						view.centreOnTile(stage, new MapPoint(3, 3));
						stageFile = r;
						localLibrary = lib;
						saved = true;
						tool = null;
					}
				} catch (IOException e) {
					Alert d = new Alert(Alert.AlertType.ERROR);
					d.setHeaderText("Cannot read file " + r.toString());
					d.setContentText(e.toString());
					d.show();
				} catch (CorruptDataException e) {
					Alert d = new Alert(Alert.AlertType.ERROR);
					d.setHeaderText("Error in file " + r.toString());
					d.setContentText(e.toString());
					d.show();
				} catch (ParseException e) {
					Alert d = new Alert(Alert.AlertType.ERROR);
					d.setHeaderText("Error in file " + r.toString());
					d.setContentText(e.toString());
					d.show();
				} catch (ClassCastException e) {
					Alert d = new Alert(Alert.AlertType.ERROR);
					d.setHeaderText("Error in file " + r.toString());
					d.setContentText(e.toString());
					d.show();
				}
			}
		}
	}

	/**
	 * Save the current stage.
	 * */
	public void saveStage(File dataDir) {
		if (saved || stage == null) return;
		if (stageFile == null) {
			saveStageAs(dataDir);
		} else {
			try {
				localLibrary.writeToStream(new FileOutputStream(stageFile), stage);
				saved = true;
			} catch (IOException e) {
				Alert d = new Alert(Alert.AlertType.ERROR);
				d.setHeaderText("Cannot save file as " + stageFile.toString());
				d.setContentText(e.toString());
				d.show();
			}
		}
	}

	/**
	 * Save the current stage under a new name.
	 * */
	public void saveStageAs(File dataDir) {
		if (saved || stage == null) return;

		FileChooser fc = new FileChooser();
		fc.setTitle("Save map file");
		fc.setInitialDirectory(dataDir);
		fc.getExtensionFilters().addAll(new ExtensionFilter("Map Files", "*.map"));
		File r = fc.showSaveDialog(window);
		if (r != null) {
			// automatically append .map if the user didn't give an extension
			String name = r.getName();
			if (name.lastIndexOf('.') == -1) {
				File p = r.getAbsoluteFile().getParentFile();
				r = new File(p, name + ".map");
			}
			stageFile = r;
			saveStage(dataDir);
		}
	}

	/**
	 * Prompt the user to save before closing a stage.
	 * @return true if the close action should continue, otherwise false.
	 * */
	public boolean promptSaveContinue(LibraryPane library, File dataDir) {
		if (saved) {
			return true;
		} else {
			Alert d = new Alert(Alert.AlertType.CONFIRMATION, null, 
				ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
			d.setHeaderText("Save first?");

			Optional<ButtonType> r = d.showAndWait();
			if (!r.isPresent()) {
				return false;
			} else {
				ButtonType bt = r.get();
				if (bt == ButtonType.YES) {
					saveStage(dataDir);
					return true;
				} else if (bt == ButtonType.NO) {
					return true;
				} else {
					return false;
				}
			}
		}
	}

	/**
	 * Make a new stage.
	 * */
	public void newStage(LibraryPane library, File dataDir) {
		if (!promptSaveContinue(library, dataDir)) return;

		try {
			(new NewMapDialog(library.getGlobalLibrary().getTerrain("blank")))
				.showAndWait()
				.ifPresent(terrain -> {
					stage = new Stage(terrain);
					stageFile = null;
					saved = false;
					localLibrary = library.newLocalLibrary();
					stage.setHighlightColors(highlightColors);
					view.centreOnTile(stage, new MapPoint(3, 3));
				});
		} catch (CorruptDataException e) {
			Alert d = new Alert(Alert.AlertType.ERROR);
			d.setHeaderText("Cannot create map");
			d.setContentText(
				"You may be missing some textures.\n\nException was:\n" +
				e.toString());
			d.show();
			stage = null;
			stageFile = null;
			saved = true;
			localLibrary = null;
		}
	}

	public EditorCanvas(Node root, Window window) throws CorruptDataException {
		super();
		this.setFocusTraversable(true);

		this.window = window;

		Canvas canvas = new Canvas();
		this.getChildren().add(canvas);
		canvas.widthProperty().bind(this.widthProperty());
		canvas.heightProperty().bind(this.heightProperty());

		view = new View(960, 540);

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
					now0 = now0 + 5000000000l;
					count0 = count;
				}

				if (stage != null) {
					view.setScrollPos(scrolling.valueAt(now));
					view.renderFrame(cx, stage);
				}
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
		root.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
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
						stage.clearAllHighlighting();
						if (p != null) {
							stage.setHighlight(p, 0);

							if (event.isPrimaryButtonDown() && tool != null) {
								tool.apply(p, stage);
							}
						}
					}
				} else if (etype == MouseEvent.MOUSE_PRESSED) {
					MapPoint p = view.tileAtMouse(
						new Point2D(event.getX(), event.getY()), stage);

					if (p != null && tool != null) tool.apply(p, stage);
				}
			}
		});
	}

	public void setTool(Tool tool) {
		this.tool = tool;
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

