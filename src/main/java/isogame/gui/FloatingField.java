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

public class FloatingField extends TypedTextField<Double> {
	public FloatingField(final double init) {
		super(init);
	}

	public FloatingField(final String text) {
		super(text);
	}

	public FloatingField() {
		super();
	}

	@Override protected Optional<Double> parseValue(String t) {
		try {
			if (t.endsWith(".")) t = t + "0";
			return Optional.of(Double.parseDouble(t));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	@Override protected Double getDefaultValue() {return 0.0;}

	@Override protected String showValue(Double i) {
		return i.toString();
	}
}


