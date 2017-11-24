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
package isogame.engine;

public enum SlopeType {
	N, S, E, W, NONE;

	public SlopeType opposite() {
		switch(this) {
			case N: return S;
			case S: return N;
			case E: return W;
			case W: return E;
			case NONE: return NONE;
			default: throw new RuntimeException("This cannot happen");
		}
	}
}

