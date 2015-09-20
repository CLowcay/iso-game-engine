package isogame;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;

public class CliffTexture {
	// a paint for every direction the slope could be going.
	public final Paint ul;
	public final Paint ll;
	public final Paint lr;
	public final Paint ur;
	public final Paint flat;

	public CliffTexture(String urlWide, String urlNarrow) {
		Image imgWide = new Image(urlWide, false);
		Image imgNarrow = new Image(urlNarrow, false);
		ul = new ImagePattern(imgNarrow, -0.5, 0, 1, 1, true);
		ll = new ImagePattern(imgWide,      0, 0, 1, 1, true);
		lr = new ImagePattern(imgWide,      0, 0, 1, 1, true);
		ur = new ImagePattern(imgNarrow,    0, 0, 1, 1, true);
		flat = new ImagePattern(imgWide,    0, 0, 1, 1, true);
	}

	public Paint getTexture(SlopeType s) {
		switch (s) {
			case N: return ur;
			case S: return ll;
			case E: return lr;
			case W: return ul;
			case NONE: return flat;
			default:
				throw new RuntimeException("Invalid slope type, this cannot happen");
		}
	}
}

