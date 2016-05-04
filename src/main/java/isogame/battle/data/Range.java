package isogame.battle.data;

import isogame.engine.CorruptDataException;
import org.json.simple.JSONObject;

public class Range {
	public final int range;
	public final int radius;
	public final boolean piercing; // ignore for now
	public final int ribbon;       // ignore for now
	public final TargetMode targetMode;
	public final int nTargets;
	public final boolean los;

	public Range(
		int range, int radius,
		boolean piercing, int ribbon,
		TargetMode targetMode,
		int nTargets, boolean los
	) {
		this.range = range;
		this.piercing = piercing;
		this.ribbon = ribbon;
		this.radius = radius;
		this.targetMode = targetMode;
		this.nTargets = nTargets;
		this.los = los;
	}

	public static Range fromJSON(JSONObject json)
		throws CorruptDataException
	{
		Object rrange = json.get("range");
		Object rpiercing = json.get("piercing");
		Object rribbon = json.get("ribbon");
		Object rradius = json.get("radius");
		Object rtargetMode = json.get("targetMode");
		Object rnTargets = json.get("nTargets");
		Object rlos = json.get("los");

		if (rrange == null) throw new CorruptDataException("Missing range range");
		if (rpiercing == null) throw new CorruptDataException("Missing piercing");
		if (rribbon == null) throw new CorruptDataException("Missing ribbon");
		if (rtargetMode == null) throw new CorruptDataException("Missing targetMode");
		if (rnTargets == null) throw new CorruptDataException("Missing nTargets");
		if (rlos == null) throw new CorruptDataException("Missing los");

		try {
			Number range = (Number) rrange;
			Boolean piercing = (Boolean) rpiercing;
			Number ribbon = (Number) rribbon;
			Number radius = (Number) rradius;
			String targetMode = (String) rtargetMode;
			Number nTargets = (Number) rnTargets;
			Boolean los = (Boolean) rlos;

			return new Range(
				range.intValue(), radius.intValue(),
				piercing, ribbon.intValue(),
				new TargetMode(targetMode),
				nTargets.intValue(), los);
		} catch (ClassCastException e) {
			throw new CorruptDataException("Type error in range", e);
		}
	}
}

