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

import java.util.Optional;
import java.util.function.Supplier;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

import org.json.JSONException;
import org.json.JSONObject;

import isogame.GlobalConstants;

public class Sprite extends VisibleObject implements HasJSONRepresentation {
	public final SpriteInfo info;

	// position of the sprite on the map.
	private Optional<MapPoint> pos0 = Optional.of(new MapPoint(0, 0));
	private MapPoint pos = new MapPoint(0, 0);
	public MapPoint getPos() { return pos; }

	// The direction the sprite is facing
	private FacingDirection direction = FacingDirection.UP;
	public FacingDirection getDirection() { return direction; }

	// Extra data that can be used to identify this sprite
	public Object userData;

	// The current animation
	private SpriteAnimation animation;

	// The current scenegraph node
	public final Group sceneGraph = new Group();
	public final Rectangle sceneGraphNode = new Rectangle();
	public final Rectangle slicedGraphNode = new Rectangle();

	// animate the frames
	private FrameAnimator frameAnimator;
	private int frame = 0;

	private Optional<AnimationChain> animationChain = Optional.empty();
	private Runnable onExternalAnimationFinished = () -> {};

	/**
	 * @param info template to make the sprite
	 * */
	public Sprite(final SpriteInfo info) {
		this.info = info;
		sceneGraph.getChildren().add(sceneGraphNode);
		setAnimation(info.defaultAnimation.id);
	}

	public void doOnExternalAnimationFinished(final Runnable k) {
		this.onExternalAnimationFinished = k;
	}

	/**
	 * Queue an animation which can move the sprite around the map etc.
	 * */
	public void queueExternalAnimation(final Animation anim) {
		if (!this.animationChain.isPresent()) {
			final AnimationChain chain = new AnimationChain(this);
			chain.doOnFinished(() -> {
				this.animationChain = Optional.empty();
				this.onExternalAnimationFinished.run();
			});
			this.animationChain = Optional.of(chain);
		}

		this.animationChain.ifPresent(chain -> chain.queueAnimation(anim));
	}

	/**
	 * Get the current animation chain
	 * */
	public Optional<AnimationChain> getAnimationChain() {
		return animationChain;
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

		sceneGraphNode.setTranslateY(GlobalConstants.TILEH - this.animation.h);
		onChange.accept(sceneGraph);
	}

	/**
	 * Set the position of this sprite
	 * @param pos The new position for the sprite
	 * */
	public void setPos(final MapPoint pos) {
		if (!pos0.isPresent()) {
			pos0 = Optional.of(this.pos);
		}
		this.pos = pos;
	}

	/**
	 * Set the direction that this sprite should face.
	 * @param direction The new direction for this sprite to face
	 * */
	public void setDirection(final FacingDirection direction) {
		this.direction = direction;
	}

	private boolean isSliced = false;

	/**
	 * Compute the index at which this node should be inserted into the scene
	 * graph.
	 * */
	public Optional<Integer> findIndex(
		final ObservableList<Node> parent, final boolean isSlice
	) {
		final Node needle = isSlice? slicedGraphNode : sceneGraph;
		final int i = parent.indexOf(needle);
		return i == -1? Optional.empty(): Optional.of(i + 1);
	}

	/**
	 * Update this sprite manually.
	 * @param parent the scenegraph
	 * @param iL the index at which to insert the left slice 
	 * @param iR the index at which to insert the right slice
	 *           (if this sprite is sliced)
	 * */
	void update(
		final ObservableList<Node> parent,
		final Supplier<Integer> piL,
		final Optional<Supplier<Integer>> piR,
		final CameraAngle angle, final long t
	) {
		if (pos0.isPresent() || piR.isPresent() != isSliced) {
			final int iL = piL.get();
			final Optional<Integer> iR = piR.map(v -> v.get());

			// the sprite has been moved
			parent.remove(sceneGraph);
			parent.remove(slicedGraphNode);

			parent.add(iL, sceneGraph);

			iR.ifPresent(i -> parent.add(i, slicedGraphNode));
			pos0 = Optional.empty();

			isSliced = iR.isPresent();

			onChange.accept(sceneGraph);
		}

		final int frame = frameAnimator.frameAt(t);
		animation.updateFrame(sceneGraphNode, frame, angle, direction);
	}

	/**
	 * Update this sprite in the scene graph using the default method.
	 * */
	public void updateSceneGraph(
		final ObservableList<Node> graph,
		final StageInfo terrain,
		final CameraAngle angle,
		final long t
	) {
		if (animationChain.isPresent()) {
			animationChain.get().updateSceneGraph(graph, terrain, angle, t);
		} else {
			final Tile tile = terrain.getTile(pos);
			final Point2D l = terrain.correctedIsoCoord(pos, angle);
			sceneGraph.setTranslateX(l.getX());
			sceneGraph.setTranslateY(l.getY());
			final Supplier<Integer> iL = () ->
				findIndex(graph, false).orElse(tile.getSceneGraphIndex(graph) + 1);
			update(graph, iL, Optional.empty(), angle, t);
		}
	}

	/**
	 * Mark the scene graph information in the sprite as invalid.
	 * This will force the sprite to be reconstructed on the next update.
	 * */
	public void invalidate() {
		pos0 = Optional.of(pos);
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
	public void drawFrame(
		final GraphicsContext cx, final long t, final CameraAngle angle
	) {
		final int frame = frameAnimator.frameAt(t);
		cx.translate(0, GlobalConstants.TILEH - animation.h);
		animation.drawFrame(cx, frame, angle, direction);
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

