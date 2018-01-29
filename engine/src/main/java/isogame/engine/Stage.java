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

import isogame.resource.ResourceLocator;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.json.JSONException;
import org.json.JSONObject;
import ssjsjs.annotations.As;
import ssjsjs.annotations.Field;
import ssjsjs.annotations.Implicit;
import ssjsjs.annotations.JSON;
import ssjsjs.JSONable;
import ssjsjs.JSONdecodeException;
import ssjsjs.SSJSJS;

/**
 * The environment in which the sprites move around.
 * */
public class Stage implements JSONable {
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

	@JSON
	public Stage(
		@Implicit("library") Library localLibrary,
		@Field("name") final String name,
		@Field("terrain") final StageInfo terrain,
		@Field("allSprites")@As("sprites") final Collection<Sprite> sprites
	) {
		this(terrain, localLibrary);
		this.name = name;
		for (final Sprite s : sprites) this.addSprite(s);
	}

	/**
	 * @param terrain the terrain this stage is built on
	 * @param localLibrary a library containing private assets for this stage
	 * */
	public Stage(final StageInfo terrain, final Library localLibrary) {
		this.terrain = terrain;
		this.localLibrary = localLibrary;
		this.collisions = new CollisionDetector(this);

		this.spritesByPriority.addAll(localLibrary.priorities
			.stream().map(x -> new HashSet<Sprite>())
			.collect(Collectors.toList()));

		// manually clear out any old highlighting state from the tiles
		final Iterator<Tile> it = terrain.iterateTiles(CameraAngle.UL);
		while (it.hasNext()) {
			it.next().setHighlight(CameraAngle.UL, Optional.empty());
		}
	}

	/**
	 * Load a Stage from a file
	 * @param filename the name of the file to load from
	 * @param loc the resource locator
	 * @param globl the global library
	 * @return the loaded stage
	 * @throws IOException if there is an error reading the file
	 * @throws CorruptDataException if the data in the file is malformed
	 * */
	public static Stage fromFile(
		final File filename, final ResourceLocator loc, final Library global
	) throws IOException, CorruptDataException
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

			return Stage.fromJSON(filename.toString(), json, loc, global);
		}
	}

	/**
	 * Load a Stage from a JSON object
	 * @param url the name of the resource we're loading from
	 * @param json the JSON object to parse
	 * @param locator the resource locator
	 * @param global the global library
	 * @return the loaded stage
	 * @throws CorruptDataException if the data in the file is malformed
	 * */
	public static Stage fromJSON(
		final String url,
		final JSONObject json,
		final ResourceLocator locator,
		final Library global
	) throws CorruptDataException {
		try {
			final Map<String, Object> jsonEnvironment = new HashMap<>();
			jsonEnvironment.put("library",
				Library.fromJSON(json, url, locator, global, false));
			return SSJSJS.decode((JSONObject) json.get("stage"), Stage.class, jsonEnvironment);

		} catch (final JSONException|JSONdecodeException e) {
			throw new CorruptDataException("Error parsing stage", e);
		}
	}

	/**
	 * Create a copy of this stage.
	 * */
	public Stage clone() {
		final Stage r = new Stage(this.terrain, this.localLibrary);
		for (final Sprite s : allSprites) r.addSprite(s);
		return r;
	}


	/**
	 * Control the order in which sprites are rendered.
	 * */
	private int mapPriority(final int priority) {
		return spritesByPriority.size() - 1 - priority;
	}

	private final Map<MapPoint, List<Sprite>> spritesByTile = new HashMap<>();

	/**
	 * Add a sprite to the map.  z-order is determined by sprite priority.
	 * @param sprite the sprite to add to the map
	 * */
	public void addSprite(final Sprite sprite) {
		sprite.doOnMove(this::moveSprite);
		allSprites.add(sprite);
		spritesByPriority.get(mapPriority(sprite.info.priority)).add(sprite);
		moveSprite(sprite, sprite.getPos());
	}

	/**
	 * Callback to handle when sprites move
	 * @param sprite the sprite to move
	 * @param from where the sprite is when the movement starts
	 * */
	private void moveSprite(final Sprite sprite, final MapPoint from) {
		final List<Sprite> old = spritesByTile.get(from);
		if (old != null) old.remove(sprite);

		spritesByTile.putIfAbsent(sprite.getPos(), new LinkedList<>());
		final List<Sprite> byTile = spritesByTile.get(sprite.getPos());

		final Iterator<Sprite> it = byTile.iterator();
		int i = 0;
		boolean inserted = false;
		while (it.hasNext()) {
			final Sprite c = it.next();
			if (sprite.info.priority >= c.info.priority) {
				byTile.add(i, sprite);
				inserted = true;
				break;
			}

			i += 1;
		}
		if (!inserted) byTile.add(sprite);

		for (final Sprite s : byTile) s.invalidate();
	}

	/**
	 * Add a sprite to the map, removing any sprites already at the same
	 * location.
	 * @param sprite the sprite to add
	 * */
	public void replaceSprite(final Sprite sprite) {
		clearTileOfSprites(sprite.getPos());
		addSprite(sprite);
	}

	/**
	 * Remove a single sprite.
	 * @param sprite the sprite to remove
	 * */
	public void removeSprite(final Sprite sprite) {
		allSprites.remove(sprite);
		spritesByPriority.get(mapPriority(sprite.info.priority)).remove(sprite);
		removedSprites.add(sprite);
	}

	/**
	 * Get all the sprites on a tile.
	 * @param p the map point to examine
	 * @return a list of all the sprites at position p
	 * */
	public List<Sprite> getSpritesByTile(final MapPoint p) {
		return new ArrayList<>(spritesByTile.getOrDefault(p, new LinkedList<>()));
	}

	/**
	 * Remove all the sprites on a given tile.
	 * @param p the tile to clear of sprites
	 * */
	public void clearTileOfSprites(final MapPoint p) {
		getSpritesByTile(p).stream().forEach(this::removeSprite);
	}

	/**
	 * Rotate all the sprites on a particular tile.
	 * @param p the tile containing the sprites to rotate
	 * */
	public void rotateSprites(final MapPoint p) {
		getSpritesByTile(p).forEach(s -> s.rotate());
	}

	/**
	 * Does this stage use that TerrainTexture.
	 * @param tex the texture to check for
	 * */
	public boolean usesTerrainTexture(final TerrainTexture tex) {
		return terrain.usesTerrainTexture(tex);
	}

	/**
	 * Does this stage use that Sprite.
	 * @param info the sprite to check for
	 * */
	public boolean usesSprite(final SpriteInfo info) {
		return allSprites.stream().anyMatch(s -> s.info == info);
	}

	/**
	 * Does this stage use that CliffTexture.
	 * @param tex the cliff texture to check for
	 * */
	public boolean usesCliffTexture(final CliffTexture tex) {
		return terrain.usesCliffTexture(tex);
	}

	private final List<HighlightLayer> highlighting = new ArrayList<>();
	private final Set<MapPoint> highlightChanged = new HashSet<>();

	/**
	 * Set the highlighting colour scheme.
	 * @param highlightColors the colours to use for the highlighters
	 * */
	public void setHighlightColors(final Paint[] highlightColors) {
		clearAllHighlighting();
		highlighting.clear();
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
		for (final HighlightLayer layer : highlighting) {
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
	 * @param graph the scene graph
	 * @param isDebug true to display debug information
	 * @param t the current timestamp in nanoseconds
	 * @param a the new camera angle
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
		for (final Tile tile : terrain.getUpdatedTiles()) {
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
					terrain.getTile(p).setHighlight(currentAngle, color);
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

		for (final Sprite s : removedSprites) graph.remove(s.sceneGraph);
	}

	/**
	 * Reconstruct the scene graph
	 * @param t the current time value
	 * @param isDebug true to show extra debugging information
	 * @param graph part of the scene graph
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

		for (final Sprite s : allSprites) s.invalidate();
	}
}

