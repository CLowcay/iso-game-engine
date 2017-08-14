/* © Callum Lowcay 2015, 2016

This file is part of iso-game-engine.

iso-game-engine is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

iso-game-engine is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with iso-game-engine.  If not, see <http://www.gnu.org/licenses/>.

*/
package isogame.engine;

import java.io.IOException;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import org.json.JSONException;
import org.json.JSONObject;

import isogame.GlobalConstants;
import isogame.resource.ResourceLocator;


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
		final ResourceLocator loc,
		final String id,
		final String url,
		final int frames,
		final int framerate
	) throws CorruptDataException
	{
		this.frames = frames;
		this.framerate = framerate;
		this.id = id;
		this.url = url;

		try {
			final Image buffer = new Image(loc.gfx(url));
			hitTester = buffer.getPixelReader();
			hitW = (int) buffer.getWidth();
			hitH = (int) buffer.getHeight();

			final double iw = buffer.getWidth() / ((double) frames);
			final double ih = buffer.getHeight() / 4.0d;
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
		final int x,
		final int y0,
		final int frame,
		final CameraAngle angle,
		final FacingDirection direction
	) {
		final int y = y0 + h - ((int) GlobalConstants.TILEH);
		if (x < 0 || x >= w || y < 0 || y >= h) return false;

		final int rotation = direction.transform(angle);
		final int xt = (int) ((double) (x + (frame * GlobalConstants.TILEW)) / sf);
		final int yt = (int) ((double) (y + (rotation * h)) / sf);

		return hitTester.getColor(xt, yt).isOpaque();
	}

	public static SpriteAnimation fromJSON(
		final JSONObject json, final ResourceLocator loc
	) throws CorruptDataException
	{
		try {
			final String id = json.getString("id");
			final String url = json.getString("url");
			final int frames = json.getInt("nframes");
			final int framerate = json.getInt("framerate");

			return new SpriteAnimation(loc, id, url, frames, framerate);
		} catch (JSONException e) {
			throw new CorruptDataException("Error parsing animation " + e.getMessage(), e);
		}
	}

	public void renderFrame(
		final GraphicsContext cx,
		final int xoff, int w1,
		final int frame0,
		final CameraAngle angle,
		final FacingDirection direction
	) {
		final int frame = frame0 % frames;
		final int rotation = direction.transform(angle);
		cx.setFill(frameTextures[(frame * 4) + rotation]);
		cx.fillRect(xoff, 0, w1, h);
	}

	/**
	 * Update this frame object
	 * */
	public void updateFrame(
		final Rectangle sceneGraphNode,
		final int frame0,
		final CameraAngle angle,
		final FacingDirection direction
	) {
		final int frame = frame0 % frames;
		final int rotation = direction.transform(angle);
		sceneGraphNode.setWidth(w);
		sceneGraphNode.setHeight(h);
		sceneGraphNode.setFill(frameTextures[(frame * 4) + rotation]);
	}

	@Override
	public JSONObject getJSON() {
		final JSONObject r = new JSONObject();
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

