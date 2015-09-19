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

	public Tile getTile(MapPoint pos) throws IndexOutOfBoundsException {
		if (pos.x < 0 || pos.y < 0 || pos.x >= w || pos.y >= h)
			throw new IndexOutOfBoundsException();
		return data[(pos.y * w) + pos.x];
	}
}

