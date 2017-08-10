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

import isogame.GlobalConstants;
import javafx.scene.canvas.GraphicsContext;
import java.util.Optional;
import org.json.JSONException;
import org.json.JSONObject;

public class Sprite implements HasJSONRepresentation {
	public final SpriteInfo info;

	// position of the sprite on the map.
	public MapPoint pos = new MapPoint(0, 0);

	// The direction the sprite is facing
	public FacingDirection direction = FacingDirection.UP;

	// Extra data that can be used to identify this sprite
	public Object userData;

	private Optional<SpriteDecalRenderer> renderDecal = Optional.empty();

	private SpriteAnimation animation;

	// animate the frames
	private FrameAnimator frameAnimator;
	private int frame = 0;

	private AnimationChain animationChain = null;

	public Sprite(SpriteInfo info) {
		this.info = info;
		setAnimation(info.defaultAnimation.id);
	}

	public void setAnimationChain(AnimationChain chain) {
		this.animationChain = chain;
	}

	public AnimationChain getAnimationChain() {
		return animationChain;
	}

	/**
	 * Register a function that can draw extra decals over the sprite.
	 * @param r The decal renderer function, null to deregister the current decal
	 * renderer.
	 * */
	public void setDecalRenderer(final SpriteDecalRenderer r) {
		renderDecal = Optional.ofNullable(r);
	}

	/**
	 * Check if a point is on this sprite.
	 * @param x coordinate transformed relative to the origin of this sprite
	 * @param y coordinate transformed relative to the origin of this sprite
	 * @return true if the point collides, otherwise false
	 * */
	public boolean hitTest(
		final double x, final double y, final CameraAngle angle
	) {
		return animation.hitTest((int) x, (int) y, frame, angle, direction);
	}

	/**
	 * Render a single frame of this sprite.
	 * WARNING: does not preserve the current translation.
	 * */
	public void renderFrame(
		final GraphicsContext cx,
		final int xoff,
		final int w,
		final long t,
		final CameraAngle angle
	) {
		final int frame = frameAnimator.frameAt(t);

		cx.translate(0, GlobalConstants.TILEH - animation.h);
		animation.renderFrame(cx, xoff, w, frame, angle, direction);
		renderDecal.ifPresent(r -> r.render(cx, this, t, angle));
	}

	/**
	 * Rotate the sprite anticlockwise.
	 * */
	public void rotate() {
		direction = direction.rotateAntiClockwise();
	}

	/**
	 * Set the animation for rendering this sprite.
	 * */
	public void setAnimation(final String animation) {
		this.animation = info.animations.get(animation);
		this.frame = 0;
		this.frameAnimator = new FrameAnimator(
			this.animation.frames, this.animation.framerate);
	}

	public static Sprite fromJSON(final JSONObject json, final Library lib)
		throws CorruptDataException
	{
		try {
			final JSONObject pos = json.getJSONObject("pos");
			final String direction = json.getString("direction");
			final String spriteID = json.getString("sprite");
			final String animation = json.getString("animation");

			final Sprite sprite = new Sprite(lib.getSprite(spriteID));
			sprite.direction = FacingDirection.valueOf(direction);
			sprite.pos = MapPoint.fromJSON(pos);
			sprite.setAnimation(animation);
			return sprite;
		} catch (JSONException e) {
			throw new CorruptDataException("Error parsing sprite, " + e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new CorruptDataException("Type error in sprite", e);
		}
	}

	@Override
	public JSONObject getJSON() {
		final JSONObject r = new JSONObject();
		r.put("pos", pos.getJSON());
		r.put("direction", direction.name());
		r.put("animation", animation.id);
		r.put("sprite", info.id);
		return r;
	}

	@Override public String toString() {
		return "Sprite " + info.toString() + " at " + pos.toString();
	}
}

