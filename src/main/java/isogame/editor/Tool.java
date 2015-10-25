package isogame.editor;

import isogame.engine.MapPoint;
import isogame.engine.Stage;
import isogame.engine.View;

/**
 * Abstract superclass of map editing tools
 * */
public abstract class Tool {
	public abstract void apply(MapPoint p, Stage stage, View v);
}

