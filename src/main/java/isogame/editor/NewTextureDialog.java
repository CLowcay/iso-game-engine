package isogame.editor;

import isogame.engine.CorruptDataException;
import isogame.engine.TerrainTexture;
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
 * Dialog box to create new terrain textures
 * */
public class NewTextureDialog extends Dialog<TerrainTexture> {
	public NewTextureDialog(File dataDirectory) {
		super();

		// Set up the header and footer
		this.setTitle("New terrain texture");
		this.setHeaderText("Choose an image file to use as a terrain texture");
		this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// The dialog content
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField id = new TextField();
		id.setText(UUID.randomUUID().toString());
		id.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> id.selectAll());

		TextField texture = new TextField();
		texture.setEditable(false);

		Button browse = new Button("Browse...");
		browse.setOnAction(event -> {
			FileChooser fc = new FileChooser();
			fc.setTitle("Browse for texture file");
			fc.setInitialDirectory(dataDirectory);
			fc.getExtensionFilters().addAll(new ExtensionFilter("Graphics files",
				"*.png", "*.PNG", "*.jpg", "*.jpeg", "*.JPG", "*.JPEG", "*.bmp", "*.BMP"));
			File r = fc.showOpenDialog(this.getOwner());
			if (r != null) {
				texture.setText(dataDirectory.toPath().relativize(r.toPath()).toString());
			}
		});

		grid.add(new Label("Texture name"), 0, 0);
		grid.add(id, 1, 0);

		grid.add(new Label("Texture image"), 0, 1);
		grid.add(texture, 1, 1);
		grid.add(browse, 2, 1);

		this.getDialogPane().setContent(grid);

		this.setResultConverter(clickedButton -> {
			if (clickedButton == ButtonType.OK && !texture.getText().equals("")) {
				try {
					return new TerrainTexture(id.getText(), texture.getText());
				} catch (CorruptDataException e) {
					Alert err = new Alert(Alert.AlertType.ERROR);
					err.setTitle("Cannot load texture image");
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

