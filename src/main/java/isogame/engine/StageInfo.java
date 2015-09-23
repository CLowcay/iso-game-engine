package isogame.engine;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class StageInfo {
	public final int w;
	public final int h;
	private final Tile[] data;

	public StageInfo(int w, int h, Tile[] data) throws CorruptDataException {
		this.w = w;
		this.h = h;
		this.data = data;
		if (data.length != w * h)
			throw new CorruptDataException("Incorrect number of tiles in stage");
	}

	public Tile getTile(MapPoint pos) throws IndexOutOfBoundsException {
		if (pos.x < 0 || pos.y < 0 || pos.x >= w || pos.y >= h)
			throw new IndexOutOfBoundsException();
		return data[(pos.y * w) + pos.x];
	}

	public boolean hasTile(MapPoint pos) {
		return pos.x >= 0 && pos.y >= 0 && pos.x < w && pos.y < h;
	}

	/**
	 * Iterate over the tiles in this sort of order:
	 * 0 2 5
	 * 1 4 7
	 * 3 6 8
	 *
	 * The starting corner is determined by the camera angle.
	 *
	 * Iterating over the tiles in this order guarantees that we draw them from
	 * the back to the front, so objects closer to the camera properly obscure
	 * objects that are further away.
	 * */
	public Iterator<Tile> iterateTiles(CameraAngle a) {
		// coordinates of the next tile to return
		final int x0;
		final int y0;

		// coordinates of the last tile to return
		final int x1;
		final int y1;

		switch (a) {
			case UL: x0 =     0; y0 =     0; x1 = w - 1; y1 = h - 1; break;
			case LL: x0 =     0; y0 = h - 1; x1 = w - 1; y1 =     0; break;
			case LR: x0 = w - 1; y0 = h - 1; x1 =     0; y1 =     0; break;
			case UR: x0 = w - 1; y0 =     0; x1 =     0; y1 = h - 1; break;
			default: throw new RuntimeException("Invalid camera angle, this cannot happen");
		}

		// vector to move across rows of tiles
		final int dx;
		final int dy;

		// vector to move down rows of tiles
		final int vx1;
		final int vy1;

		// auxiliary vector to move down rows of tiles
		final int vx2;
		final int vy2;

		switch (a) {
			case UL: dx =  1; dy = -1; vx1 =  0; vy1 =  1; vx2 =  1; vy2 =  0; break;
			case LL: dx = -1; dy = -1; vx1 =  1; vy1 =  0; vx2 =  0; vy2 = -1; break;
			case LR: dx = -1; dy =  1; vx1 =  0; vy1 = -1; vx2 = -1; vy2 =  0; break;
			case UR: dx =  1; dy =  1; vx1 = -1; vy1 =  0; vx2 =  0; vy2 =  1; break;
			default: throw new RuntimeException("Invalid camera angle, this cannot happen");
		}

		return new Iterator<Tile>() {
			// current coordinates
			private int x = x0;
			private int y = y0;

			// coordinates of the first tile on the current row
			private int rx = x0;
			private int ry = y0;

			private boolean done = false;
			private boolean useAuxVector = false;

			@Override
			public boolean hasNext() {
				return !done;
			}

			@Override
			public Tile next() {
				if (done) {
					throw new NoSuchElementException();
				} else {
					Tile r = data[(y * w) + x];
					x += dx;
					y += dy;
					if (x < 0 || x >= w || y < 0 || y >= h) {
						if (useAuxVector) {
							rx += vx2;
							ry += vy2;
							if (rx < 0 || rx >= w || ry < 0 || ry >= h) done = true;
						} else {
							rx += vx1;
							ry += vy1;
							if (rx < 0 || rx >= w || ry < 0 || ry >= h) {
								useAuxVector = true;
								rx += vx2 - vx1;
								ry += vy2 - vy1;
							}
						}
						x = rx;
						y = ry;
					}
					return r;
				}
			}
		};
	}
}

