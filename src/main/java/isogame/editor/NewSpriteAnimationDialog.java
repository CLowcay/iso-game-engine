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
public class NewSpriteAnimationDialog extends Dialog<SpriteAnimation> {
	public NewSpriteAnimationDialog(File dataDirectory, ResourceLocator loc) {
		super();

		// Set up the header and footer
		this.setTitle("New sprite animation");
		this.setHeaderText("Configure the animation");
		this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// The dialog content
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField id = new TextField();
		id.setText(UUID.randomUUID().toString());
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

