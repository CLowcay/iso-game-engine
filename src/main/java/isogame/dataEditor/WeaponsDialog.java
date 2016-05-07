package isogame.dataEditor;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TreeItem;
import javafx.stage.Modality;

public class WeaponsDialog extends Dialog<Void> {
	private final WeaponsPane weapons;
	public WeaponsDialog() {
		super();
		initModality(Modality.NONE);

		weapons = new WeaponsPane();
		weapons.setPrefWidth(400);
		weapons.setPrefHeight(300);
		weapons.setMinHeight(300);
		this.getDialogPane().setContent(weapons);
		this.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
	}

	public void setCharacter(
		ObservableValue<String> name,
		TreeItem<WeaponInfoModel> weapons,
		TreeItem<AbilityInfoModel> abilities
	) {
		titleProperty().bind(Bindings.concat("Weapons for ", name));
		this.weapons.setCharacter(name, weapons, abilities);
	}

	public void clearCharacter() {
		this.weapons.clearCharacter();
	}
}

