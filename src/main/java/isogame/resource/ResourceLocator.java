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

import java.io.InputStream;
import java.io.IOException;

public interface ResourceLocator {
	public InputStream gfx(String file) throws IOException;
	public InputStream sfx(String file) throws IOException;
	public InputStream gameData() throws IOException;
	public InputStream globalLibrary() throws IOException;

	public String gameDataFilename();
	public String globalLibraryFilename();
}

