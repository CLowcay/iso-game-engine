package isogame.editor;

import isogame.engine.StartZoneType;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

public class ToolBar extends FlowPane {
	private final ToggleButton pointer =
		new ToggleButton("", new ImageView("editor_assets/pointer.png"));
	private final ToggleButton delete =
		new ToggleButton("", new ImageView("editor_assets/cross.png"));
	private final ToggleButton rotate =
		new ToggleButton("", new ImageView("editor_assets/rotate.png"));
	private final ToggleButton start1 =
		new ToggleButton("", new ImageView("editor_assets/1.png"));
	private final ToggleButton start2 =
		new ToggleButton("", new ImageView("editor_assets/2.png"));
	private final ToggleButton mana =
		new ToggleButton("", new ImageView("editor_assets/M.png"));
	private final ToggleButton specialDelete =
		new ToggleButton("", new ImageView("editor_assets/scross.png"));
	
	public ToolBar(EditorCanvas canvas, ToggleGroup group) {
		super();

		pointer.setTooltip(new Tooltip("Pointer tool"));
		delete.setTooltip(new Tooltip("Delete sprites"));
		rotate.setTooltip(new Tooltip("Rotate sprites"));
		start1.setTooltip(new Tooltip("Player 1 start zone"));
		start2.setTooltip(new Tooltip("Player 2 start zone"));
		mana.setTooltip(new Tooltip("Mana zone"));
		specialDelete.setTooltip(new Tooltip("Delete start zones and mana zones"));

		pointer.setFocusTraversable(false);
		delete.setFocusTraversable(false);
		rotate.setFocusTraversable(false);
		start1.setFocusTraversable(false);
		start2.setFocusTraversable(false);
		mana.setFocusTraversable(false);
		specialDelete.setFocusTraversable(false);

		pointer.setToggleGroup(group);
		delete.setToggleGroup(group);
		rotate.setToggleGroup(group);
		start1.setToggleGroup(group);
		start2.setToggleGroup(group);
		mana.setToggleGroup(group);
		specialDelete.setToggleGroup(group);

		pointer.setOnAction(event -> canvas.setTool(null));

		delete.setOnAction(event -> {
			if (delete.isSelected()) canvas.setTool(new RemoveSpriteTool());
			else canvas.setTool(null);
		});

		rotate.setOnAction(event -> {
			if (rotate.isSelected()) canvas.setTool(new RotateSpriteTool());
			else canvas.setTool(null);
		});

		start1.setOnAction(event -> {
			if (start1.isSelected()) canvas.setTool(new StartZoneTool(StartZoneType.PLAYER));
			else canvas.setTool(null);
		});

		start2.setOnAction(event -> {
			if (start2.isSelected()) canvas.setTool(new StartZoneTool(StartZoneType.AI));
			else canvas.setTool(null);
		});

		mana.setOnAction(event -> {
			if (mana.isSelected()) canvas.setTool(new ManaZoneTool());
			else canvas.setTool(null);
		});

		specialDelete.setOnAction(event -> {
			if (specialDelete.isSelected()) canvas.setTool(new DeleteSpecialTool());
			else canvas.setTool(null);
		});

		this.getChildren().addAll(pointer, delete, rotate,
			start1, start2, mana, specialDelete);
	}
}

