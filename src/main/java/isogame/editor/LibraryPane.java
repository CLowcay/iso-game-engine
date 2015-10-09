package isogame.editor;

import isogame.engine.CameraAngle;
import isogame.engine.CliffTexture;
import isogame.engine.CorruptDataException;
import isogame.engine.Library;
import isogame.engine.SlopeType;
import isogame.engine.TerrainTexture;
import isogame.engine.Tile;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryPane extends VBox {
	private final FlowPane spritesG = new FlowPane();
	private final FlowPane spritesL = new FlowPane();
	private final FlowPane texturesG = new FlowPane();
	private final FlowPane texturesL = new FlowPane();
	private final FlowPane cliffTexturesG = new FlowPane();
	private final FlowPane cliffTexturesL = new FlowPane();

	private final Accordion sprites = new Accordion();
	private final Accordion textures = new Accordion();
	private final Accordion cliffTextures = new Accordion();

	private final ScrollPane palette = new ScrollPane();

	private final ToggleGroup spritesGroup = new ToggleGroup();
	private final ToggleGroup texturesGroup = new ToggleGroup();
	private final ToggleGroup cliffsGroup = new ToggleGroup();

	private final Map<String, ToggleButton> textureButtonsG = new HashMap<>();
	private final Map<String, ToggleButton> spriteButtonsG = new HashMap<>();
	private final Map<String, List<ToggleButton>> cliffButtonsG = new HashMap<>();
	private final Map<String, ToggleButton> textureButtonsL = new HashMap<>();
	private final Map<String, ToggleButton> spriteButtonsL = new HashMap<>();
	private final Map<String, List<ToggleButton>> cliffButtonsL = new HashMap<>();

	private final File globalLibraryFile;
	private final Library global;
	private Library local = null;

	private final EditorCanvas canvas;

	public Library getGlobalLibrary() {
		return global;
	}

	public LibraryPane(File dataRoot, EditorCanvas canvas)
		throws IOException, CorruptDataException
	{
		super();
		this.setFocusTraversable(false);
		this.canvas = canvas;

		HBox header = new HBox();
		ToggleButton selectTextures = new ToggleButton("Textures");
		selectTextures.setSelected(true);
		ToggleButton selectSprites = new ToggleButton("Sprites");
		ToggleButton selectCliffs = new ToggleButton("Cliffs");
		ToggleGroup headerButtons = new ToggleGroup();
		selectTextures.setToggleGroup(headerButtons);
		selectSprites.setToggleGroup(headerButtons);
		selectCliffs.setToggleGroup(headerButtons);
		Button newButton = new Button("New...");

		sprites.getPanes().addAll(
			new TitledPane("Global", spritesG),
			new TitledPane("Local", spritesL));
		textures.getPanes().addAll(
			new TitledPane("Global", texturesG),
			new TitledPane("Local", texturesL));
		cliffTextures.getPanes().addAll(
			new TitledPane("Global", cliffTexturesG),
			new TitledPane("Local", cliffTexturesL));

		selectSprites.setOnAction(event -> {
			if (!selectSprites.isSelected()) selectSprites.setSelected(true);
			else palette.setContent(sprites);
		});

		selectTextures.setOnAction(event -> {
			if (!selectTextures.isSelected()) selectTextures.setSelected(true);
			palette.setContent(textures);
		});

		selectCliffs.setOnAction(event -> {
			if (!selectCliffs.isSelected()) selectCliffs.setSelected(true);
			palette.setContent(cliffTextures);
		});

		newButton.setOnAction(event -> {
			Node selected = palette.getContent();
			if (selected == sprites) {
			} else if (selected == textures) {
				(new NewTextureDialog(dataRoot))
					.showAndWait()
					.ifPresent(tex -> addTextureToLibrary(tex, local == null));
			} else if (selected == cliffTextures) {
				(new NewCliffTextureDialog(dataRoot))
					.showAndWait()
					.ifPresent(tex -> addCliffTextureToLibrary(tex, local == null));
			}
		});

		VBox.setVgrow(palette, Priority.ALWAYS);
		palette.setPrefWidth(0);
		palette.setContent(textures);
		palette.setFitToWidth(true);

		selectTextures.setFocusTraversable(false);
		selectSprites.setFocusTraversable(false);
		selectCliffs.setFocusTraversable(false);
		newButton.setFocusTraversable(false);
		palette.setFocusTraversable(false);
		sprites.setFocusTraversable(false);
		textures.setFocusTraversable(false);
		cliffTextures.setFocusTraversable(false);

		header.getChildren().addAll(
			selectTextures, selectSprites, selectCliffs, newButton);

		this.getChildren().addAll(header, palette);

		globalLibraryFile = new File(dataRoot, "global_library.json");
		global = new Library(
			new FileInputStream(globalLibraryFile),
			globalLibraryFile.toString(), null);

		global.allTerrains().forEach(t -> addTexture(t, true));
		global.allCliffTextures().forEach(t -> addCliffTexture(t, true));
	}

	public Library newLocalLibrary() {
		local = new Library(global);
		return local;
	}

	/**
	 * Close the local library.
	 * */
	public void closeLocal() {
		local = null;
		// TODO: clean up local library
	}

	/**
	 * Save the global library.  The local library is saved with the stage.
	 * */
	private void saveGlobal() {
		try {
			global.writeToStream(new FileOutputStream(globalLibraryFile));
		} catch (IOException e) {
			Alert d = new Alert(Alert.AlertType.ERROR);
			d.setHeaderText("Cannot save global library to " +
				globalLibraryFile.toString());
			d.setContentText(e.toString());
			d.show();
		}
	}

	/**
	 * To be called when loading a new map file.
	 * */
	public Library loadLocalLibrary(File filename)
		throws IOException, CorruptDataException
	{
		closeLocal();
		local = new Library(
			new FileInputStream(filename),
			filename.toString(), global);
		// TODO: create new buttons
		return local;
	}

	public void addTextureToLibrary(TerrainTexture tex, boolean isGlobal) {
		if (isGlobal) {
			global.addTerrain(tex);
			addTexture(tex, isGlobal);
			saveGlobal();
		} else if (local != null) {
			local.addTerrain(tex);
			addTexture(tex, isGlobal);
		}
	}

	public void addCliffTextureToLibrary(CliffTexture tex, boolean isGlobal) {
		if (isGlobal) {
			global.addCliffTexture(tex);
			addCliffTexture(tex, isGlobal);
			saveGlobal();
		} else if (local != null) {
			local.addCliffTexture(tex);
			addCliffTexture(tex, isGlobal);
		}
	}

	private void addTexture(TerrainTexture tex, boolean isGlobal) {
		Canvas preview = new Canvas(64, 32);
		GraphicsContext gc = preview.getGraphicsContext2D();
		gc.setFill(tex.evenPaint);
		gc.fillRect(0, 0, 64, 32);
		ToggleButton t = new ToggleButton("", preview);
		t.setFocusTraversable(false);
		t.setToggleGroup(texturesGroup);
		t.setOnAction(event -> {
			if (t.isSelected()) canvas.setTool(new TerrainTextureTool(tex));
			else canvas.setTool(null);
		});
		if (isGlobal) {
			texturesG.getChildren().add(t);
			textureButtonsG.put(tex.id, t);
		} else {
			texturesL.getChildren().add(t);
			textureButtonsL.put(tex.id, t);
		}
	}

	private void addCliffTexture(CliffTexture tex, boolean isGlobal) {
		Canvas n = new Canvas(64, 48);
		Canvas s = new Canvas(64, 48);
		Canvas w = new Canvas(64, 48);
		Canvas e = new Canvas(64, 48);
		Canvas up = new Canvas(64, 48);
		Canvas down = new Canvas(64, 48);

		ToggleButton bn = makeCliffButton(tex, n, SlopeType.N, 0);
		ToggleButton bs = makeCliffButton(tex, s, SlopeType.S, 0);
		ToggleButton bw = makeCliffButton(tex, w, SlopeType.W, 0);
		ToggleButton be = makeCliffButton(tex, e, SlopeType.E, 0);
		ToggleButton bup = makeCliffButton(tex, up, SlopeType.NONE, 1);
		ToggleButton bdown = makeCliffButton(tex, down, SlopeType.NONE, 0);

		bn.setOnAction(event -> {
			if (bn.isSelected()) canvas.setTool(new ElevationTool(tex, 0, SlopeType.N));
			else canvas.setTool(null);
		});
		bs.setOnAction(event -> {
			if (bs.isSelected()) canvas.setTool(new ElevationTool(tex, 0, SlopeType.S));
			else canvas.setTool(null);
		});
		bw.setOnAction(event -> {
			if (bw.isSelected()) canvas.setTool(new ElevationTool(tex, 0, SlopeType.W));
			else canvas.setTool(null);
		});
		be.setOnAction(event -> {
			if (be.isSelected()) canvas.setTool(new ElevationTool(tex, 0, SlopeType.E));
			else canvas.setTool(null);
		});
		bup.setOnAction(event -> {
			if (bup.isSelected()) canvas.setTool(new ElevationTool(tex, 1, SlopeType.NONE));
			else canvas.setTool(null);
		});
		bdown.setOnAction(event -> {
			if (bdown.isSelected()) canvas.setTool(new ElevationTool(tex, -1, SlopeType.NONE));
			else canvas.setTool(null);
		});

		if (isGlobal) {
			cliffTexturesG.getChildren().addAll(bn, bs, bw, be, bup, bdown);
			cliffButtonsG.put(tex.id, Arrays.asList(bn, bs, bw, be, bup, bdown));
		} else {
			cliffTexturesL.getChildren().addAll(bn, bs, bw, be, bup, bdown);
			cliffButtonsL.put(tex.id, Arrays.asList(bn, bs, bw, be, bup, bdown));
		}
	}

	private ToggleButton makeCliffButton(
		CliffTexture tex, Canvas canvas, SlopeType slope, int elevation
	) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.translate(0, (1 - elevation) * 16);
		gc.scale(1.0d/8.0d, 1.0d/8.0d);
		try {
			(new Tile(elevation, slope, global.getTerrain("blank"), tex))
				.render(gc, null, CameraAngle.UL);
		} catch (CorruptDataException e) {
			throw new RuntimeException("Missing blank texture");
		}

		ToggleButton t = new ToggleButton("", canvas);
		t.setFocusTraversable(false);
		t.setToggleGroup(cliffsGroup);
		return t;
	}
}

