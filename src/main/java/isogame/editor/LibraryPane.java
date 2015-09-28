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
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
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

public class LibraryPane extends VBox {
	private FlowPane sprites = new FlowPane();
	private FlowPane textures = new FlowPane();
	private FlowPane cliffTextures = new FlowPane();
	private ScrollPane palette = new ScrollPane();

	ToggleGroup spritesGroup = new ToggleGroup();
	ToggleGroup texturesGroup = new ToggleGroup();
	ToggleGroup cliffsGroup = new ToggleGroup();

	private Library global;
	private Library local = null;

	public Library getGlobalLibrary() {
		return global;
	}

	public LibraryPane(File dataRoot, EditorCanvas canvas)
		throws IOException, CorruptDataException
	{
		super();
		this.setFocusTraversable(false);

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
					.ifPresent(tex -> addTexture(tex, canvas));
			} else if (selected == cliffTextures) {
				(new NewCliffTextureDialog(dataRoot))
					.showAndWait()
					.ifPresent(tex -> addCliffTexture(tex, canvas));
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

		loadGlobalLibrary((new File(dataRoot, "global_library.json")), canvas);
	}

	/**
	 * Save the global library.  The local library is saved with the stage.
	 * */
	public void save(String filename) throws IOException {
		global.writeToStream(new FileOutputStream(filename));
	}

	/**
	 * To be called once at the time when this object is constructed.
	 * */
	private void loadGlobalLibrary(File filename, EditorCanvas canvas)
		throws IOException, CorruptDataException
	{
		global = new Library(new FileInputStream(filename), filename.toString());

		global.allTerrains().forEach(t -> addTexture(t, canvas));
		global.allCliffTextures().forEach(t -> addCliffTexture(t, canvas));
	}

	private void addTexture(TerrainTexture tex, EditorCanvas canvas) {
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
		textures.getChildren().add(t);
	}

	private void addCliffTexture(CliffTexture tex, EditorCanvas canvas) {
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

		cliffTextures.getChildren().add(bn);
		cliffTextures.getChildren().add(bs);
		cliffTextures.getChildren().add(bw);
		cliffTextures.getChildren().add(be);
		cliffTextures.getChildren().add(bup);
		cliffTextures.getChildren().add(bdown);
	}

	private ToggleButton makeCliffButton(
		CliffTexture tex, Canvas canvas, SlopeType slope, int elevation
	) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.translate(0, (1 - elevation) * 16);
		gc.scale(1.0d/8.0d, 1.0d/8.0d);
		try {
			(new Tile(elevation, slope, global.getTerrain("black"), tex))
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

