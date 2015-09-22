package isogame.editor;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

public class MainMenu extends MenuBar {
	public MainMenu(LibraryPane libraryPane) {
		super();

		Menu menuFile = new Menu("File");
		MenuItem fileNew = new MenuItem("New");
		fileNew.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
		MenuItem fileOpen = new MenuItem("Open");
		fileOpen.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
		MenuItem fileSave = new MenuItem("Save");
		fileSave.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
		MenuItem fileSaveAs = new MenuItem("Save As...");
		fileSaveAs.setAccelerator(KeyCombination.keyCombination("Shift+Ctrl+S"));
		MenuItem fileExit = new MenuItem("Exit");
		fileExit.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
		fileExit.setOnAction(event -> {
			System.exit(0);
		});
		menuFile.getItems().addAll(
			fileNew, fileOpen, fileSave, fileSaveAs, new SeparatorMenuItem(), fileExit);

		Menu menuWindow = new Menu("Window");
		CheckMenuItem showLibrary = new CheckMenuItem("Show library");
		showLibrary.setSelected(true);
		showLibrary.setAccelerator(KeyCombination.keyCombination("F2"));
		libraryPane.visibleProperty().bind(showLibrary.selectedProperty());
		menuWindow.getItems().add(showLibrary);


		this.getMenus().addAll(menuFile, menuWindow);
	}
}

