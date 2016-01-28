package isogame.comptroller;

import isogame.battle.Ability;
import isogame.battle.Battle;
import isogame.battle.Character;
import isogame.battle.commands.Command;
import isogame.battle.commands.CommandRequest;
import isogame.battle.Player;

public class BattleInProgress implements Runnable {
	private final Battle battle;
	private final Player thisPlayer;

	public BattleInProgress(Battle battle, Player thisPlayer) {
		this.battle = battle;
		this.thisPlayer = thisPlayer;
	}

	@Override
	public void run() {
	}

	public synchronized void requestCommand(CommandRequest cmd) {
	}

	public synchronized void getMoveRange(Character c) {
	}

	public synchronized void getTargetingInfo(Character c, Ability a) {
	}
}

