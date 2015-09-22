package isogame.editor;
 
import javafx.application.Application;
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

		try {
			LibraryPane library = new LibraryPane("global_library.json");
			EditorCanvas canvas = new EditorCanvas();
			MainMenu menuBar = new MainMenu(library);

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

