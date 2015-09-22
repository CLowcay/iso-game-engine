package isogame;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
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

	public TerrainTexture(String id, String url) {
		this.id = id;
		this.url = url;

		Image texture = new Image(url, false);
		int w = (int) texture.getWidth();
		int h = (int) texture.getHeight();

		this.evenPaint = new ImagePattern(texture, 0, 0, 1, 1, true);
		this.oddPaint = new ImagePattern(texture, -0.5, -0.5, 1, 1, true);
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

