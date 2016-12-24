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

/**
 * The four camera angles.
 * */
public enum CameraAngle {
	// Internally, we represent the game as a conventional 2D chess board, but we
	// display it from an angle rather than a bird's-eye-view.  This enum tells
	// us which corner of the chess board appears the *furthest* from the camera.
	// From this we determine what order to draw the terrain tiles, and which
	// rotation to use for the sprites.

	// Upper Left, Upper Right, Lower Left, Lower Right.
	UL, UR, LL, LR;

	public CameraAngle nextClockwise() {
		switch (this) {
			case UL: return LL;
			case UR: return UL;
			case LL: return LR;
			case LR: return UR;
		}
		throw new RuntimeException("Invalid camera angle, This cannot happen");
	}

	public CameraAngle nextAnticlockwise() {
		switch (this) {
			case UL: return UR;
			case UR: return LR;
			case LL: return UL;
			case LR: return LL;
		}
		throw new RuntimeException("Invalid camera angle, This cannot happen");
	}
}

