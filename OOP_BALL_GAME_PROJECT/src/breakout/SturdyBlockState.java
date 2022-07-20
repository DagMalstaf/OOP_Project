package breakout;
import java.awt.Color;

import breakout.radioactivity.*;
import breakout.utils.*;

public class SturdyBlockState extends BlockState {

	private static final Color COLOR1 = new Color(0x80, 0x00, 0xff);
	private static final Color COLOR2 = new Color(0x80, 0x00, 0xcf);
	private static final Color COLOR3 = new Color(0x80, 0x00, 0x9f);
	private final int livesLeft;

	public SturdyBlockState(Rect location, int lives) {
		super(location);
		livesLeft = lives;
	}

	public int getLivesLeft() {
		return livesLeft;
	}
	
	@Override
	public BlockState blockStateAfterHit() {
		if (livesLeft == 1) {
			return null;
		} else {
			return new SturdyBlockState(getLocation(), livesLeft - 1);
		}
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
		switch (livesLeft) {
		case 1:
			return COLOR1;
		case 2:
			return COLOR2;
		default:
			return COLOR3;
		}
	}

}
