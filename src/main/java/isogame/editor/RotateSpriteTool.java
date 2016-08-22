package isogame.editor;

import isogame.engine.MapPoint;
import isogame.engine.Stage;
import isogame.engine.View;

public class RotateSpriteTool extends Tool {
	@Override
	public void apply(MapPoint p, Stage stage, View v) {
		stage.rotateSprites(p);
	}
}

