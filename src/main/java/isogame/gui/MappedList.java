package isogame.gui;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.List;

public class MappedList<E, F> extends TransformationList<E, F> {
	private Function<F, E> map;
	private List<E> internal = new ArrayList<>();

	public MappedList(ObservableList<? extends F> source, Function<F, E> map) {
		super(source);
		this.map = map;

		for (F i : source) {
			internal.add(map.apply(i));
		}
	}

	private void doMap() {
		ObservableList<? extends F> source = getSource();
		int size = source.size();
		for (int i = 0; i < size; i++) {
			internal.set(i, map.apply(source.get(i)));
		}
	}

	@Override
	public int getSourceIndex(int index) {
		return index;
	}

	@Override
	public E get(int i) {
		return internal.get(i);
	}

	@Override
	public int size() {
		return internal.size();
	}

	@Override
	protected void sourceChanged(ListChangeListener.Change<? extends F> c) {
		doMap();
	}
}

