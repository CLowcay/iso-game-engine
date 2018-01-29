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

import isogame.GlobalConstants;
import isogame.resource.ResourceLocator;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import java.io.IOException;
import ssjsjs.annotations.As;
import ssjsjs.annotations.Field;
import ssjsjs.annotations.Implicit;
import ssjsjs.annotations.JSON;
import ssjsjs.JSONable;


/**
 * Every sprite has four rotations, which are laid out vertically.  Each layer
 * may have multiple frames, which are laid out horizontally.
 *
 * The rotations are in this order:
 * facing up and right
 * facing up and left
 * facing down and left
 * facing down and right
 * 
 * */
public class SpriteAnimation implements JSONable {
	public final String id;
	public final String url;
	public final int frames;
	public final int framerate;  // in fps
	public final int w;
	public final int h; 

	private final Image buffer;
	private final Paint[] frameTextures;
	private final Rectangle2D[] frameClips;
	private final PixelReader hitTester;
	private final int hitW;
	private final int hitH;
	private final double sf;

	@JSON
	public SpriteAnimation(
		@Implicit("locator") final ResourceLocator loc,
		@Field("id") final String id,
		@Field("url") final String url,
		@Field("frames")@As("nframes") final int frames,
		@Field("framerate") final int framerate
	) throws CorruptDataException
	{
		this.frames = frames;
		this.framerate = framerate;
		this.id = id;
		this.url = url;

		try {
			buffer = new Image(loc.gfx(url));
			hitTester = buffer.getPixelReader();
			hitW = (int) buffer.getWidth();
			hitH = (int) buffer.getHeight();

			final double iw = buffer.getWidth() / ((double) frames);
			final double ih = buffer.getHeight() / 4.0d;
			sf = GlobalConstants.TILEW / iw;
			w = (int) GlobalConstants.TILEW;
			h = (int) ((GlobalConstants.TILEW / iw) * ih);

			frameTextures = new Paint[frames * 4];
			frameClips = new Rectangle2D[frames * 4];
			for (int d = 0; d < 4; d++) {
				for (int f = 0; f < frames; f++) {
					final int i = (f * 4) + d;
					frameTextures[i] =
						new ImagePattern(buffer, -f * w, -d * h, frames * w, 4 * h, false);
					frameClips[i] = new Rectangle2D(f * iw, d * ih, iw, ih);
				}
			}

		} catch (final IOException e) {
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

		if (xt < 0 || xt >= hitW || yt < 0 || yt >= hitH) return false;
		return hitTester.getColor(xt, yt).isOpaque();
	}

	/**
	 * Draw this frame onto a canvas.
	 * @param cx the graphics context
	 * @param frame the frame to draw
	 * @param angle the current camera angle
	 * @param the direction the sprite is facing
	 * */
	public void drawFrame(
		final GraphicsContext cx,
		final int frame,
		final CameraAngle angle,
		final FacingDirection direction
	) {
		final int rotation = direction.transform(angle);
		cx.setFill(frameTextures[(frame * 4) + rotation]);
		cx.fillRect(0, 0, GlobalConstants.TILEW, h);
	}

	/**
	 * Update this frame object.
	 * @param sceneGraphNode the node to draw the frame onto
	 * @param frame0 the frame to draw
	 * @param angle the current camera angle
	 * @param direction the direction the sprite is facing
	 * */
	public void updateFrame(
		final Rectangle sceneGraphNode,
		final int frame0,
		final CameraAngle angle,
		final FacingDirection direction
	) {
		final int frame = frame0 % frames;
		final int rotation = direction.transform(angle);
		final int i = (frame * 4) + rotation;
		sceneGraphNode.setFill(frameTextures[i]);
		final ImageView clip = new ImageView(buffer);
		clip.setPreserveRatio(true);
		clip.setFitWidth(GlobalConstants.TILEW);
		clip.setViewport(frameClips[i]);
		sceneGraphNode.setClip(clip);
	}

	@Override public String toString() {
		return id;
	}
}

