package isogame.editor;

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
	
	public ToolBar(EditorCanvas canvas, ToggleGroup group) {
		super();

		pointer.setTooltip(new Tooltip("Pointer tool"));
		delete.setTooltip(new Tooltip("Delete sprites"));
		rotate.setTooltip(new Tooltip("Rotate sprites"));

		pointer.setFocusTraversable(false);
		delete.setFocusTraversable(false);
		rotate.setFocusTraversable(false);

		pointer.setToggleGroup(group);
		delete.setToggleGroup(group);
		rotate.setToggleGroup(group);

		pointer.setOnAction(event -> canvas.setTool(null));

		delete.setOnAction(event -> {
			if (delete.isSelected()) canvas.setTool(new RemoveSpriteTool());
			else canvas.setTool(null);
		});

		rotate.setOnAction(event -> {
			if (rotate.isSelected()) canvas.setTool(new RotateSpriteTool());
			else canvas.setTool(null);
		});

		this.getChildren().addAll(pointer, delete, rotate);
	}
}

