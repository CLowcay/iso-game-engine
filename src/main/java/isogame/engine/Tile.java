package isogame.engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import static isogame.GlobalConstants.ELEVATION_H;
import static isogame.GlobalConstants.TILEH;
import static isogame.GlobalConstants.TILEW;

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
		int elevation,
		SlopeType slope,
		TerrainTexture texture,
		CliffTexture cliffTexture
	) {
		this(new MapPoint(0, 0), elevation, slope,
			false, StartZoneType.NONE, texture, cliffTexture);
	}

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
	public Tile newElevation(int elevation, SlopeType slope, CliffTexture cliffTexture) {
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

	private double[] xs = new double[6];
	private double[] ys = new double[6];

	/**
	 * Render this tile at (0,0).  If you need to draw the tile somewhere else,
	 * do a translation before calling this method.
	 * */
	public void render(GraphicsContext cx, Paint hcolor, CameraAngle angle) {
		switch (adjustSlopeForCameraAngle(angle)) {
			case NONE:
				xs[0] = TILEW / 2; ys[0] = -2;
				xs[1] = TILEW + 4; ys[1] = TILEH / 2;
				xs[2] = TILEW / 2; ys[2] = TILEH + 2;
				xs[3] = -4;        ys[3] = TILEH / 2;
				cx.setFill(texture);
				cx.fillPolygon(xs, ys, 4);
				doHighlight(cx, hcolor, xs, ys, 4);
				break;
			case N:
				xs[0] = -4;        ys[0] = (TILEH / 2) + 2;
				xs[1] = TILEW / 2; ys[1] = 0 - (TILEH / 2) - 2;
				xs[2] = TILEW + 4; ys[2] = 0;
				xs[3] = TILEW / 2; ys[3] = TILEH + 4;
				cx.setFill(texture);
				cx.fillPolygon(xs, ys, 4);
				doHighlight(cx, hcolor, xs, ys, 4);

				xs[0] = TILEW / 2; ys[0] = TILEH;
				xs[1] = TILEW;     ys[1] = 0;
				xs[2] = TILEW;     ys[2] = (TILEH / 2) + 2;
				xs[3] = TILEW / 2; ys[3] = TILEH + 2;
				cx.setFill(getCliffTexture(angle));
				cx.fillPolygon(xs, ys, 4);
				doHighlight(cx, hcolor, xs, ys, 4);
				break;
			case E:
				xs[0] = -4;        ys[0] = (TILEH / 2) + 2;
				xs[1] = TILEW / 2; ys[1] = -2;
				xs[2] = TILEW + 4; ys[2] = -2;
				xs[3] = TILEW / 2; ys[3] = (TILEH / 2) + 2;
				cx.setFill(texture);
				cx.fillPolygon(xs, ys, 4);
				doHighlight(cx, hcolor, xs, ys, 4);

				xs[0] = 0;         ys[0] = TILEH / 2;
				xs[1] = TILEW / 2; ys[1] = TILEH / 2;
				xs[2] = TILEW;     ys[2] = 0;
				xs[3] = TILEW;     ys[3] = (TILEH / 2) + 2;
				xs[4] = TILEW / 2; ys[4] = TILEH + 2;
				xs[5] = 0;         ys[5] = (TILEH / 2) + 2;
				cx.setFill(getCliffTexture(angle));
				cx.fillPolygon(xs, ys, 6);
				doHighlight(cx, hcolor, xs, ys, 6);
				break;
			case S:
				xs[0] = -4;        ys[0] = -2;
				xs[1] = TILEW / 2; ys[1] = -2;
				xs[2] = TILEW + 4; ys[2] = (TILEH / 2) + 2;
				xs[3] = TILEW / 2; ys[3] = (TILEH / 2) + 2;
				cx.setFill(texture);
				cx.fillPolygon(xs, ys, 4);
				doHighlight(cx, hcolor, xs, ys, 4);

				xs[0] = 0;         ys[0] = 0;
				xs[1] = TILEW / 2; ys[1] = TILEH / 2;
				xs[2] = TILEW;     ys[2] = TILEH / 2;
				xs[3] = TILEW;     ys[3] = (TILEH / 2) + 2;
				xs[4] = TILEW / 2; ys[4] = TILEH + 2;
				xs[5] = 0;         ys[5] = (TILEH / 2) + 2;
				cx.setFill(getCliffTexture(angle));
				cx.fillPolygon(xs, ys, 6);
				doHighlight(cx, hcolor, xs, ys, 6);
				break;
			case W:
				xs[0] = -4;        ys[0] = 0;
				xs[1] = TILEW / 2; ys[1] = 0 - (TILEH / 2) - 2;
				xs[2] = TILEW + 4; ys[2] = (TILEH / 2) + 2;
				xs[3] = TILEW / 2; ys[3] = TILEH + 4;
				cx.setFill(texture);
				cx.fillPolygon(xs, ys, 4);
				doHighlight(cx, hcolor, xs, ys, 4);

				xs[0] = 0;         ys[0] = 0;
				xs[1] = TILEW / 2; ys[1] = TILEH;
				xs[2] = TILEW / 2; ys[2] = TILEH + 2;
				xs[3] = 0;         ys[3] = (TILEH / 2) + 2;
				cx.setFill(getCliffTexture(angle));
				cx.fillPolygon(xs, ys, 4);
				doHighlight(cx, hcolor, xs, ys, 4);
				break;
		}

		Paint epaint = cliffTexture.getFlatTexture();
		for (int i = 0; i < elevation; i++) {
			cx.translate(0, TILEH / 2);
			xs[0] = 0;         ys[0] = 0;
			xs[1] = 0;         ys[1] = (TILEH / 2) + 2;
			xs[2] = TILEW / 2; ys[2] = TILEH + 2;
			xs[3] = TILEW;     ys[3] = (TILEH / 2) + 2;
			xs[4] = TILEW;     ys[4] = 0;
			xs[5] = TILEW / 2; ys[5] = TILEH / 2;

			cx.setFill(epaint);
			cx.fillPolygon(xs, ys, 6);
			doHighlight(cx, hcolor, xs, ys, 6);
		}
	}

	private void doHighlight(
		GraphicsContext cx,
		Paint hcolor,
		double[] xs,
		double[] ys,
		int pts
	) {
			if (hcolor != null) {
				cx.setFill(hcolor);
				cx.fillPolygon(xs, ys, pts);
			}
	}

	@Override
	public String toString() {
		return pos.toString();
	}
}

