package isogame.engine;

import javafx.scene.canvas.GraphicsContext;
import org.json.simple.JSONObject;

public class Sprite implements HasJSONRepresentation {
	public final SpriteInfo info;

	// position of the sprite on the map.
	public MapPoint pos;

	// The direction the sprite is facing
	public FacingDirection direction;

	private SpriteAnimation animation;

	// animate the frames
	private FrameAnimator frameAnimator;
	private int frame = 0;

	public Sprite(SpriteInfo info) {
		this.info = info;
		this.pos = new MapPoint(0, 0);
		this.direction = FacingDirection.UP;
		setAnimation(info.defaultAnimation.id);
	}

	/**
	 * Check if a point is on this sprite.
	 * @param x coordinate transformed relative to the origin of this sprite
	 * @param y coordinate transformed relative to the origin of this sprite
	 * @return true if the point collides, otherwise false
	 * */
	public boolean hitTest(double x, double y) {
		return animation.hitTest((int) x, (int) y, frame);
	}

	/**
	 * Render a single frame of this sprite.
	 * */
	public void renderFrame(
		GraphicsContext cx, int x, int y, long t, CameraAngle angle
	) {
		int frame = frameAnimator.frameAt(t);
		animation.renderFrame(cx, x, y, frame, angle, direction);
	}

	/**
	 * Rotate the sprite anticlockwise.
	 * */
	public void rotate() {
		direction = direction.rotateAntiClockwise();
	}

	/**
	 * Set the animation for rendering this sprite.
	 * */
	public void setAnimation(String animation) {
		this.animation = info.animations.get(animation);
		this.frame = 0;
		this.frameAnimator = new FrameAnimator(
			this.animation.frames, this.animation.framerate);
	}

	public static Sprite fromJSON(JSONObject json, Library lib)
		throws CorruptDataException
	{
		Object rPos = json.get("pos");
		Object rDirection = json.get("direction");
		Object rSprite = json.get("sprite");
		Object rAnimation = json.get("animation");

		if (rPos == null) throw new CorruptDataException("Error in sprite, missing pos");
		if (rDirection == null) throw new CorruptDataException("Error in sprite, missing direction");
		if (rSprite == null) throw new CorruptDataException("Error in sprite, missing sprite id");
		if (rAnimation == null) throw new CorruptDataException("Error in sprite, missing animation id");

		try {
			Sprite sprite = new Sprite(lib.getSprite((String) rSprite));
			sprite.direction = FacingDirection.valueOf((String) rDirection);
			sprite.pos = MapPoint.fromJSON((JSONObject) rPos);
			sprite.setAnimation((String) rAnimation);
			return sprite;
		} catch (ClassCastException e) {
			throw new CorruptDataException("Type error in sprite", e);
		} catch (IllegalArgumentException e) {
			throw new CorruptDataException("Type error in sprite", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public JSONObject getJSON() {
		JSONObject r = new JSONObject();
		r.put("pos", pos.getJSON());
		r.put("direction", direction.name());
		r.put("animation", animation.id);
		r.put("sprite", info.id);
		return r;
	}
}

