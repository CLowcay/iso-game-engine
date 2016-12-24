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
 * What direction a sprite is facing, relative to a bird's-eye-view.
 * */
public enum FacingDirection {
	UP, DOWN, LEFT, RIGHT;

	public FacingDirection rotateClockwise() {
		switch (this) {
			case UP: return RIGHT;
			case LEFT: return UP;
			case DOWN: return LEFT;
			case RIGHT: return DOWN;
		}

		throw new IllegalArgumentException("Invalid FacingDirection, this cannot happen");
	}

	public FacingDirection rotateAntiClockwise() {
		switch (this) {
			case UP: return LEFT;
			case LEFT: return DOWN;
			case DOWN: return RIGHT;
			case RIGHT: return UP;
		}

		throw new IllegalArgumentException("Invalid FacingDirection, this cannot happen");
	}

	/**
	 * Which slope would you be going up if you went up along this direction.
	 * */
	public SlopeType upThisWay() {
		switch (this) {
			case UP: return SlopeType.N;
			case LEFT: return SlopeType.W;
			case DOWN: return SlopeType.S;
			case RIGHT: return SlopeType.E;
			default: throw new RuntimeException("This cannot happen");
		}
	}

	/**
	 * Return values are:
	 * 0: UP
	 * 1: LEFT
	 * 2: DOWN
	 * 3: RIGHT
	 * */
	public int transform(CameraAngle angle) {
		int d = 0;
		int a = 0;
		switch (this) {
			case UP: d = 0; break;
			case LEFT: d = 1; break;
			case DOWN: d = 2; break;
			case RIGHT: d = 3; break;
		}
		switch (angle) {
			case UL: a = 0; break;
			case UR: a = 1; break;
			case LR: a = 2; break;
			case LL: a = 3; break;
		}
		return (d + a) % 4;
	}

	public FacingDirection inverseTransform(CameraAngle angle) {
		int d = 0;
		int a = 0;
		switch (this) {
			case UP: d = 0; break;
			case LEFT: d = 1; break;
			case DOWN: d = 2; break;
			case RIGHT: d = 3; break;
		}
		switch (angle) {
			case UL: a = 0; break;
			case UR: a = 1; break;
			case LR: a = 2; break;
			case LL: a = 3; break;
		}

		switch ((d - a + 4) % 4) {
			case 0: return FacingDirection.UP;
			case 1: return FacingDirection.LEFT;
			case 2: return FacingDirection.DOWN;
			case 3: return FacingDirection.RIGHT;
			default: throw new RuntimeException("This cannot happen");
		}
	}

}

