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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

import org.json.JSONException;

import isogame.engine.CorruptDataException;
import isogame.engine.Highlighter;
import isogame.engine.Library;
import isogame.engine.MapPoint;
import isogame.engine.MapView;
import isogame.engine.Stage;
import isogame.resource.ResourceLocator;

public class EditorCanvas extends MapView {
	private Tool tool = null;
	private final Window window;

	final SimpleBooleanProperty saved = new SimpleBooleanProperty(true);
	File stageFile = null;
	Library localLibrary = null;

	/**
	 * Load a stage from a file.
	 * */
	public void loadStage(
		final LibraryPane library,
		final ResourceLocator loc,
		final File dataDir
	) {
		if (promptSaveContinue(library, dataDir)) {
			final FileChooser fc = new FileChooser();
			fc.setTitle("Open map file");
			fc.setInitialDirectory(dataDir);
			fc.getExtensionFilters().addAll(new ExtensionFilter("Map Files", "*.map"));
			final File r = fc.showOpenDialog(window);
			if (r != null) {
				try {
					final Stage stage = Stage.fromFile(r,
						loc, library.getGlobalLibrary());
					library.setLocalLibrary(stage.localLibrary);

					setStage(stage);
					setAllSelectable();

					stageFile = r;
					localLibrary = stage.localLibrary;
					saved.setValue(true);
					tool = null;
				} catch (IOException e) {
					final Alert d = new Alert(Alert.AlertType.ERROR);
					d.setHeaderText("Cannot read file " + r.toString());
					d.setContentText(e.toString());
					d.show();
				} catch (CorruptDataException|JSONException|ClassCastException e) {
					final Alert d = new Alert(Alert.AlertType.ERROR);
					d.setHeaderText("Error in file " + r.toString());
					d.setContentText(e.toString());
					d.show();
				}
			}
		}
	}

	/**
	 * Save the current stage.
	 * */
	public void saveStage(final File dataDir) {
		final Stage stage = getStage();
		if (saved.getValue() || stage == null) return;
		if (stageFile == null) {
			saveStageAs(dataDir);
		} else {
			try {
				localLibrary.writeToStream(new FileOutputStream(stageFile), stage);
				saved.setValue(true);
			} catch (IOException e) {
				Alert d = new Alert(Alert.AlertType.ERROR);
				d.setHeaderText("Cannot save file as " + stageFile.toString());
				d.setContentText(e.toString());
				d.show();
			}
		}
	}

	/**
	 * Save the current stage under a new name.
	 * */
	public void saveStageAs(final File dataDir) {
		final Stage stage = getStage();
		if (stage == null) return;

		final FileChooser fc = new FileChooser();
		fc.setTitle("Save map file");
		fc.setInitialDirectory(dataDir);
		fc.getExtensionFilters().addAll(new ExtensionFilter("Map Files", "*.map"));
		File r = fc.showSaveDialog(window);
		if (r != null) {
			// automatically append .map if the user didn't give an extension
			final String name = r.getName();
			if (name.lastIndexOf('.') == -1) {
				final File p = r.getAbsoluteFile().getParentFile();
				r = new File(p, name + ".map");
			}
			stageFile = r;
			String n = r.getName();
			getStage().name = n.substring(0, n.lastIndexOf('.'));
			saveStage(dataDir);
		}
	}

	/**
	 * Prompt the user to save before closing a stage.
	 * @return true if the close action should continue, otherwise false.
	 * */
	public boolean promptSaveContinue(
		final LibraryPane library, final File dataDir
	) {
		if (saved.getValue()) {
			return true;
		} else {
			final Alert d = new Alert(Alert.AlertType.CONFIRMATION, null, 
				ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
			d.setHeaderText("Save first?");

			final Optional<ButtonType> r = d.showAndWait();
			if (!r.isPresent()) {
				return false;
			} else {
				final ButtonType bt = r.get();
				if (bt == ButtonType.YES) {
					saveStage(dataDir);
					return true;
				} else if (bt == ButtonType.NO) {
					return true;
				} else {
					return false;
				}
			}
		}
	}

	/**
	 * Make a new stage.
	 * */
	public void newStage(
		final LibraryPane library, final File dataDir
	) {
		if (!promptSaveContinue(library, dataDir)) return;

		try {
			(new NewMapDialog(library.getGlobalLibrary().getTerrain("blank")))
				.showAndWait()
				.ifPresent(terrain -> {
					localLibrary = library.newLocalLibrary();
					setStage(new Stage(terrain, localLibrary));
					setAllSelectable();
					stageFile = null;
					saved.setValue(false);
				});
		} catch (CorruptDataException e) {
			final Alert d = new Alert(Alert.AlertType.ERROR);
			d.setHeaderText("Cannot create map");
			d.setContentText(
				"You may be missing some textures.\n\nException was:\n" +
				e.toString());
			d.show();
			setStage(null);
			stageFile = null;
			saved.setValue(true);
			localLibrary = null;
		}
	}

	private boolean enableAnimations = false;
	public void enableAnimations(final boolean enable) {
		enableAnimations = enable;
	}

	public EditorCanvas(
		final Node root, final Window window
	) throws CorruptDataException {
		super(root, null, true, true,
			new Paint[] {Color.rgb(0x00, 0x00, 0xFF, 0.2)});

		this.setFocusTraversable(true);

		this.window = window;

		final Collection<MapPoint> emptyList = new ArrayList<>();
		final Collection<MapPoint> oneList = new ArrayList<>();

		this.doOnMouseOut(() -> super.setHighlight(emptyList, 0));
		this.doOnMouseOver(p -> {
			oneList.clear(); oneList.add(p);
			super.setHighlight(oneList, 0);
		});
		this.doOnSelection((selection, button) -> {
			if (tool != null) {
				selection.pointPriority().ifPresent(p ->
					tool.apply(p, getStage(), this));
			}
			saved.setValue(false);
		});
	}

	public void setTool(Tool tool) {
		this.tool = tool;
	}
}

