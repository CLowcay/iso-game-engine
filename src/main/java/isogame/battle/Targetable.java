package isogame.battle;

import isogame.engine.MapPoint;

public interface Targetable {
	public Stats getStats();
	public MapPoint getPos();
	public double getAttackBuff();
	public double getDefenceBuff();
	public void dealDamage(int damage);
	public boolean isPushable();
	public Player getPlayer();
}

