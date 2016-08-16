package isogame.engine;

public enum AssetType {
	TEXTURE, SPRITE, CLIFF_TEXTURE;

	@Override
	public String toString() {
		switch(this) {
			case TEXTURE: return "texture";
			case SPRITE: return "sprite";
			case CLIFF_TEXTURE: return "cliff";
			default: throw new RuntimeException("This cannot happen");
		}
	}
}

