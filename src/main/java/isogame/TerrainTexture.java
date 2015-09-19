package isogame;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;

/**
 * A texture for use on terrain.  This could be embellished later to make more
 * sophisticated terrain.
 * */
public class TerrainTexture {
	public final Paint evenPaint;
	public final Paint oddPaint;

	public TerrainTexture(String url) {
		Image texture = new Image(url, false);
		int w = (int) texture.getWidth();
		int h = (int) texture.getHeight();

		this.evenPaint = new ImagePattern(texture, 0, 0, w, h, false);
		this.oddPaint = new ImagePattern(texture, w / 2, h / 2, w, h, false);
	}

}

