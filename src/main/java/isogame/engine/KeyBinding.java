package isogame.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * A KeyBinding is a token that represents an action that we might want to bind
 * a key to.
 * */
public class KeyBinding {
	// Keybindings for moving the view around
	public final static KeyBinding scrollUp = new KeyBinding("Scroll up");
	public final static KeyBinding scrollDown = new KeyBinding("Scroll down");
	public final static KeyBinding scrollLeft = new KeyBinding("Scroll left");
	public final static KeyBinding scrollRight = new KeyBinding("Scroll right");
	public final static KeyBinding rotateLeft = new KeyBinding("Rotate left");
	public final static KeyBinding rotateRight = new KeyBinding("Rotate right");

	private final String name;

	protected KeyBinding(final String name) {
		this.name = name;
	}

	public static List<KeyBinding> allBindings() {
		final List<KeyBinding> r = new ArrayList<>();
		r.add(rotateLeft);
		r.add(rotateRight);
		r.add(scrollUp);
		r.add(scrollDown);
		r.add(scrollLeft);
		r.add(scrollRight);
		return r;
	}

	@Override public String toString() { return name; }

	public static KeyBinding valueOf(final String s) {
		switch (s) {
			case "Scroll up": return scrollUp;
			case "Scroll down": return scrollDown;
			case "Scroll left": return scrollLeft;
			case "Scroll right": return scrollRight;
			case "Rotate left": return scrollLeft;
			case "Rotate right": return scrollRight;
			default:
				throw new RuntimeException("Unhandled keybinding " + s);
		}
	}
}

