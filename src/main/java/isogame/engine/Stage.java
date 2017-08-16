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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Paint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import isogame.resource.ResourceLocator;

public class Stage implements HasJSONRepresentation {
	public String name = null;
	public final StageInfo terrain;

	/**
	 * A convenient way to get a reference to all the sprites
	 * */
	public final Set<Sprite> allSprites = new HashSet<>();

	private final Set<Sprite> removedSprites = new HashSet<>();
	private final ArrayList<Set<Sprite>> spritesByPriority = new ArrayList<>();

	/**
	 * Assets for just this stage.
	 * */
	public final Library localLibrary;

	/**
	 * The collision detector.
	 * */
	public final CollisionDetector collisions;

	public Stage clone() {
		final Stage r = new Stage(this.terrain, this.localLibrary);
		for (Sprite s : allSprites) r.addSprite(s);
		return r;
	}

	public Stage(final StageInfo terrain, final Library localLibrary) {
		this.terrain = terrain;
		this.localLibrary = localLibrary;
		this.collisions = new CollisionDetector(this);

		this.spritesByPriority.addAll(localLibrary.priorities
			.stream().map(x -> new HashSet<Sprite>())
			.collect(Collectors.toList()));
	}

	public static Stage fromFile(
		final File filename, final ResourceLocator loc, final Library global
	) throws IOException, CorruptDataException, JSONException
	{
		try (BufferedReader in =
			new BufferedReader(
			new InputStreamReader(
			new FileInputStream(filename), "UTF-8")))
		{
			final StringBuilder raw = new StringBuilder();
			String line = null;
			while ((line = in.readLine()) != null) raw.append(line);
			final JSONObject json = new JSONObject(raw.toString());

			return Stage.fromJSON(json, loc, global);
		}
	}

	public static Stage fromJSON(
		final JSONObject json, final ResourceLocator loc, final Library global
	) throws CorruptDataException
	{
		try {
			final JSONObject stageJSON = json.getJSONObject("stage");
			final String name = stageJSON.getString("name");

			final Library lib = Library.fromJSON(json, name, loc, global, false);

			final JSONObject terrain = stageJSON.getJSONObject("terrain");
			final JSONArray sprites = stageJSON.getJSONArray("sprites");

			final Stage r = new Stage(StageInfo.fromJSON(terrain, lib), lib);
			r.name = name;
			for (Object s : sprites) {
				r.addSprite(Sprite.fromJSON((JSONObject) s, lib));
			}

			return r;
		} catch (ClassCastException e) {
			throw new CorruptDataException("Type error in stage", e);
		} catch (JSONException e) {
			throw new CorruptDataException("Error parsing stage, " + e.getMessage(), e);
		}
	}

	@Override
	public JSONObject getJSON() {
		final JSONArray s = new JSONArray();
		for (Sprite sprite : allSprites) s.put(sprite.getJSON());

		final JSONObject r = new JSONObject();
		r.put("name", name);
		r.put("terrain", terrain.getJSON());
		r.put("sprites", s);
		return r;
	}

	/**
	 * Add a sprite to the map.  z-order is determined by sprite priority.
	 * */
	public void addSprite(final Sprite sprite) {
		allSprites.add(sprite);
		spritesByPriority.get(sprite.info.priority).add(sprite);
	}

	/**
	 * Add a sprite to the map, removing any sprites already at the same
	 * location.
	 * */
	public void replaceSprite(final Sprite sprite) {
		clearTileOfSprites(sprite.getPos());
		addSprite(sprite);
	}

	/**
	 * Remove a single sprite.
	 * */
	public void removeSprite(final Sprite sprite) {
		allSprites.remove(sprite);
		spritesByPriority.get(sprite.info.priority).remove(sprite);
		removedSprites.add(sprite);
	}

	/**
	 * Get all the sprites on a tile.
	 * */
	public List<Sprite> getSpritesByTile(final MapPoint p) {
		return allSprites.stream()
			.filter(s -> s.getPos().equals(p))
			.collect(Collectors.toList());
	}

	/**
	 * Remove all the sprites on a given tile.
	 * */
	public void clearTileOfSprites(final MapPoint p) {
		getSpritesByTile(p).stream().forEach(this::removeSprite);
	}

	/**
	 * Rotate all the sprites on a particular tile.
	 * */
	public void rotateSprites(final MapPoint p) {
		getSpritesByTile(p).forEach(s -> s.rotate());
	}

	/**
	 * Does this stage use that TerrainTexture.
	 * */
	public boolean usesTerrainTexture(final TerrainTexture tex) {
		return terrain.usesTerrainTexture(tex);
	}

	/**
	 * Does this stage use that Sprite.
	 * */
	public boolean usesSprite(final SpriteInfo info) {
		return allSprites.stream().anyMatch(s -> s.info == info);
	}

	/**
	 * Does this stage use that CliffTexture.
	 * */
	public boolean usesCliffTexture(final CliffTexture tex) {
		return terrain.usesCliffTexture(tex);
	}

	private final List<HighlightLayer> highlighting = new ArrayList<>();
	private final Set<MapPoint> highlightChanged = new HashSet<>();

	/**
	 * Set the highlighting colour scheme.
	 * */
	public void setHighlightColors(final Paint[] highlightColors) {
		clearAllHighlighting();
		for (int i = 0; i < highlightColors.length; i++) {
			highlighting.add(new HighlightLayer(highlightColors[i]));
		}
	}

	/**
	 * Highlight a tile.
	 * @param p The tile to highlight
	 * @param priority The highlighter to use.  A tile map be under several
	 * highlights at once, but only the highest priority highlight is actually
	 * rendered.  Lower number == higher priority
	 * */
	public void setHighlight(final MapPoint p, final int priority) {
		if (priority < 0 || priority >= highlighting.size()) {
			throw new RuntimeException("Invalid highlight priority " + priority);
		}

		highlightChanged.add(p);
		highlighting.get(priority).points.add(p);
	}

	/**
	 * Clear a highlighting level.
	 * @param priority The level to clear
	 * */
	public void clearHighlighting(final int priority) {
		highlightChanged.addAll(highlighting.get(priority).points);
		highlighting.get(priority).points.clear();
	}

	/**
	 * Clear all highlighting.
	 * */
	public void clearAllHighlighting() {
		for (HighlightLayer layer : highlighting) {
			highlightChanged.addAll(layer.points);
			layer.points.clear();
		}
	}

	/**
	 * Is that tile highlighted?
	 * @param p The tile to test
	 * @return true if the tile is highlighted
	 * */
	public boolean isHighlighted(final MapPoint p) {
		return highlighting.stream().anyMatch(layer -> layer.points.contains(p));
	}

	/**
	 * Invalidate this stage (to force a redraw)
	 * */
	public void invalidate() {
		currentAngle = null;
	}

	private CameraAngle currentAngle = null;

	/**
	 * Update the scene graph for a new frame
	 * @param graph The scene graph
	 * @param t The current timestamp in nanoseconds
	 * @param a The new camera angle
	 * */
	public void update(
		final ObservableList<Node> graph,
		final ObservableBooleanValue isDebug,
		final long t,
		final CameraAngle a
	) {
		if (a != currentAngle) {
			currentAngle = a;
			rebuildSceneGraph(t, isDebug, graph);
		}

		// update any tiles that have changed
		for (Tile tile : terrain.getUpdatedTiles()) {
			final Point2D l = terrain.correctedIsoCoord(tile.pos, currentAngle);
			tile.rebuildSceneGraph(isDebug, currentAngle);
			tile.subGraph.setTranslateX(l.getX());
			tile.subGraph.setTranslateY(l.getY());
		}

		// update highlighting
		for (final HighlightLayer layer : highlighting) {
			final Optional<Paint> color = Optional.of(layer.color);

			for (final MapPoint p : layer.points) {
				if (highlightChanged.contains(p)) {
					final Tile tile = terrain.getTile(p);
					final Point2D l = terrain.correctedIsoCoord(tile.pos, currentAngle);
					tile.setHighlight(currentAngle, color);
					highlightChanged.remove(p);
				}
			}
		}

		// clear highlighting on unhighlighted nodes.
		for (final MapPoint p : highlightChanged) {
			final Tile tile = terrain.getTile(p);
			tile.setHighlight(currentAngle, Optional.empty());
		}
		highlightChanged.clear();

		// update the sprites
		for (final Set<Sprite> layer : spritesByPriority) {
			for (final Sprite s : layer)
				s.updateSceneGraph(graph, terrain, currentAngle, t);
		}

		for (Sprite s : removedSprites) graph.remove(s.sceneGraph);
	}

	/**
	 * Reconstruct the scene graph
	 * */
	private void rebuildSceneGraph(
		final long t,
		final ObservableBooleanValue isDebug,
		final ObservableList<Node> graph
	) {
		graph.clear();
		terrain.iterateTiles(currentAngle).forEachRemaining(tile -> {
			final Point2D l = terrain.correctedIsoCoord(tile.pos, currentAngle);
			tile.rebuildSceneGraph(isDebug, currentAngle);
			tile.subGraph.setTranslateX(l.getX());
			tile.subGraph.setTranslateY(l.getY());
			graph.add(tile.subGraph);
		});

		for (Sprite s : allSprites) s.invalidate();
	}
}

