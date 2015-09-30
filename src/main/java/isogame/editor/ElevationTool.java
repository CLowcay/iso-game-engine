package isogame.editor;

import isogame.engine.CliffTexture;
import isogame.engine.MapPoint;
import isogame.engine.SlopeType;
import isogame.engine.Stage;
import isogame.engine.Tile;

public class ElevationTool extends Tool {
	private CliffTexture texture;
	private int de;
	private SlopeType slope;

	public ElevationTool(CliffTexture texture, int de, SlopeType slope) {
		this.texture = texture;
		this.de = de;
		this.slope = slope;
	}

	@Override
	public void apply(MapPoint p, Stage stage) {
		if (stage.terrain.hasTile(p)) {
			Tile t = stage.terrain.getTile(p);
			if (de < 0 && t.slope != SlopeType.NONE) {
				stage.terrain.setTile(t.newElevation(
					t.elevation, SlopeType.NONE, texture));
			} else {
				if (t.elevation + de >= 0) {
					stage.terrain.setTile(t.newElevation(
						t.elevation + de, slope, texture));
				}
			}
		}
	}
}
