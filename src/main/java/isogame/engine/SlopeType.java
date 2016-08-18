package isogame.engine;

public enum SlopeType {
	N, S, E, W, NONE;

	public SlopeType opposite() {
		switch(this) {
			case N: return S;
			case S: return N;
			case E: return W;
			case W: return E;
			case NONE: return NONE;
			default: throw new RuntimeException("This cannot happen");
		}
	}
}

