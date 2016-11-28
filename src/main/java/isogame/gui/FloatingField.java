package isogame.gui;

import java.util.Optional;

public class FloatingField extends TypedTextField<Double> {
	public FloatingField(double init) {
		super(init);
	}

	public FloatingField(String text) {
		super(text);
	}

	public FloatingField() {
		super();
	}

	@Override protected Optional<Double> parseValue(String t) {
		try {
			return Optional.of(Double.parseDouble(t));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	@Override protected Double getDefaultValue() {return 0.0;}

	@Override protected String showValue(Double i) {
		return i.toString();
	}
}


