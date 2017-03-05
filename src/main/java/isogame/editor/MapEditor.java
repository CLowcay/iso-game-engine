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
 
import isogame.resource.DevelopmentResourceLocator;
import isogame.resource.ResourceLocator;
import javafx.application.Application;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.function.Function;
import java.util.Map;
import java.util.Optional;
 
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
			Map<String, String> args = getParameters().getNamed();
			
			Optional<File> dataDir;
			dataDir = Optional.ofNullable(args.get("basedir")).map(s -> new File(s));
			if (!dataDir.isPresent()) {
				dataDir = Optional.ofNullable(getDataDir(primaryStage));
			}

			if (!dataDir.isPresent()) System.exit(1);

			ResourceLocator loc = new DevelopmentResourceLocator(dataDir.get());

			ToggleGroup toolsGroup = new ToggleGroup();
			EditorCanvas canvas = new EditorCanvas(root, primaryStage);
			LibraryPane library = new LibraryPane(
				dataDir.get(), loc, toolsGroup, canvas);
			MainMenu menuBar = new MainMenu(library, dataDir.get(), loc, canvas);
			ToolBar toolBar = new ToolBar(canvas, toolsGroup);

			VBox top = new VBox();
			top.getChildren().addAll(menuBar, toolBar);

			guiRoot.setTop(top);
			guiRoot.setLeft(library);
			root.getChildren().addAll(canvas, guiRoot);

			canvas.widthProperty().bind(root.widthProperty());
			canvas.heightProperty().bind(root.heightProperty());

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
		final DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select data directory...");
		File dataDir = directoryChooser.showDialog(primaryStage);
		return dataDir;
	}
}

