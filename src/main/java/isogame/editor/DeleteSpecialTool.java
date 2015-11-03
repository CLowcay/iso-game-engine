package isogame.editor;

import isogame.engine.MapPoint;
import isogame.engine.Stage;
import isogame.engine.Tile;
import isogame.engine.View;

public class DeleteSpecialTool extends Tool {
	@Override
	public void apply(MapPoint p, Stage stage, View view) {
		if (stage.terrain.hasTile(p)) {
			Tile t = stage.terrain.getTile(p);
			stage.terrain.setTile(t.clearSpecialProperties());
		}
	}
}

