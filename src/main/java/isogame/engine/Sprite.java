package isogame.engine;

import isogame.GlobalConstants;
import javafx.scene.canvas.GraphicsContext;
import java.util.Optional;
import org.json.simple.JSONObject;

public class Sprite implements HasJSONRepresentation {
	public final SpriteInfo info;

	// position of the sprite on the map.
	public MapPoint pos = new MapPoint(0, 0);

	// The direction the sprite is facing
	public FacingDirection direction = FacingDirection.UP;

	// Extra data that can be used to identify this sprite
	public Object userData;

	private Optional<SpriteDecalRenderer> renderDecal = Optional.empty();

	private SpriteAnimation animation;

	// animate the frames
	private FrameAnimator frameAnimator;
	private int frame = 0;

	private AnimationChain animationChain = null;

	public Sprite(SpriteInfo info) {
		this.info = info;
		setAnimation(info.defaultAnimation.id);
	}

	public void setAnimationChain(AnimationChain chain) {
		this.animationChain = chain;
	}

	public AnimationChain getAnimationChain() {
		return animationChain;
	}

	/**
	 * Register a function that can draw extra decals over the sprite.
	 * @param r The decal renderer function, null to deregister the current decal
	 * renderer.
	 * */
	public void setDecalRenderer(SpriteDecalRenderer r) {
		renderDecal = Optional.ofNullable(r);
	}

	/**
	 * Check if a point is on this sprite.
	 * @param x coordinate transformed relative to the origin of this sprite
	 * @param y coordinate transformed relative to the origin of this sprite
	 * @return true if the point collides, otherwise false
	 * */
	public boolean hitTest(double x, double y, CameraAngle angle) {
		return animation.hitTest((int) x, (int) y, frame, angle, direction);
	}

	/**
	 * Render a single frame of this sprite.
	 * WARNING: does not preserve the current translation.
	 * */
	public void renderFrame(
		GraphicsContext cx, int xoff, int w, long t, CameraAngle angle
	) {
		int frame = frameAnimator.frameAt(t);

		cx.translate(0, GlobalConstants.TILEH - animation.h);
		animation.renderFrame(cx, xoff, w, frame, angle, direction);
		renderDecal.ifPresent(r -> r.render(cx, this, t, angle));
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

