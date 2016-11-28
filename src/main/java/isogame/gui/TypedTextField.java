package isogame.gui;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import java.util.Optional;

/**
 * A text field that can only contain values of a specified format.
 * */
public abstract class TypedTextField<T> extends TextField {
	protected abstract Optional<T> parseValue(String t);
	protected abstract T getDefaultValue();
	protected abstract String showValue(T v);

	public TypedTextField(T init) {
		this();
		this.setText((showValue(init)).toString());
	}

	public TypedTextField(String text) {
		this();
		this.setText(text);
	}

	public TypedTextField() {
		super();

		this.setTextFormatter(new TextFormatter<>(change -> {
			if ((change.isAdded() || change.isReplaced())
				&& !parseValue(change.getText()).isPresent()
			) {
				change.setText("");
			}
			return change;
		}));
	}

	public void setValue(T v) {
		this.setText(v == null? "" : showValue(v));
	}

	public T getValue() {
		return parseValue(this.getText()).orElse(getDefaultValue());
	}

}

