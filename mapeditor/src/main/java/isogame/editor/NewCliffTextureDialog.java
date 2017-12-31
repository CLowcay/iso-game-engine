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

import isogame.engine.CliffTexture;
import isogame.engine.CorruptDataException;
import isogame.resource.ResourceLocator;

import java.io.File;
import java.util.UUID;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.stage.Window;

/**
 * Dialog box to create new terrain textures
 * */
public class NewCliffTextureDialog extends Dialog<CliffTexture> {
	public NewCliffTextureDialog(
		final File dataDirectory, final ResourceLocator loc
	) {
		super();

		// Set up the header and footer
		this.setTitle("New cliff texture");
		this.setHeaderText("Choose an image file to use as a cliff texture");
		this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// The dialog content
		final GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		final TextField id = new TextField();
		id.setText(UUID.randomUUID().toString());
		id.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> id.selectAll());

		final TextField wide = new TextField();
		final TextField narrow = new TextField();
		wide.setEditable(false);
		narrow.setEditable(false);

		final Button browseWide = new Button("Browse...");
		final Button browseNarrow = new Button("Browse...");
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
				try {
					return new CliffTexture(loc, id.getText(),
						wide.getText(), narrow.getText(), false);
				} catch (CorruptDataException e) {
					e.printStackTrace();
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

	/**
	 * Handler for when the user clicks the browse button.
	 * */
	private class BrowseButtonAction implements EventHandler<ActionEvent> {
		private TextField result;
		private File dataDirectory;
		private Window parent;

		public BrowseButtonAction(
			final Window parent, final File dataDirectory, final TextField result
		) {
			this.result = result;
			this.dataDirectory = dataDirectory;
			this.parent = parent;
		}

		@Override
		public void handle(final ActionEvent event) {
			final FileChooser fc = new FileChooser();
			fc.setTitle("Browse for texture file");
			fc.setInitialDirectory(dataDirectory);
			fc.getExtensionFilters().addAll(new ExtensionFilter("Graphics files",
				"*.png", "*.PNG", "*.jpg", "*.jpeg", "*.JPG", "*.JPEG", "*.bmp", "*.BMP"));
			final File r = fc.showOpenDialog(parent);
			if (r != null) {
				result.setText(dataDirectory.toPath().relativize(r.toPath()).toString());
			}
		}
	}
}

