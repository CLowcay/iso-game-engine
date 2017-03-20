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
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import java.util.function.BiConsumer;

public class MoveSpriteAnimation extends Animation {
	private final double walkSpeed;
	private final StageInfo terrain;
	private final BiConsumer<MapPoint, MapPoint> crossBoundary;
	private final ContinuousAnimator animator;
	private final FacingDirection direction;
	private final MapPoint directionVector;
	private final String spriteAnimation;

	private final MapPoint start;
	private final MapPoint target;
	private MapPoint point;

	private double jump = 0;
	private double elevationDelta = 0;
	private boolean fromFlatElevation = false;

	private final double targetv;
	private double v = 0;

	private final static Point2D upV =
		new Point2D(GlobalConstants.TILEW / 2, -(GlobalConstants.TILEH / 2));
	private final static Point2D downV =
		new Point2D(-(GlobalConstants.TILEW / 2), GlobalConstants.TILEH / 2);
	private final static Point2D leftV =
		new Point2D(-(GlobalConstants.TILEW / 2), -(GlobalConstants.TILEH / 2));
	private final static Point2D rightV =
		new Point2D(GlobalConstants.TILEW / 2, GlobalConstants.TILEH / 2);
	
	private final static MapPoint upPV = new MapPoint(0, -1);
	private final static MapPoint downPV = new MapPoint(0, 1);
	private final static MapPoint leftPV = new MapPoint(-1, 0);
	private final static MapPoint rightPV = new MapPoint(1, 0);

	private void updateElevationDelta() {
		Tile tfrom = terrain.getTile(point);
		Tile tto = terrain.getTile(point.add(directionVector));
		if (tfrom.slope == tto.slope && tfrom.elevation == tto.elevation) {
			elevationDelta = 0;
		} else if (tfrom.slope == SlopeType.NONE) {
			fromFlatElevation = true;
			if (tto.slope == SlopeType.NONE) {
				elevationDelta = 0;
				jump = tfrom.elevation - tto.elevation;
			} else {
				elevationDelta = tto.slope == direction.upThisWay()? -0.5 : 0.5;
			}
		} else {
			fromFlatElevation = false;
			if (tto.slope == SlopeType.NONE) {
				elevationDelta = tfrom.slope == direction.upThisWay()? -0.5 : 0.5;
			} else {
				elevationDelta = tfrom.slope == direction.upThisWay()? -1 : 1;
			}
		}
	}

	public MoveSpriteAnimation(
		MapPoint start, MapPoint target,
		String spriteAnimation, double walkSpeed,
		StageInfo terrain,
		BiConsumer<MapPoint, MapPoint> crossBoundary
	) {
		if (start.equals(target)) throw new RuntimeException(
			"Start and end points of movement must be different");

		this.walkSpeed = walkSpeed;
		this.terrain = terrain;
		this.spriteAnimation = spriteAnimation;
		this.crossBoundary = crossBoundary;
		this.animator = new ContinuousAnimator();
		animator.setAnimation(new Point2D(1, 0), walkSpeed);
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

		updateElevationDelta();
	}

	@Override public void start(Sprite s) {
		s.setAnimation(spriteAnimation);
		s.direction = direction;
		animator.start();
		crossBoundary.accept(s.pos, s.pos.add(directionVector));
	}

	/**
	 * @return true if the animation is now complete.
	 * */
	@Override public boolean updateAnimation(long t) {
		double v1 = animator.valueAt(t).getX();
		if (v1 >= targetv) {
			v = targetv;
			crossBoundary.accept(target, target);
			animator.stop();
			return true;
		} else if (Math.floor(v) != Math.floor(v1)){
			v = v1;
			point = start.addScale(directionVector, (int) Math.floor(v));
			updateElevationDelta();
			crossBoundary.accept(point, point.add(directionVector));
		} else {
			v = v1;
		}

		return false;
	}

	/**
	 * Render a sprite taking into account this movement animation.
	 * @param isTargetSlice true if we are rendering onto the tile the sprite is
	 * moving into.  False if we are rendering onto the tile where the sprite is
	 * moving from.
	 * */
	public void renderSprite(
		GraphicsContext gx,
		CameraAngle angle,
		Sprite s,
		long t,
		boolean isTargetSlice
	) {
		double scale = v - Math.floor(v);
		Point2D directionVector;
		boolean isLeftSlice;
		switch (direction.transform(angle)) {
			case 0: directionVector = upV; isLeftSlice = !isTargetSlice; break;
			case 1: directionVector = leftV; isLeftSlice = isTargetSlice; break;
			case 2: directionVector = downV; isLeftSlice = isTargetSlice; break;
			case 3: directionVector = rightV; isLeftSlice = !isTargetSlice; break;
			default: throw new RuntimeException("This cannot happen");
		}

		Point2D offset = directionVector.multiply(scale);
		double elevationOffset;
		if (Math.abs(elevationDelta) == 1.0d) {
			elevationOffset = elevationDelta * scale;
		} else if (jump != 0d) {
			elevationOffset = scale < 0.5d? 0d : jump;
		} else if (fromFlatElevation)
			elevationOffset = scale < 0.5d? 0d : elevationDelta * 2.0d * (scale - 0.5d);
		else {
			elevationOffset = scale >= 0.5d? elevationDelta : elevationDelta * 2.0d * scale;
		}

		if (isTargetSlice) {
			offset = offset.subtract(directionVector);
			elevationOffset = elevationOffset - elevationDelta - jump;
		}

		gx.save();
		gx.translate(offset.getX(), offset.getY() +
			(elevationOffset * (GlobalConstants.TILEH / 2)));
		if (isLeftSlice) {
			s.renderFrame(gx, 0, (int) (GlobalConstants.TILEW - offset.getX()), t, angle);
		} else {
			s.renderFrame(gx, (int) (-offset.getX()),
				(int) (GlobalConstants.TILEW + offset.getX()), t, angle);
		}
		gx.restore();
	}
}

