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

import isogame.resource.ResourceLocator;

import java.io.File;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

public class MainMenu extends MenuBar {
	public MainMenu(
		final LibraryPane libraryPane,
		final File dataDir,
		final ResourceLocator loc,
		final EditorCanvas canvas
	) {
		super();

		final Menu menuFile = new Menu("File");

		final MenuItem fileNew = new MenuItem("New");
		fileNew.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
		fileNew.setOnAction(event -> canvas.newStage(libraryPane, dataDir));

		final MenuItem fileOpen = new MenuItem("Open");
		fileOpen.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
		fileOpen.setOnAction(event -> canvas.loadStage(
			libraryPane, loc, dataDir));

		final MenuItem fileSave = new MenuItem("Save");
		fileSave.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
		fileSave.setOnAction(event -> canvas.saveStage(dataDir));
		fileSave.disableProperty().bind(canvas.saved);

		final MenuItem fileSaveAs = new MenuItem("Save As...");
		fileSaveAs.setAccelerator(KeyCombination.keyCombination("Shift+Ctrl+S"));
		fileSaveAs.setOnAction(event -> canvas.saveStageAs(dataDir));

		final MenuItem fileExit = new MenuItem("Exit");
		fileExit.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
		fileExit.setOnAction(event -> {
			if (canvas.promptSaveContinue(libraryPane, dataDir)) {
				System.exit(0);
			}
		});

		menuFile.getItems().addAll(
			fileNew, fileOpen, fileSave, fileSaveAs, new SeparatorMenuItem(), fileExit);

		final Menu menuWindow = new Menu("Window");

		final CheckMenuItem showLibrary = new CheckMenuItem("Show library");
		showLibrary.setSelected(true);
		showLibrary.setAccelerator(KeyCombination.keyCombination("F2"));
		libraryPane.visibleProperty().bind(showLibrary.selectedProperty());

		menuWindow.getItems().add(showLibrary);

		this.getMenus().addAll(menuFile, menuWindow);
	}
}

