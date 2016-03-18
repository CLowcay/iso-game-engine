package isogame.comptroller;

import isogame.battle.Ability;
import isogame.battle.Battle;
import isogame.battle.Character;
import isogame.battle.commands.Command;
import isogame.battle.commands.CommandException;
import isogame.battle.commands.CommandRequest;
import isogame.battle.commands.EndTurnCommand;
import isogame.battle.Player;
import isogame.engine.MapPoint;
import javafx.application.Platform;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class BattleInProgress implements Runnable {
	private final Battle battle;
	private final Player thisPlayer;
	private final boolean thisPlayerGoesFirst;
	private final BattleListener listener;

	private final BlockingQueue<CommandRequest> commandRequests =
		new LinkedBlockingQueue<>();

	public BattleInProgress(
		Battle battle, Player thisPlayer,
		boolean thisPlayerGoesFirst, BattleListener listener
	) {
		this.battle = battle;
		this.thisPlayer = thisPlayer;
		this.thisPlayerGoesFirst = thisPlayerGoesFirst;
		this.listener = listener;
	}


	@Override
	public void run() {
		boolean gameOver = false;
		if (!thisPlayerGoesFirst) {
			otherTurn();
		}

		while (!gameOver) {
			turn();
			otherTurn();
			// TODO: check for game over condition
		}

		// TODO: determine win condition
		Platform.runLater(() -> listener.endBattle(true));
	}

	private void turn() {
		while(true) {
			try {
				CommandRequest r = commandRequests.take();
				Command cmd = r.makeCommand(battle.battleState);
				Platform.runLater(() -> listener.command(cmd));
				// TODO: hook into network code here
				if (cmd instanceof EndTurnCommand) {
					return;
				}
			} catch (CommandException e) {
				Platform.runLater(() -> listener.badCommand(e));
			} catch (InterruptedException e) {
				// Do nothing
			}
		}
	}

	private void otherTurn() {
		// TODO: hook into network code here
		return;
	}

	public void requestCommand(CommandRequest cmd) {
		boolean retry = true;
		while (retry) {
			try {
				commandRequests.put(cmd);
				retry = false;
			} catch (InterruptedException e) {
				// do nothing
			}
		}
	}

	public synchronized Future<Collection<MapPoint>> getMoveRange(Character c) {
		return null;
	}

	public synchronized Future<Collection<MapPoint>> getTargetingInfo(Character c, Ability a) {
		return null;
	}
}

