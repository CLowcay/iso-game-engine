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

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

public class MappedList<E, F> extends TransformationList<E, F> {
	private Function<F, E> map;

	public MappedList(
		final ObservableList<? extends F> source, final Function<F, E> map
	) {
		super(source);
		this.map = map;
	}

	@Override
	public int getSourceIndex(final int index) {
		return index;
	}

	@Override
	public E get(final int i) {
		return map.apply(getSource().get(i));
	}

	@Override
	public int size() {
		return getSource().size();
	}

	@Override
	protected void sourceChanged(final ListChangeListener.Change<? extends F> c) {
		// adapted from https://gist.github.com/TomasMikula/8883719
		fireChange(new Change<E>(this) {
			@Override
			public boolean wasAdded() {return c.wasAdded();}

			@Override
			public boolean wasRemoved() {return c.wasRemoved();}

			@Override
			public boolean wasReplaced() {return c.wasReplaced();}

			@Override
			public boolean wasUpdated() {return c.wasUpdated();}

			@Override
			public boolean wasPermutated() {return c.wasPermutated();}

			@Override
			public int getPermutation(final int i) {return c.getPermutation(i);}

			@Override
			public int getFrom() {return c.getFrom();}

			@Override
			public int getTo() {return c.getTo();}

			@Override
			public boolean next() {return c.next();}

			@Override
			public void reset() {c.reset();}

			@Override
			protected int[] getPermutation() {
				// This method is only called by the superclass methods
				// wasPermutated() and getPermutation(int), which are
				// both overriden by this class. There is no other way
				// this method can be called.
				throw new AssertionError("Unreachable code");
			}

			@Override
			public List<E> getRemoved() {
				return c.getRemoved().stream().map(map).collect(Collectors.toList());
			}
		});
	}
}

