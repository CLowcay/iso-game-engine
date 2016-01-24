package isogame.battle.commands;

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
	public void doCmd(Turn turn) throws CommandException {
		if (!turn.canChangeWeapon(agent, weapon))
			throw new CommandException("Invalid change command");
		turn.doChangeWeapon(agent, weapon);
	}
}

