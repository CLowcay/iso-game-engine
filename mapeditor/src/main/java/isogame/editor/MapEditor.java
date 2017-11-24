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

import java.io.File;
import java.util.Map;
import java.util.Optional;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
 
public class MapEditor extends Application {
	public static void main(final String[] arguments) {
		Application.launch(arguments);
	}

	@Override
	public void start(final Stage primaryStage) {
		final StackPane root = new StackPane();
		final Scene scene = new Scene(root, 960, 540);
		final BorderPane guiRoot = new BorderPane();

		try {
			final Map<String, String> args = getParameters().getNamed();
			
			Optional<File> dataDir;
			dataDir = Optional.ofNullable(args.get("basedir")).map(s -> new File(s));
			if (!dataDir.isPresent()) {
				dataDir = Optional.ofNullable(getDataDir(primaryStage));
			}

			if (!dataDir.isPresent()) System.exit(1);

			final ResourceLocator loc =
				new DevelopmentResourceLocator(dataDir.get());

			final ToggleGroup toolsGroup = new ToggleGroup();
			final EditorCanvas canvas = new EditorCanvas(root, primaryStage);
			final LibraryPane library = new LibraryPane(
				dataDir.get(), loc, toolsGroup, canvas);
			final MainMenu menuBar = new MainMenu(library, dataDir.get(), loc, canvas);
			final ToolBar toolBar = new ToolBar(canvas, toolsGroup);

			final VBox top = new VBox();
			top.getChildren().addAll(menuBar, toolBar);

			guiRoot.setTop(top);
			guiRoot.setLeft(library);
			root.getChildren().addAll(canvas, guiRoot);

			//canvas.widthProperty().bind(root.widthProperty());
			//canvas.heightProperty().bind(root.heightProperty());

			primaryStage.setTitle("isogame map editor");
			primaryStage.setScene(scene);
			canvas.startAnimating();
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public File getDataDir(final Stage primaryStage) {
		final DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select data directory...");
		final File dataDir = directoryChooser.showDialog(primaryStage);
		return dataDir;
	}
}

