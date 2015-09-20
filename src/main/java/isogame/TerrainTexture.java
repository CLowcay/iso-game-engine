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

		this.evenPaint = new ImagePattern(texture, 0, 0, 1, 1, true);
		this.oddPaint = new ImagePattern(texture, -0.5, -0.5, 1, 1, true);
	}

}

