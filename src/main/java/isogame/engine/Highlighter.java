package isogame.engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.Map;
import static isogame.engine.TilePrerenderer.OFFSETX;
import static isogame.engine.TilePrerenderer.OFFSETY;

/**
 * A class for rendering highlighted tiles.
 * */
public class Highlighter {
	public final Color color;

	private final Map<SlopeType, Image> tileTexture;
	private final Map<SlopeType, Image> cliffTexture;

	public Highlighter(Color color) {
		this.color = color;

		tileTexture = TilePrerenderer.prerenderTile(color);
		cliffTexture = TilePrerenderer.prerenderCliff(s -> color);
	}

	public void renderTop(GraphicsContext cx, SlopeType slope) {
		cx.drawImage(tileTexture.get(slope), -OFFSETX, -OFFSETY);
	}

	public void renderCliff(GraphicsContext cx, SlopeType slope) {
		cx.drawImage(cliffTexture.get(slope), -OFFSETX, -OFFSETY);
	}

	public void renderElevation(GraphicsContext cx) {
		cx.drawImage(cliffTexture.get(SlopeType.NONE), -OFFSETX, -OFFSETY);
	}
}

