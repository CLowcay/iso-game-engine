package isogame.gui;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;

public class FloatingField extends TextField {
	public FloatingField(double init) {
		this();
		this.setText((new Double(init)).toString());
	}

	public FloatingField(String text) {
		this();
		this.setText(text);
	}

	public FloatingField() {
		super();

		this.setTextFormatter(new TextFormatter<>(change -> {
			if (change.isAdded() || change.isReplaced()) {
				try {
					Double.parseDouble(change.getText());
				} catch (NumberFormatException e) {
					change.setText("");
				}
			}
			return change;
		}));
	}

	public double getDouble() {
		try {
			return Double.parseDouble(this.getText());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public float getFloat() {
		try {
			return Float.parseFloat(this.getText());
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}


