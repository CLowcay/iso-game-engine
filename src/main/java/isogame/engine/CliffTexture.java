package isogame.engine;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import org.json.simple.JSONObject;

public class CliffTexture implements HasJSONRepresentation {
	// a paint for every direction the slope could be going.
	private final Paint ul;
	private final Paint ur;
	private final Paint flat;

	public final String id;
	private final String urlWide;
	private final String urlNarrow;

	public CliffTexture(String id, String urlWide, String urlNarrow) {
		this.id = id;
		this.urlWide = urlWide;
		this.urlNarrow = urlNarrow;

		Image imgWide = new Image(urlWide, false);
		Image imgNarrow = new Image(urlNarrow, false);
		ul = new ImagePattern(imgNarrow, -1, 0, 2, 1, true);
		ur = new ImagePattern(imgNarrow,  0, 0, 2, 1, true);
		flat = new ImagePattern(imgWide,  0, 0, 1, 1, true);
	}

	public static CliffTexture fromJSON(JSONObject json)
		throws CorruptDataException
	{
		Object rId = json.get("id");
		Object rUrlWide = json.get("urlWide");
		Object rUrlNarrow = json.get("urlNarrow");

		if (rId == null) throw new CorruptDataException("Error in cliff texture, missing id");
		if (rUrlWide == null) throw new CorruptDataException("Error in cliff texture, missing urlWide");
		if (rUrlNarrow == null) throw new CorruptDataException("Error in cliff texture, missing urlNarrow");

		try {
			return new CliffTexture(
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

