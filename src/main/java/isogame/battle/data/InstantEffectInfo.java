package isogame.battle.data;

import isogame.engine.CorruptDataException;

public class InstantEffectInfo {
	public final InstantEffectType type;
	public final int param;

	private static final int DEFAULT_PARAMETER = 1;

	public InstantEffectInfo(String effect)
		throws CorruptDataException
	{
		String parts[] = effect.split("\\s");
		if (parts.length < 1) throw new CorruptDataException("Expected instant effect");
		this.type = InstantEffectType.parse(parts[0]);

		int paramv = DEFAULT_PARAMETER;
		if (parts.length >= 2) {
			try {
				paramv = Integer.parseInt(parts[1]);
			} catch (NumberFormatException e) {
				// ignore
			}
		}
		this.param = paramv;
	}
}

