package isogame.battle.commands;

import java.util.concurrent.ThreadLocalRandom;

import isogame.battle.data.GameDataFactory;
import isogame.battle.data.Loadout;
import isogame.engine.MapPoint;
import isogame.engine.StageInfo;

/**
 * This one works a bit different.  Player 1 generates a
 * StartBattleCommandRequest and sends to it player 2.  Player 2 then generates
 * his StartBattleCommandRequest and uses it to make a StartBattleCommand,
 * which he executes then sends to player 1, who executes it.  Then, the battle
 * begins.
 * */
public class StartBattleCommandRequest {
	private final String stage;
	private final Loadout me;

	private static final MapPoint[] mapPointArray = new MapPoint[0];

	public StartBattleCommandRequest(String stage, Loadout me) {
		this.stage = stage;
		this.me = me;
	}

	public StartBattleCommand makeCommand(
		StartBattleCommandRequest p1, GameDataFactory factory
	) {
		StageInfo si = factory.getStage(stage);
		MapPoint[] p1ps = si.getPlayerStartTiles().toArray(mapPointArray);
		MapPoint[] p2ps = si.getAIStartTiles().toArray(mapPointArray);
		shuffleN(p1ps, 4);
		shuffleN(p2ps, 4);

		return new StartBattleCommand(
			stage, p1.me, me,
			p1ps[0], p1ps[1], p1ps[2], p1ps[3],
			p2ps[0], p2ps[1], p2ps[2], p2ps[3]);
	}

	private <T> void shuffleN(T[] data, int n) {
		int l = 0;
		T t;
		while (l < n) {
			int s = ThreadLocalRandom.current().nextInt(l, n);
			t = data[l];
			data[l] = data[s];
			data[s] = t;
			l += 1;
		}
	}
}

