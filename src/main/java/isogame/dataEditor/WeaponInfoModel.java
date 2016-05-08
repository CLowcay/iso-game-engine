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

	public SimpleStringProperty nameProperty() { return name; }
	public String getName() { return name.getValue(); }

	public SimpleIntegerProperty rangeProperty() { return range; }
	public int getRange() { return range.getValue(); }

	public SimpleStringProperty attackProperty() { return attack; }
	public String getAttack() { return attack.getValue(); }
}

