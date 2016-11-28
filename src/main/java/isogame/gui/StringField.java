package isogame.gui;

import java.util.Optional;

public class StringField extends TypedTextField<String> {
	public StringField() {
		super();
	}

	public StringField(String v) {
		super();
		this.setText(v);
	}

	@Override protected Optional<String> parseValue(String t) {
		return Optional.of(t);
	}

	@Override protected String getDefaultValue() {return "";}

	@Override protected String showValue(String i) {
		return i;
	}
}

