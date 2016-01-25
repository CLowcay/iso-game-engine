package isogame.battle;

import java.util.Collection;
import java.util.List;

import isogame.engine.MapPoint;

public class Turn {
	public Player player;

	public Targetable getTargetableAt(MapPoint x) {
		return null;
	}

	public Character getCharacterAt(MapPoint x) {
		return null;
	}

	public void doMove(List<MapPoint> path) {
	}

	public boolean canMove(List<MapPoint> path) {
		return true;
	}

	public List<MapPoint> findPath(MapPoint start, MapPoint target) {
		return null;
	}

	public Collection<Targetable> getAbilityTargets(
		MapPoint agent, Ability ability, MapPoint target
	) {
		return null;
	}

	public void doAttack(MapPoint agent, Collection<DamageToTarget> targets) {
	}

	public boolean canAttack(MapPoint agent, Collection<DamageToTarget> targets) {
		return true;
	}

	public void doAbility(
		MapPoint agent, Ability ability, Collection<DamageToTarget> targets
	) {
	}

	public boolean canDoAbility(
		MapPoint agent, Ability ability, Collection<DamageToTarget> targets
	) {
		return true;
	}

	public void doChangeWeapon(MapPoint agent, Weapon weapon) {
	}

	public boolean canChangeWeapon(MapPoint agent, Weapon weapon) {
		return true;
	}

	public void doUseItem(MapPoint agent, Item item) {
	}

	public boolean canUseItem(MapPoint agent, Item item) {
		return true;
	}

	public void doPush(MapPoint agent, MapPoint target, boolean effective) {
	}

	public boolean canPush(MapPoint agent, MapPoint target, boolean effective) {
		return true;
	}
}

