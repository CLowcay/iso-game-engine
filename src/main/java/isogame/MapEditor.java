package isogame;
 
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
 
public class MapEditor extends Application {
	public static void main(final String[] arguments) {
		Application.launch(arguments);
	}

	@Override
	public void start(javafx.stage.Stage primaryStage) {
		Canvas canvas = new Canvas(960, 540);
		StackPane root = new StackPane();
		root.getChildren().add(canvas);

		try {
			Stage stage = exampleStage();
			View view = new View(960, 540);
			view.centreOnTile(stage, new MapPoint(3, 3));

			AnimationTimer animateCanvas = new AnimationTimer() {
				int count0 = 0;
				int count = 0;
				long now0 = 0;

				@Override
				public void handle(long now) {
					count++;
					if (now0 == 0) now0 = now;
					if ((now - now0) >= 5000000000l) {
						System.err.println("fps: " + ((count - count0) / 5));
						now0 = now;
						count0 = count;
					}


					GraphicsContext cx = canvas.getGraphicsContext2D();
					view.renderFrame(cx, stage);
				}
			};

			animateCanvas.start();

			Scene scene = new Scene(root, 960, 540);

			primaryStage.setTitle("Hello World!");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Make a simple example stage for testing purposes.
	 * */
	private Stage exampleStage() throws CorruptDataException {
		TerrainTexture black = new TerrainTexture("/black.jpg");
		TerrainTexture white = new TerrainTexture("/white.jpg");
		CliffTexture cliff = new CliffTexture(
			"/cliff_texture.png",
			"/cliff_texture_narrow.png");
		Tile[] data = new Tile[8 * 8];
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				TerrainTexture t;
				if ((x + y) % 2 == 0) t = black; else t = white;
				int elevation = 0;
				SlopeType slope = SlopeType.NONE;
				if (x == 0 && y == 1) slope = SlopeType.N;
				if (x == 1 && y == 1) slope = SlopeType.N;
				if (x == 0 && y == 0) elevation = 1;
				if (x == 1 && y == 0) elevation = 1;

				if (x == 0 && y == 6) slope = SlopeType.S;
				if (x == 1 && y == 6) slope = SlopeType.S;
				if (x == 0 && y == 7) elevation = 1;
				if (x == 1 && y == 7) elevation = 1;

				if (x == 6 && y == 0) slope = SlopeType.E;
				if (x == 6 && y == 1) slope = SlopeType.E;
				if (x == 7 && y == 0) elevation = 1;
				if (x == 7 && y == 1) elevation = 1;

				if (x == 7 && y == 6) slope = SlopeType.W;
				if (x == 7 && y == 7) slope = SlopeType.W;
				if (x == 6 && y == 6) elevation = 1;
				if (x == 6 && y == 7) elevation = 1;

				if (x == 4 && y == 3) slope = SlopeType.S;
				if (x == 3 && y == 4) slope = SlopeType.E;
				if (x == 4 && y == 5) slope = SlopeType.N;
				if (x == 5 && y == 4) slope = SlopeType.W;

				data[(y * 8) + x] = new Tile(
					new MapPoint(x, y), elevation,
					slope, false,
					StartZoneType.NONE, t, cliff);
			}
		}
		StageInfo terrain = new StageInfo(8, 8, data);
		return new Stage(terrain);
	}
}

