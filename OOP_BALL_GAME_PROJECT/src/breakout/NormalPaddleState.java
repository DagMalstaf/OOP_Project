package breakout;
import java.awt.Color;

import breakout.utils.*;

public class NormalPaddleState extends PaddleState {

	private static final Color COLOR = new Color(0x99, 0xff, 0xff);

	public NormalPaddleState(Point center) {
		super(center);
	}

	@Override
	public int numberOfBallsAfterHit() {
		return 1;
	}

	@Override
	public PaddleState move(Vector v, Rect field) {
		Point ncenter = getCenter().plus(v);
		return new NormalPaddleState(field.minusMargin(PaddleState.WIDTH / 2, 0).constrain(ncenter));
	}

	@Override
	public Color getColor() {
		return COLOR;
	}

	@Override
	public PaddleState stateAfterHit() {
		return this;
	}

}
