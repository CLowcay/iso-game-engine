package isogame.dataEditor;

import isogame.battle.data.AbilityInfo;
import isogame.battle.data.AbilityType;
import isogame.gui.FloatingField;
import isogame.gui.PositiveIntegerField;
import isogame.gui.StringField;
import isogame.gui.TypedTextFieldTreeTableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.ChoiceBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AbilitiesPane extends VBox {
	private final TreeTableView<AbilityInfoModel> table;
	private final TreeItem<AbilityInfoModel> tableRoot;

	private final FlowPane tools = new FlowPane(Orientation.HORIZONTAL);
	private final Button add = new Button("Add ability");
	private final Button remove = new Button("Remove ability");
	private final Button addSubsequent = new Button("Add subsequent ability");
	private final Button addMana = new Button("Add mana ability");
	private final Button up = new Button("Up");
	private final Button down = new Button("Down");
	
	private final TreeTableColumn<AbilityInfoModel, String> name = new TreeTableColumn<>("Name");
	private final TreeTableColumn<AbilityInfoModel, String> type = new TreeTableColumn<>("Type");
	private final TreeTableColumn<AbilityInfoModel, Integer> ap = new TreeTableColumn<>("AP cost");
	private final TreeTableColumn<AbilityInfoModel, Integer> mp = new TreeTableColumn<>("MP cost");
	private final TreeTableColumn<AbilityInfoModel, Integer> pp = new TreeTableColumn<>("PP cost");
	private final TreeTableColumn<AbilityInfoModel, Double> eff = new TreeTableColumn<>("Efficiency");
	private final TreeTableColumn<AbilityInfoModel, Double> chance = new TreeTableColumn<>("Chance to inflict status");
	private final TreeTableColumn<AbilityInfoModel, Boolean> heal = new TreeTableColumn<>("Healing");

	private final TreeTableColumn<AbilityInfoModel, Integer> range = new TreeTableColumn<>("Range");
	private final TreeTableColumn<AbilityInfoModel, Integer> radius = new TreeTableColumn<>("Radius");
	private final TreeTableColumn<AbilityInfoModel, Boolean> piercing = new TreeTableColumn<>("Piercing");
	private final TreeTableColumn<AbilityInfoModel, Integer> ribbon = new TreeTableColumn<>("Ribbon");
	private final TreeTableColumn<AbilityInfoModel, String> targetMode = new TreeTableColumn<>("Target");
	private final TreeTableColumn<AbilityInfoModel, Integer> nTargets = new TreeTableColumn<>("Number of targets");
	private final TreeTableColumn<AbilityInfoModel, Boolean> los = new TreeTableColumn<>("Requires LOS");

	private final TreeTableColumn<AbilityInfoModel, Boolean> useWeaponRange = new TreeTableColumn<>("Use weapon range");
	private final TreeTableColumn<AbilityInfoModel, Integer> recursion = new TreeTableColumn<>("Recursion");

	private final TreeTableColumn<AbilityInfoModel, String> instantBefore = new TreeTableColumn<>("Instant before damage");
	private final TreeTableColumn<AbilityInfoModel, String> instantAfter = new TreeTableColumn<>("Instant after damage");
	private final TreeTableColumn<AbilityInfoModel, String> statusEffect = new TreeTableColumn<>("Status effect");

	private final ObservableList<String> types =
		FXCollections.observableArrayList(enumValues(AbilityType.class));

	private final ObservableList<String> targetModes =
		FXCollections.observableArrayList("E", "A", "S", "EA", "ES", "AS", "EAS");

	private static String[] enumValues(Class<? extends Enum> c) {
		return Arrays.stream(c.getEnumConstants())
			.map(x -> x.toString().toLowerCase())
			.collect(Collectors.toList()).toArray(new String[0]);
	}

	private final Image manaIcon = new Image(
		getClass().getResourceAsStream("/editor_assets/mana_ability.png"));
	private final Image subsequentIcon = new Image(
		getClass().getResourceAsStream("/editor_assets/sub_ability.png"));

	public AbilitiesPane(Collection<AbilityInfo> abilities) {
		super();

		tableRoot = new TreeItem<>(new AbilityInfoModel(false, false));
		tableRoot.setExpanded(true);
		table = new TreeTableView<AbilityInfoModel>(tableRoot);
		table.setShowRoot(false);
		table.setEditable(true);

		tools.getChildren().addAll(add, remove, addSubsequent, addMana, up, down);
		this.getChildren().addAll(tools, table);

		add.setOnAction(event -> {
			tableRoot.getChildren().add(new TreeItem<>(new AbilityInfoModel(false, false)));
		});

		remove.setOnAction(event -> {
			table.getSelectionModel().getSelectedIndices().stream()
				.sorted((a, b) -> {if (b < a) return -1; else if (b > a) return 1; else return 0;})
				.forEach(i -> {
					TreeItem<AbilityInfoModel> item = table.getTreeItem(i);
					item.getParent().getChildren().remove(item);
				});

		});

		addMana.setOnAction(event -> {
			List<Integer> selected = table.getSelectionModel().getSelectedIndices();
			if (selected.size() >= 1) {
				TreeItem<AbilityInfoModel> item = table.getTreeItem(selected.get(0));
				AbilityInfoModel v = item.getValue();
				AbilityInfoModel pv = item.getParent().getValue();
				if (!v.getIsMana() && !v.getIsSubsequent() && !pv.getIsMana()) {
					item.getChildren().add(new TreeItem<>(v.cloneMana(), new ImageView(manaIcon)));
				}
			}
		});

		addSubsequent.setOnAction(event -> {
			List<Integer> selected = table.getSelectionModel().getSelectedIndices();
			if (selected.size() >= 1) {
				TreeItem<AbilityInfoModel> item = table.getTreeItem(selected.get(0));
				TreeItem<AbilityInfoModel> parent;
				if (item.getValue().getIsSubsequent()) {
					parent = item.getParent();
				} else {
					parent = item;
				}

				List<TreeItem<AbilityInfoModel>> all = parent.getChildren();
				int i;
				AbilityInfoModel toClone = item.getValue();
				for (i = 0; i < all.size(); i++) {
					if (all.get(i).getValue().getIsSubsequent()) {
						toClone = all.get(i).getValue();
					} else {
						break;
					}
				}
				all.add(i, new TreeItem<>(toClone.cloneSubsequent(), new ImageView(subsequentIcon)));
			}
		});

		up.setOnAction(event -> {
			List<Integer> selected = table.getSelectionModel().getSelectedIndices();
			if (selected.size() >= 1) {
				TreeItem<AbilityInfoModel> item = table.getTreeItem(selected.get(0));
				AbilityInfoModel v = item.getValue();
				if (!v.getIsMana()) {
					List<TreeItem<AbilityInfoModel>> all = item.getParent().getChildren();
					int i = all.indexOf(item);
					if (i > 0) {
						all.remove(i);
						all.add(i - 1, item);
						table.getSelectionModel().select(selected.get(0) - 1);
					}
				}
			}
		});

		down.setOnAction(event -> {
			List<Integer> selected = table.getSelectionModel().getSelectedIndices();
			if (selected.size() >= 1) {
				TreeItem<AbilityInfoModel> item = table.getTreeItem(selected.get(0));
				AbilityInfoModel v = item.getValue();
				if (!v.getIsMana()) {
					List<TreeItem<AbilityInfoModel>> all = item.getParent().getChildren();
					int i = all.indexOf(item);
					if (i < all.size() - 1 && !all.get(i + 1).getValue().getIsMana()) {
						all.remove(i);
						all.add(i + 1, item);
						// TODO: why doesn't this work
						table.getSelectionModel().select(selected.get(0) + 1);
					}
				}
			}
		});

		name.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, String>("name"));
		type.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, String>("type"));
		ap.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, Integer>("ap"));
		mp.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, Integer>("mp"));
		pp.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, Integer>("pp"));
		eff.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, Double>("eff"));
		chance.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, Double>("chance"));
		heal.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, Boolean>("heal"));
		range.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, Integer>("range"));
		radius.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, Integer>("radius"));
		piercing.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, Boolean>("piercing"));
		ribbon.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, Integer>("ribbon"));
		targetMode.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, String>("targetMode"));
		nTargets.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, Integer>("nTargets"));
		los.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, Boolean>("los"));
		useWeaponRange.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, Boolean>("useWeaponRange"));
		recursion.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, Integer>("recursion"));
		instantBefore.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, String>("instantBefore"));
		instantAfter.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, String>("instantAfter"));
		statusEffect.setCellValueFactory(new TreeItemPropertyValueFactory<AbilityInfoModel, String>("statusEffect"));

		name.setSortable(false);
		type.setSortable(false);
		ap.setSortable(false);
		mp.setSortable(false);
		pp.setSortable(false);
		eff.setSortable(false);
		chance.setSortable(false);
		heal.setSortable(false);
		range.setSortable(false);
		radius.setSortable(false);
		piercing.setSortable(false);
		ribbon.setSortable(false);
		targetMode.setSortable(false);
		nTargets.setSortable(false);
		los.setSortable(false);
		useWeaponRange.setSortable(false);
		recursion.setSortable(false);
		instantBefore.setSortable(false);
		instantAfter.setSortable(false);
		statusEffect.setSortable(false);

		name.setCellFactory(TypedTextFieldTreeTableCell.<AbilityInfoModel, String>
			forTreeTableColumn(StringField::new));
		type.setCellFactory(ChoiceBoxTreeTableCell.<AbilityInfoModel, String>
			forTreeTableColumn(types));
		ap.setCellFactory(TypedTextFieldTreeTableCell.<AbilityInfoModel, Integer>
			forTreeTableColumn(PositiveIntegerField::new));
		mp.setCellFactory(TypedTextFieldTreeTableCell.<AbilityInfoModel, Integer>
			forTreeTableColumn(PositiveIntegerField::new));
		pp.setCellFactory(TypedTextFieldTreeTableCell.<AbilityInfoModel, Integer>
			forTreeTableColumn(PositiveIntegerField::new));
		eff.setCellFactory(TypedTextFieldTreeTableCell.<AbilityInfoModel, Double>
			forTreeTableColumn(FloatingField::new));
		chance.setCellFactory(TypedTextFieldTreeTableCell.<AbilityInfoModel, Double>
			forTreeTableColumn(FloatingField::new));
		heal.setCellFactory(CheckBoxTreeTableCell.<AbilityInfoModel>
			forTreeTableColumn(heal));
		range.setCellFactory(TypedTextFieldTreeTableCell.<AbilityInfoModel, Integer>
			forTreeTableColumn(PositiveIntegerField::new));
		radius.setCellFactory(TypedTextFieldTreeTableCell.<AbilityInfoModel, Integer>
			forTreeTableColumn(PositiveIntegerField::new));
		piercing.setCellFactory(CheckBoxTreeTableCell.<AbilityInfoModel>
			forTreeTableColumn(piercing));
		ribbon.setCellFactory(TypedTextFieldTreeTableCell.<AbilityInfoModel, Integer>
			forTreeTableColumn(PositiveIntegerField::new));
		targetMode.setCellFactory(ChoiceBoxTreeTableCell.<AbilityInfoModel, String>
			forTreeTableColumn(targetModes));
		nTargets.setCellFactory(TypedTextFieldTreeTableCell.<AbilityInfoModel, Integer>
			forTreeTableColumn(PositiveIntegerField::new));
		los.setCellFactory(CheckBoxTreeTableCell.<AbilityInfoModel>
			forTreeTableColumn(los));
		useWeaponRange.setCellFactory(CheckBoxTreeTableCell.<AbilityInfoModel>
			forTreeTableColumn(useWeaponRange));
		recursion.setCellFactory(TypedTextFieldTreeTableCell.<AbilityInfoModel, Integer>
			forTreeTableColumn(PositiveIntegerField::new));

		table.getColumns().setAll(
			name, type, ap, mp, pp, eff, chance, heal, range, radius,
			piercing, ribbon, targetMode, nTargets, los, useWeaponRange,
			recursion, instantBefore, instantAfter, statusEffect);
	}
}

