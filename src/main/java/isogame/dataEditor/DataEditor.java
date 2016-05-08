package isogame.dataEditor;

import isogame.battle.data.GameDataFactory;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
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
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 960, 540);

		try {
			File dataDir = getDataDir(primaryStage);
			if (dataDir == null) System.exit(1);
			GameDataFactory factory = new GameDataFactory(Optional.of(dataDir));

			// used to detect changes in the data so we know if it is necessary to save
			final SimpleBooleanProperty changed = new SimpleBooleanProperty(false);

			WeaponsDialog weaponsDialog = new WeaponsDialog(changed);
			AbilitiesPane abilitiesPane = new AbilitiesPane(changed);
			CharactersPane charactersPane = new CharactersPane(
				factory, dataDir, changed, abilitiesPane, weaponsDialog);

			root.setLeft(charactersPane);
			root.setCenter(abilitiesPane);
			root.setBottom(null);

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

