package breakout;
import java.awt.Color;

import breakout.radioactivity.*;
import breakout.utils.*;

public class PowerupBallBlockState extends NormalBlockState {

	private static final int SUPERCHARGED_BALL_LIFETIME = 10000;
	private static final Color COLOR = new Color(0xff, 0x5e, 0x81);

	public PowerupBallBlockState(Rect location) {
		super(location);
	}

	@Override
	public Ball ballStateAfterHit(Ball ballState) {
		return new SuperChargedBall(ballState.getLocation(), ballState.getVelocity(), SUPERCHARGED_BALL_LIFETIME);
	}

	@Override
	public Color getColor() {
		return COLOR;
	}

}
