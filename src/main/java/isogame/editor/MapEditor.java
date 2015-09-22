package isogame.editor;
 
import javafx.application.Application;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
 
public class MapEditor extends Application {
	public static void main(final String[] arguments) {
		Application.launch(arguments);
	}

	@Override
	public void start(javafx.stage.Stage primaryStage) {
		StackPane root = new StackPane();
		Scene scene = new Scene(root, 960, 540);
		BorderPane guiRoot = new BorderPane();

		// the main menu
		MenuBar menuBar = new MenuBar();
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
		menuBar.getMenus().add(menuFile);

		try {
			LibraryPane library = new LibraryPane("global_library.json");
			EditorCanvas canvas = new EditorCanvas();

			guiRoot.setTop(menuBar);
			guiRoot.setLeft(library);
			root.getChildren().addAll(canvas, guiRoot);

			primaryStage.setTitle("isogame map editor");
			primaryStage.setScene(scene);
			canvas.startAnimating();
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}

