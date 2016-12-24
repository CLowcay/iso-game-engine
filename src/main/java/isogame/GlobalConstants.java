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
package isogame;

/**
 * Any global configuration goes here so it's easy to find when we need to
 * change it.
 * */
public class GlobalConstants {
	public static final double TILEW = 512;
	public static final double TILEH = 256;

	public static final double ELEVATION_H = -128;

	public static final double ISO_VIEWPORTW = 1920 * 2;
	public static final double ISO_VIEWPORTH = 1080 * 2;

	// number of seconds to scroll one tile height
	public static final double SCROLL_SPEED = 4;
}

