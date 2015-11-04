package isogame.engine;

import javafx.scene.canvas.GraphicsContext;
import org.json.simple.JSONObject;

public class Sprite implements HasJSONRepresentation {
	public final SpriteInfo info;

	// position of the sprite on the map.
	public MapPoint pos;

	// The direction the sprite is facing
	public FacingDirection direction;

	public SpriteAnimation animation;

	public Sprite(SpriteInfo info) throws CorruptDataException {
		this.info = info;
		setAnimation(info.getDefaultAnimation().id);
	}

	public void renderFrame(
		GraphicsContext cx, int x, int y, int frame, CameraAngle angle
	) {
		animation.renderFrame(cx, x, y, frame, angle, direction);
	}

	public void rotate() {
		direction = direction.rotateAntiClockwise();
	}

	public static Sprite fromJSON(JSONObject json, Library lib)
		throws CorruptDataException
	{
		Object rPos = json.get("pos");
		Object rDirection = json.get("direction");
		Object rSprite = json.get("sprite");

		if (rPos == null) throw new CorruptDataException("Error in sprite, missing pos");
		if (rDirection == null) throw new CorruptDataException("Error in sprite, missing direction");
		if (rSprite == null) throw new CorruptDataException("Error in sprite, missing sprite id");

		try {
			Sprite sprite = new Sprite(lib.getSprite((String) rSprite));
			sprite.direction = FacingDirection.valueOf((String) rDirection);
			sprite.pos = MapPoint.fromJSON((JSONObject) rPos);
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
		r.put("sprite", info.id);
		return r;
	}

	public void setAnimation(String animation) {
		this.animation = info.animations.get(animation);
	}
}

