package isogame.editor;

import isogame.engine.CliffTexture;
import isogame.engine.CorruptDataException;
import isogame.engine.Library;
import isogame.engine.SlopeType;
import isogame.engine.TerrainTexture;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;

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
		t.setOnAction(event -> canvas.setTool(new TerrainTextureTool(tex)));
		textures.getChildren().add(t);
	}

	private void addCliffTexture(CliffTexture tex, EditorCanvas canvas) {
		Canvas preview = new Canvas(64, 32);
		GraphicsContext gc = preview.getGraphicsContext2D();
		gc.setFill(tex.getTexture(SlopeType.NONE));
		gc.fillRect(0, 0, 64, 32);
		ToggleButton t = new ToggleButton("", preview);
		t.setFocusTraversable(false);
		t.setToggleGroup(texturesGroup);
		cliffTextures.getChildren().add(t);
	}
}

