package breakout;
import java.awt.Color;

import breakout.radioactivity.Ball;
import breakout.utils.*;

public class NormalBlockState extends BlockState {

	private static final Color COLOR = new Color(0x80, 0x00, 0xff);

	/**
	 * Construct a block occupying a given rectangle in the field.
	 * | @pre | location != null
	 * | @post | getLocation().equals(location)
	 */
	public NormalBlockState(Rect location) {
		super(location);
	}

	@Override
	public BlockState blockStateAfterHit() {
		return null;
	}

	@Override
	public Ball ballStateAfterHit(Ball ballState) {
		return ballState;
	}

	@Override
	public PaddleState paddleStateAfterHit(PaddleState paddleState) {
		return paddleState;
	}

	@Override
	public Color getColor() {
		return COLOR;
	}

}
