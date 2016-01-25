package isogame.battle;

public interface Obstacle {
	public boolean blocksSpace(Player player);
	public boolean blocksPath(Player player);
}

