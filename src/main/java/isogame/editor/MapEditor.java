package isogame.editor;
 
import javafx.application.Application;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
 
public class MapEditor extends Application {
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

			ToggleGroup toolsGroup = new ToggleGroup();
			EditorCanvas canvas = new EditorCanvas(root, primaryStage);
			LibraryPane library = new LibraryPane(dataDir, toolsGroup, canvas);
			MainMenu menuBar = new MainMenu(library, dataDir, canvas);
			ToolBar toolBar = new ToolBar(canvas, toolsGroup);

			VBox top = new VBox();
			top.getChildren().addAll(menuBar, toolBar);

			guiRoot.setTop(top);
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

	public File getDataDir(Stage primaryStage) {
		File homeDir = new File(System.getProperty("user.home"));
		File configDir = new File(homeDir, ".isogame_map_editor");
		File configFile = new File(configDir, "config.txt");

		File dataDir;
		try (BufferedReader in = new BufferedReader(
				new InputStreamReader(new FileInputStream(configFile), "UTF-8"))
		) {
			// try to read the data directory from the configuration file
			dataDir = new File(in.readLine());
		} catch (IOException e) {
			// If that fails, ask the user for the data directory, then make a new
			// configuration file.
			final DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Select data directory...");
			dataDir = directoryChooser.showDialog(primaryStage);

			// Ensure the configuration directory exists
			if (!configDir.exists()) {
				try {
					configDir.mkdir();
				} catch (SecurityException e1) {
					System.err.println("Failed to create config dir at " +
						configDir.toString() + "\n" + e1.toString());
				}
			}

			// Make the configuration file
			try (PrintWriter out = new PrintWriter(configFile, "UTF-8")) {
				out.println(dataDir.toString());
			} catch (IOException e1) {
				System.err.println("Cannot write config file at " +
					configFile.toString() + "\n" + e1.toString());
			}
		}

		return dataDir;
	}
}

