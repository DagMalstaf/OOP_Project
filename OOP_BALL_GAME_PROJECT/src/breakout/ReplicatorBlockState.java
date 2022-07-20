package breakout;
import java.awt.Color;

import breakout.utils.*;

public class ReplicatorBlockState extends NormalBlockState {

	private static final Color COLOR = new Color(0xcf, 0x5e, 0x51);

	public ReplicatorBlockState(Rect location) {
		super(location);
	}

	@Override
	public PaddleState paddleStateAfterHit(PaddleState paddleState) {
		return new ReplicatingPaddleState(paddleState.getCenter(), 4);
	}

	@Override
	public Color getColor() {
		return COLOR;
	}

}
