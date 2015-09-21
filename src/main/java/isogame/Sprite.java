package isogame;

public class Sprite {
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

	public void setAnimation(String animation) {
		this.animation = info.animations.get(animation);
	}
}


