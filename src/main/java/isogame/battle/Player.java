package isogame.battle;

public enum Player {
	PLAYER_A, PLAYER_B;

	@Override
	public String toString() {
		switch(this) {
			case PLAYER_A: return "Player 1";
			case PLAYER_B: return "Player 2";
			default:
				throw new RuntimeException("Invalid player, this cannot happen");
		}
	}
}

