package isogame.dataEditor;

import isogame.battle.data.CharacterInfo;
import isogame.gui.PositiveIntegerField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;

public class CharacterPane extends TitledPane {
	private final GridPane grid = new GridPane();
	private final TextField name;
	private final TextField ap;
	private final TextField mp;
	private final TextField power;
	private final TextField vitality;
	private final TextField attack;
	private final TextField defence;
	private final TreeItem<AbilityInfoModel> abilitiesRoot;
	private final TreeItem<WeaponInfoModel> weaponsRoot;

	public String getName() {
		return name.getText() == null? "" : name.getText();
	}

	public CharacterPane(
		CharacterInfo character,
		AbilitiesPane abilities,
		WeaponsDialog weapons
	) {
		super();

		abilitiesRoot = new TreeItem<>(new AbilityInfoModel(false, false));
		weaponsRoot = new TreeItem<>(new WeaponInfoModel());
		abilitiesRoot.setExpanded(true);
		weaponsRoot.setExpanded(true);

		name = new TextField(character.name);
		ap = new PositiveIntegerField(character.stats.ap);
		mp = new PositiveIntegerField(character.stats.mp);
		power = new PositiveIntegerField(character.stats.power);
		vitality = new PositiveIntegerField(character.stats.vitality);
		attack = new PositiveIntegerField(character.stats.attack);
		defence = new PositiveIntegerField(character.stats.defence);

		Button weaponsButton = new Button("Weapons ...");

		grid.addRow(0, new Label("Name"), name);
		grid.addRow(1, new Label("Base AP"), ap);
		grid.addRow(2, new Label("Base MP"), mp);
		grid.addRow(3, new Label("Base Power"), power);
		grid.addRow(4, new Label("Base Vitality"), vitality);
		grid.addRow(5, new Label("Base Attack"), attack);
		grid.addRow(6, new Label("Base Defence"), defence);
		grid.add(weaponsButton, 1, 7);

		this.setText(character.name);
		this.setContent(grid);
		this.textProperty().bind(name.textProperty());

		this.expandedProperty().addListener((v, oldv, newv) -> {
			if (newv) {
				abilities.setAbilities(name.textProperty(), abilitiesRoot);
				weapons.setCharacter(name.textProperty(), weaponsRoot, abilitiesRoot);
			}
		});

		weaponsButton.setOnAction(event -> {
			weapons.show();
		});
	}
}

