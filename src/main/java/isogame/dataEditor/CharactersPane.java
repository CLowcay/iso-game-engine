package isogame.dataEditor;

import isogame.battle.data.GameDataFactory;
import isogame.battle.data.CharacterInfo;
import isogame.battle.data.Stats;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.Accordion;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class CharactersPane extends VBox {
	private final HBox tools = new HBox();
	private final Button add = new Button("New");
	private final Button remove = new Button("Remove");
	private final Accordion characters;

	public CharactersPane(GameDataFactory data) {
		super();

		this.tools.getChildren().addAll(add, remove);

		this.characters = new Accordion(
			data.getCharacters().stream().map(c -> new CharacterPane(c))
			.collect(Collectors.toList()).toArray(new CharacterPane[0]));

		this.getChildren().addAll(tools, characters);

		add.setOnAction(event -> {
			Stats s = new Stats(3, 3, 1, 1, 1, 1);
			CharacterInfo c = new CharacterInfo("New character", s, new LinkedList<>());
			this.characters.getPanes().add(new CharacterPane(c));
		});
	}
}

