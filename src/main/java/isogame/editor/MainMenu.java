package isogame.editor;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import java.io.File;
import java.util.function.Function;

public class MainMenu extends MenuBar {
	public MainMenu(
		LibraryPane libraryPane,
		File dataDir,
		Function<String, String> urlConverter,
		EditorCanvas canvas
	) {
		super();

		Menu menuFile = new Menu("File");

		MenuItem fileNew = new MenuItem("New");
		fileNew.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
		fileNew.setOnAction(event -> canvas.newStage(libraryPane, dataDir));

		MenuItem fileOpen = new MenuItem("Open");
		fileOpen.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
		fileOpen.setOnAction(event -> canvas.loadStage(
			libraryPane, urlConverter, dataDir));

		MenuItem fileSave = new MenuItem("Save");
		fileSave.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
		fileSave.setOnAction(event -> canvas.saveStage(dataDir));

		MenuItem fileSaveAs = new MenuItem("Save As...");
		fileSaveAs.setAccelerator(KeyCombination.keyCombination("Shift+Ctrl+S"));
		fileSaveAs.setOnAction(event -> canvas.saveStageAs(dataDir));

		MenuItem fileExit = new MenuItem("Exit");
		fileExit.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
		fileExit.setOnAction(event -> {
			if (canvas.promptSaveContinue(libraryPane, dataDir)) {
				System.exit(0);
			}
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

