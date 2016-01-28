package isogame.battle.commands;

import isogame.battle.Battle;
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
	public void doCmd(Battle battle) throws CommandException {
		if (!battle.battleState.canChangeWeapon(agent, weapon))
			throw new CommandException("Invalid change command");
		battle.doChangeWeapon(agent, weapon);
	}
}

