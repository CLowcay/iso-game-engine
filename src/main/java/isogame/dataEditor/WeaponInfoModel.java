package isogame.dataEditor;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class WeaponInfoModel {
	private final SimpleStringProperty name;
	private final SimpleIntegerProperty range;
	private final SimpleStringProperty attack;

	public WeaponInfoModel() {
		this.name = new SimpleStringProperty("New weapon");
		this.range = new SimpleIntegerProperty(1);
		this.attack = new SimpleStringProperty("none");
	}

	public SimpleStringProperty nameProperty() {
		return name;
	}

	public SimpleIntegerProperty rangeProperty() {
		return range;
	}

	public SimpleStringProperty attackProperty() {
		return attack;
	}
}

