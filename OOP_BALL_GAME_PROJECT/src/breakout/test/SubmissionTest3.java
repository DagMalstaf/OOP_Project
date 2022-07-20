package breakout.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import breakout.BlockState;
import breakout.BreakoutFacade;
import breakout.BreakoutState;
import breakout.PaddleState;
import breakout.radioactivity.Alpha;
import breakout.radioactivity.Ball;
import breakout.utils.Point;
import breakout.utils.Rect;
import breakout.utils.Vector;

class SubmissionTest3 {

	static final Point BR = new Point(50000, 30000);
	static final Point origin = new Point(0, 0);
	static final BreakoutFacade fac = new BreakoutFacade();
	static final Point defPoint = new Point(5000, 5000);
	static final Vector downSpeed = new Vector(0, 5);
	
//	@BeforeEach
//	void setUp() throws Exception {
//	}
	
	@Test
	void noEquals() {
		Alpha alph1 = fac.createAlpha(defPoint, 700, downSpeed);
		Alpha alph2 = fac.createAlpha(defPoint, 700, downSpeed);
		Ball ball1 = fac.createNormalBall(defPoint, 700, downSpeed);
		Ball ball2 = fac.createNormalBall(defPoint, 700, downSpeed);
		
		//because equals should do the default reference check and nothing more.
		assertNotEquals(alph1, alph2);
		assertNotEquals(ball1, ball2);
	}

	@Test
	void paddleSize() {
		Rect paddleRect = fac.getLocation( fac.createNormalPaddleState(defPoint) );
		assertTrue( paddleRect.getHeight() == 500);
		assertTrue( paddleRect.getWidth() == 3000);
	}
	
	@Test
	void simpleTick() {
		Alpha alpha = fac.createAlpha(defPoint.plus(downSpeed), 700, downSpeed);
		Ball ball = fac.createSuperchargedBall(defPoint, 700, downSpeed, 10000);
		fac.addLink(ball, alpha);
		BlockState tlBlock = fac.createNormalBlockState(origin, new Point(5000, 3750));
		PaddleState paddle = fac.createNormalPaddleState(defPoint.plus(new Vector(10000,10000)));
		
		
		BreakoutState state = fac.createBreakoutState(
				new Alpha[] {alpha}, new Ball[] {ball}, new BlockState[] {tlBlock}, BR, paddle);
		fac.tickDuring(state, 50000); //the ball and the alpha disappear below the bot line.
		assertTrue( fac.isDead(state) );
	}
	
	@Test
	void simplePaddleBounce() {
		//To the left, a ball. To the right an alpha. Both are going down.
		Alpha alpha = fac.createAlpha(defPoint.plus(new Vector(1000,0)), 700, downSpeed);
		Ball ball = fac.createSuperchargedBall(defPoint, 700, downSpeed, 10000);
		Vector oldBallSpeed = fac.getVelocity(ball);
		Vector oldAlphaSpeed = fac.getVelocity(alpha);
		fac.addLink(ball, alpha); //(besides they are linked)
		BlockState tlBlock = fac.createNormalBlockState(origin, new Point(5000, 3750));
		// the paddle is below our ball/alpha and does not collide them.
		PaddleState paddle = fac.createNormalPaddleState(defPoint.plus(new Vector(0,351 + 250)));//fails with 249
		BreakoutState state = fac.createBreakoutState(
				new Alpha[] {alpha}, new Ball[] {ball}, new BlockState[] {tlBlock}, BR, paddle);
		
		assertFalse( fac.collidesWith(
				fac.getAlphas(state)[0],
				fac.getLocation(paddle))) ;
		assertFalse( fac.collidesWith(
				fac.getBalls(state)[0],
				fac.getLocation(paddle))) ;
		
		fac.tickDuring(state, 20); //the ball/alpha bounce and move away from the paddle
		
		assertFalse( fac.collidesWith(
				fac.getAlphas(state)[0],
				fac.getLocation(paddle))) ;
		assertFalse( fac.collidesWith(
				fac.getBalls(state)[0],
				fac.getLocation(paddle))) ;
		
		assertNotEquals( oldBallSpeed, fac.getBall0Vel(state) ); //speeds have been mirrored.
		assertNotEquals( oldAlphaSpeed, fac.getVelocity(fac.getAlphas(state)[0]) );
		
		assertFalse( fac.isDead(state) );
	}
	
	@Test
	void simpleAlphaBounceWall() {
		Alpha alpha = fac.createAlpha(defPoint, 700, new Vector(5, 0));
		BlockState tlBlock = fac.createNormalBlockState(origin, new Point(5000, 3750));
		Point BRbis = new Point(5351, 10000);
		PaddleState paddle = fac.createNormalPaddleState(new Point(2500, 8000));
		BreakoutState state = fac.createBreakoutState(new Alpha[] {alpha}, new Ball[] {}, new BlockState[] {tlBlock}, BRbis, paddle);
		Rect rightWall = new Rect(new Point(BRbis.getX(), 0),
				new Point(BRbis.getX() + 1000, BRbis.getY()));
		assertFalse( fac.collidesWith(alpha, rightWall) );
		
		fac.tickDuring(state,20);
		assertEquals( fac.getVelocity(fac.getAlphas(state)[0]) , new Vector(-5,0) ); //alpha has bounced on right wall.
		}
	
	/**
	 * Some sanity checks coming from submission test suite of iteration 2, and adapted.
	 */
	@Nested
	class SubmissionTestSuiteIter2 {

		Ball[] oneBall;
		BlockState[] oneBlock;
		Point bottomRight;
		PaddleState paddle;
		BreakoutState state1;
		BreakoutState stateWon;
		BreakoutState stateDead;
		BreakoutState stateBeforeBounceBlock;
		Vector origBallVelocity;
		BlockState bounceBlock;
		Ball ball;

	    public static final String initMap1 = """
#		       
		       
		       
		       
		       
		     o

		     =

""";
	    public static final String initMapWon = """
		       
		       
		       
		       
		       
		     o

		     =

""";
	    public static final String initMapDead = """
#		       
		       
		       
		       
		       
		     

		     =

""";
	
	    public static final String initMapBeforeBounce = """
		       
		       
	o	       
	###	       
		       
		     

		     =

""";


		@BeforeEach
		void setUp() throws Exception {
			state1 = fac.createStateFromDescription(initMap1);
			oneBall = fac.getBalls(state1);
			ball = oneBall[0];
			origBallVelocity = fac.getVelocity(ball);
			oneBlock = fac.getBlocks(state1);
			bottomRight = fac.getBottomRight(state1);
			paddle = fac.getPaddle(state1);
			stateWon = fac.createStateFromDescription(initMapWon);
			stateDead = fac.createStateFromDescription(initMapDead);
			stateBeforeBounceBlock = fac.createStateFromDescription(initMapBeforeBounce);
		}

		@Test
		void testBreakoutStateNull() {
			assertThrows(IllegalArgumentException.class, 
					() -> fac.createBreakoutState(null, oneBlock, bottomRight, paddle) );
			assertThrows(IllegalArgumentException.class, 
					() -> fac.createBreakoutState(oneBall, null, bottomRight, paddle) );
			assertThrows(IllegalArgumentException.class, 
					() -> fac.createBreakoutState(oneBall, oneBlock, null, paddle) );
			assertThrows(IllegalArgumentException.class, 
					() -> fac.createBreakoutState(oneBall, oneBlock, bottomRight, null) );
		}
		
		@Test
		void testBreakoutStateNormal() {
			BreakoutState state = fac.createBreakoutState(oneBall,oneBlock,bottomRight,paddle);
			assertTrue(Arrays.equals(oneBlock, fac.getBlocks(state)));
			assertEquals(bottomRight,fac.getBottomRight(state));
			assertEquals(paddle, fac.getPaddle(state));
		}

		@Test
		void testTickNormal() {
			fac.tickDuring(state1, 20);
			assertEquals(1,fac.getBallsLen(state1));
			assertEquals(origBallVelocity, fac.getBall0Vel(state1));
		}

		@Test
		void testIsWon() {
			assertFalse(fac.isWon(state1));
			assertTrue(fac.isWon(stateWon));
			assertFalse(fac.isWon(stateDead));
		}

		@Test
		void testIsDead() {
			assertFalse(fac.isDead(state1));
			assertFalse(fac.isDead(stateWon));
			assertTrue(fac.isDead(stateDead));
		}

	}
	
	/**
	 * Other tests coming from iteration2 and adapted
	 */
	@Nested class SubmissionTestSuiteIter2Bis {

		private Ball[] oneBall;
		private BlockState block;
		private BlockState[] oneBlock;
		private Point bottomRight;
		private PaddleState paddle;
		private BreakoutState stateBeforeBounceBlock;
		private Ball ball;

		@BeforeEach
		void setUp() throws Exception {
			ball = fac.createNormalBall(new Point(1000, 1000), 700, new Vector(0, 5));
			oneBall = new Ball[] { ball };
			block = fac.createNormalBlockState(new Point(0, 1351), new Point(2000, 1550));
			oneBlock = new BlockState[] { block };
			bottomRight = new Point(10000, 3000);
			paddle = fac.createNormalPaddleState(new Point(2000, 2100));
			  //top left point of paddle is (500, 1850)
			stateBeforeBounceBlock = fac.createBreakoutState(oneBall, oneBlock, bottomRight, paddle);
		}

		@Test
		void testTickBounceBlock() {
			assertEquals(1, fac.getBallsLen(stateBeforeBounceBlock));
			assertEquals(1, fac.getBlocksLen(stateBeforeBounceBlock));
			//the ball does not collide with the block (yet).
			assertFalse( fac.collidesWith(fac.getBalls(stateBeforeBounceBlock)[0],
								fac.getLocation(fac.getBlocks(stateBeforeBounceBlock)[0])));
			fac.tickDuring(stateBeforeBounceBlock, 20);
			assertEquals(1, fac.getBallsLen(stateBeforeBounceBlock));
			assertEquals(0, fac.getBlocksLen(stateBeforeBounceBlock)); //block has been destroyed.
			assertEquals( new Vector(0, -5) , fac.getBall0Vel(stateBeforeBounceBlock)); //speed has been mirrored.
		}

	}

}
