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

import isogame.engine.CorruptDataException;
import isogame.engine.SpriteAnimation;
import isogame.gui.PositiveIntegerField;
import isogame.resource.ResourceLocator;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Dialog box to create new sprites
 * */
public class EditSpriteAnimationDialog extends Dialog<SpriteAnimation> {
	public EditSpriteAnimationDialog(
		File dataDirectory, ResourceLocator loc, SpriteAnimation anim
	) {
		super();

		// Set up the header and footer
		if (anim == null) {
			this.setTitle("New sprite animation");
		} else {
			this.setTitle("Edit sprite animation");
		}
		this.setHeaderText("Configure the animation");
		this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// The dialog content
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField id = new TextField();
		if (anim == null) {
			id.setText(UUID.randomUUID().toString());
		} else {
			id.setText(anim.id);
		}
		id.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> id.selectAll());

		TextField spriteFile = new TextField();
		spriteFile.setEditable(false);

		Button browse = new Button("Browse...");
		browse.setOnAction(event -> {
			FileChooser fc = new FileChooser();
			fc.setTitle("Browse for sprite image");
			fc.setInitialDirectory(dataDirectory);
			fc.getExtensionFilters().addAll(new ExtensionFilter("Graphics files",
				"*.png", "*.PNG", "*.jpg", "*.jpeg",
				"*.JPG", "*.JPEG", "*.bmp", "*.BMP"));
			File r = fc.showOpenDialog(this.getOwner());
			if (r != null) {
				spriteFile.setText(
					dataDirectory.toPath().relativize(r.toPath()).toString());
			}
		});

		grid.add(new Label("Animation name"), 0, 0);
		grid.add(id, 1, 0);

		grid.add(new Label("Image file"), 0, 1);
		grid.add(spriteFile, 1, 1);
		grid.add(browse, 2, 1);

		PositiveIntegerField frames = new PositiveIntegerField();
		PositiveIntegerField framerate = new PositiveIntegerField();

		if (anim != null) {
			spriteFile.setText(anim.url);
			frames.setValue(anim.frames);
			framerate.setValue(anim.framerate);
		}

		grid.add(new Label("Frame count"), 0, 2);
		grid.add(frames, 1, 2);

		grid.add(new Label("Frame rate"), 0, 3);
		grid.add(framerate, 1, 3);

		this.getDialogPane().setContent(grid);

		this.setResultConverter(clickedButton -> {
			if (clickedButton == ButtonType.OK) {
				int nframes = frames.getValue();
				int rframerate = framerate.getValue();

				if (nframes == 0 || rframerate == 0) return null;

				try {
					return new SpriteAnimation(loc,
						id.getText(), spriteFile.getText(), nframes, rframerate);
				} catch (CorruptDataException e) {
					Alert err = new Alert(Alert.AlertType.ERROR);
					err.setTitle("Cannot load sprite image");
					err.setContentText(e.getMessage());
					err.showAndWait();
					return null;
				}
			} else {
				return null;
			}
		});
	}
}

