package isogame.editor;

import isogame.engine.ResourceLocator;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

public class DevelopmentResourceLocator implements ResourceLocator {
	private final File dataDir;

	public DevelopmentResourceLocator(File dataDir) {
		this.dataDir = dataDir;
	}

	@Override
	public InputStream gfx(String file) throws IOException {
		return new FileInputStream(new File(dataDir, "gfx/" + file));
	}

	@Override
	public InputStream sfx(String file) throws IOException {
		return new FileInputStream(new File(dataDir, "sfx/" + file));
	}

	@Override
	public InputStream gameData() throws IOException {
		return new FileInputStream(new File(dataDir, "game_data.json"));
	}

	@Override
	public InputStream globalLibrary() throws IOException {
		return new FileInputStream(new File(dataDir, "global_library.json"));
	}
}

