package isogame.dataEditor;

import isogame.gui.PositiveIntegerField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import java.util.function.UnaryOperator;

public class EffectField<S> extends TreeTableCell<S, String> {
	private final String[] options;
	private final TreeTableViewSelectionModel<S> selection;
	public EffectField(
		String[] options,
		TreeTableViewSelectionModel<S> selection
	) {
		this.options = options;
		this.selection = selection;
	}

	public static <S> Callback<TreeTableColumn<S, String>, TreeTableCell<S, String>>
		forTreeTableColumn(
			String[] options,
			TreeTableViewSelectionModel<S> selection
	) {
		return (column -> new EffectField<>(options, selection));
	}

	private HBox editor;
	private PositiveIntegerField parameter;
	private ChoiceBox<String> effect;

	public String getValue() {
		if (editor == null) return ""; else {
			return effect.getValue() + " " + parameter.getText();
		}
	}

	private void createTextField() {
		editor = new HBox();
		parameter = new PositiveIntegerField();
		effect = new ChoiceBox<>();
		//effect.setMinWidth(80);
		parameter.setMinWidth(40);
		parameter.setMaxWidth(40);
		effect.getItems().add("none");
		effect.getItems().addAll(options);
		effect.setFocusTraversable(true);
		HBox.setHgrow(effect, Priority.ALWAYS);
		editor.getChildren().addAll(effect, parameter);

		String[] v = getItem().split("\\s");
		effect.setValue(v.length > 0? v[0] : "none");
		parameter.setText(v.length > 1? v[1] : "");

		effect.valueProperty().addListener((arg0, arg1, arg2) -> {
			if (arg2 == "none") parameter.setText("");
		});

		effect.focusedProperty().addListener((arg0, arg1, arg2) -> {
			if (!arg2 && !parameter.isFocused()) {
				commitEdit(this.getValue());
			}
		});
		parameter.focusedProperty().addListener((arg0, arg1, arg2) -> {
			if (!arg2 && !effect.isFocused()) {
				commitEdit(this.getValue());
			}
		});

		ChangeListener<Number> selectionChanged = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				commitEdit(getValue());
				selection.selectedIndexProperty().removeListener(this);
			}
		};

		selection.selectedIndexProperty().addListener(selectionChanged);
		parameter.setOnAction(event -> commitEdit(this.getValue()));
	}


	@Override
	public void startEdit() {
		if (!isEmpty()) {
			super.startEdit();
			createTextField();
			setText(null);
			setGraphic(editor);
		}
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();

		setText(getItem().toString());
		setGraphic(null);
	}

	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (editor != null) {
					String[] v = getItem().split("\\s");
					effect.setValue(v.length > 0? v[0] : "none");
					parameter.setText(v.length > 1? v[1] : "");
				}
				setText(null);
				setGraphic(editor);
			} else {
				setText(getItem() == null? "none" : getItem().toString());
				setGraphic(null);
			}
		}
	}
}

