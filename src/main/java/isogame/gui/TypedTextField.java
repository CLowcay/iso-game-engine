package isogame.gui;

import javafx.scene.control.TextField;

public abstract class TypedTextField<T> extends TextField {
	public abstract T getValue();
	public abstract void setValue(T value);
}

