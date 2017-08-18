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
import javafx.scene.Node;

import static isogame.GlobalConstants.TILEW;
import static isogame.GlobalConstants.TILEH;

public class MoveSpriteAnimation extends Animation {
	private final double walkSpeed;
	private final ContinuousAnimator animator;
	private final FacingDirection direction;
	private final MapPoint directionVector;
	private final String spriteAnimation;

	private final MapPoint start;
	private final MapPoint target;
	private MapPoint point;

	private double jump = 0d;
	private boolean doJump = false;
	private boolean doneJump = false;
	private double elevationDelta = 0d;
	private boolean fromFlatElevation = false;

	private final double targetv;
	private double v = 0d;

	private final static Point2D upV = new Point2D(TILEW / 2d, -(TILEH / 2d));
	private final static Point2D downV = new Point2D(-(TILEW / 2d), TILEH / 2d);
	private final static Point2D leftV = new Point2D(-(TILEW / 2d), -(TILEH / 2d));
	private final static Point2D rightV = new Point2D(TILEW / 2d, TILEH / 2d);
	
	private final static MapPoint upPV = new MapPoint(0, -1);
	private final static MapPoint downPV = new MapPoint(0, 1);
	private final static MapPoint leftPV = new MapPoint(-1, 0);
	private final static MapPoint rightPV = new MapPoint(1, 0);

	private void updateElevationDelta(final StageInfo terrain) {
		final Tile tfrom = terrain.getTile(point);
		final Tile tto = terrain.getTile(point.add(directionVector));

		if (tfrom.slope == tto.slope && tfrom.elevation == tto.elevation) {
			elevationDelta = 0;
		} else if (tfrom.slope == SlopeType.NONE) {
			fromFlatElevation = true;
			if (tto.slope == SlopeType.NONE) {
				elevationDelta = 0;
				doJump = true;
				jump = tfrom.elevation - tto.elevation;
			} else {
				elevationDelta = tto.slope == direction.upThisWay()? -0.5d : 0.5d;
			}
		} else {
			fromFlatElevation = false;
			if (tto.slope == SlopeType.NONE) {
				elevationDelta = tfrom.slope == direction.upThisWay()? -0.5d : 0.5d;
				if (
					tfrom.slope == direction.upThisWay() &&
					tto.elevation != tfrom.elevation + 1d
				) {
					doJump = true;
					jump = tfrom.elevation - tto.elevation + 0.5d;
				}
			} else {
				elevationDelta = tfrom.slope == direction.upThisWay()? -1d : 1d;
			}
		}
	}

	public MoveSpriteAnimation(
		final MapPoint start,
		final MapPoint target,
		final Sprite sprite,
		final String spriteAnimation,
		final double walkSpeed,
		final StageInfo terrain
	) {
		super(sprite);

		if (start.equals(target)) throw new RuntimeException(
			"Start and end points of movement must be different");

		this.walkSpeed = walkSpeed;
		this.spriteAnimation = spriteAnimation;
		this.animator = new ContinuousAnimator();
		animator.setAnimation(new Point2D(1d, 0d), walkSpeed);
		this.start = start;
		this.target = target;
		this.point = start;

		if (start.x == target.x) {
			this.targetv = Math.abs(target.y - start.y);
			if (target.y > start.y) {
				direction = FacingDirection.DOWN;
				directionVector = downPV;
			} else {
				direction = FacingDirection.UP;
				directionVector = upPV;
			}
		} else if (start.y == target.y){
			this.targetv = Math.abs(target.x - start.x);
			if (target.x > start.x) {
				direction = FacingDirection.RIGHT;
				directionVector = rightPV;
			} else {
				direction = FacingDirection.LEFT;
				directionVector = leftPV;
			}
		} else {
			throw new RuntimeException("Must travel in straight lines");
		}

		updateElevationDelta(terrain);
	}

	@Override public void start() {
		if (!start.equals(sprite.getPos()))
			throw new RuntimeException("Attempted to start invalid move animation");
		sprite.setAnimation(spriteAnimation);
		sprite.setDirection(direction);
		animator.start();
	}

	/**
	 * @return true if the animation is now complete.
	 * */
	@Override public boolean updateAnimation(
		final StageInfo terrain, final long t
	) {
		double v1 = animator.valueAt(t).getX();
		if (v1 >= targetv) {
			v = targetv;
			doneJump = false;
			sprite.setPos(target);
			animator.stop();
			return true;

		} else if (Math.floor(v) != Math.floor(v1)){
			v = v1;
			point = start.addScale(directionVector, (int) Math.floor(v));
			doneJump = false;
			updateElevationDelta(terrain);
			sprite.setPos(point);
		} else {
			v = v1;
		}

		return false;
	}

	@Override public void updateSceneGraph(
		final ObservableList<Node> graph,
		final StageInfo terrain,
		final CameraAngle angle,
		final long t
	) {
		final double scale = v - Math.floor(v);

		// get the motion direction
		Point2D offsetVector;
		boolean movingAway;
		switch (direction.transform(angle)) {
			case 0: offsetVector = upV; movingAway = true; break;
			case 1: offsetVector = leftV; movingAway = false; break;
			case 2: offsetVector = downV; movingAway = false; break;
			case 3: offsetVector = rightV; movingAway = true; break;
			default: throw new RuntimeException("This cannot happen");
		}

		final Point2D offset = offsetVector.multiply(scale);

		// get the elevation correction
		double elevationOffset = 0d;
		if (Math.abs(elevationDelta) == 1.0d) {
			elevationOffset = elevationDelta * scale;
		} else if (fromFlatElevation) {
			elevationOffset = scale < 0.5d?
				0d : elevationDelta * 2.0d * (scale - 0.5d);
		} else {
			elevationOffset = scale >= 0.5d?
				elevationDelta : elevationDelta * 2.0d * scale;
		}

		// handle jumps
		if (doJump && scale >= 0.5d) {
			elevationOffset = jump;
			if (!doneJump) sprite.invalidate();
			doneJump = true;
		}

		// get the left and right tiles and their coordinates
		final MapPoint pos0 = sprite.getPos();
		final MapPoint pos1 = pos0.add(directionVector);

		final Point2D pl = terrain.correctedSpriteIsoCoord(pos0, angle);

		final Tile tile0 = terrain.getTile(pos0);
		final Tile tile1 = terrain.getTile(pos1);

		// update the translations
		sprite.sceneGraph.setTranslateX(pl.getX() + offset.getX());
		sprite.sceneGraph.setTranslateY(pl.getY() +
			offset.getY() + (elevationOffset * (TILEH / 2d)));

		if (movingAway) {
			sprite.slicedGraphNode.setX((TILEW / 2d) - offset.getX());
			sprite.slicedGraphNode.setWidth((TILEW / 2d) + offset.getX());
			sprite.slicedGraph.setTranslateX(pl.getX() + (TILEW / 2d) -
				sprite.slicedGraphNode.getX());
		} else {
			sprite.slicedGraphNode.setX(0d);
			sprite.slicedGraphNode.setWidth((TILEW / 2d) - offset.getX());
			sprite.slicedGraph.setTranslateX(sprite.sceneGraph.getTranslateX());
		}

		sprite.slicedGraph.setTranslateY(sprite.sceneGraph.getTranslateY());

		// update the sprite
		final Supplier<Integer> iMain = () -> {
			if (doJump && scale >= 0.5d) {
				return tile1.getSceneGraphIndex(graph, sprite.info.priority) + 1;
			} else {
				return tile0.getSceneGraphIndex(graph, sprite.info.priority) + 1;
			}
		};
		final Supplier<Integer> iSlice = () ->
			tile1.getSceneGraphIndex(graph, sprite.info.priority) + 1;

		sprite.update(graph, iMain, Optional.of(iSlice), angle, t);
	}
}

