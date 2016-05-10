package isogame.engine;

import isogame.GlobalConstants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import java.util.function.Function;
import org.json.simple.JSONObject;


/**
 * Every sprite has four rotations, which are layed out vertically.  Each layer
 * may have multiple frames, which are layed out horizontally.
 *
 * The rotations are in this order:
 * facing up and right
 * facing up and left
 * facing down and left
 * facing down and right
 * 
 * */
public class SpriteAnimation implements HasJSONRepresentation {
	public final String id;
	private final String url;
	public final int frames;
	public final int framerate;  // in fps
	public final int w;
	public final int h; 

	private final Paint[] frameTextures;

	public SpriteAnimation(
		String id,
		String url,
		int frames,
		int framerate
	) throws CorruptDataException
	{
		this.frames = frames;
		this.framerate = framerate;
		this.id = id;
		this.url = url;

		Image buffer = new Image(url);
		if (buffer == null) throw new CorruptDataException("Missing texture " + url);

		frameTextures = new Paint[frames * 4];
		for (int d = 0; d < 4; d++) {
			for (int f = 0; f < frames; f++) {
				frameTextures[(f * 4) + d] =
					new ImagePattern(buffer, -f, -d, frames, 4, true);
			}
		}

		double iw = buffer.getWidth() / ((double) frames);
		double ih = buffer.getHeight() / 4.0d;
		w = (int) GlobalConstants.TILEW;
		h = (int) ((GlobalConstants.TILEW / iw) * ih);
	}

	public static SpriteAnimation fromJSON(
		JSONObject json, Function<String, String> urlConverter
	) throws CorruptDataException
	{
		Object rId = json.get("id");
		Object rUrl = json.get("url");
		Object rFrames = json.get("nframes");
		Object rFramerate = json.get("framerate");

		if (rId == null) throw new CorruptDataException("Error in animation, missing id");
		if (rUrl == null) throw new CorruptDataException("Error in animation, missing url");
		if (rFrames == null) throw new CorruptDataException("Error in animation, missing nframes");
		if (rFramerate == null) throw new CorruptDataException("Error in animation, missing framerate");

		try {
			return new SpriteAnimation(
				(String) rId,
				urlConverter.apply((String) rUrl),
				((Number) rFrames).intValue(),
				((Number) rFramerate).intValue());
		} catch (ClassCastException e) {
			throw new CorruptDataException("Type error in animation", e);
		}
	}

	public void renderFrame(
		GraphicsContext cx,
		int x,
		int y,
		int frame,
		CameraAngle angle,
		FacingDirection direction
	) {
		frame = frame % frames;
		int rotation = directionTransform(direction, angle);
		cx.setFill(frameTextures[(frame * 4) + rotation]);
		cx.fillRect(x, y - h + GlobalConstants.TILEH, w, h);
	}

	public static int directionTransform(
		FacingDirection direction, CameraAngle angle
	) {
		int d = 0;
		int a = 0;
		switch (direction) {
			case UP: d = 0; break;
			case LEFT: d = 1; break;
			case DOWN: d = 2; break;
			case RIGHT: d = 3; break;
		}
		switch (angle) {
			case UL: a = 0; break;
			case UR: a = 1; break;
			case LR: a = 2; break;
			case LL: a = 3; break;
		}
		return (d + a) % 4;
	}

	public static FacingDirection inverseDirectionTransform(
		FacingDirection direction, CameraAngle angle
	) {
		int d = 0;
		int a = 0;
		switch (direction) {
			case UP: d = 0; break;
			case LEFT: d = 1; break;
			case DOWN: d = 2; break;
			case RIGHT: d = 3; break;
		}
		switch (angle) {
			case UL: a = 0; break;
			case UR: a = 1; break;
			case LR: a = 2; break;
			case LL: a = 3; break;
		}

		switch ((d - a + 4) % 4) {
			case 0: return FacingDirection.UP;
			case 1: return FacingDirection.LEFT;
			case 2: return FacingDirection.DOWN;
			case 3: return FacingDirection.RIGHT;
			default: throw new RuntimeException("This cannot happen");
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public JSONObject getJSON() {
		JSONObject r = new JSONObject();
		r.put("id", id);
		r.put("url", url);
		r.put("nframes", new Integer(frames));
		r.put("framerate", new Integer(framerate));
		return r;
	}

	@Override
	public String toString() {
		return id;
	}
}

