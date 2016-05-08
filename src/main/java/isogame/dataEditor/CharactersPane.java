package isogame.dataEditor;

import com.diffplug.common.base.Errors;
import isogame.battle.data.CharacterInfo;
import isogame.battle.data.GameDataFactory;
import isogame.battle.data.Stats;
import isogame.battle.data.WeaponInfo;
import isogame.engine.CorruptDataException;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CharactersPane extends VBox {
	private final HBox tools = new HBox();
	private final Button save = new Button("Save");
	private final Button add = new Button("New");
	private final Button remove = new Button("Remove");
	private final Accordion characters;

	private final SimpleBooleanProperty changed = new SimpleBooleanProperty(false);

	private List<File> getStages(File baseDir) {
		List<File> r = Arrays.stream(baseDir.listFiles())
			.filter(f -> f.getName().endsWith(".map"))
			.collect(Collectors.toList());
		r.addAll(Arrays.stream(baseDir.listFiles())
			.filter(f -> f.isDirectory())
			.flatMap(f -> getStages(f).stream())
			.collect(Collectors.toList()));
		return r;
	}

	public CharactersPane(
		GameDataFactory data,
		File dataDir,
		AbilitiesPane abilities,
		WeaponsDialog weapons
	) {
		super();

		this.tools.getChildren().addAll(save, add, remove);
		save.disableProperty().bind(changed.not());

		this.characters = new Accordion(
			data.getCharacters().stream().map(c ->
				new CharacterPane(c, abilities, weapons))
			.collect(Collectors.toList()).toArray(new CharacterPane[0]));

		this.getChildren().addAll(tools, characters);

		save.setOnAction(event -> {
			try {
				List<File> stages = getStages(dataDir);
				List<CharacterInfo> cdata = characters.getPanes().stream()
					.map(Errors.rethrow().wrapFunction(
						c -> ((CharacterPane) c).getCharacter()))
					.collect(Collectors.toList());
				List<WeaponInfo> wdata = characters.getPanes().stream()
					.flatMap(Errors.rethrow().wrapFunction(
						c -> ((CharacterPane) c).getWeapons().stream()))
					.collect(Collectors.toList());

				data.writeToStream(
					new FileOutputStream(new File(dataDir, GameDataFactory.gameDataName)),
					stages, cdata, wdata
				);
				changed.setValue(false);
			} catch (IOException e) {
				Alert error = new Alert(Alert.AlertType.ERROR);
				error.setTitle("Error saving game data");
				error.setHeaderText(e.toString());
				error.showAndWait();
			} catch (RuntimeException e) {
				if (e.getCause() instanceof CorruptDataException) {  
					Alert error = new Alert(Alert.AlertType.ERROR);
					error.setTitle("Error in data");
					error.setHeaderText(e.toString());
					error.showAndWait();
				}
			}
		});

		add.setOnAction(event -> {
			Stats s = new Stats(3, 3, 1, 1, 1, 1);
			CharacterInfo c = new CharacterInfo("New character", s, new LinkedList<>());
			CharacterPane pane = new CharacterPane(c, abilities, weapons);
			characters.getPanes().add(pane);
			characters.setExpandedPane(pane);
			changed.setValue(true);
		});

		remove.setOnAction(event -> {
			CharacterPane r = (CharacterPane) characters.getExpandedPane();
			if (r != null) {
				Alert m = new Alert(Alert.AlertType.CONFIRMATION);
				m.setHeaderText("Really delete " + r.getName() + "?");
				m.showAndWait()
					.filter(response -> response == ButtonType.OK)
					.ifPresent(response -> {
						abilities.clearAbilities();
						characters.getPanes().remove(r);
					});
			}
			changed.setValue(true);
		});
	}
}

