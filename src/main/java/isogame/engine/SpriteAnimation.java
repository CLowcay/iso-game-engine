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

			double iw = buffer.getWidth() / ((double) frames);
			double ih = buffer.getHeight() / 4.0d;
			sf = GlobalConstants.TILEW / iw;
			w = (int) GlobalConstants.TILEW;
			h = (int) ((GlobalConstants.TILEW / iw) * ih);

			frameTextures = new Paint[frames * 4];
			for (int d = 0; d < 4; d++) {
				for (int f = 0; f < frames; f++) {
					frameTextures[(f * 4) + d] =
						new ImagePattern(buffer, -f * w, -d * h, frames * w, 4 * h, false);
				}
			}

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
	 * @param angle The angle of the camera
	 * @param direction The direction the sprite is facing
	 * @return true if the hit test passes.
	 * */
	public boolean hitTest(
		int x, int y0, int frame, CameraAngle angle, FacingDirection direction
	) {
		int y = y0 + h - ((int) GlobalConstants.TILEH);
		if (x < 0 || x >= w || y < 0 || y >= h) return false;

		int rotation = direction.transform(angle);
		int xt = (int) ((double) (x + (frame * GlobalConstants.TILEW)) / sf);
		int yt = (int) ((double) (y + (rotation * h)) / sf);

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
		int xoff, int w1,
		int frame,
		CameraAngle angle,
		FacingDirection direction
	) {
		frame = frame % frames;
		int rotation = direction.transform(angle);
		cx.setFill(frameTextures[(frame * 4) + rotation]);
		cx.fillRect(xoff, 0, w1, h);
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

