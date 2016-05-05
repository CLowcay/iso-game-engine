package isogame.gui;

public class StringField extends TypedTextField<String> {
	public StringField() {
		super();
	}

	public StringField(String v) {
		super();
		this.setText(v);
	}

	@Override
	public String getValue() {
		return this.getText();
	}

	@Override
	public void setValue(String value) {
		this.setText(value == null? "" : value);
	}
}

