package isogame.engine;

import java.io.InputStream;
import java.io.IOException;

public interface ResourceLocator {
	public InputStream gfx(String file) throws IOException;
	public InputStream sfx(String file) throws IOException;
	public InputStream gameData() throws IOException;
	public InputStream globalLibrary() throws IOException;
}

