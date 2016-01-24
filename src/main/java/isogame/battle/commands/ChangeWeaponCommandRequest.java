package isogame.battle.commands;

import isogame.battle.Turn;
import isogame.battle.Weapon;
import isogame.engine.MapPoint;

public class ChangeWeaponCommandRequest extends CommandRequest {
	private final MapPoint agent;
	private final Weapon weapon;

	public ChangeWeaponCommandRequest(MapPoint agent, Weapon weapon) {
		this.agent = agent;
		this.weapon = weapon;
	}

	@Override
	public Command makeCommand(Turn turn) throws CommandException {
		if (turn.canChangeWeapon(agent, weapon)) {
			return new ChangeWeaponCommand(agent, weapon);
		} else {
			throw new CommandException("Invalid change command request");
		}
	}
}

