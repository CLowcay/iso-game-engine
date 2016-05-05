package isogame.dataEditor;

import isogame.battle.data.CharacterInfo;
import isogame.gui.PositiveIntegerField;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
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

	public CharacterPane(CharacterInfo character) {
		super();

		name = new TextField(character.name);
		ap = new PositiveIntegerField(character.stats.ap);
		mp = new PositiveIntegerField(character.stats.mp);
		power = new PositiveIntegerField(character.stats.power);
		vitality = new PositiveIntegerField(character.stats.vitality);
		attack = new PositiveIntegerField(character.stats.attack);
		defence = new PositiveIntegerField(character.stats.defence);

		grid.addRow(0, new Label("Name"), name);
		grid.addRow(1, new Label("Base AP"), ap);
		grid.addRow(2, new Label("Base MP"), mp);
		grid.addRow(3, new Label("Base Power"), power);
		grid.addRow(4, new Label("Base Vitality"), vitality);
		grid.addRow(5, new Label("Base Attack"), attack);
		grid.addRow(6, new Label("Base Defence"), defence);

		this.setText(character.name);
		this.setContent(grid);
		this.textProperty().bind(name.textProperty());
	}
}

