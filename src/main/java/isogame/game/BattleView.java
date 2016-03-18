package isogame.game;

import isogame.comptroller.BattleInProgress;
import isogame.engine.Stage;
import isogame.engine.View;
import javafx.scene.canvas.Canvas;

public class BattleView extends Canvas {
	private final View view;
	private final Stage stage;

	public BattleView(Stage stage, BattleInProgress battle) {
		view = new View(960, 540);
		this.stage = stage;

		// Listen for window resize events
		this.widthProperty().addListener((obs, w0, w) -> {
			view.setViewport(w.intValue(), (int) this.getHeight());
		});
		this.heightProperty().addListener((obs, h0, h) -> {
			view.setViewport((int) this.getWidth(), h.intValue());
		});
	}
}

