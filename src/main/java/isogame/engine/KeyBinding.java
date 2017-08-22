package isogame.engine;

import java.util.HashSet;
import java.util.Set;

/**
 * A KeyBinding is a token that represents an action that we might want to bind
 * a key to.
 * */
public class KeyBinding {
	// Keybindings for moving the view around
	public final static KeyBinding scrollUp = new KeyBinding("scrollUp");
	public final static KeyBinding scrollDown = new KeyBinding("scrollDown");
	public final static KeyBinding scrollLeft = new KeyBinding("scrollLeft");
	public final static KeyBinding scrollRight = new KeyBinding("scrollRight");
	public final static KeyBinding rotateLeft = new KeyBinding("rotateLeft");
	public final static KeyBinding rotateRight = new KeyBinding("rotateRight");

	private final String name;

	protected KeyBinding(final String name) {
		this.name = name;
	}

	public static Set<KeyBinding> allBindings() {
		final Set<KeyBinding> r = new HashSet<>();
		r.add(scrollUp);
		r.add(scrollDown);
		r.add(scrollLeft);
		r.add(scrollRight);
		r.add(rotateLeft);
		r.add(rotateRight);
		return r;
	}

	@Override public String toString() { return name; }

	public static KeyBinding valueOf(final String s) {
		switch (s) {
			case "scrollUp": return scrollUp;
			case "scrollDown": return scrollDown;
			case "scrollLeft": return scrollLeft;
			case "scrollRight": return scrollRight;
			case "rotateLeft": return scrollLeft;
			case "rotateRight": return scrollRight;
			default:
				throw new RuntimeException("Unhandled keybinding " + s);
		}
	}
}

