package isogame.battle;

import isogame.engine.MapPoint;
import java.util.Optional;

public class Character implements Targetable {
	private final Player player;
	private final Stats baseStats;

	private Weapon weapon;

	private Optional<StatusEffect> statusBuff;
	private Optional<StatusEffect> statusDebuff;

	private MapPoint pos;
	private int hp;
	private final int maxHP;

	private int getMaxHP(Stats stats) {
		return (stats.vitality * 750) / 150;
	}

	public Character(
		Player player, MapPoint pos, Stats baseStats, Weapon weapon
	) {
		this.player = player;
		this.baseStats = baseStats;
		this.pos = pos;
		this.weapon = weapon;
		this.maxHP = getMaxHP(baseStats);
		this.hp = maxHP;
	}

	/* Make a clone of the character at a new position.
	 * The clone is reset to default stats.
	 * */
	public Character cloneTo(MapPoint pos) {
		return new Character (player, pos, baseStats, weapon);
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public void changeWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	@Override public Stats getStats() {
		return baseStats;
	}

	@Override public MapPoint getPos() {
		return pos;
	}

	@Override public double getAttackBuff() {
		double buff = statusBuff.map(s -> s.attackBuff).orElse(0.0);
		double debuff = statusDebuff.map(s -> s.attackBuff).orElse(0.0);
		return buff - debuff;
	}

	@Override public double getDefenceBuff() {
		double buff = statusBuff.map(s -> s.defenceBuff).orElse(0.0);
		double debuff = statusDebuff.map(s -> s.defenceBuff).orElse(0.0);
		return buff - debuff;
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

