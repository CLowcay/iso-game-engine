package isogame.comptroller;

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
	public void docmd(Turn turn) {
		turn.doUseItem(agent, item);
	}
}

