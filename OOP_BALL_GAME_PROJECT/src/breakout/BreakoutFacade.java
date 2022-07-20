package breakout;


import java.awt.Color;
import java.util.Set;

import breakout.radioactivity.Alpha;
import breakout.radioactivity.Ball;
import breakout.utils.*;

//No documentation required for this class
public class BreakoutFacade {
	
	public PaddleState createNormalPaddleState(Point center) {
		return new NormalPaddleState(center);
	}
	
	public PaddleState createReplicatingPaddleState(Point p, int count) {
		return new ReplicatingPaddleState(p,count);
	}

	/**
	 * newly created balls / alphas have an empty peer set.
	 */
	public Ball createNormalBall(Point center, int diameter, Vector initBallVelocity) {
		return new NormalBall(new Circle(center, diameter), initBallVelocity);
	}

	public Ball createSuperchargedBall(Point center, int diameter, Vector initBallVelocity, int lifetime) {
		return new SuperChargedBall(new Circle(center, diameter), initBallVelocity, lifetime);
	}
	
	public Alpha createAlpha(Point center, int diameter, Vector speed) {
		return new Alpha(new Circle(center, diameter), speed);
	}

	/**
	 * pre: balls have no peer alphas.
	 */
	public BreakoutState createBreakoutState(Ball[] balls, BlockState[] blocks, Point bottomRight,PaddleState paddle) {
		return new BreakoutState(balls, blocks, bottomRight, paddle);
	}
	
	/**
	 * Here balls and alphas are allowed to have peers. (a defensive check of exhaustiveness
	 * must be performed
	 */
	public BreakoutState createBreakoutState(
			Alpha[] alphas,
			Ball[] balls,
			BlockState[] blocks,
			Point bottomRight,
			PaddleState paddle) {
		return new BreakoutState(alphas, balls, blocks, bottomRight, paddle) ;
	}

	public BlockState createNormalBlockState(Point topLeft, Point bottomRight) {
		return new NormalBlockState(new Rect(topLeft, bottomRight));
	}

	public BlockState createSturdyBlockState(Point topLeft, Point bottomRight, int i) {
		return new SturdyBlockState(new Rect(topLeft, bottomRight), i);
	}

	public BlockState createReplicatorBlockState(Point topLeft, Point bottomRight) {
		return new ReplicatorBlockState(new Rect(topLeft,bottomRight));
	}

	public BlockState createPowerupBallBlockState(Point topLeft, Point bottomRight) {
		return new PowerupBallBlockState( new Rect(topLeft, bottomRight));
	}
	
	public Color getColor(PaddleState paddle) {
		return paddle.getColor();
	}

	public Color getColor(Ball ball) {
		return ball.getColor();
	}
	
	public Color getColor(Alpha alpha) {
		return alpha.getColor();
	}

	public Rect getLocation(PaddleState paddle) {
		return paddle.getLocation();
	}

	public Point getCenter(Ball ball) {
		return ball.getCenter();
	}
	
	public Point getCenter(Alpha alpha) {
		return alpha.getCenter();
	}
	

	public int getDiameter(Ball ball) {
		return ball.getLocation().getDiameter(); //TODO
	}
	
	public int getDiameter(Alpha alpha) {
		return alpha.getLocation().getDiameter(); //TODO
	}

	public Ball[] getBalls(BreakoutState breakoutState) {
		return breakoutState.getBalls();
	}
	
	public Alpha[] getAlphas(BreakoutState breakoutState) {
		return breakoutState.getAlphas();
	}

	public Color getColor(BlockState block) {
		return block.getColor();
	}

	public Rect getLocation(BlockState block) {
		return block.getLocation();
	}
	
	/**
	 * Returns the peer balls of alpha.
	 * (Clients of the Alpha - Ball abstraction are allowed to have access to
	 * the peer references.)
	 */
	public Set<Ball> getBalls(Alpha alpha) {
		return alpha.getBalls();
	}
	
	public Set<Alpha> getAlphas(Ball ball) {
		return ball.getAlphas();
	}
	
	/**
	 * note: re-adding a link does nothing
	 */
	public void addLink(Ball ball, Alpha alpha) {
		ball.linkTo(alpha);

	}
	
	/**
	 * note: re-removing a link does nothing.
	 */
	public void removeLink(Ball ball, Alpha alpha) {
		ball.unLink(alpha);
	}
	
	/**
	 * should be in constant time (forwarding private charge)
	 */
	public int getEcharge(Ball ball) {
		return ball.getECharge(); //TODO
	}
	
	/**
	 * mutates the position and diam of ball
	 */
	public void setLocation(Ball ball, Point center, int diam) {
		ball.changeLocation(new Circle(center, diam));
	}
	
	public void setLocation(Alpha alpha, Point center, int diam) {
		alpha.changeLocation(new Circle(center, diam));
	}

	/**
	 * mutates the velocity of ball
	 */
	public void setSpeed(Ball ball, Vector speed) {
		ball.changeVelocity(speed);

	}
	
	public void setSpeed(Alpha alpha, Vector speed) {
		alpha.changeVelocity(speed);
	}
	

	public Vector getVelocity(Ball ball) {
		return ball.getVelocity();
	}
	
	public Vector getVelocity(Alpha alpha) {
		return alpha.getVelocity();
	}
	
	public void hitBlock(Ball ball, Rect rect, boolean destroyed) {
		ball.hitBlock(rect, destroyed);
	}
	
	public BlockState[] getBlocks(BreakoutState state) {
		return state.getBlocks();
	}
	
	public Point getBottomRight(BreakoutState state) {
		return state.getBottomRight();
	}
	
	public PaddleState getPaddle(BreakoutState state) {
		return state.getPaddle();
	}
	
	public void tick(BreakoutState state, int paddleDir, int elapsedTime) {
		state.tick(paddleDir, elapsedTime);
	}
	
	public void tickDuring(BreakoutState state, int elapsedTime) {
		for (int i = 0 ; i + 20 <= elapsedTime ; i += 20) {
			tick(state, 0, 20);
		}
		if( elapsedTime % 20 != 0) { 
		  tick(state, 0, elapsedTime % 20);
		}
	}
	
	public boolean isWon(BreakoutState state) {
		return state.isWon(); 
	}
	
	public boolean isDead(BreakoutState state) {
		return state.isDead(); 
	}
	
	//for GameMap
	//createStateFromDescription
	public BreakoutState createStateFromDescription(String string) {
		return GameMap.createStateFromDescription(string);
	}
	
	
	public int getBallsLen(BreakoutState state) {
		return getBalls(state).length;
	}
	
	public int getBlocksLen(BreakoutState state) {
		return getBlocks(state).length;
	}
	
	/**
	 * @pre | getBalls(state) != null
	 * @pre | getBalls(state).length >= 1
	 */
	public Vector getBall0Vel(BreakoutState state) {
		return getVelocity( getBalls(state)[0] );
	}
	
	// for Rect
	public Point getRectTL(Rect rect) {
		return rect.getTopLeft();
	}
	
	public Point getRectBR(Rect rect) {
		return rect.getBottomRight();
	}
	
	// for blocks
	public Point getBlockTL(BlockState block) {
		return  getRectTL( getLocation(block) );
	}
	
	public Point getBlockBR(BlockState block) {
		return  getRectBR( getLocation(block) );
	}
	
	public void movePaddleRight(BreakoutState state, int elapsedTime) {
		state.movePaddleRight(elapsedTime);
	}
	
	public void movePaddleLeft(BreakoutState state, int elapsedTime) {
		state.movePaddleLeft(elapsedTime);
	}
	
	public boolean collidesWith(Ball ball, Rect rect) {
		return ball.collidesWith(rect); 
	}
	
	public boolean collidesWith(Alpha alpha, Rect rect) {
		return alpha.collidesWith(rect);
	}
	
	
	
	
}
