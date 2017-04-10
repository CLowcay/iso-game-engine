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

import isogame.engine.SpriteAnimation;
import isogame.engine.SpriteInfo;
import isogame.resource.ResourceLocator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * A dialog for creating and editing sprites
 * */
public class EditSpriteDialog extends Dialog<SpriteInfo> {
	private final TextField idField;
	private final ObservableList<String> priorities =
		FXCollections.observableArrayList(new ArrayList<>());
	private final ComboBox<String> priority = new ComboBox<>(priorities);
	private final ListView<SpriteAnimation> anims;
	private final ObservableList<SpriteAnimation> animList;
	private final Button add = new Button("Add animation");
	private final Button edit = new Button("Edit animation");
	private final Button remove = new Button("Remove");

	/**
	 * @param info The sprite to edit or null to create a new sprite
	 * */
	public EditSpriteDialog(
		File dataRoot, ResourceLocator loc,
		List<String> priorities, SpriteInfo info
	) {
		super();

		boolean isNew;
		final SpriteInfo baseInfo;
		if (info == null) {
			baseInfo = new SpriteInfo(UUID.randomUUID().toString(), 0, null);
			isNew = true;
		} else {
			baseInfo = info;
			isNew = false;
		}

		// Set up the header and footer
		if (isNew) {
			this.setTitle("New sprite");
		} else {
			this.setTitle("Edit sprite");
		}
		this.setHeaderText("Set up the sprite animations");
		this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// The dialog content
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		idField = new TextField();
		idField.setText(baseInfo.id);
		idField.addEventHandler(
			MouseEvent.MOUSE_CLICKED,
			event -> idField.selectAll());

		this.priorities.addAll(priorities);
		priority.getSelectionModel().select(baseInfo.priority);

		animList = FXCollections.observableArrayList(baseInfo.getAllAnimations());
		anims = new ListView<>(animList);

		edit.disableProperty().bind(anims.getSelectionModel().selectedItemProperty().isNull());
		remove.disableProperty().bind(anims.getSelectionModel().selectedItemProperty().isNull());

		add.setOnAction(event -> {
			(new EditSpriteAnimationDialog(dataRoot, loc, null))
				.showAndWait()
				.ifPresent(a -> animList.add(a));
		});

		edit.setOnAction(event -> {
			SpriteAnimation anim = anims.getSelectionModel().getSelectedItem();
			(new EditSpriteAnimationDialog(dataRoot, loc, anim))
				.showAndWait()
				.ifPresent(a -> animList.set(animList.indexOf(anim), a));
		});

		remove.setOnAction(event -> {
			SpriteAnimation a = anims.getFocusModel().getFocusedItem();
			if (a != null) animList.remove(a);
		});

		FlowPane buttons = new FlowPane();
		buttons.getChildren().addAll(add, edit, remove);

		VBox listGroup = new VBox();
		listGroup.getChildren().addAll(anims, buttons);

		grid.addRow(0, new Label("Sprite name"), idField);
		grid.addRow(1, new Label("Layer"), priority);

		grid.add(listGroup, 0, 2, 2, 1);

		this.getDialogPane().setContent(grid);

		this.setResultConverter(clickedButton -> {
			if (clickedButton == ButtonType.OK) {
				if (animList.size() == 0) return null;

				for (int i = 0; i < animList.size(); i++) {
					if (animList.get(i).id.equals("idle")) {
						if (i != 0) {
							final SpriteAnimation a = animList.remove(i);
							animList.add(0, a);
						}
						break;
					}
				}

				final int p = priority.getSelectionModel().getSelectedIndex();

				final Iterator<SpriteAnimation> it = animList.iterator();
				if (!it.hasNext()) return null;
				final SpriteInfo i = new SpriteInfo(idField.getText(), p, it.next());
				while (it.hasNext()) {
					i.addAnimation(it.next());
				}
				return i;
			} else {
				return null;
			}
		});
	}
}

