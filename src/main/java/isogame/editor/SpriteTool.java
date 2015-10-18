package isogame.editor;

import isogame.engine.CorruptDataException;
import isogame.engine.FacingDirection;
import isogame.engine.MapPoint;
import isogame.engine.Sprite;
import isogame.engine.SpriteInfo;
import isogame.engine.Stage;

public class SpriteTool extends Tool {
	private final SpriteInfo sprite;
	private final FacingDirection direction;

	public SpriteTool(SpriteInfo sprite, FacingDirection direction) {
		this.sprite = sprite;
		this.direction = direction;
	}

	@Override
	public void apply(MapPoint p, Stage stage) {
		if (stage.terrain.hasTile(p)) {
			try {
				Sprite s = new Sprite(sprite);
				s.pos = stage.terrain.getTile(p).pos;
				s.direction = direction;
				stage.addSprite(s);
			} catch (CorruptDataException e) {
				throw new RuntimeException("This cannot happen", e);
			}
		}
	}
}


