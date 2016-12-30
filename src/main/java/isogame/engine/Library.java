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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * A library of game assets.
 *
 * File format:
 *
 * {
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
 * 		]
 * 		"terrains" : [
 * 			{
 * 				"id" : STRING,
 * 				"url" : STRING
 * 			}
 * 			...
 * 		]
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
	private Map<String, SpriteInfo> sprites = new HashMap<>();
	private Map<String, TerrainTexture> terrains = new HashMap<>();
	private Map<String, CliffTexture> cliffTextures = new HashMap<>();

	private CliffTexture defaultCliffTexture = null;

	private final Library parent;

	public Sprite newSprite(String id) throws CorruptDataException {
		SpriteInfo i = getSprite(id);
		return new Sprite(i);
	}

	public SpriteInfo getSprite(String id) throws CorruptDataException {
		SpriteInfo i = sprites.get(id);
		if (i == null) {
			if (parent == null)
				throw new CorruptDataException("Missing sprite " + id);
			else return parent.getSprite(id);
		} else return i;
	}

	public TerrainTexture getTerrain(String id) throws CorruptDataException {
		TerrainTexture r = terrains.get(id);
		if (r == null) {
			if (parent == null)
				throw new CorruptDataException("Missing terrain texture " + id);
			else return parent.getTerrain(id);
		} else return r;
	}

	public CliffTexture getCliffTexture(String id) throws CorruptDataException {
		CliffTexture r = cliffTextures.get(id);
		if (r == null) {
			if (parent == null)
				throw new CorruptDataException("Missing cliff texture " + id);
			else return parent.getCliffTexture(id);
		} else return r;
	}

	public void deleteTerrain(String id) throws CorruptDataException {
		if (terrains.remove(id) == null) throw
			new CorruptDataException("No such terrain " + id);
	}

	public void deleteSprite(String id) throws CorruptDataException {
		if (sprites.remove(id) == null) throw
			new CorruptDataException("No such sprite " + id);
	}

	public void deleteCliffTexture(String id) throws CorruptDataException {
		if (cliffTextures.remove(id) == null) throw
			new CorruptDataException("No such cliff texture " + id);
	}

	public void updateSprite(SpriteInfo sprite) throws CorruptDataException {
		if (sprites.containsKey(sprite.id)) {
			sprites.put(sprite.id, sprite);
		} else if (parent != null) {
			parent.updateSprite(sprite);
		} else {
			throw new CorruptDataException(
				"Cannot update sprite because it doesn't exist yet: " + sprite.id);
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

	public void addSprite(SpriteInfo sprite) {
		sprites.put(sprite.id, sprite);
	}

	public void addTerrain(TerrainTexture terrain) {
		terrains.put(terrain.id, terrain);
	}

	public void addCliffTexture(CliffTexture cliffTexture) {
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
	public Library(Library parent) {
		this.parent = parent;
	}

	/**
	 * Load the library described in a JSON file.
	 * @param inStream The input stream.  It will be closed automatically.
	 * */
	public static Library fromFile(
		InputStream inStream, String url,
		ResourceLocator loc, Library parent, boolean nofx
	) throws IOException, CorruptDataException
	{
		try (BufferedReader in =
			new BufferedReader(new InputStreamReader(inStream, "UTF-8"))
		) {
			if (in == null) throw new FileNotFoundException("File not found " + url);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(in);
			return fromJSON(json, url, loc, parent, nofx);
		} catch (ParseException e) {
			throw new CorruptDataException(url + " is corrupted");
		}
	}

	/**
	 * Parse a library out of JSON data
	 * @param nofx True if we cannot use JavaFX in this environment
	 * */
	public static Library fromJSON(
		JSONObject json, String url,
		ResourceLocator loc, Library parent, boolean nofx
	) throws CorruptDataException
	{
		try {
			Library r = new Library(parent);

			JSONArray sprites = (JSONArray) json.get("sprites");
			JSONArray terrains = (JSONArray) json.get("terrains");
			JSONArray cliffTextures = (JSONArray) json.get("cliffTextures");

			if (sprites == null) throw new CorruptDataException(
				"Missing sprites section in " + url);
			if (terrains == null) throw new CorruptDataException(
				"Missing terrains section in " + url);
			if (cliffTextures == null) throw new CorruptDataException(
				"Missing cliffTextures section in " + url);

			for (Object x : sprites) {
				JSONObject sprite = (JSONObject) x;
				String id = (String) sprite.get("id");
				if (id == null)
					throw new CorruptDataException("Missing id for sprite in " + url);
				r.sprites.put(id, SpriteInfo.fromJSON(sprite, loc));
			}

			for (Object x : terrains) {
				JSONObject terrain = (JSONObject) x;
				String id = (String) terrain.get("id");
				if (id == null)
					throw new CorruptDataException("Missing id for sprite in " + url);
				r.terrains.put(id, TerrainTexture.fromJSON(terrain, loc, nofx));
			}

			for (Object x : cliffTextures) {
				JSONObject cliffTerrain = (JSONObject) x;
				String id = (String) cliffTerrain.get("id");
				if (id == null)
					throw new CorruptDataException("Missing id for sprite in " + url);
				r.cliffTextures.put(id, CliffTexture.fromJSON(cliffTerrain, loc, nofx));
			}

			return r;
		} catch (ClassCastException e) {
			throw new CorruptDataException(url + " is corrupted");
		}
	}

	/**
	 * Write this library to an output stream.
	 * */
	public void writeToStream(OutputStream outStream) throws IOException {
		writeToStream(outStream, null);
	}

	/**
	 * Write this library to an output stream along with stage data to make a map
	 * file.
	 * */
	@SuppressWarnings("unchecked")
	public void writeToStream(OutputStream outStream, Stage stage) throws IOException {
		try (PrintWriter out =
			new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"));
		) {
			JSONObject o = new JSONObject();

			JSONArray spriteArray = new JSONArray();
			sprites.values().forEach(x -> spriteArray.add(x.getJSON()));
			JSONArray terrainArray = new JSONArray();
			terrains.values().forEach(x -> terrainArray.add(x.getJSON()));
			JSONArray cliffArray = new JSONArray();
			cliffTextures.values().forEach(x -> cliffArray.add(x.getJSON()));

			o.put("sprites", spriteArray);
			o.put("terrains", terrainArray);
			o.put("cliffTextures", cliffArray);
			if (stage != null) o.put("stage", stage.getJSON());

			out.print(o);
		}
	}
}

