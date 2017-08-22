package isogame.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A table of key bindings
 * */
public class KeyBindingTable implements HasJSONRepresentation {
	private final Map<KeyCodeCombination, KeyBinding> keys =
		new HashMap<>();
	private final Map<KeyBinding, KeyCodeCombination> primaryKeys =
		new HashMap<>();
	private final Map<KeyBinding, KeyCodeCombination> secondaryKeys =
		new HashMap<>();

	public Optional<KeyBinding> getKeyAction(final KeyEvent e) {
		for (final KeyCodeCombination k : keys.keySet()) {
			if (k.match(e)) return Optional.of(keys.get(k));
		}
		return Optional.empty();
	}

	public void setPrimaryKey(final KeyBinding b, final KeyCodeCombination k) {
		final KeyBinding last = keys.put(k, b);
		removeOldBinding(last, b, k);
		primaryKeys.put(b, k);
	}

	public void setSecondaryKey(final KeyBinding b, final KeyCodeCombination k) {
		final KeyBinding last = keys.put(k, b);
		removeOldBinding(last, b, k);
		secondaryKeys.put(b, k);
	}

	private void removeOldBinding(
		final KeyBinding b0,
		final KeyBinding b1,
		final KeyCodeCombination k
	) {
		if (b0 != null && b0 != b1) {
			if (primaryKeys.get(b0) == k) primaryKeys.remove(b0);
			if (secondaryKeys.get(b0) == k) secondaryKeys.remove(b0);
		}
	}

	public KeyBinding getKeyAction(final KeyCodeCombination k) {
		return keys.get(k);
	}

	public Map<KeyBinding, KeyCodeCombination> getPrimaryKeys() {
		return new HashMap<>(primaryKeys);
	}

	public Map<KeyBinding, KeyCodeCombination> getSecondaryKeys() {
		return new HashMap<>(secondaryKeys);
	}

	public void loadBindings(final KeyBindingTable table) {
		this.primaryKeys.clear();
		this.secondaryKeys.clear();
		this.keys.clear();

		final Map<KeyBinding, KeyCodeCombination> tp = table.getPrimaryKeys();
		final Map<KeyBinding, KeyCodeCombination> ts = table.getSecondaryKeys();

		for (final KeyBinding b : tp.keySet()) this.setPrimaryKey(b, tp.get(b));
		for (final KeyBinding b : ts.keySet()) this.setSecondaryKey(b, ts.get(b));
	}

	@Override public JSONObject getJSON() {
		final JSONObject o = new JSONObject();
		final JSONArray a = new JSONArray();
		for (final KeyCodeCombination key : keys.keySet()) {
			final JSONObject k = new JSONObject();
			k.put("code", key.getCode().toString());
			k.put("control", key.getControl().toString());
			k.put("alt", key.getAlt().toString());
			k.put("shift", key.getShift().toString());
			k.put("shortcut", key.getShortcut().toString());
			k.put("action", keys.get(key).toString());
			a.put(k);
		}
		o.put("keys", a);
		return o;
	}

	public static KeyBindingTable fromJSON(
		final JSONObject json,
		final Function<String, KeyBinding> getBinding
	) {
		final KeyBindingTable table = new KeyBindingTable();

		final JSONArray a = json.optJSONArray("keys");

		for (int i = 0; i < a.length(); i++) {
			final JSONObject ko = a.getJSONObject(i);
			final KeyCode code = KeyCode.valueOf(ko.getString("code"));
			final KeyCombination.ModifierValue control =
				KeyCombination.ModifierValue.valueOf(ko.getString("control"));
			final KeyCombination.ModifierValue alt =
				KeyCombination.ModifierValue.valueOf(ko.getString("alt"));
			final KeyCombination.ModifierValue shift =
				KeyCombination.ModifierValue.valueOf(ko.getString("shift"));
			final KeyCombination.ModifierValue shortcut =
				KeyCombination.ModifierValue.valueOf(ko.getString("shortcut"));

			table.keys.put(
				new KeyCodeCombination(code, shift, control, alt,
					KeyCombination.ModifierValue.UP, shortcut),
				getBinding.apply(ko.getString("action")));
		}

		return table;
	}
}

