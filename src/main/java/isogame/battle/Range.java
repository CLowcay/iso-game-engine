package isogame.battle;

public class Range {
	public final int range;
	public final int radius;
	public final TargetMode targetMode;
	public final boolean los;

	public Range(int range, int radius, TargetMode targetMode, boolean los) {
		this.range = range;
		this.radius = radius;
		this.targetMode = targetMode;
		this.los = los;
	}
}

