package isogame.editor;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;

public class PositiveIntegerField extends TextField {
	PositiveIntegerField() {
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

