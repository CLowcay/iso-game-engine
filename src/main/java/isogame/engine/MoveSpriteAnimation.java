package isogame.engine;

import isogame.GlobalConstants;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import java.util.function.BiConsumer;

public class MoveSpriteAnimation extends Animation {
	private final double walkSpeed;
	private final BiConsumer<MapPoint, MapPoint> crossBoundary;
	private final ContinuousAnimator animator;
	private final FacingDirection direction;
	private final MapPoint directionVector;
	private final String spriteAnimation;

	private final MapPoint start;
	private final MapPoint target;
	private MapPoint point;

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

	public MoveSpriteAnimation(
		MapPoint start, MapPoint target,
		String spriteAnimation, double walkSpeed,
		BiConsumer<MapPoint, MapPoint> crossBoundary
	) {
		if (start.equals(target)) throw new RuntimeException(
			"Start and end points of movement must be different");

		this.walkSpeed = walkSpeed;
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
	}

	public void start(Sprite s) {
		s.setAnimation(spriteAnimation);
		s.direction = direction;
		animator.start();
		crossBoundary.accept(s.pos, s.pos.add(directionVector));
	}

	/**
	 * @return true if the animation is now complete.
	 * */
	public boolean updateAnimation(long t) {
		double v1 = animator.valueAt(t).getX();
		if (v1 >= targetv) {
			v = targetv;
			crossBoundary.accept(target, target);
			animator.stop();
			return true;
		} else if (Math.floor(v) != Math.floor(v1)){
			v = v1;
			point = start.addScale(directionVector, (int) Math.floor(v));
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
		if (isTargetSlice) offset = offset.subtract(directionVector);

		gx.save();
		gx.translate(offset.getX(), offset.getY());
		if (isLeftSlice) {
			s.renderFrame(gx, 0, (int) (GlobalConstants.TILEW - offset.getX()), t, angle);
		} else {
			s.renderFrame(gx, (int) (-offset.getX()),
				(int) (GlobalConstants.TILEW + offset.getX()), t, angle);
		}
		gx.restore();
	}
}

