package breakout;
import java.awt.Color;

import breakout.radioactivity.*;
import breakout.utils.*;

/**
 * Represents the state of a block in the breakout game.
 *
 * @immutable
 * @invar getLocation() != null
 */
public abstract class BlockState {

	/**
	 * @invar | location != null
	 */
	private final Rect location;

	/**
	 * Construct a block occupying a given rectangle in the field.
	 * | @pre | location != null
	 * | @post | getLocation().equals(location)
	 */
	public BlockState(Rect location) {
		this.location = location;
	}

	/**
	 * Return the rectangle occupied by this block in the field.
	 */
	public Rect getLocation() {
		return location;
	}

	/**
	 * Return the block state after getting hit by the ball, null if block is destroyed.
	 * 
	 * | @post (result == null || result.getLocation().equals(getLocation()))
	 */
	public abstract BlockState blockStateAfterHit();

	/**
	 * Return the block state after getting hit by the ball, based on the old ball
	 * state. Return null if not hit.
	 * 
	 * @pre | ballState != null
	 */
	public abstract Ball ballStateAfterHit(Ball ballState);

	/**
	 * Return the paddle state after getting hit by the ball, based on the old
	 * paddle state.
	 * 
	 * @pre | paddleState != null
	 */
	public abstract PaddleState paddleStateAfterHit(PaddleState paddleState);

	/**
	 * Return this block state's color.
	 * 
	 * @post result != null
	 */
	public abstract Color getColor();
}
