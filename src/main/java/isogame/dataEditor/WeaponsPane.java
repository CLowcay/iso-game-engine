package isogame.dataEditor;

import isogame.gui.MappedList;
import isogame.gui.PositiveIntegerField;
import isogame.gui.StringField;
import isogame.gui.TypedTextFieldTreeTableCell;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.cell.ChoiceBoxTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class WeaponsPane extends VBox {
	private final TreeTableView<WeaponInfoModel> table;
	private final ObservableList<Integer> selected;

	private final FlowPane tools = new FlowPane(Orientation.HORIZONTAL);
	private final Button add = new Button("Add");
	private final Button remove = new Button("Remove");
	private final Button up = new Button("Up");
	private final Button down = new Button("Down");

	private final TreeTableColumn<WeaponInfoModel, String> name = new TreeTableColumn<>("Name");
	private final TreeTableColumn<WeaponInfoModel, Integer> range = new TreeTableColumn<>("Range");
	private final TreeTableColumn<WeaponInfoModel, String> attack = new TreeTableColumn<>("Attack");

	private TreeItem<WeaponInfoModel> tableRoot;

	private ObservableList<String> weaponsList = FXCollections.emptyObservableList();

	private void setAbilities(
		ObservableList<TreeItem<AbilityInfoModel>> abilitiesList
	) {
		weaponsList =
			new MappedList<>(
				abilitiesList.filtered(i -> i.getValue().getType().equals("weapon")),
					i -> i.getValue().getName());
		attack.setCellFactory(ChoiceBoxTreeTableCell.<WeaponInfoModel, String>
			forTreeTableColumn(weaponsList));
	}

	private final Label noCharacterMessage = new Label("No message selected");
	private final Label noWeaponsMessage = new Label();

	private SimpleBooleanProperty isCharacterLoaded =
		new SimpleBooleanProperty(false);
	public void setCharacter(
		ObservableValue<String> name,
		TreeItem<WeaponInfoModel> weapons,
		TreeItem<AbilityInfoModel> abilities
	) {
		setAbilities(abilities.getChildren());
		isCharacterLoaded.setValue(true);
		table.setPlaceholder(noWeaponsMessage);
		noWeaponsMessage.textProperty().bind(
			Bindings.concat("No weapons defined for ", name));
		tableRoot = weapons;
		tableRoot.setExpanded(true);
		table.setRoot(tableRoot);
		table.setShowRoot(false);
	}
	public void clearCharacter() {
		setCharacter(
			new SimpleStringProperty(""),
			new TreeItem<>(new WeaponInfoModel()),
			new TreeItem<>(new AbilityInfoModel(false, false)));
		isCharacterLoaded.setValue(false);
		table.setPlaceholder(noCharacterMessage);
	}

	public WeaponsPane() {
		super();

		tableRoot = new TreeItem<>(new WeaponInfoModel());
		tableRoot.setExpanded(true);
		table = new TreeTableView<WeaponInfoModel>(tableRoot);
		table.setShowRoot(false);
		table.setEditable(true);
		table.setPlaceholder(noCharacterMessage);

		TreeTableView.TreeTableViewSelectionModel<WeaponInfoModel> selection =
			table.getSelectionModel();
		selected = selection.getSelectedIndices();

		tools.getChildren().addAll(add, remove, up, down);
		VBox.setVgrow(table, Priority.ALWAYS);
		this.getChildren().addAll(tools, table);

		name.setCellValueFactory(new TreeItemPropertyValueFactory<WeaponInfoModel, String>("name"));
		range.setCellValueFactory(new TreeItemPropertyValueFactory<WeaponInfoModel, Integer>("range"));
		attack.setCellValueFactory(new TreeItemPropertyValueFactory<WeaponInfoModel, String>("attack"));

		name.setSortable(false);
		name.setPrefWidth(240);
		range.setSortable(false);
		attack.setSortable(false);
		name.setPrefWidth(120);

		name.setCellFactory(TypedTextFieldTreeTableCell.<WeaponInfoModel, String>
			forTreeTableColumn(StringField::new, selection));
		range.setCellFactory(TypedTextFieldTreeTableCell.<WeaponInfoModel, Integer>
			forTreeTableColumn(PositiveIntegerField::new, selection));
		attack.setCellFactory(ChoiceBoxTreeTableCell.<WeaponInfoModel, String>
			forTreeTableColumn(weaponsList));

		add.disableProperty().bind(isCharacterLoaded.not());
		remove.disableProperty().bind(Bindings.isEmpty(selected));

		add.setOnAction(event -> {
			if (isCharacterLoaded.getValue()) {
				tableRoot.getChildren().add(new TreeItem<>(new WeaponInfoModel()));
			}
		});
		remove.setOnAction(event -> {
			selected.stream()
				.sorted((a, b) -> {if (b < a) return -1; else if (b > a) return 1; else return 0;})
				.forEach(i -> {
					TreeItem<WeaponInfoModel> item = table.getTreeItem(i);
					item.getParent().getChildren().remove(item);
				});
		});

		table.getColumns().setAll(name, range, attack);
	}
}

