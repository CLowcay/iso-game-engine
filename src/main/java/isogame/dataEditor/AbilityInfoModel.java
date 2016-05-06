package isogame.dataEditor;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class AbilityInfoModel {
	private SimpleStringProperty name;
	private SimpleStringProperty type;
	private SimpleIntegerProperty ap;
	private SimpleIntegerProperty mp;
	private SimpleIntegerProperty pp;
	private SimpleDoubleProperty eff;
	private SimpleDoubleProperty chance;
	private SimpleBooleanProperty heal;

	private SimpleIntegerProperty range;
	private SimpleIntegerProperty radius;
	private SimpleBooleanProperty piercing;
	private SimpleIntegerProperty ribbon;
	private SimpleStringProperty targetMode;
	private SimpleIntegerProperty nTargets;
	private SimpleBooleanProperty los;

	private SimpleBooleanProperty useWeaponRange;
	private SimpleBooleanProperty isMana;
	private SimpleBooleanProperty isSubsequent;
	private SimpleIntegerProperty recursion;

	private SimpleStringProperty instantBefore;
	private SimpleStringProperty instantAfter;
	private SimpleStringProperty statusEffect;

	public AbilityInfoModel(boolean isMana, boolean isSubsequent) {
		this.name = new SimpleStringProperty("New ability");
		this.type = new SimpleStringProperty("Skill");
		this.ap = new SimpleIntegerProperty(2);
		this.mp = new SimpleIntegerProperty(0);
		this.pp = new SimpleIntegerProperty(1);
		this.eff = new SimpleDoubleProperty(1.0);
		this.chance = new SimpleDoubleProperty(1.0);
		this.heal = new SimpleBooleanProperty(false);
		this.range = new SimpleIntegerProperty(1);
		this.radius = new SimpleIntegerProperty(1);
		this.piercing = new SimpleBooleanProperty(false);
		this.ribbon = new SimpleIntegerProperty(0);
		this.targetMode = new SimpleStringProperty("E");
		this.nTargets = new SimpleIntegerProperty(1);
		this.los = new SimpleBooleanProperty(true);
		this.useWeaponRange = new SimpleBooleanProperty(false);
		this.isMana = new SimpleBooleanProperty(isMana);
		this.isSubsequent = new SimpleBooleanProperty(isSubsequent);
		this.recursion = new SimpleIntegerProperty(0);
		this.instantBefore = new SimpleStringProperty("none");
		this.instantAfter = new SimpleStringProperty("none");
		this.statusEffect = new SimpleStringProperty("none");
	}

	public AbilityInfoModel cloneMana() {
		AbilityInfoModel r = this.clone(true, false);
		r.name.setValue(name.getValue() + " + Mana");
		r.ap.setValue(0);
		r.mp.setValue(0);
		r.pp.setValue(0);
		return r;
	}

	public AbilityInfoModel cloneSubsequent() {
		AbilityInfoModel r = this.clone(false, true);
		String oldName = name.getValue();
		r.name.setValue(oldName.endsWith("I")? oldName + "I" : oldName + " II");
		r.ap.setValue(0);
		r.mp.setValue(0);
		r.pp.setValue(0);
		return r;
	}

	private AbilityInfoModel clone(boolean isMana, boolean isSubsequent) {
		AbilityInfoModel r = new AbilityInfoModel(isMana, isSubsequent);
		r.name.setValue(name.getValue());
		r.type.setValue(type.getValue());
		r.ap.setValue(ap.getValue());
		r.mp.setValue(mp.getValue());
		r.pp.setValue(pp.getValue());
		r.eff.setValue(eff.getValue());
		r.chance.setValue(chance.getValue());
		r.heal.setValue(heal.getValue());
		r.radius.setValue(radius.getValue());
		r.piercing.setValue(piercing.getValue());
		r.ribbon.setValue(ribbon.getValue());
		r.targetMode.setValue(targetMode.getValue());
		r.nTargets.setValue(nTargets.getValue());
		r.los.setValue(los.getValue());
		r.useWeaponRange.setValue(useWeaponRange.getValue());
		r.recursion.setValue(recursion.getValue());
		r.instantBefore.setValue(instantBefore.getValue());
		r.instantAfter.setValue(instantAfter.getValue());
		r.statusEffect.setValue(statusEffect.getValue());
		return r;
	}

	public SimpleStringProperty nameProperty() {
		return name;
	}

	public SimpleStringProperty typeProperty() {
		return type;
	}

	public SimpleIntegerProperty apProperty() {
		return ap;
	}

	public SimpleIntegerProperty mpProperty() {
		return mp;
	}

	public SimpleIntegerProperty ppProperty() {
		return pp;
	}

	public SimpleDoubleProperty effProperty() {
		return eff;
	}

	public SimpleDoubleProperty chanceProperty() {
		return chance;
	}

	public SimpleBooleanProperty healProperty() {
		return heal;
	}

	public SimpleIntegerProperty rangeProperty() {
		return range;
	}

	public SimpleIntegerProperty radiusProperty() {
		return radius;
	}

	public SimpleBooleanProperty piercingProperty() {
		return piercing;
	}

	public SimpleIntegerProperty ribbonProperty() {
		return ribbon;
	}

	public SimpleStringProperty targetModeProperty() {
		return targetMode;
	}

	public SimpleIntegerProperty nTargetsProperty() {
		return nTargets;
	}

	public SimpleBooleanProperty losProperty() {
		return los;
	}

	public SimpleBooleanProperty useWeaponRangeProperty() {
		return useWeaponRange;
	}

	public SimpleBooleanProperty isManaProperty() {
		return isMana;
	}

	public boolean getIsMana() {
		return isMana.getValue();
	}

	public boolean getIsSubsequent() {
		return isSubsequent.getValue();
	}

	public SimpleBooleanProperty isSubsequentProperty() {
		return isSubsequent;
	}

	public SimpleIntegerProperty recursionProperty() {
		return recursion;
	}

	public SimpleStringProperty instantBeforeProperty() {
		return instantBefore;
	}

	public SimpleStringProperty instantAfterProperty() {
		return instantAfter;
	}

	public SimpleStringProperty statusEffectProperty() {
		return statusEffect;
	}

}

