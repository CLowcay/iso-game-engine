package isogame;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
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
	 * */
	public Library(String url) throws IOException, CorruptDataException {
		try (BufferedReader in =
			new BufferedReader(
			new InputStreamReader(
				this.getClass().getResourceAsStream(url), "UTF-8"))
		) {
			if (in == null) throw new FileNotFoundException("File not found " + url);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(in);
			JSONArray sprites = (JSONArray) json.get("sprites");
			JSONArray terrains = (JSONArray) json.get("terrains");
			JSONArray cliffTextures = (JSONArray) json.get("cliffTextures");

			for (Object x : sprites) {
				JSONObject sprite = (JSONObject) x;
				String id = (String) sprite.get("id");
				this.sprites.put(id, parseSprite(sprite));
			}

			for (Object x : terrains) {
				JSONObject terrain = (JSONObject) x;
				String id = (String) terrain.get("id");
				this.terrains.put(id, parseTerrain(terrain));
			}

			for (Object x : cliffTextures) {
				JSONObject cliffTerrain = (JSONObject) x;
				String id = (String) cliffTerrain.get("id");
				this.cliffTextures.put(id, parseCliffTexture(cliffTerrain));
			}

		} catch (ClassCastException e) {
			throw new CorruptDataException(url + " is corrupted");
		} catch (ParseException e) {
			throw new CorruptDataException(url + " is corrupted");
		}
	}

	private SpriteInfo parseSprite(JSONObject sprite) {
		String id = (String) sprite.get("id");
		SpriteInfo r = new SpriteInfo(id);

		JSONArray animations = (JSONArray) sprite.get("animations");
		for (Object x : animations) {
			JSONObject animation = (JSONObject) x;
			String animID = (String) animation.get("id");
			String url = (String) animation.get("url");
			Number frames = (Number) animation.get("nframes");
			Number framerate =(Number) animation.get("framerate");

			r.addAnimation(animID, new SpriteAnimation(
				animID, url, frames.intValue(), framerate.intValue()));
		}
		return r;
	}

	private TerrainTexture parseTerrain(JSONObject terrain) {
		String url = (String) terrain.get("url");
		return new TerrainTexture(url);
	}

	private CliffTexture parseCliffTexture(JSONObject cliffTerrain) {
		String urlWide = (String) cliffTerrain.get("urlWide");
		String urlNarrow = (String) cliffTerrain.get("urlNarrow");
		return new CliffTexture(urlWide, urlNarrow);
	}
}

