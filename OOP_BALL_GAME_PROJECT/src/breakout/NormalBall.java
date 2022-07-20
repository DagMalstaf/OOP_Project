package breakout;

import java.awt.Color;

import breakout.radioactivity.Ball;
import breakout.utils.*;

//import java.util.Arrays;

public class NormalBall extends Ball {

	private static final Color BALL_COLOR = Color.yellow;
	
	public NormalBall(Circle location, Vector velocity) {
		super(location, velocity);
	}

	/**
	 * Update the BallState after hitting a block at a given location.
	 * 
	 * @pre | rect != null
	 * @pre | collidesWith(rect)
	 * @post | getLocation().equals(old(getLocation()))
	 * @post | getVelocity().equals(old(getVelocity()).mirrorOver(rect.collideWith(old(getLocation()))))
	 * @mutates this
	 */
	@Override
	public void hitBlock(Rect rect, boolean destroyed) {
		this.changeVelocity(bounceOn(rect));
	}

	@Override
	public void move(Vector v, int elapsedTime) {
		this.changeLocation(new Circle(getLocation().getCenter().plus(v), getLocation().getDiameter()));
	}

	@Override
	public void hitPaddle(Rect rect, Vector paddleVel) {
		Vector nspeed = bounceOn(rect);
		this.changeVelocity(nspeed.plus(paddleVel.scaledDiv(5)));
	}

	@Override
	public void hitWall(Rect rect) {
		this.changeVelocity(bounceOn(rect));
	}

	@Override
	public Color getColor() {
		return BALL_COLOR;
	}

	@Override
	public Ball cloneWithVelocity(Vector v) {
		return new NormalBall(getLocation(), v);
	}

}
 