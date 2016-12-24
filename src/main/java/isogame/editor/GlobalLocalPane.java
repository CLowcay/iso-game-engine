/* © Callum Lowcay 2015, 2016

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

