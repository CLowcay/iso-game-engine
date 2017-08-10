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
package isogame.resource;

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
	public InputStream gfx(final String file) throws IOException {
		return new FileInputStream(new File(dataDir, "gfx/" + file));
	}

	@Override
	public InputStream sfx(final String file) throws IOException {
		return new FileInputStream(new File(dataDir, "sfx/" + file));
	}

	@Override
	public InputStream gameData() throws IOException {
		return new FileInputStream(gameDataFilename());
	}

	@Override
	public InputStream globalLibrary() throws IOException {
		return new FileInputStream(globalLibraryFilename());
	}

	@Override
	public String gameDataFilename() {
		return (new File(dataDir, "game_data.json")).toString();
	}

	@Override
	public String globalLibraryFilename() {
		return (new File(dataDir, "global_library.json")).toString();
	}
}

