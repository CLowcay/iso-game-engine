package isogame.battle;

import isogame.engine.MapPoint;

public interface Targetable {
	public Stats getStats();
	public MapPoint getPos();
	public int getPhysicalDefence();
	public int getMagicalDefence();
	public void dealDamage(int damage);
	public boolean isPushable();
	public Player getPlayer();
}

