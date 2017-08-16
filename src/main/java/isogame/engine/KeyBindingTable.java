package isogame.engine;

import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A table of key bindings
 * */
public class KeyBindingTable {
	public final Map<KeyCodeCombination, KeyBinding> keys = new HashMap<>();

	public Optional<KeyBinding> getKeyAction(final KeyEvent e) {
		for (final KeyCodeCombination k : keys.keySet()) {
			if (k.match(e)) return Optional.of(keys.get(k));
		}
		return Optional.empty();
	}
}

