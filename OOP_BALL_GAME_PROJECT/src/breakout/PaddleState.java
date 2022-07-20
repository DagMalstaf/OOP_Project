package breakout;
import java.awt.Color;

import breakout.utils.*;

/**
 * Represents the state of a paddle in the breakout game.
 *
 * @immutable
 * @invar | getCenter() != null
 */
public abstract class PaddleState {

	public static final int HEIGHT = 500;
	public static final int WIDTH = 3000;
	/**
	 * @invar | center != null
	 */
	private final Point center;

	/**
	 * Construct a paddle located around a given center in the field.
	 * 
	 * @pre | center != null
	 * @post | getCenter().equals(center)
	 */
	public PaddleState(Point center) {
		this.center = center;
	}

	/**
	 * Return the center point of this paddle.
	 */
	public Point getCenter() {
		return center;
	}

	/**
	 * Return the rectangle occupied by this paddle in the field.
	 * 
	 * @post | result != null
	 * @post | result.getTopLeft().equals(getCenter().plus(new Vector(-WIDTH/2,-HEIGHT/2)))
	 * @post | result.getBottomRight().equals(getCenter().plus(new Vector(WIDTH/2,HEIGHT/2)))
	 */
	public Rect getLocation() {
		Vector halfDiag = new Vector(-WIDTH / 2, -HEIGHT / 2);
		return new Rect(center.plus(halfDiag), center.plus(halfDiag.scaled(-1)));
	}

	/**
	 * Return the amount of balls should be generated after hitting this paddle.
	 * 
	 * @post | result > 0
	 * @post | result < BreakoutState.MAX_BALL_REPLICATE
	 */
	public abstract int numberOfBallsAfterHit();

	/**
	 * Return the new state of the paddle after it is hit by a ball.
	 * 
	 * @post | result != null
	 * @post | result.getLocation().equals(getLocation())
	 */
	public abstract PaddleState stateAfterHit();

	/**
	 * Return the color which the paddle should be painted in.
	 * 
	 * @post | result != null
	 */
	public abstract Color getColor();

	/**
	 * Return a copy of this PaddleState moved in a given direction, within a given
	 * field.
	 * 
	 * @pre | v != null
	 * @pre | field != null
	 * @post | result != null
	 * @post | field.contains(result.getLocation())
	 */
	public abstract PaddleState move(Vector v, Rect field);
	
	

}
