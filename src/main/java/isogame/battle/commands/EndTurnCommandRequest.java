package isogame.battle.commands;

import isogame.battle.BattleState;

public class EndTurnCommandRequest extends CommandRequest {
	public EndTurnCommandRequest() {
	}

	@Override
	public Command makeCommand(BattleState turn) {
		return new EndTurnCommand();
	}
}

