package isogame.editor;

import isogame.engine.SpriteAnimation;
import isogame.engine.SpriteInfo;
import isogame.resource.ResourceLocator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.io.File;
import java.util.Iterator;
import java.util.UUID;

/**
 * A dialog for creating and editing sprites
 * */
public class EditSpriteDialog extends Dialog<SpriteInfo> {
	private final TextField idField;
	private final ListView<SpriteAnimation> anims;
	private final ObservableList<SpriteAnimation> animList;
	private final Button add = new Button("Add animation");
	private final Button edit = new Button("Edit animation");
	private final Button remove = new Button("Remove");

	/**
	 * @param info The sprite to edit or null to create a new sprite
	 * */
	public EditSpriteDialog(File dataRoot, ResourceLocator loc, SpriteInfo info) {
		super();

		boolean isNew;
		final SpriteInfo baseInfo;
		if (info == null) {
			baseInfo = new SpriteInfo(UUID.randomUUID().toString(), null);
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

		grid.add(new Label("Sprite name"), 0, 0);
		grid.add(idField, 1, 0);

		grid.add(listGroup, 0, 1, 2, 1);

		this.getDialogPane().setContent(grid);

		this.setResultConverter(clickedButton -> {
			if (clickedButton == ButtonType.OK) {
				if (animList.size() == 0) return null;

				Iterator<SpriteAnimation> it = animList.iterator();
				if (!it.hasNext()) return null;
				SpriteInfo i = new SpriteInfo(idField.getText(), it.next());
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

