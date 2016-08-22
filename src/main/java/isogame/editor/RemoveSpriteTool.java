package isogame.editor;

import isogame.engine.MapPoint;
import isogame.engine.Stage;
import isogame.engine.View;

public class RemoveSpriteTool extends Tool {
	@Override
	public void apply(MapPoint p, Stage stage, View v) {
		stage.clearTileOfSprites(p);
	}
}

