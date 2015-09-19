package isogame;

/**
 * The four camera angles.
 * */
public enum CameraAngle {
	// Internally, we represent the game as a conventional 2D chess board, but we
	// display it from an angle rather than a bird's-eye-view.  This enum tells
	// us which corner of the chess board appears the *furthest* from the camera.
	// From this we determine what order to draw the terrain tiles, and which
	// rotation to use for the sprites.

	// Upper Left, Upper Right, Lower Left, Lower Right.
	UL, UR, LL, LR;

	public CameraAngle nextClockwise() {
		switch (this) {
			case UL: return LL;
			case UR: return UL;
			case LL: return LR;
			case LR: return UR;
		}
		throw new RuntimeException("Invalid camera angle, This cannot happen");
	}

	public CameraAngle nextAnticlockwise() {
		switch (this) {
			case UL: return UR;
			case UR: return LR;
			case LL: return UL;
			case LR: return LL;
		}
		throw new RuntimeException("Invalid camera angle, This cannot happen");
	}
}

