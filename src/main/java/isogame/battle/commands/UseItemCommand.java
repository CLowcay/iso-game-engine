package isogame.battle.commands;

import isogame.battle.Battle;
import isogame.battle.Item;
import isogame.engine.MapPoint;

public class UseItemCommand extends Command {
	private final MapPoint agent;
	private final Item item;

	public UseItemCommand(MapPoint agent, Item item) {
		this.agent = agent;
		this.item = item;
	}

	@Override
	public void doCmd(Battle battle) throws CommandException {
		if (!battle.battleState.canUseItem(agent, item))
			throw new CommandException("Invalid item command");
		battle.doUseItem(agent, item);
	}
}

