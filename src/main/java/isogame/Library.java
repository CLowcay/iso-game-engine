package isogame;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
	public Map<String, SpriteInfo> sprites = new HashMap<>();
	public Map<String, TerrainTexture> terrains = new HashMap<>();
	public Map<String, CliffTexture> cliffTextures = new HashMap<>();

	public Sprite newSprite(String id) throws CorruptDataException {
		SpriteInfo i = sprites.get(id);
		if (i == null)
			throw new CorruptDataException("Missing sprite " + id);
		else return new Sprite(i);
	}

	public TerrainTexture getTerrain(String id) throws CorruptDataException {
		TerrainTexture r = terrains.get(id);
		if (r == null)
			throw new CorruptDataException("Missing terrain texture " + id);
		else return r;
	}

	public CliffTexture getCliffTexture(String id) throws CorruptDataException {
		CliffTexture r = cliffTextures.get(id);
		if (r == null)
			throw new CorruptDataException("Missing cliff texture " + id);
		else return r;
	}

	/**
	 * Load the library described in a JSON file.
	 * @param inStream The input stream.  It will be closed automatically.
	 * */
	public Library(InputStream inStream, String url)
		throws IOException, CorruptDataException
	{
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
				this.sprites.put(id, parseSprite(sprite));
			}

			for (Object x : terrains) {
				JSONObject terrain = (JSONObject) x;
				String id = (String) terrain.get("id");
				if (id == null)
					throw new CorruptDataException("Missing id for sprite in " + url);
				this.terrains.put(id, parseTerrain(terrain));
			}

			for (Object x : cliffTextures) {
				JSONObject cliffTerrain = (JSONObject) x;
				String id = (String) cliffTerrain.get("id");
				if (id == null)
					throw new CorruptDataException("Missing id for sprite in " + url);
				this.cliffTextures.put(id, parseCliffTexture(cliffTerrain));
			}

		} catch (ClassCastException e) {
			throw new CorruptDataException(url + " is corrupted");
		} catch (ParseException e) {
			throw new CorruptDataException(url + " is corrupted");
		}
	}

	@SuppressWarnings("unchecked")
	public void writeToStream(OutputStream outStream) throws IOException {
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

			out.print(o);
		}
	}

	private SpriteInfo parseSprite(JSONObject sprite)
		throws CorruptDataException
	{
		String id = (String) sprite.get("id");
		SpriteInfo r = new SpriteInfo(id);

		JSONArray animations = (JSONArray) sprite.get("animations");
		if (animations == null) throw new CorruptDataException(
			"Sprite " + id + " is missing animations");

		for (Object x : animations) {
			JSONObject animation = (JSONObject) x;
			String animID = (String) animation.get("id");
			String url = (String) animation.get("url");
			Number frames = (Number) animation.get("nframes");
			Number framerate =(Number) animation.get("framerate");

			if (
				animID == null || url == null || frames == null || framerate == null
			) {
				throw new CorruptDataException("Corrupted animation in sprite " + id);
			}

			r.addAnimation(animID, new SpriteAnimation(
				animID, url, frames.intValue(), framerate.intValue()));
		}
		return r;
	}

	private TerrainTexture parseTerrain(JSONObject terrain)
		throws CorruptDataException
	{
		String id = (String) terrain.get("id");
		String url = (String) terrain.get("url");
		if (url == null) throw new CorruptDataException(
			"Terrain " + id + " missing url");
	
		return new TerrainTexture(id, url);
	}

	private CliffTexture parseCliffTexture(JSONObject cliffTerrain)
		throws CorruptDataException
	{
		String id = (String) cliffTerrain.get("id");
		String urlWide = (String) cliffTerrain.get("urlWide");
		String urlNarrow = (String) cliffTerrain.get("urlNarrow");
		if (urlWide == null || urlNarrow == null) throw new CorruptDataException(
			"Cliff texture " + id + " is missing urls");

		return new CliffTexture(id, urlWide, urlNarrow);
	}
}

