package isogame;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Any global configuration goes here so it's easy to find when we need to
 * change it.
 * */
public class GlobalConstants {
	public static final double TILEW = 512;
	public static final double TILEH = 256;

	public static final double ELEVATION_H = -128;

	public static final double ISO_VIEWPORTW = 1920 * 2;
	public static final double ISO_VIEWPORTH = 1080 * 2;

	// number of seconds to scroll one tile height
	public static final double SCROLL_SPEED = 4;

	// Colors to use for highlighting
	public static final Paint[] HIGHLIGHT_COLORS = {
		Color.BLUE
	};
}

