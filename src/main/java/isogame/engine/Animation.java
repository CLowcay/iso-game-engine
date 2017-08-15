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
package isogame.engine;

import java.util.Optional;
import java.util.function.Supplier;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import static isogame.GlobalConstants.TILEW;

public abstract class Animation {
	protected final Sprite sprite;

	protected Animation(final Sprite sprite) {
		this.sprite = sprite;
	}

	public void start() { }

	/**
	 * @param terrain the terrain we're rendering onto
	 * @param t the current time
	 * @return true if the animation is now complete.
	 * */
	public abstract boolean updateAnimation(
		final StageInfo terrain, final long t
	);

	/**
	 * Update the scene graph
	 * @param graph the scenegraph
	 * @param terrain the terrain we're rendering onto
	 * @param angle the current camera angle
	 * @param t the current time
	 * */
	public void updateSceneGraph(
		final ObservableList<Node> graph,
		final StageInfo terrain,
		final CameraAngle angle,
		final long t
	) {
		final Tile tile = terrain.getTile(sprite.getPos());
		final Point2D l = terrain.correctedIsoCoord(sprite.getPos(), angle);
		sprite.sceneGraph.setTranslateX(l.getX());
		sprite.sceneGraph.setTranslateY(l.getY());
		final Supplier<Integer> iL = () ->
			sprite.findIndex(graph, false).orElse(tile.getSceneGraphIndex(graph) + 1);
		sprite.update(graph, iL, Optional.empty(), angle, t);
	}
}

