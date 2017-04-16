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
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import static isogame.GlobalConstants.ELEVATION_H;
import static isogame.GlobalConstants.TILEH;
import static isogame.GlobalConstants.TILEW;

public class Stage implements HasJSONRepresentation {
	public String name = null;
	public final StageInfo terrain;

	/**
	 * A convenient way to get a reference to all the sprites
	 * */
	public final Set<Sprite> allSprites = new HashSet<>();

	private final Map<MapPoint, List<Sprite>> sprites;
	// sprites that are moving into new squares.
	private final Map<MapPoint, List<Sprite>> slicedSprites;

	private final Collection<AnimationChain> animationChains = new LinkedList<>();

	/**
	 * Assets for just this stage.
	 * */
	public final Library localLibrary;

	// transformation from map coordinates to iso coordinates
	private final Affine isoTransform;

	private final Rotate rUL;
	private final Rotate rLL;
	private final Rotate rLR;
	private final Rotate rUR;

	/**
	 * The collision detector.
	 * */
	public final CollisionDetector collisions;

	public Stage clone() {
		Stage r = new Stage(this.terrain, this.localLibrary);
		for (Sprite s : allSprites) r.addSprite(s);
		return r;
	}

	public Stage(StageInfo terrain, Library localLibrary) {
		this.terrain = terrain;
		this.localLibrary = localLibrary;
		this.collisions = new CollisionDetector(this);

		sprites = new HashMap<>();
		slicedSprites = new HashMap<>();

		// set the camera angle rotations
		double xPivot = ((double) terrain.w) / 2.0d;
		double yPivot = ((double) terrain.h) / 2.0d;
		rUL = new Rotate();
		rLL = new Rotate(90, xPivot, yPivot);
		rLR = new Rotate(180, xPivot, yPivot);
		rUR = new Rotate(270, xPivot, yPivot);

		// compute the iso coordinate transformation
		// note that javafx transformations appear to compose backwards
		isoTransform = new Affine();
		isoTransform.appendTranslation((0 - TILEW) / 2, 0);
		isoTransform.appendScale(TILEW / Math.sqrt(2), TILEH / Math.sqrt(2));
		isoTransform.appendRotation(45, 0, 0);
	}

	public static Stage fromFile(
		File filename, ResourceLocator loc, Library global
	) throws IOException, CorruptDataException, ParseException
	{
		try (BufferedReader in =
			new BufferedReader(
			new InputStreamReader(
			new FileInputStream(filename), "UTF-8")))
		{
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(in);

			return Stage.fromJSON(json, loc, global);
		}
	}

	public static Stage fromJSON(
		JSONObject json, ResourceLocator loc, Library global
	) throws CorruptDataException
	{
		try {
			Object oStageJSON = json.get("stage");
			if (oStageJSON == null) throw new CorruptDataException(
				"Error in map file, missing stage");

			JSONObject stageJSON = (JSONObject) oStageJSON;

			Object rName = stageJSON.get("name");
			if (rName == null) throw new CorruptDataException("Error in stage, missing name");
			String name = (String) rName;

			Library lib = Library.fromJSON(json, name, loc, global, false);

			Object rTerrain = stageJSON.get("terrain");
			Object rSprites = stageJSON.get("sprites");

			if (rTerrain == null) throw new CorruptDataException("Error in stage, missing terrain");
			if (rSprites == null) throw new CorruptDataException("Error in stage, missing sprites");

			Stage r = new Stage(StageInfo.fromJSON((JSONObject) rTerrain, lib), lib);
			r.name = name;
			JSONArray sprites = (JSONArray) rSprites;
			for (Object s : sprites) {
				r.addSprite(Sprite.fromJSON((JSONObject) s, lib));
			}
			return r;
		} catch (ClassCastException e) {
			throw new CorruptDataException("Type error in stage", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public JSONObject getJSON() {
		JSONArray s = new JSONArray();
		for (Sprite sprite : allSprites) {
			s.add(sprite.getJSON());
		}

		JSONObject r = new JSONObject();
		r.put("name", name);
		r.put("terrain", terrain.getJSON());
		r.put("sprites", s);
		return r;
	}

	/**
	 * Add a sprite to the map.  z-order is determined by sprite priority.
	 * */
	public void addSprite(Sprite sprite) {
		allSprites.add(sprite);
		addSpriteToList(sprite, sprite.pos, sprites);
	}

	/**
	 * Add a sprite to the map, removing any sprites already at the same
	 * location.
	 * */
	public void replaceSprite(Sprite sprite) {
		clearTileOfSprites(sprite.pos);
		addSprite(sprite);
	}

	/**
	 * Remove a single sprite.
	 * */
	public void removeSprite(Sprite sprite) {
		allSprites.remove(sprite);
		removeSpriteFromList(sprite, sprite.pos, sprites);
	}

	/**
	 * Get all the sprites on a tile.
	 * */
	public List<Sprite> getSpritesByTile(MapPoint p) {
		List<Sprite> l = sprites.get(p);
		return l == null? new LinkedList<>() : new ArrayList<>(l);
	}

	/**
	 * Remove all the sprites on a given tile.
	 * */
	public void clearTileOfSprites(MapPoint p) {
		List<Sprite> l = sprites.get(p);
		if (l != null) allSprites.removeAll(l);
		sprites.remove(p);
	}

	private void addSpriteToList(
		Sprite sprite, MapPoint p, Map<MapPoint, List<Sprite>> sprites
	) {
		List<Sprite> l = sprites.get(p);
		if (l == null) {
			l = new LinkedList<>();
			sprites.put(p, l);
		}

		int i;
		for (i = 0; i < l.size(); i++)
			if (l.get(i).info.priority > sprite.info.priority) break;

		l.add(i, sprite);
	}

	private void removeSpriteFromList(
		Sprite sprite, MapPoint p, Map<MapPoint, List<Sprite>> sprites
	) {
		List<Sprite> l = sprites.get(p);
		if (l != null) l.remove(sprite);
	}

	/**
	 * Register a new animation chain with this stage.  Must be invoked in order
	 * to activate the animations.
	 * */
	public void registerAnimationChain(AnimationChain chain) {
		animationChains.add(chain);
	}

	public void deregisterAnimationChain(AnimationChain chain) {
		animationChains.remove(chain);
		chain.terminateChain();
	}

	/**
	 * Queue a move animation on a sprite.
	 * @param animation The walking animation to use.
	 * */
	public void queueMoveSprite(
		Sprite s, MapPoint start, MapPoint target, String animation, double speed
	) {
		AnimationChain chain = s.getAnimationChain();
		if (chain == null) {
			chain = new AnimationChain(s);
			s.setAnimationChain(chain);
			registerAnimationChain(chain);
		}
		
		chain.queueAnimation(new MoveSpriteAnimation(
			start, target, animation, speed, terrain,
				(current, next) -> {
					removeSpriteFromList(s, s.pos, sprites);

					if (!s.pos.equals(current)) removeSpriteFromList(s, current, slicedSprites);

					s.pos = current;
					addSpriteToList(s, s.pos, sprites);

					if (!current.equals(next)) addSpriteToList(s, next, slicedSprites);
				}));
	}

	/**
	 * Queue a teleport "animation".  Instantly moves the character sprite from
	 * one location to another.
	 * */
	public void queueTeleportSprite(Sprite s, MapPoint target) {
		AnimationChain chain = s.getAnimationChain();
		if (chain == null) {
			chain = new AnimationChain(s);
			s.setAnimationChain(chain);
			registerAnimationChain(chain);
		}

		chain.queueAnimation(new TeleportAnimation(
			s.pos, target, (from, to) -> {
				removeSpriteFromList(s, s.pos, sprites);
				s.pos = to;
				addSpriteToList(s, s.pos, sprites);
			}));
	}

	/**
	 * Rotate all the sprites on a particular tile.
	 * */
	public void rotateSprites(MapPoint p) {
		List<Sprite> l = sprites.get(p);
		if (l != null) for (Sprite s : l) s.rotate();
	}

	/**
	 * Does this stage use that TerrainTexture.
	 * */
	public boolean usesTerrainTexture(TerrainTexture tex) {
		return terrain.usesTerrainTexture(tex);
	}

	/**
	 * Does this stage use that Sprite.
	 * */
	public boolean usesSprite(SpriteInfo info) {
		return allSprites.stream().anyMatch(s -> s.info == info);
	}

	/**
	 * Does this stage use that CliffTexture.
	 * */
	public boolean usesCliffTexture(CliffTexture tex) {
		return terrain.usesCliffTexture(tex);
	}

	/**
	 * Get the upper left hand coordinate of a tile in iso space,
	 * assuming no elevation.
	 * */
	public Point2D toIsoCoord(MapPoint p, CameraAngle a) {
		Point2D in = new Point2D(p.x, p.y);
		switch (a) {
			case UL: return isoTransform.transform(rUL.transform(in));
			case LL: return isoTransform.transform(rLL.transform(in));
			case LR: return isoTransform.transform(rLR.transform(in));
			case UR: return isoTransform.transform(rUR.transform(in));
			default: throw new RuntimeException(
				"Invalid camera angle.  This cannot happen");
		}
	}

	/**
	 * Convert an iso coordinate to the (uncorrected) map tile that lives there.
	 * */
	public MapPoint fromIsoCoord(Point2D in, CameraAngle a) {
		Point2D t;
		try {
			switch (a) {
				case UL:
					t = rUL.inverseTransform(isoTransform.inverseTransform(in));
					return new MapPoint((int) (t.getX() - 0.5), (int) t.getY());
				case LL:
					t = rLL.inverseTransform(isoTransform.inverseTransform(in));
					return new MapPoint((int) (t.getX() + 0.5), (int) (t.getY() + 1.5));
				case LR:
					t = rLR.inverseTransform(isoTransform.inverseTransform(in));
					return new MapPoint((int) (t.getX() + 1.5), (int) t.getY());
				case UR:
					t = rUR.inverseTransform(isoTransform.inverseTransform(in));
					return new MapPoint((int) (t.getX() - 1.5), (int) (t.getY() + 0.5));
				default: throw new RuntimeException(
					"Invalid camera angle.  This cannot happen");
			}
		} catch (NonInvertibleTransformException e) {
			throw new RuntimeException("This cannot happen", e);
		}
	}

	/**
	 * Get the upper left hand coordinate of a tile in iso space, accounting for
	 * its elevation.
	 * */
	public Point2D correctedIsoCoord(MapPoint p, CameraAngle a) {
		return toIsoCoord(p, a).add(0, ELEVATION_H * terrain.getTile(p).elevation);
	}

	private Map<MapPoint, LinkedList<Integer>> highlighting = new HashMap<>();
	private Highlighter[] highlightColors = {};

	/**
	 * Set the highlighting colour scheme.
	 * */
	public void setHighlightColors(Highlighter[] highlightColors) {
		this.highlightColors = highlightColors;
	}

	/**
	 * Highlight a tile.
	 * @param p The tile to highlight
	 * @param priority The highlighter to use.  A tile map be under several
	 * highlights at once, but only the highest priority highlight is actually
	 * rendered.  Lower number == lower priority
	 * */
	public void setHighlight(MapPoint p, int priority) {
		if (priority < 0 || priority >= highlightColors.length) {
			throw new RuntimeException("Invalid highlight priority " + priority);
		}

		LinkedList<Integer> highlights = highlighting.get(p);

		// insert new highlight value into the list, keeping the list sorted.  Ugly
		// but it works
		if (highlights == null) {
			highlights = new LinkedList<>();
			highlights.add(priority);
			highlighting.put(p, highlights);
		} else if (highlights.isEmpty()) {
			highlights.add(priority);
		} else {
			int i = 0;
			for (int h : highlights) {
				if (priority > h) {
					highlights.add(i, priority);
					break;
				} else {
					i++;
				}
			}
		}
	}

	/**
	 * Clear a highlighting level.
	 * */
	public void clearHighlighting(Integer priority) {
		highlighting.values().forEach(h -> h.remove(priority));
	}

	/**
	 * Clear all highlighting.
	 * */
	public void clearAllHighlighting() {
		highlighting.clear();
	}

	/**
	 * Is that tile highlighted.
	 * */
	public boolean isHighlighted(MapPoint p) {
		return highlighting.containsKey(p) && highlighting.get(p).size() > 0;
	}

	/**
	 * Render the entire stage (skipping the invisible bits for efficiency).
	 * @param cx The graphics context
	 * @param angle The camera angle
	 * @param visible Bounding box for the visible part of the map
	 * @param renderDebug Render debugging information
	 * */
	public void render(GraphicsContext cx, CameraAngle angle,
		long t, BoundingBox visible, boolean renderDebug
	) {
		for (AnimationChain chain : animationChains) {
			chain.updateAnimation(t);
		}

		terrain.iterateTiles(angle).forEachRemaining(tile -> {
			Point2D p = correctedIsoCoord(tile.pos, angle);
			double x = p.getX();
			double y = p.getY();

			if (visible.intersects(x, y, TILEW, TILEH)) {
				// get the highlight color
				Highlighter hcolor = null;
				LinkedList<Integer> h = highlighting.get(tile.pos);
				if (h != null) {
					Integer i = h.peekFirst();
					if (i != null) hcolor = highlightColors[i];
				}

				cx.save();
				cx.translate(x, y);

				cx.save();
				tile.render(cx, hcolor, angle);
				cx.restore();

				if (tile.slope != SlopeType.NONE) {
					cx.translate(0, -(TILEH / 4));
				}

				List<Sprite> l = sprites.get(tile.pos);
				if (l != null) for (Sprite s : l) doSprite(cx, angle, t, s, false);
				l = slicedSprites.get(tile.pos);
				if (l != null) for (Sprite s : l) doSprite(cx, angle, t, s, true);

				if (renderDebug) {
					cx.setFill(Color.RED);
					String status = tile.specialStatusString();
					if (status != null) {
						cx.fillText(status, TILEW / 3, TILEH / 2);
					}
				}

				cx.restore();
			}
		});
	}

	private void doSprite(
		GraphicsContext cx, CameraAngle angle, long t, Sprite s, boolean sliced
	) {
		AnimationChain chain = s.getAnimationChain();
		if (chain != null) {
			chain.renderSprite(cx, angle, s, t, sliced);
		} else {
			cx.save();
			s.renderFrame(cx, 0, (int) TILEW, t, angle);
			cx.restore();
		}
	}
}

