package isogame.battle.commands;

import isogame.battle.Item;
import isogame.battle.Turn;
import isogame.battle.TurnCharacter;
import isogame.engine.MapPoint;

public class UseItemCommandRequest extends CommandRequest {
	private final MapPoint agent;
	private final Item item;

	public UseItemCommandRequest(MapPoint agent, Item item) {
		this.agent = agent;
		this.item = item;
	}

	@Override
	public Command makeCommand(Turn turn) throws CommandException {
		if (turn.canUseItem(agent, item)) {
			return new UseItemCommand(agent, item);
		} else {
			throw new CommandException("Invalid item command request");
		}
	}
}

