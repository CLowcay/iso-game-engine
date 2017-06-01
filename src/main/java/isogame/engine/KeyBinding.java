package isogame.engine;

/**
 * A KeyBinding is a token that represents an action that we might want to bind
 * a key to.
 * */
public class KeyBinding {
	// Keybindings for moving the view around
	public final static KeyBinding scrollUp = new KeyBinding();
	public final static KeyBinding scrollDown = new KeyBinding();
	public final static KeyBinding scrollLeft = new KeyBinding();
	public final static KeyBinding scrollRight = new KeyBinding();
	public final static KeyBinding rotateLeft = new KeyBinding();
	public final static KeyBinding rotateRight = new KeyBinding();

	private KeyBinding() { super(); }
}

