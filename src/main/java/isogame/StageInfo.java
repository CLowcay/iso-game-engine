package isogame;

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

	public Tile getTile(int x, int y) throws IndexOutOfBoundsException {
		if (x < 0 || y < 0 || x >= w || y >= h)
			throw new IndexOutOfBoundsException();
		return data[(y * w) + x];
	}
}

