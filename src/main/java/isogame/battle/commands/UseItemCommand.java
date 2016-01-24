package isogame.battle.commands;

import isogame.battle.Item;
import isogame.battle.Turn;
import isogame.engine.MapPoint;

public class UseItemCommand extends Command {
	private final MapPoint agent;
	private final Item item;

	public UseItemCommand(MapPoint agent, Item item) {
		this.agent = agent;
		this.item = item;
	}

	@Override
	public void doCmd(Turn turn) throws CommandException {
		if (!turn.canUseItem(agent, item))
			throw new CommandException("Invalid item command");
		turn.doUseItem(agent, item);
	}
}

