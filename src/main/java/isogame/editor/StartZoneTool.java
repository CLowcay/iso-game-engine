package isogame.editor;

import isogame.engine.MapPoint;
import isogame.engine.Stage;
import isogame.engine.StartZoneType;
import isogame.engine.Tile;
import isogame.engine.View;

public class StartZoneTool extends Tool {
	StartZoneType startZone;

	public StartZoneTool(StartZoneType startZone) {
		this.startZone = startZone;
	}

	@Override
	public void apply(MapPoint p, Stage stage, View view) {
		if (stage.terrain.hasTile(p)) {
			Tile t = stage.terrain.getTile(p);
			stage.terrain.setTile(t.newStartZone(startZone));
		}
	}
}

