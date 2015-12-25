package isogame.battle;

public class Ability {
	public final String name;
	public final Range range;
	public final boolean isMana;
	public final Ability replacement;
	public final double effFactor;

	public Ability(
		String name,
		Range range,
		boolean isMana,
		Ability replacement,
		double effFactor
	) {
		this.name = name;
		this.range = range;
		this.isMana = isMana;
		this.replacement = replacement;
		this.effFactor = effFactor;
	}
}

