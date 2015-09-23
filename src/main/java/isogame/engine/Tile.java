package isogame.engine;

import javafx.scene.paint.Paint;

/**
 * Represents a single tile in a stage.
 * */
public class Tile {
	public final int elevation;
	private final TerrainTexture tex;
	public final Paint texture;
	public final CliffTexture cliffTexture;
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
		TerrainTexture texture,
		CliffTexture cliffTexture
	) {
		this.elevation = elevation;
		this.pos = pos;

		tex = texture;
		if ((pos.x + pos.y) % 2 == 0) {
			this.texture = texture.evenPaint;
		} else {
			this.texture = texture.oddPaint;
		}

		this.cliffTexture = cliffTexture;

		this.slope = slope;
		this.isManaZone = isManaZone;
		this.startZone = startZone;
	}

	/**
	 * Make a new tile with a different texture
	 * */
	public Tile newTexture(TerrainTexture tex) {
		return new Tile(pos, elevation, slope, isManaZone, startZone, tex, cliffTexture);
	}

	/**
	 * Make a new tile with different elevation characteristics
	 * */
	public Tile newElevatoin(int elevation, SlopeType slope, CliffTexture cliffTexture) {
		return new Tile(pos, elevation, slope, isManaZone, startZone, tex, cliffTexture);
	}

	/**
	 * Make a new tile with a different mana zone property
	 * */
	public Tile newManaZone(boolean isManaZone) {
		return new Tile(pos, elevation, slope, isManaZone, startZone, tex, cliffTexture);
	}

	/**
	 * Make a new tile with a different start zone type
	 * */
	public Tile newStartZone(StartZoneType startZone) {
		return new Tile(pos, elevation, slope, isManaZone, startZone, tex, cliffTexture);
	}

	public SlopeType adjustSlopeForCameraAngle(CameraAngle angle) {
		int s;
		int d;

		switch (slope) {
			case N: s = 0; break;
			case E: s = 1; break;
			case S: s = 2; break;
			case W: s = 3; break;
			case NONE: return SlopeType.NONE;
			default: throw new RuntimeException(
				"Invalid slope type, this cannot happen");
		}

		switch (angle) {
			case UL: d = 0; break;
			case LL: d = 1; break;
			case LR: d = 2; break;
			case UR: d = 3; break;
			default: throw new RuntimeException(
				"Invalid camera angle, this cannot happen");
		}

		switch ((s + d) % 4) {
			case 0: return SlopeType.N;
			case 1: return SlopeType.E;
			case 2: return SlopeType.S;
			case 3: return SlopeType.W;
			default: throw new RuntimeException(
				"Computed invalid slope type, this cannot happen");
		}
	}

	public Paint getCliffTexture(CameraAngle angle) {
		return cliffTexture.getTexture(adjustSlopeForCameraAngle(angle));
	}

	@Override
	public String toString() {
		return pos.toString();
	}
}

