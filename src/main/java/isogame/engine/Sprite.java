package isogame.engine;

import org.json.simple.JSONObject;

public class Sprite implements HasJSONRepresentation {
	private final SpriteInfo info;

	// position of the sprite on the map.
	public MapPoint pos;

	// The direction the sprite is facing
	public FacingDirection direction;

	public SpriteAnimation animation;

	public Sprite(SpriteInfo info) throws CorruptDataException {
		this.info = info;
		setAnimation(info.getDefaultAnimation().id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public JSONObject getJSON() {
		JSONObject r = new JSONObject();
		r.put("pos", pos.getJSON());
		r.put("direction", direction.name());
		r.put("sprite", info.getJSON());
		return r;
	}

	public void setAnimation(String animation) {
		this.animation = info.animations.get(animation);
	}
}


