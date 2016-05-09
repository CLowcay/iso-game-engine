package isogame.battle;

import isogame.battle.data.Stats;
import isogame.battle.data.StatusEffectInfo;

public class StatusEffect {
	public final StatusEffectInfo info;
	//public final Stats buff;

	public final double attackBuff;
	public final double defenceBuff;
	public final double chanceBuff;

	public StatusEffect(StatusEffectInfo info) {
		this.info = info;

		this.attackBuff = 0;
		this.defenceBuff = 0;
		this.chanceBuff = 0;
	}

	public StatusEffect instantiate() {
		return this;
	}
}

