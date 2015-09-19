package isogame;

import javafx.scene.paint.Paint;

/**
 * Represents a single tile in a stage.
 * */
public class Tile {
	public final int elevation;
	public final Paint texture;
	public final SlopeType slope;
	public final boolean isManaZone;
	public final StartZoneType startZone;
	public final MapPoint pos;

	public Tile(
		MapPoint pos,
		int elevation,
		SlopeType slope,
		boolean isManaZone,
		StartZoneType startZone,
		TerrainTexture texture
	) {
		this.elevation = elevation;
		this.pos = pos;

		if ((pos.x + pos.y) % 2 == 0) {
			this.texture = texture.evenPaint;
		} else {
			this.texture = texture.oddPaint;
		}

		this.slope = slope;
		this.isManaZone = isManaZone;
		this.startZone = startZone;
	}
}

