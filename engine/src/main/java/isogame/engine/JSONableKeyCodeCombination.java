package isogame.engine;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination.ModifierValue;
import ssjsjs.annotations.Field;
import ssjsjs.annotations.JSON;
import ssjsjs.JSONable;

/**
 * A KeyCodeCombination which can be serialized/deserialized from/to JSON
 * */
public class JSONableKeyCodeCombination implements JSONable {
	private final KeyCode code;
	private final ModifierValue control;
	private final ModifierValue alt;
	private final ModifierValue meta;
	private final ModifierValue shift;
	private final ModifierValue shortcut;
	private final boolean primary;

	public final String action;

	public final KeyCodeCombination k;

	/**
	 * @param k the key to extend
	 * @param action the action to bind to 
	 * @param primary is this a primary key?
	 * */
	public JSONableKeyCodeCombination(
		final KeyCodeCombination k,
		final String action,
		final boolean primary
	) {
		this.k = k;
		this.code = k.getCode();
		this.control = k.getControl();
		this.alt = k.getAlt();
		this.meta = k.getMeta();
		this.shift = k.getShift();
		this.shortcut = k.getShortcut();
		this.action = action;
		this.primary = primary;
	}

	@JSON
	public JSONableKeyCodeCombination(
		@Field("code") final KeyCode code,
		@Field("control") final ModifierValue control,
		@Field("alt") final ModifierValue alt,
		@Field("meta") final ModifierValue meta,
		@Field("shift") final ModifierValue shift,
		@Field("shortcut") final ModifierValue shortcut,
		@Field("action") final String action,
		@Field("primary") final boolean primary
	) {
		this.k = new KeyCodeCombination(code, shift, control, alt, meta, shortcut);
		this.code = code;
		this.control = control;
		this.alt = alt;
		this.meta = meta;
		this.shift = shift;
		this.shortcut = shortcut;
		this.action = action;
		this.primary = primary;
	}
}

