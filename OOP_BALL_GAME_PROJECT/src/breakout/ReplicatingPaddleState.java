package breakout;
import java.awt.Color;

import breakout.utils.*;

public class ReplicatingPaddleState extends PaddleState {
	private int count;
	private static final Color COLOR = new Color(0xff, 0xff, 0x00);

	@Override
	public int numberOfBallsAfterHit() {
		return count;
	}
	
	/**
	 * Return the remaining amount of ball replications this paddle will perform.
	 */
	public int getCount() {
		return count;
	}

	public ReplicatingPaddleState(Point center, int count) {
		super(center);
		this.count = count;
	}

	@Override
	public PaddleState stateAfterHit() {
		if (count > 2) {
			return new ReplicatingPaddleState(getCenter(), count - 1);
		} else {
			return new NormalPaddleState(getCenter());
		}
	}

	@Override
	public Color getColor() {
		return COLOR;
	}

	@Override
	public PaddleState move(Vector v, Rect field) {
		Point ncenter = getCenter().plus(v);
		return new ReplicatingPaddleState(field.minusMargin(PaddleState.WIDTH / 2, 0).constrain(ncenter), count);
	}

}
