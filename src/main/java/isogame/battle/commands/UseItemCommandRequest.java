package isogame.battle.commands;

import isogame.battle.BattleState;
import isogame.battle.Item;
import isogame.engine.MapPoint;

public class UseItemCommandRequest extends CommandRequest {
	private final MapPoint agent;
	private final Item item;

	public UseItemCommandRequest(MapPoint agent, Item item) {
		this.agent = agent;
		this.item = item;
	}

	@Override
	public Command makeCommand(BattleState battleState) throws CommandException {
		if (battleState.canUseItem(agent, item)) {
			return new UseItemCommand(agent, item);
		} else {
			throw new CommandException("Invalid item command request");
		}
	}
}

