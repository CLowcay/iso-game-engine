package isogame.engine;

import isogame.GlobalConstants;
import isogame.resource.ResourceLocator;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import java.io.IOException;
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
	public final String url;
	public final int frames;
	public final int framerate;  // in fps
	public final int w;
	public final int h; 

	private final Paint[] frameTextures;
	private final PixelReader hitTester;
	private final int hitW;
	private final int hitH;
	private final double sf;

	public SpriteAnimation(
		ResourceLocator loc,
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

		try {
			Image buffer = new Image(loc.gfx(url));
			hitTester = buffer.getPixelReader();
			hitW = (int) buffer.getWidth();
			hitH = (int) buffer.getHeight();

			frameTextures = new Paint[frames * 4];
			for (int d = 0; d < 4; d++) {
				for (int f = 0; f < frames; f++) {
					frameTextures[(f * 4) + d] =
						new ImagePattern(buffer, -f, -d, frames, 4, true);
				}
			}

			double iw = buffer.getWidth() / ((double) frames);
			double ih = buffer.getHeight() / 4.0d;
			sf = GlobalConstants.TILEW / iw;
			w = (int) GlobalConstants.TILEW;
			h = (int) ((GlobalConstants.TILEW / iw) * ih);
		} catch (IOException e) {
			throw new CorruptDataException(
				"Cannot locate resource " + url, e);
		}
	}

	/**
	 * Do a low level pixel perfect hit test.
	 * @param x The x coordinate to test, relative to the origin of this animation's tile
	 * @param y The y coordinate to test, relative to the origin of this animation's tile
	 * @param frame The frame to hit test on
	 * @return true if the hit test passes.
	 * */
	public boolean hitTest(int x, int y, int frame) {
		int xt = (int) ((double) (x + (frame * GlobalConstants.TILEW)) / sf);
		int yt = (int) ((double) (y + h - GlobalConstants.TILEH) / sf);
		if (xt < 0 || yt < 0 || xt >= hitW || yt >= hitH) return false;
		return hitTester.getColor(xt, yt).isOpaque();
	}

	public static SpriteAnimation fromJSON(
		JSONObject json, ResourceLocator loc
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
			return new SpriteAnimation(loc,
				(String) rId,
				((String) rUrl),
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

