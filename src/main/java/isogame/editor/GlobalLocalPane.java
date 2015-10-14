package isogame.editor;

import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

public class GlobalLocalPane extends VBox {
	public final Pane global;
	public final Pane local;

	GlobalLocalPane(Pane global, Pane local) {
		super();

		this.global = global;
		this.local = local;

		global.setFocusTraversable(false);
		local.setFocusTraversable(false);

		Node l1 = new Label("Global");
		Node l2 = new Label("Local");
		Node v1 = new VBox(l1, global);
		Node v2 = new Separator(Orientation.HORIZONTAL);
		Node v3 = new VBox(l2, local);

		this.getChildren().addAll(v1, v2, v3);

		this.setFocusTraversable(false);
		l1.setFocusTraversable(false);
		l2.setFocusTraversable(false);
		v1.setFocusTraversable(false);
		v2.setFocusTraversable(false);
		v3.setFocusTraversable(false);
	}
}

