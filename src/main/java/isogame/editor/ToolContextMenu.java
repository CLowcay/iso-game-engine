package isogame.editor;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class ToolContextMenu extends ContextMenu {
	public ToolContextMenu(boolean isGlobal) {
		super();

		MenuItem global = new MenuItem("Make global");
		MenuItem delete = new MenuItem("Delete");

		if (isGlobal) {
			this.getItems().addAll(delete);
		} else {
			this.getItems().addAll(global, delete);
		}
	}
}

