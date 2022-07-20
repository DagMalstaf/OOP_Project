package breakout;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import breakout.radioactivity.Alpha;
import breakout.radioactivity.Ball;
import breakout.utils.Point;
import breakout.utils.Rect;
import breakout.utils.Vector;

class BreakoutFacadeTest {
	
	@BeforeEach
	void setUp() throws Exception {
		
	}

	@Test
	void test1() {
		
		BreakoutFacade fac = new BreakoutFacade();
		PaddleState Rpaddle = fac.createReplicatingPaddleState(new Point(100,100), 1);
		PaddleState Npaddle = fac.createNormalPaddleState(new Point(100,100));
		assertEquals(false, Rpaddle.equals(Npaddle));
	}
	
	@Test
	void test2() {
		BreakoutFacade fac = new BreakoutFacade();
		BlockState Rblock = fac.createReplicatorBlockState(new Point(100,100), new Point(200,200));
		BlockState Sblock = fac.createSturdyBlockState(new Point(100,100), new Point(200,200),1);
		BlockState Pblock = fac.createPowerupBallBlockState(new Point(100,100), new Point(200,200));
		
		assertEquals(false, Rblock.equals(Sblock));
		assertEquals(false, Rblock.equals(Pblock));
		assertEquals(false, Pblock.equals(Sblock));

	}
	
	@Test 
	void test3() {
		Point point1 = new Point(100,100);
		Point point2 = new Point(200,200);
		Vector speed = new Vector(5,5);
		
		
		BreakoutFacade fac = new BreakoutFacade();
		BlockState block = fac.createNormalBlockState(point1, point2);
		PaddleState paddle = fac.createNormalPaddleState(point2);
		
		Ball ball = fac.createNormalBall(point2, 50, speed);
		Alpha alpha = fac.createAlpha(point2, 20, speed);
		
		assertEquals(false, fac.getColor(paddle).equals(fac.getColor(ball)));
		assertEquals(false, fac.getColor(block).equals(fac.getColor(alpha)));
		assertNotEquals(fac.getDiameter(ball), fac.getDiameter(alpha));
		assertEquals(true, fac.getCenter(ball).equals(fac.getCenter(alpha)));
		
		Point point3 = new Point(400,400);
		Vector speed2 = new Vector(10,10);
		fac.setLocation(ball, point3, 30);
		fac.setLocation(alpha, point3, 30);
		fac.setSpeed(alpha, speed2);
		fac.setSpeed(ball, speed2);
		
		assertNotEquals(ball.getCenter(), point2);
		assertNotEquals(ball.getLocation().getDiameter(), 50);
		assertNotEquals(alpha.getCenter(), point2);
		assertNotEquals(alpha.getLocation().getDiameter(), 20);
		assertNotEquals(alpha.getVelocity(), speed);
		assertNotEquals(ball.getVelocity(),speed);
		
	}
	
	@Test
	void test4() {
		Point point2 = new Point(200,200);
		Vector speed = new Vector(5,5);
		
		BreakoutFacade fac = new BreakoutFacade();
		Ball ball = fac.createNormalBall(point2, 50, speed);
		Alpha alpha = fac.createAlpha(point2, 20, speed);
		ball.linkTo(alpha);
		
		assertEquals(1, fac.getBalls(alpha).size());
		assertEquals(1, fac.getAlphas(ball).size());
		ball.unLink(alpha);
		assertEquals(0, fac.getBalls(alpha).size());
		assertEquals(0, fac.getAlphas(ball).size());
		
		fac.removeLink(ball, alpha);
		fac.getEcharge(ball);
		assertEquals(0, fac.getBalls(alpha).size());
		assertEquals(0, fac.getAlphas(ball).size());
		
		
		
	}
	
	@Test
	void test5() {
		Point TopLeft = new Point(100,100);
		Point BottomRight = new Point(200,200);
		Rect rect = new Rect(TopLeft, BottomRight);
		BlockState block = new NormalBlockState(rect);
		BreakoutFacade fac = new BreakoutFacade();
		
		assertEquals(TopLeft, fac.getRectTL(rect));
		assertEquals(BottomRight, fac.getRectBR(rect));
		assertEquals(TopLeft, fac.getBlockTL(block));
		assertEquals(BottomRight, fac.getBlockBR(block));


	}
	
	@Test
	void test6() {		
		Point point1 = new Point(100,100);
		Point point2 = new Point(200,200);
		Point point3 = new Point(2000,2000);

		Point bottomRight = new Point(10000,10000);
		Vector speed = new Vector(5,5);
		BreakoutFacade fac = new BreakoutFacade();
		Ball ball = fac.createNormalBall(point2, 50, speed);
		Alpha alpha = fac.createAlpha(point2, 20, speed);
		BlockState block = fac.createNormalBlockState(point1, point2);
		Alpha[] alphas = {alpha};
		Ball[] balls = {ball};
		BlockState[] blocks = {block};
		PaddleState paddle = fac.createNormalPaddleState(point3);
		BreakoutState state = new BreakoutState(alphas, balls, blocks, bottomRight, paddle);
		fac.movePaddleRight(state, 10);
		assertNotEquals(paddle.getLocation(),fac.getPaddle(state).getLocation() );
		fac.movePaddleLeft(state, 5);
		assertNotEquals(paddle.getLocation(),fac.getPaddle(state).getLocation() );

		
	
	}
	
}
