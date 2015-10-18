package isogame.editor;

import isogame.engine.MapPoint;
import isogame.engine.Stage;
import isogame.engine.TerrainTexture;
import isogame.engine.Tile;

public class TerrainTextureTool extends Tool {
	private final TerrainTexture texture;

	public TerrainTextureTool(TerrainTexture texture) {
		this.texture = texture;
	}

	@Override
	public void apply(MapPoint p, Stage stage) {
		if (stage.terrain.hasTile(p)) {
			Tile t = stage.terrain.getTile(p);
			stage.terrain.setTile(t.newTexture(texture));
		}
	}
}

