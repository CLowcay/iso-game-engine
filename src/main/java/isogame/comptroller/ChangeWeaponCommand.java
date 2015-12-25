package isogame.comptroller;

import isogame.battle.Turn;
import isogame.battle.Weapon;
import isogame.engine.MapPoint;

public class ChangeWeaponCommand extends Command {
	private final MapPoint agent;
	private final Weapon weapon;

	public ChangeWeaponCommand(MapPoint agent, Weapon weapon) {
		this.agent = agent;
		this.weapon = weapon;
	}

	@Override
	public void docmd(Turn turn) {
		turn.doChangeWeapon(agent, weapon);
	}
}

