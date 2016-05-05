package isogame.gui;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;

public class PositiveIntegerField extends TextField {
	public PositiveIntegerField(int init) {
		this();
		this.setText((new Integer(init)).toString());
	}

	public PositiveIntegerField(String text) {
		this();
		this.setText(text);
	}

	public PositiveIntegerField() {
		super();

		this.setTextFormatter(new TextFormatter<>(change -> {
			if (change.isAdded() || change.isReplaced()) {
				try {
					Integer.parseUnsignedInt(change.getText());
				} catch (NumberFormatException e) {
					change.setText("");
				}
			}
			return change;
		}));
	}

	public int getInt() {
		try {
			return Integer.parseUnsignedInt(this.getText());
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}

