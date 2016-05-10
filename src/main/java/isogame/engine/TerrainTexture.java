package isogame.engine;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import java.util.function.Function;
import org.json.simple.JSONObject;

/**
 * A texture for use on terrain.  This could be embellished later to make more
 * sophisticated terrain.
 * */
public class TerrainTexture implements HasJSONRepresentation {
	public final Paint evenPaint;
	public final Paint oddPaint;

	public final String id;
	private final String url;

	public TerrainTexture(String id, String url) throws CorruptDataException {
		this.id = id;
		this.url = url;

		Image texture = new Image(url, false);
		if (texture == null) throw new CorruptDataException("Missing texture " + url);
		int w = (int) texture.getWidth();
		int h = (int) texture.getHeight();

		this.evenPaint = new ImagePattern(texture, 0, 0, 1, 1, true);
		this.oddPaint = new ImagePattern(texture, -0.5, -0.5, 1, 1, true);
	}

	public static TerrainTexture fromJSON(
		JSONObject json, Function<String, String> urlConverter
	) throws CorruptDataException
	{
		Object rId = json.get("id");
		Object rUrl = json.get("url");

		if (rId == null) throw new CorruptDataException("Error in texture, missing id");
		if (rUrl == null) throw new CorruptDataException("Error in texture, missing url");

		try {
			return new TerrainTexture(
				urlConverter.apply((String) rId),
				urlConverter.apply((String) rUrl));
		} catch (ClassCastException e) {
			throw new CorruptDataException("Type error in texture", e);
		} catch (IllegalArgumentException e) {
			throw new CorruptDataException("Bad filename in texture", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public JSONObject getJSON() {
		JSONObject r = new JSONObject();
		r.put("id", id);
		r.put("url", url);

		return r;
	}
}

