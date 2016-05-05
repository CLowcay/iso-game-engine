package isogame.battle.data;

import isogame.engine.CorruptDataException;
import isogame.engine.Library;
import isogame.engine.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GameDataFactory {
	private final Library globalLibrary;
	private static final String globalLibraryName = "global_library.json";
	private static final String gameDataName = "game_data.json";
	private static final File gameDataCacheDir =
		new File(System.getProperty("user.home"), ".inthezone");

	public GameDataFactory(Optional<File> baseDir)
		throws IOException, CorruptDataException
	{
		InputStream gameData;
		File gameDataFile;

		// developer mode
		if (baseDir.isPresent()) {
			File base = baseDir.get();
			String uri = (new File(base, globalLibraryName)).toString();
			this.globalLibrary = Library.fromFile(new FileInputStream(uri), uri, null);
			gameDataFile = new File(base, gameDataName);
			gameData = new FileInputStream(gameDataFile);

		// normal mode
		} else {
			this.globalLibrary = Library.fromFile(
				GameDataFactory.class.getResourceAsStream("/" + globalLibraryName),
				"/" + globalLibraryName, null);

			gameDataFile = new File(gameDataCacheDir, gameDataName);

			if (!gameDataFile.exists()) {
				// copy the compiled-in version to make a new cached version
				if (!gameDataCacheDir.exists()) gameDataCacheDir.mkdir();
				OutputStream fout = new FileOutputStream(gameDataFile);
				InputStream fin = GameDataFactory.class.getResourceAsStream(
					"/" + gameDataFile.toString());
				int b;
				while ((b = fin.read()) != -1) fout.write(b);
				fout.close();
				fin.close();
			}
			gameData = new FileInputStream(gameDataFile);
		}

		// load the game data
		try (BufferedReader in =
			new BufferedReader(new InputStreamReader(gameData, "UTF-8"))
		) {
			if (in == null) throw new FileNotFoundException(
				"File not found " + gameDataFile.toString());
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(in);
			loadGameData(json);
		} catch (ParseException e) {
			throw new CorruptDataException("game data is corrupted");
		}
	}

	public void updateGameData(JSONObject json) throws CorruptDataException {
		stages.clear();
		weapons.clear();
		characters.clear();
		loadGameData(json);
	}

	private void loadGameData(JSONObject json) throws CorruptDataException {
		Object oVersion = json.get("version");
		Object oStages = json.get("stages");
		Object oWeapons = json.get("weapons");
		Object oCharacters = json.get("characters");

		if (oStages == null) throw new CorruptDataException("No stages in game data");
		if (oWeapons == null) throw new CorruptDataException("No weapons in game data");
		if (oCharacters == null) throw new CorruptDataException("No characters in game data");

		try {
			this.version = UUID.fromString((String) oVersion);

			JSONArray aStages = (JSONArray) oStages;
			JSONArray aWeapons = (JSONArray) oWeapons;
			JSONArray aCharacters = (JSONArray) oCharacters;

			for (Object x : aWeapons) {
				Stage i = Stage.fromJSON((JSONObject) x, globalLibrary);
				stages.put(i.name, i);
			}

			for (Object x : aWeapons) {
				WeaponInfo i = WeaponInfo.fromJSON((JSONObject) x);
				weapons.put(i.name, i);
			}

			for (Object x : aCharacters) {
				CharacterInfo i = CharacterInfo.fromJSON((JSONObject) x);
				characters.put(i.name, i);
			}

		} catch (ClassCastException e) {
			throw new CorruptDataException("Type error in game data: ", e);
		} catch (IllegalArgumentException e) {
			throw new CorruptDataException("Type error in game data: ", e);
		}
	}

	private UUID version;
	private Map<String, Stage> stages = new HashMap<>();
	private Map<String, WeaponInfo> weapons = new HashMap<>();
	private Map<String, CharacterInfo> characters = new HashMap<>();

	public UUID getVersion() {
		return version;
	}

	/**
	 * May return null
	 * */
	public Stage getStage(String name) {
		return stages.get(name);
	}

	public Collection<Stage> getStages() {
		return stages.values();
	}

	/**
	 * May return null
	 * */
	public WeaponInfo getWeapon(String name) {
		return weapons.get(name);
	}

	/**
	 * May return null
	 * */
	public CharacterInfo getCharacter(String name) {
		return characters.get(name);
	}

	public Collection<CharacterInfo> getCharacters() {
		return characters.values();
	}
}

