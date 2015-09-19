package isogame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

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
public class SpriteAnimation {
	public final int frames;
	public final int framerate;  // in fps
	public final int w;  // width of a single frame.  Width of the image is w * frames
	public final int h;  // height of a single frame.  Height of the image is h * 4
	private final Image buffer;

	public SpriteAnimation(
		int frames,
		int framerate,
		int w,
		int h,
		String url
	) throws CorruptDataException {
		this.frames = frames;
		this.framerate = framerate;
		this.w = w;
		this.h = h;
		this.buffer = new Image(url);

		if ((int) buffer.getWidth() != w * frames || (int) buffer.getHeight() != h * 4)
			throw new CorruptDataException("Image " + url + " has the wrong dimensions");
	}

	public void renderFrame(
		GraphicsContext cx,
		int x,
		int y,
		int frame,
		CameraAngle angle,
		FacingDirection direction
	)
		throws IndexOutOfBoundsException
	{
		if (frame < 0 || frame >= frames)
			throw new IndexOutOfBoundsException();

		// compute the rotation to use, based on the direction the sprite is facing
		// (from a bird's-eye-view), and the direction the camera is pointing.
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
		int rotation = (a + d) % 4;

		cx.drawImage(buffer, frame * w, rotation * h, w, h, x, y, w, h);
	}


}

