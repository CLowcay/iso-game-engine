package isogame.editor;

import isogame.engine.AssetType;
import isogame.engine.CameraAngle;
import isogame.engine.CliffTexture;
import isogame.engine.CorruptDataException;
import isogame.engine.FacingDirection;
import isogame.engine.Library;
import isogame.engine.SlopeType;
import isogame.engine.SpriteAnimation;
import isogame.engine.SpriteInfo;
import isogame.engine.TerrainTexture;
import isogame.engine.Tile;
import isogame.GlobalConstants;
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
	private final GlobalLocalPane sprites;
	private final GlobalLocalPane textures;
	private final GlobalLocalPane cliffTextures;

	private final ScrollPane palette = new ScrollPane();

	private final Button newButton;

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
		newButton = new Button("New...");

		sprites = new GlobalLocalPane(new FlowPane(), new FlowPane());
		textures = new GlobalLocalPane(new FlowPane(), new FlowPane());
		cliffTextures = new GlobalLocalPane(new FlowPane(), new FlowPane());

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

		newButton.setDisable(true);
		newButton.setOnAction(event -> {
			Node selected = palette.getContent();
			if (selected == sprites) {
				(new EditSpriteDialog(dataRoot, null))
					.showAndWait()
					.ifPresent(sprite -> addSpriteToLibrary(sprite, false));
			} else if (selected == textures) {
				(new NewTextureDialog(dataRoot))
					.showAndWait()
					.ifPresent(tex -> addTextureToLibrary(tex, false));
			} else if (selected == cliffTextures) {
				(new NewCliffTextureDialog(dataRoot))
					.showAndWait()
					.ifPresent(tex -> addCliffTextureToLibrary(tex, false));
			}
		});

		VBox.setVgrow(palette, Priority.ALWAYS);
		palette.setPrefWidth(0);
		palette.setContent(textures);
		palette.setFitToWidth(true);

		// This is a nasty hack to prevent the scrollbars from stealing focus
		palette.focusedProperty().addListener(x -> {
			if (palette.isFocused()) canvas.requestFocus();
		});

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
		newButton.setDisable(false);
		return local;
	}

	/**
	 * Close the local library.
	 * */
	public void closeLocal() {
		local = null;
		newButton.setDisable(true);

		textures.local.getChildren().clear();
		sprites.local.getChildren().clear();
		cliffTextures.local.getChildren().clear();

		textureButtonsL.clear();
		spriteButtonsL.clear();
		cliffButtonsL.clear();
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

		newButton.setDisable(false);
		
		local.allTerrains().forEach(t -> addTexture(t, false));
		local.allSprites().forEach(s -> addSprite(s, false));
		local.allCliffTextures().forEach(t -> addCliffTexture(t, false));

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

	public void addSpriteToLibrary(SpriteInfo sprite, boolean isGlobal) {
		if (isGlobal) {
			global.addSprite(sprite);
			addSprite(sprite, isGlobal);
			saveGlobal();
		} else if (local != null) {
			local.addSprite(sprite);
			addSprite(sprite, isGlobal);
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

	public void makeTextureGlobal(String id) {
		try {
			TerrainTexture tex = local.getTerrain(id);
			deleteTexture(id);
			addTextureToLibrary(tex, true);
		} catch (CorruptDataException e) {
			throw new RuntimeException("This cannot happen", e);
		}
	}

	public void makeSpriteGlobal(String id) {
	}

	public void makeCliffTextureGlobal(String id) {
		try {
			CliffTexture tex = local.getCliffTexture(id);
			deleteCliffTexture(id);
			addCliffTextureToLibrary(tex, true);
		} catch (CorruptDataException e) {
			throw new RuntimeException("This cannot happen", e);
		}
	}

	public void deleteTexture(String id) {
		try {
			local.deleteTerrain(id);
			ToggleButton b = textureButtonsL.get(id);
			if (b != null) textures.local.getChildren().removeAll(b);
		} catch (CorruptDataException e) {
			throw new RuntimeException("This cannot happen", e);
		}
	}

	public void deleteSprite(String id) {
		try {
			local.deleteSprite(id);
			ToggleButton b = spriteButtonsL.get(id);
			if (b != null) sprites.local.getChildren().removeAll(b);
		} catch (CorruptDataException e) {
			throw new RuntimeException("This cannot happen", e);
		}
	}

	public void deleteCliffTexture(String id) {
		try {
			local.deleteCliffTexture(id);
			List<ToggleButton> bs = cliffButtonsL.get(id);
			if (bs != null) cliffTextures.local.getChildren().removeAll(bs);
		} catch (CorruptDataException e) {
			throw new RuntimeException("This cannot happen", e);
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
		if (!isGlobal) {
			t.setContextMenu(new ToolContextMenu(this, AssetType.TEXTURE, tex.id));
		}

		t.setOnAction(event -> {
			if (t.isSelected()) canvas.setTool(new TerrainTextureTool(tex));
			else canvas.setTool(null);
		});

		if (isGlobal) {
			textures.global.getChildren().add(t);
			textureButtonsG.put(tex.id, t);
		} else {
			textures.local.getChildren().add(t);
			textureButtonsL.put(tex.id, t);
		}
	}
	
	private void addSprite(SpriteInfo sprite, boolean isGlobal) {
		try {
			SpriteAnimation anim = sprite.getDefaultAnimation();

			int h = anim.h / 8;
			Canvas preview = new Canvas(64, h);
			GraphicsContext gc = preview.getGraphicsContext2D();
			gc.scale(1.0d/8.0d, 1.0d/8.0d);
			anim.renderFrame(gc,
				0, (h * 8) - (int) GlobalConstants.TILEH,
				0, CameraAngle.UL, FacingDirection.UP);

			ToggleButton t = new ToggleButton("", preview);
			t.setFocusTraversable(false);
			t.setToggleGroup(spritesGroup);
			if (!isGlobal) {
				t.setContextMenu(new ToolContextMenu(this, AssetType.SPRITE, sprite.id));
			}

			t.setOnAction(event -> {
				if (t.isSelected()) {
					canvas.setTool(new SpriteTool(sprite, FacingDirection.UP));
				} else {
					canvas.setTool(null);
				}
			});

			if (isGlobal) {
				sprites.global.getChildren().add(t);
				spriteButtonsG.put(sprite.id, t);
			} else {
				sprites.local.getChildren().add(t);
				spriteButtonsL.put(sprite.id, t);
			}
		} catch (CorruptDataException e) {
			throw new RuntimeException("This cannot happen", e);
		}
	}

	private void addCliffTexture(CliffTexture tex, boolean isGlobal) {
		Canvas n = new Canvas(64, 48);
		Canvas s = new Canvas(64, 48);
		Canvas w = new Canvas(64, 48);
		Canvas e = new Canvas(64, 48);
		Canvas up = new Canvas(64, 48);
		Canvas down = new Canvas(64, 48);

		ToolContextMenu menu = isGlobal? null :
			new ToolContextMenu(this, AssetType.CLIFF_TEXTURE, tex.id);

		ToggleButton bn = makeCliffButton(tex, n, SlopeType.N, 0, menu);
		ToggleButton bs = makeCliffButton(tex, s, SlopeType.S, 0, menu);
		ToggleButton bw = makeCliffButton(tex, w, SlopeType.W, 0, menu);
		ToggleButton be = makeCliffButton(tex, e, SlopeType.E, 0, menu);
		ToggleButton bup = makeCliffButton(tex, up, SlopeType.NONE, 1, menu);
		ToggleButton bdown = makeCliffButton(tex, down, SlopeType.NONE, 0, menu);

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
			cliffTextures.global.getChildren().addAll(bn, bs, bw, be, bup, bdown);
			cliffButtonsG.put(tex.id, Arrays.asList(bn, bs, bw, be, bup, bdown));
		} else {
			cliffTextures.local.getChildren().addAll(bn, bs, bw, be, bup, bdown);
			cliffButtonsL.put(tex.id, Arrays.asList(bn, bs, bw, be, bup, bdown));
		}
	}

	private ToggleButton makeCliffButton(
		CliffTexture tex, Canvas canvas,
		SlopeType slope, int elevation, ToolContextMenu menu
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
		if (menu != null) {
			t.setContextMenu(menu);
		}
		return t;
	}
}

