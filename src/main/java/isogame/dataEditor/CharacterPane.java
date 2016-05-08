package isogame.dataEditor;

import com.diffplug.common.base.Errors;
import isogame.battle.data.AbilityInfo;
import isogame.battle.data.AbilityType;
import isogame.battle.data.CharacterInfo;
import isogame.battle.data.InstantEffectInfo;
import isogame.battle.data.Range;
import isogame.battle.data.Stats;
import isogame.battle.data.StatusEffectInfo;
import isogame.battle.data.TargetMode;
import isogame.battle.data.WeaponInfo;
import isogame.engine.CorruptDataException;
import isogame.gui.PositiveIntegerField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CharacterPane extends TitledPane {
	private final GridPane grid = new GridPane();
	private final TextField name;
	private final PositiveIntegerField ap;
	private final PositiveIntegerField mp;
	private final PositiveIntegerField power;
	private final PositiveIntegerField vitality;
	private final PositiveIntegerField attack;
	private final PositiveIntegerField defence;
	private final TreeItem<AbilityInfoModel> abilitiesRoot;
	private final TreeItem<WeaponInfoModel> weaponsRoot;

	public String getName() {
		return name.getText() == null? "" : name.getText();
	}

	private static AbilityInfo encodeAbility(
		TreeItem<AbilityInfoModel> item, boolean isMana
	) throws CorruptDataException
	{
		try {
			Optional<AbilityInfo> mana;
			if (isMana) mana = Optional.empty(); else {
				mana = item.getChildren().stream()
					.filter(i -> i.getValue().getIsMana()).findAny()
					.map(Errors.rethrow().wrapFunction(i -> encodeAbility(i, true)));
			}
			List<TreeItem<AbilityInfoModel>> subs =
				item.getChildren().stream()
					.filter(i -> i.getValue().getIsSubsequent())
					.collect(Collectors.toList());
			Optional<AbilityInfo> subsequent = Optional.empty();
			for (int i = subs.size() - 1; i >= 0; i--) {
				subsequent = Optional.of(encodeAbility0(
					subs.get(i).getValue(), Optional.empty(), subsequent));
			}
			return encodeAbility0(item.getValue(), mana, subsequent);
		} catch (RuntimeException e) {
			if (e.getCause() instanceof CorruptDataException) {
				throw (CorruptDataException) e.getCause();
			} else throw e;
		}
	}

	private static AbilityInfo encodeAbility0(
		AbilityInfoModel a,
		Optional<AbilityInfo> mana,
		Optional<AbilityInfo> subsequent
	) throws CorruptDataException
	{
		Optional<InstantEffectInfo> ib = Optional.empty();
		Optional<InstantEffectInfo> ia = Optional.empty();
		Optional<StatusEffectInfo> se = Optional.empty();

		try {
			ib = Optional.of(new InstantEffectInfo(a.getInstantBefore()));
		} catch (CorruptDataException e) { /* IGNORE */ }
		try {
			ia = Optional.of(new InstantEffectInfo(a.getInstantAfter()));
		} catch (CorruptDataException e) { /* IGNORE */ }
		try {
			se = Optional.of(new StatusEffectInfo(a.getStatusEffect()));
		} catch (CorruptDataException e) { /* IGNORE */ }

		return new AbilityInfo(
			a.getName(),
			AbilityType.parse(a.getType()),
			a.getAP(),
			a.getMP(),
			a.getPP(),
			a.getEff(),
			a.getChance(),
			a.getHeal(),
			new Range(
				a.getRange(),
				a.getRadius(),
				a.getPiercing(),
				a.getRibbon(),
				new TargetMode(a.getTargetMode()),
				a.getnTargets(),
				a.getLOS()
			),
			a.getUseWeaponRange(),
			mana,
			subsequent,
			a.getRecursion(),
			ib, ia, se);
	}

	private WeaponInfo encodeWeapon(
		TreeItem<WeaponInfoModel> item, Collection<AbilityInfo> abilities
	) {
		WeaponInfoModel w = item.getValue();
		String abilityName = w.getAttack();
		return new WeaponInfo(
			w.getName(),
			w.getRange(),
			name.getText(),
			abilities.stream()
				.filter(a -> a.name.equals(abilityName))
				.findAny().orElse(WeaponInfo.defaultAbility));
	}

	public CharacterInfo getCharacter()
		throws CorruptDataException
	{
		return new CharacterInfo(
			name.getText(),
			new Stats(
				ap.getValue(), mp.getValue(),
				power.getValue(), vitality.getValue(),
				attack.getValue(), defence.getValue()),
			getAbilities());
	}

	public Collection<AbilityInfo> getAbilities() throws CorruptDataException {
		try {
			return abilitiesRoot.getChildren().stream()
				.map(Errors.rethrow().wrapFunction(i -> encodeAbility(i, false)))
				.collect(Collectors.toList());
		} catch (RuntimeException e) {
			if (e.getCause() instanceof CorruptDataException) {
				throw (CorruptDataException) e.getCause();
			} else throw e;
		}
	}

	public Collection<WeaponInfo> getWeapons() throws CorruptDataException {
		Collection<AbilityInfo> abilities = getAbilities();
		return weaponsRoot.getChildren().stream()
			.map(i -> encodeWeapon(i, abilities))
			.collect(Collectors.toList());
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
			if (!weapons.isShowing()) {
				weapons.show();
			}
		});
	}
}

