package isogame.comptroller;

import java.net.URL;

import isogame.battle.commands.StartBattleCommand;
import isogame.battle.commands.StartBattleCommandRequest;
import isogame.battle.data.Loadout;

public class Lobby implements Runnable {
	private final LobbyListener lobbyListener;
	private final URL url;
	private final int port;
	private final String playerName;

	public Lobby(LobbyListener lobbyListener, URL url, int port, String playerName) {
		this.lobbyListener = lobbyListener;
		this.url = url;
		this.port = port;
		this.playerName = playerName;
	}

	@Override
	public void run() {
		// connect to the server.  If this fails, terminate the thread.
	}

	public synchronized void startBattle(StartBattleCommandRequest cmd) {
		// send the battle request, receive a battle object, then send 'ready'.
	}

	public synchronized void challengePlayer(
		StartBattleCommandRequest cmd, String player
	) {
		// send the battle request, receive a battle object, then send 'ready'.
	}

	public synchronized void acceptChallenge(
		StartBattleCommandRequest cmd, Loadout me, String player
	) {
		// generate a battle object, send it to the other player
		// wait for a 'ready' response
	}

	public synchronized void refuseChallenge(String player) {
	}
}

