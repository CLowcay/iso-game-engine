package isogame.engine;

import isogame.resource.ResourceLocator;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import java.io.IOException;
import org.json.simple.JSONObject;

public class CliffTexture implements HasJSONRepresentation {
	// a paint for every direction the slope could be going.
	private final Paint ul;
	private final Paint ur;
	private final Paint flat;

	public final String id;
	private final String urlWide;
	private final String urlNarrow;

	public CliffTexture(
		ResourceLocator loc,
		String id, String urlWide, String urlNarrow
	) throws CorruptDataException {
		this.id = id;
		this.urlWide = urlWide;
		this.urlNarrow = urlNarrow;

		try {
			Image imgWide = new Image(loc.gfx(urlWide));
			Image imgNarrow = new Image(loc.gfx(urlNarrow));

			ul = new ImagePattern(imgNarrow, -1, 0, 2, 1, true);
			ur = new ImagePattern(imgNarrow,  0, 0, 2, 1, true);
			flat = new ImagePattern(imgWide,  0, 0, 1, 1, true);
		} catch (IOException e) {
			throw new CorruptDataException(
				"Cannot locate resource " + urlWide + " or " + urlNarrow, e);
		}
	}

	public static CliffTexture fromJSON(
		JSONObject json,
		ResourceLocator loc
	) throws CorruptDataException
	{
		Object rId = json.get("id");
		Object rUrlWide = json.get("urlWide");
		Object rUrlNarrow = json.get("urlNarrow");

		if (rId == null) throw new CorruptDataException("Error in cliff texture, missing id");
		if (rUrlWide == null) throw new CorruptDataException("Error in cliff texture, missing urlWide");
		if (rUrlNarrow == null) throw new CorruptDataException("Error in cliff texture, missing urlNarrow");

		try {
			return new CliffTexture(loc,
				(String) rId,
				(String) rUrlWide,
				(String) rUrlNarrow);
		} catch (ClassCastException e) {
			throw new CorruptDataException("Type error in cliff texture", e);
		} catch (IllegalArgumentException e) {
			throw new CorruptDataException("Bad filename in cliff texture", e);
		}
	}

	public Paint getTexture(SlopeType s) {
		switch (s) {
			case N: return ur;
			case S: return flat;
			case E: return flat;
			case W: return ul;
			case NONE: return flat;
			default:
				throw new RuntimeException("Invalid slope type, this cannot happen");
		}
	}

	public Paint getFlatTexture() {
		return flat;
	}

	@Override
	@SuppressWarnings("unchecked")
	public JSONObject getJSON() {
		JSONObject r = new JSONObject();
		r.put("id", id);
		r.put("urlWide", urlWide);
		r.put("urlNarrow", urlNarrow);

		return r;
	}
}

