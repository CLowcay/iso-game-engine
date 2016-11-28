package isogame.gui;

import java.util.Optional;

public class PositiveIntegerField extends TypedTextField<Integer> {
	@Override protected Optional<Integer> parseValue(String t) {
		try {
			return Optional.of(Integer.parseUnsignedInt(t));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	@Override protected Integer getDefaultValue() {return 0;}

	@Override protected String showValue(Integer i) {
		return i.toString();
	}

	public PositiveIntegerField(int init) {
		super(init);
	}

	public PositiveIntegerField(String text) {
		super(text);
	}

	public PositiveIntegerField() {
		super();
	}
}

