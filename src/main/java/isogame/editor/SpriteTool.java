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
package isogame.editor;

import isogame.engine.FacingDirection;
import isogame.engine.MapPoint;
import isogame.engine.Sprite;
import isogame.engine.SpriteInfo;
import isogame.engine.Stage;
import isogame.engine.View;

public class SpriteTool extends Tool {
	private final SpriteInfo sprite;
	private final FacingDirection direction;

	public SpriteTool(final SpriteInfo sprite, final FacingDirection direction) {
		this.sprite = sprite;
		this.direction = direction;
	}

	@Override
	public void apply(final MapPoint p, final Stage stage, final View view) {
		if (stage.terrain.hasTile(p)) {
			final Sprite s = new Sprite(sprite);
			s.setPos(stage.terrain.getTile(p).pos);
			s.setDirection(direction.inverseTransform(view.getCameraAngle()));
			stage.replaceSprite(s);
		}
	}
}

