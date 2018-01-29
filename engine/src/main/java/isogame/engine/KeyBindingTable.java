package isogame.engine;

import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ssjsjs.annotations.As;
import ssjsjs.annotations.Field;
import ssjsjs.annotations.Implicit;
import ssjsjs.annotations.JSON;
import ssjsjs.JSONable;

/**
 * A table of key bindings
 * */
public class KeyBindingTable implements JSONable {
	private final Map<KeyCodeCombination, KeyBinding> keys =
		new HashMap<>();
	private final Map<KeyBinding, KeyCodeCombination> primaryKeys =
		new HashMap<>();
	private final Map<KeyBinding, KeyCodeCombination> secondaryKeys =
		new HashMap<>();

	private List<JSONableKeyCodeCombination> allKeys = new ArrayList<>();

	public KeyBindingTable() {
	}

	@JSON
	public KeyBindingTable(
		@Implicit("getBinding") final Function<String, KeyBinding> getBinding,
		@Field("allKeys")@As("keys") final Collection<JSONableKeyCodeCombination> keys
	) {
		for (final JSONableKeyCodeCombination key : keys) {
			final KeyBinding binding = getBinding.apply(key.action);
			this.setPrimaryKey(binding, key.k);
			this.setSecondaryKey(binding, key.k);
		}
	}

	/**
	 * Get the action associated with a KeyEvent.
	 * @param e the KeyEvent to look up
	 * @return a KeyBinding if one is registered for the event, otherwise nothing
	 * */
	public Optional<KeyBinding> getKeyAction(final KeyEvent e) {
		for (final KeyCodeCombination k : keys.keySet()) {
			if (k.match(e)) return Optional.of(keys.get(k));
		}
		return Optional.empty();
	}

	/**
	 * Set the primary key for an action.
	 * @param b the key binding
	 * @param k the key to bind
	 * @return the action that was previously bound to that key, or null
	 * */
	public KeyBinding setPrimaryKey(
		final KeyBinding b, final KeyCodeCombination k
	) {
		return setKey(b, k, true, primaryKeys);
	}

	/**
	 * Set the secondary key for an action
	 * @param b the key binding
	 * @param k the key to bind
	 * @return the action that was previously bound to that key, or null
	 * */
	public KeyBinding setSecondaryKey(
		final KeyBinding b, final KeyCodeCombination k
	) {
		return setKey(b, k, false, secondaryKeys);
	}

	private KeyBinding setKey(
		final KeyBinding b,
		final KeyCodeCombination k,
		final boolean isPrimary,
		final Map<KeyBinding, KeyCodeCombination> keysMap
	) {
		if (k == null) {
			final KeyCodeCombination previous = keysMap.get(b);
			if (previous != null) {
				keys.remove(previous);
				keysMap.remove(b);
			}
			return null;
		}

		final KeyBinding last = keys.put(k, b);
		removeOldBinding(last, b, k);
		keysMap.put(b, k);
		allKeys.add(new JSONableKeyCodeCombination(k, b.toString(), isPrimary));
		return last;
	}

	private void removeOldBinding(
		final KeyBinding b0,
		final KeyBinding b1,
		final KeyCodeCombination k
	) {
		if (b0 != null && b0 != b1) {
			boolean remove = false;
			if (k.equals(primaryKeys.get(b0))) {
				primaryKeys.remove(b0);
				remove = true;
			}
			if (k.equals(secondaryKeys.get(b0))) {
				secondaryKeys.remove(b0);
				remove = true;
			}
			if (remove) {
				final String action = b0.toString();
				for (int i = 0; i < allKeys.size(); i++) {
					if (allKeys.get(i).action.equals(action)) {
						allKeys.remove(i); break;
					}
				}
			}
		}
	}

	/**
	 * Get the key binding associated with a key
	 * @param k the key
	 * @return the key binding
	 * */
	public KeyBinding getKeyAction(final KeyCodeCombination k) {
		return keys.get(k);
	}

	/**
	 * Get all the primary keys
	 * @return all the primary keys
	 * */
	public Map<KeyBinding, KeyCodeCombination> getPrimaryKeys() {
		return new HashMap<>(primaryKeys);
	}

	/**
	 * Get all the secondary keys
	 * @return all the secondary keys
	 * */
	public Map<KeyBinding, KeyCodeCombination> getSecondaryKeys() {
		return new HashMap<>(secondaryKeys);
	}

	/**
	 * Copy the key bindings from another table into this table
	 * @param table the key binding table to copy from
	 * */
	public void loadBindings(final KeyBindingTable table) {
		this.primaryKeys.clear();
		this.secondaryKeys.clear();
		this.keys.clear();

		final Map<KeyBinding, KeyCodeCombination> tp = table.getPrimaryKeys();
		final Map<KeyBinding, KeyCodeCombination> ts = table.getSecondaryKeys();

		for (final KeyBinding b : tp.keySet()) this.setPrimaryKey(b, tp.get(b));
		for (final KeyBinding b : ts.keySet()) this.setSecondaryKey(b, ts.get(b));
	}
}

