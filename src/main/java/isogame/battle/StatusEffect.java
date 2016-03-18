package isogame.battle;

public class StatusEffect {
	public final String name;
	public final Stats buff;
	public final StatusEffectKind kind;

	public final double attackBuff;
	public final double defenceBuff;
	public final double chanceBuff;

	public StatusEffect(
		String name,
		Stats buff,
		StatusEffectKind kind,
		double attackBuff,
		double defenceBuff,
		double chanceBuff
	) {
		this.name = name;
		this.buff = buff;
		this.kind = kind;
		this.attackBuff = attackBuff;
		this.defenceBuff = defenceBuff;
		this.chanceBuff = chanceBuff;
	}

	public StatusEffect instantiate() {
		return this;
	}
}

