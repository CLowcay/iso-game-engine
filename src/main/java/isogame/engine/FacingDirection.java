package isogame.engine;

/**
 * What direction a sprite is facing, relative to a bird's-eye-view.
 * */
public enum FacingDirection {
	UP, DOWN, LEFT, RIGHT;

	public FacingDirection rotateClockwise() {
		switch (this) {
			case UP: return RIGHT;
			case LEFT: return UP;
			case DOWN: return LEFT;
			case RIGHT: return DOWN;
		}

		throw new IllegalArgumentException("Invalid FacingDirection, this cannot happen");
	}

	public FacingDirection rotateAntiClockwise() {
		switch (this) {
			case UP: return LEFT;
			case LEFT: return DOWN;
			case DOWN: return RIGHT;
			case RIGHT: return UP;
		}

		throw new IllegalArgumentException("Invalid FacingDirection, this cannot happen");
	}
}

