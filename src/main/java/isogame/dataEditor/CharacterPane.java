package isogame.dataEditor;

import isogame.battle.data.CharacterInfo;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;

public class CharacterPane extends TitledPane {
	private final GridPane grid = new GridPane();
	private final TextField name = new TextField();
	private final TextField ap = new TextField();
	private final TextField mp = new TextField();
	private final TextField power = new TextField();
	private final TextField vitality = new TextField();
	private final TextField attack = new TextField();
	private final TextField defence = new TextField();

	public CharacterPane(CharacterInfo character) {
		super();

		grid.addRow(0, new Label("Name"), name);
		grid.addRow(1, new Label("Base AP"), ap);
		grid.addRow(2, new Label("Base MP"), mp);
		grid.addRow(3, new Label("Base Power"), power);
		grid.addRow(4, new Label("Base Vitality"), vitality);
		grid.addRow(5, new Label("Base Attack"), attack);
		grid.addRow(6, new Label("Base Defence"), defence);

		this.setText(character.name);
		this.setContent(grid);
	}
}

