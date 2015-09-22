package isogame;

import org.json.simple.JSONObject;

/**
 * An interface of objects that can be serialized to JSON.
 * */
public interface HasJSONRepresentation {
	public JSONObject getJSON();
}

