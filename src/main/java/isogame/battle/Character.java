package isogame.battle;

import isogame.engine.MapPoint;

public class Character implements Targetable {
	private final Player player;
	private final Stats baseStats;

	// WARNING: if any of these are updated, we must immediately call recomputeStats().
	private Weapon weapon;
	private Armour armour;
	private Stats buff;
	private Stats debuff;
	private Stats turnStats;
	private Stats cachedStats;

	private MapPoint pos;
	private int hp;
	private final int maxHP;

	private int buffCounter = 0;
	private int debuffCounter = 0;

	private void recomputeStats() {
		cachedStats =
			baseStats.add(buff).add(debuff).add(turnStats)
				.add(weapon.buff).add(armour.buff);
	}

	private int getMaxHP(Stats stats) {
		return (stats.vitality * 750) / 150;
	}

	public Character(
		Player player, MapPoint pos, Stats baseStats, Weapon weapon, Armour armour
	) {
		this.player = player;
		this.baseStats = baseStats;
		this.pos = pos;
		this.weapon = weapon;
		this.armour = armour;
		this.maxHP = getMaxHP(baseStats);
		this.hp = maxHP;
	}

	public void turnReset() {
		turnStats = new Stats();
		if (buffCounter > 0) {
			buffCounter -= 1;
			if (buffCounter == 0) buff = turnStats;
		}
		if (debuffCounter > 0) {
			debuffCounter -= 1;
			if (debuffCounter == 0) debuff = turnStats;
		}
		recomputeStats();
	}

	public void changeWeapon(Weapon weapon) {
		this.weapon = weapon;
		recomputeStats();
	}

	public void changeArmour(Armour armour) {
		this.armour = armour;
		recomputeStats();
	}

	public Weapon getWeapon() {
		return weapon;
	}

	@Override public Stats getStats() {
		return cachedStats;
	}

	@Override public MapPoint getPos() {
		return pos;
	}

	@Override public int getPhysicalDefence() {
		return armour.physicalDefence;
	}

	@Override public int getMagicalDefence() {
		return armour.magicalDefence;
	}

	@Override public void dealDamage(int damage) {
		hp = Math.max(0, hp - damage);
	}

	@Override public boolean isPushable() {
		return true;
	}

	@Override public Player getPlayer() {
		return player;
	}
}

