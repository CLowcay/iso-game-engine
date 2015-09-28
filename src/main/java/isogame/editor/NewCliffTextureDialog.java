package isogame.editor;

import isogame.engine.CliffTexture;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Dialog box to create new terrain textures
 * */
public class NewCliffTextureDialog extends Dialog<CliffTexture> {
	public NewCliffTextureDialog(File dataDirectory) {
		super();

		// Set up the header and footer
		this.setTitle("New cliff texture");
		this.setHeaderText("Choose an image file to use as a cliff texture");
		this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// The dialog content
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField id = new TextField();
		id.setText(UUID.randomUUID().toString());
		id.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> id.selectAll());

		TextField wide = new TextField();
		TextField narrow = new TextField();
		wide.setEditable(false);
		narrow.setEditable(false);

		Button browseWide = new Button("Browse...");
		Button browseNarrow = new Button("Browse...");
		browseWide.setOnAction(new BrowseButtonAction(
			this.getOwner(), dataDirectory, wide));
		browseNarrow.setOnAction(new BrowseButtonAction(
			this.getOwner(), dataDirectory, narrow));

		grid.add(new Label("Texture name"), 0, 0);
		grid.add(id, 1, 0);

		grid.add(new Label("Wide image"), 0, 1);
		grid.add(wide, 1, 1);
		grid.add(browseWide, 2, 1);

		grid.add(new Label("Narrow image"), 0, 2);
		grid.add(narrow, 1, 2);
		grid.add(browseNarrow, 2, 2);

		this.getDialogPane().setContent(grid);

		this.setResultConverter(clickedButton -> {
			if (clickedButton == ButtonType.OK &&
				!wide.getText().equals("") &&
				!narrow.getText().equals("")
			) {
				return new CliffTexture(id.getText(),
					wide.getText(), narrow.getText());
			} else {
				return null;
			}
		});
	}

	/**
	 * Handler for when the user clicks the browse button.
	 * */
	private class BrowseButtonAction implements EventHandler<ActionEvent> {
		private TextField result;
		private File dataDirectory;
		private Window parent;

		public BrowseButtonAction(Window parent, File dataDirectory, TextField result) {
			this.result = result;
			this.dataDirectory = dataDirectory;
			this.parent = parent;
		}

		@Override
		public void handle(ActionEvent event) {
			FileChooser fc = new FileChooser();
			fc.setTitle("Browse for texture file");
			fc.setInitialDirectory(dataDirectory);
			fc.getExtensionFilters().addAll(new ExtensionFilter("Graphics files",
				"*.png", "*.PNG", "*.jpg", "*.jpeg", "*.JPG", "*.JPEG", "*.bmp", "*.BMP"));
			File r = fc.showOpenDialog(parent);
			if (r != null) {
				result.setText(dataDirectory.toPath().relativize(r.toPath()).toString());
			}
		}
	}
}


