package isogame.dataEditor;

import isogame.battle.data.GameDataFactory;
import javafx.application.Application;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.Optional;
 
public class DataEditor extends Application {
	public static void main(final String[] arguments) {
		Application.launch(arguments);
	}

	@Override
	public void start(Stage primaryStage) {
		StackPane root = new StackPane();
		Scene scene = new Scene(root, 960, 540);
		BorderPane guiRoot = new BorderPane();

		try {
			File dataDir = getDataDir(primaryStage);
			if (dataDir == null) System.exit(1);
			GameDataFactory factory = new GameDataFactory(Optional.of(dataDir));
			CharactersPane charactersPane = new CharactersPane(factory);

			guiRoot.setLeft(charactersPane);

			primaryStage.setTitle("Data editor");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public File getDataDir(Stage primaryStage) {
		final DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select data directory...");
		File dataDir = directoryChooser.showDialog(primaryStage);
		return dataDir;
	}
}

