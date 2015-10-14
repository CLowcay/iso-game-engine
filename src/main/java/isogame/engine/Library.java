package isogame.engine;

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
	public Library(InputStream inStream, String url, Library parent)
		throws IOException, CorruptDataException
	{
		this.parent = parent;
		
		try (BufferedReader in =
			new BufferedReader(new InputStreamReader(inStream, "UTF-8"))
		) {
			if (in == null) throw new FileNotFoundException("File not found " + url);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(in);
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
				this.sprites.put(id, SpriteInfo.fromJSON(sprite));
			}

			for (Object x : terrains) {
				JSONObject terrain = (JSONObject) x;
				String id = (String) terrain.get("id");
				if (id == null)
					throw new CorruptDataException("Missing id for sprite in " + url);
				this.terrains.put(id, TerrainTexture.fromJSON(terrain));
			}

			for (Object x : cliffTextures) {
				JSONObject cliffTerrain = (JSONObject) x;
				String id = (String) cliffTerrain.get("id");
				if (id == null)
					throw new CorruptDataException("Missing id for sprite in " + url);
				this.cliffTextures.put(id, CliffTexture.fromJSON(cliffTerrain));
			}

		} catch (ClassCastException e) {
			throw new CorruptDataException(url + " is corrupted");
		} catch (ParseException e) {
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

