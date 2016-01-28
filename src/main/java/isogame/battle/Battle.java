package isogame.battle;

import java.util.Collection;
import java.util.List;

import isogame.engine.MapPoint;

public class Battle {
	public BattleState battleState;

	public Battle(BattleState battleState) {
		this.battleState = battleState;
	}

	public void doMove(List<MapPoint> path) {
	}

	public void doAttack(MapPoint agent, Collection<DamageToTarget> targets) {
	}

	public void doAbility(
		MapPoint agent, Ability ability, Collection<DamageToTarget> targets
	) {
	}

	public void doChangeWeapon(MapPoint agent, Weapon weapon) {
	}

	public void doUseItem(MapPoint agent, Item item) {
	}

	public void doPush(MapPoint agent, MapPoint target, boolean effective) {
	}
}

