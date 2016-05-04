package isogame.battle.data;

import isogame.engine.CorruptDataException;

public class StatusEffectInfo {
	public StatusEffectType type;
	public StatusEffectKind kind;
	public final int param;

	private static final int DEFAULT_PARAMETER = 1;

	public StatusEffectInfo(String effect)
		throws CorruptDataException
	{
		String parts[] = effect.split("\\s");
		if (parts.length < 1) throw new CorruptDataException("Expected status effect");
		this.type = StatusEffectType.parse(parts[0]);
		this.kind = type.getEffectKind();

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

