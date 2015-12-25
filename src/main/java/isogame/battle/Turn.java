package isogame.battle;

import java.util.List;

import isogame.engine.MapPoint;

public class Turn {
	public void doMove(List<MapPoint> path) {
	}
	public void doAttack(MapPoint agent, MapPoint target, long damage,
		StatusEffect statusEffect, InstantEffect pre, InstantEffect post
	) {
	}
	public void doAbility(MapPoint agent, MapPoint target,
		Ability ability, long damage,
		StatusEffect statusEffect,
		InstantEffect pre, InstantEffect post
	) {
	}
	public void doChangeWeapon(MapPoint agent, Weapon weapon) {
	}
	public void doUseItem(MapPoint agent, Item item) {
	}
	public void doPush(MapPoint agent, MapPoint target, boolean effective) {
	}
}

