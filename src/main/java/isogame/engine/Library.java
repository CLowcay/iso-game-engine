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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A library of game assets.
 *
 * File format:
 *
 * {
 * 	"sprite_priorities" : [],
 * 	"assets" : {
 * 		"sprites" : [
 * 			{
 * 				"id" : STRING,
 * 				"animations" : [
 * 					{
 * 						"id" : STRING,
 * 						"url" : STRING,
 * 						"nframes" : INT,
 * 						"framerate" : INT  # FPS
 * 					}
 * 					...
 * 				]
 * 			}
 * 			...
 * 		],
 * 		"terrains" : [
 * 			{
 * 				"id" : STRING,
 * 				"url" : STRING
 * 			}
 * 			...
 * 		],
 * 		"cliffTextures" : [
 * 			{
 * 				"id" : STRING,
 * 				"urlWide" : STRING
 * 				"urlNarrow" : STRING
 * 			}
 * 			...
 * 		]
 * 	}
 * }
 * */
public class Library {
	private final Map<String, SpriteInfo> sprites = new HashMap<>();
	private final Map<String, TerrainTexture> terrains = new HashMap<>();
	private final Map<String, CliffTexture> cliffTextures = new HashMap<>();

	private CliffTexture defaultCliffTexture = null;

	private final Library parent;

	public final List<String> priorities = new ArrayList<>();

	public Sprite newSprite(final String id) throws CorruptDataException {
		final SpriteInfo i = getSprite(id);
		return new Sprite(i);
	}

	public SpriteInfo getSprite(final String id) throws CorruptDataException {
		final SpriteInfo i = sprites.get(id);
		if (i == null) {
			if (parent == null)
				throw new CorruptDataException("Missing sprite \"" + id + "\"");
			else return parent.getSprite(id);
		} else return i;
	}

	public TerrainTexture getTerrain(final String id) throws CorruptDataException {
		final TerrainTexture r = terrains.get(id);
		if (r == null) {
			if (parent == null)
				throw new CorruptDataException("Missing terrain texture \"" + id + "\"");
			else return parent.getTerrain(id);
		} else return r;
	}

	public CliffTexture getCliffTexture(final String id) throws CorruptDataException {
		final CliffTexture r = cliffTextures.get(id);
		if (r == null) {
			if (parent == null)
				throw new CorruptDataException("Missing cliff texture \"" + id + "\"");
			else return parent.getCliffTexture(id);
		} else return r;
	}

	public void deleteTerrain(final String id) throws CorruptDataException {
		if (terrains.remove(id) == null) throw
			new CorruptDataException("No such terrain \"" + id + "\"");
	}

	public void deleteSprite(final String id) throws CorruptDataException {
		if (sprites.remove(id) == null) throw
			new CorruptDataException("No such sprite \"" + id + "\"");
	}

	public void deleteCliffTexture(final String id) throws CorruptDataException {
		if (cliffTextures.remove(id) == null) throw
			new CorruptDataException("No such cliff texture \"" + id + "\"");
	}

	public void updateSprite(final SpriteInfo sprite) throws CorruptDataException {
		if (sprites.containsKey(sprite.id)) {
			sprites.put(sprite.id, sprite);
		} else if (parent != null) {
			parent.updateSprite(sprite);
		} else {
			throw new CorruptDataException(
				"Cannot update sprite because it doesn't exist yet: \"" + sprite.id + "\"");
		}
	}

	public Collection<SpriteInfo> allSprites() {
		return sprites.values();
	}

	public Collection<TerrainTexture> allTerrains() {
		return terrains.values();
	}

	public Collection<CliffTexture> allCliffTextures() {
		return cliffTextures.values();
	}

	public void addSprite(final SpriteInfo sprite) {
		sprites.put(sprite.id, sprite);
	}

	public void addTerrain(final TerrainTexture terrain) {
		terrains.put(terrain.id, terrain);
	}

	public void addCliffTexture(final CliffTexture cliffTexture) {
		cliffTextures.put(cliffTexture.id, cliffTexture);
	}

	public CliffTexture getDefaultCliffTexture() throws CorruptDataException {
		if (defaultCliffTexture == null)
			throw new CorruptDataException("No cliff textures defined");
		else return defaultCliffTexture;
	}

	/**
	 * Create an empty library
	 * */
	public Library(final Library parent) {
		this.parent = parent;
		if (parent != null) this.priorities.addAll(parent.priorities);
	}

	/**
	 * Load the library described in a JSON file.
	 * @param inStream The input stream.  It will be closed automatically.
	 * */
	public static Library fromFile(
		final InputStream inStream, final String url,
		final ResourceLocator loc, final Library parent, final boolean nofx
	) throws IOException, CorruptDataException
	{
		try (BufferedReader in =
			new BufferedReader(new InputStreamReader(inStream, "UTF-8"))
		) {
			if (in == null) throw new FileNotFoundException("File not found " + url);

			final StringBuilder raw = new StringBuilder();
			String line = null;
			while ((line = in.readLine()) != null) raw.append(line);

			final JSONObject json = new JSONObject(raw.toString());
			return fromJSON(json, url, loc, parent, nofx);

		} catch (JSONException e) {
			throw new CorruptDataException(url + " is corrupted");
		}
	}

	/**
	 * Parse a library out of JSON data
	 * @param nofx True if we cannot use JavaFX in this environment
	 * */
	public static Library fromJSON(
		final JSONObject json, final String url,
		final ResourceLocator loc, final Library parent, final boolean nofx
	) throws CorruptDataException
	{
		try {
			final Library r = new Library(parent);

			final Object opriorities = json.opt("sprite_priorities");
			final JSONArray sprites = json.getJSONArray("sprites");
			final JSONArray terrains = json.getJSONArray("terrains");
			final JSONArray cliffTextures = json.getJSONArray("cliffTextures");

			if (sprites == null) throw new CorruptDataException(
				"Missing sprites section in " + url);
			if (terrains == null) throw new CorruptDataException(
				"Missing terrains section in " + url);
			if (cliffTextures == null) throw new CorruptDataException(
				"Missing cliffTextures section in " + url);

			if (opriorities != null) {
				final JSONArray priorities = (JSONArray) opriorities;
				if (priorities.length() > 0) {
					r.priorities.clear();
					for (final Object x : priorities) r.priorities.add((String) x);
				}
			}

			for (final Object x : sprites) {
				final JSONObject sprite = (JSONObject) x;
				final String id = (String) sprite.get("id");
				if (id == null)
					throw new CorruptDataException("Missing id for sprite in " + url);
				r.sprites.put(id, SpriteInfo.fromJSON(sprite, loc));
			}

			for (final Object x : terrains) {
				final JSONObject terrain = (JSONObject) x;
				final String id = (String) terrain.get("id");
				if (id == null)
					throw new CorruptDataException("Missing id for sprite in " + url);
				r.terrains.put(id, TerrainTexture.fromJSON(terrain, loc, nofx));
			}

			for (final Object x : cliffTextures) {
				final JSONObject cliffTerrain = (JSONObject) x;
				final String id = (String) cliffTerrain.get("id");
				if (id == null)
					throw new CorruptDataException("Missing id for sprite in " + url);
				r.cliffTextures.put(id, CliffTexture.fromJSON(cliffTerrain, loc, nofx));
			}

			return r;
		} catch (ClassCastException e) {
			throw new CorruptDataException(url + " is corrupted");
		} catch (JSONException e) {
			throw new CorruptDataException("Error parsing data: " + e.getMessage());
		}
	}

	/**
	 * Write this library to an output stream.
	 * */
	public void writeToStream(final OutputStream outStream) throws IOException {
		writeToStream(outStream, null);
	}

	/**
	 * Write this library to an output stream along with stage data to make a map
	 * file.
	 * */
	@SuppressWarnings("unchecked")
	public void writeToStream(
		final OutputStream outStream, final Stage stage
	) throws IOException {
		try (PrintWriter out =
			new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"));
		) {
			final JSONObject o = new JSONObject();

			final JSONArray prioritiesArray = new JSONArray();
			priorities.forEach(x -> prioritiesArray.put(x));
			final JSONArray spriteArray = new JSONArray();
			sprites.values().forEach(x -> spriteArray.put(x.getJSON()));
			final JSONArray terrainArray = new JSONArray();
			terrains.values().forEach(x -> terrainArray.put(x.getJSON()));
			final JSONArray cliffArray = new JSONArray();
			cliffTextures.values().forEach(x -> cliffArray.put(x.getJSON()));

			o.put("sprite_priorities", prioritiesArray);
			o.put("sprites", spriteArray);
			o.put("terrains", terrainArray);
			o.put("cliffTextures", cliffArray);
			if (stage != null) o.put("stage", stage.getJSON());

			out.print(o);
		}
	}
}

