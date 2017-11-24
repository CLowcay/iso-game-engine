/* © Callum Lowcay 2015, 2016

This file is part of iso-game-engine.

iso-game-engine is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

iso-game-engine is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with iso-game-engine.  If not, see <http://www.gnu.org/licenses/>.

*/
package isogame.gui;

import java.util.Optional;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 * A text field that can only contain values of a specified format.
 * */
public abstract class TypedTextField<T> extends TextField {
	protected abstract Optional<T> parseValue(final String t);
	protected abstract T getDefaultValue();
	protected abstract String showValue(T v);

	public TypedTextField(final T init) {
		this();
		this.setText((showValue(init)).toString());
	}

	public TypedTextField(final String text) {
		this();
		this.setText(text);
	}

	public TypedTextField() {
		super();

		this.setTextFormatter(new TextFormatter<>(change -> {
			if ((change.isAdded() || change.isReplaced())
				&& !parseValue(change.getText()).isPresent()
			) {
				change.setText("");
			}
			return change;
		}));
	}

	public void setValue(final T v) {
		this.setText(v == null? "" : showValue(v));
	}

	public T getValue() {
		return parseValue(this.getText()).orElse(getDefaultValue());
	}

}

