package isogame.comptroller;

import java.util.List;

import isogame.battle.commands.StartBattleCommandRequest;

public interface LobbyListener {
	public void connectedToServer(List<String> players);
	public void playerNameInUse(String name);
	public void errorConnectingToServer(Exception e);
	public void playerHasLoggedOff(String player);
	public void playerRefusesChallenge(String player);
	public void challengeFrom(String player, StartBattleCommandRequest cmd);
	public void timeoutWaitingForOtherPlayer(String player);
	public void startBattle(BattleInProgress battle, String player);
}

