package breakout;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import breakout.radioactivity.Alpha;
import breakout.radioactivity.Ball;
import breakout.utils.Circle;
import breakout.utils.Point;
import breakout.utils.Rect;
import breakout.utils.Vector;

class BreakoutStateTest {
	Alpha alpha1;
	Alpha alpha2;
	
	Ball ball1;
	Ball ball2;
	
	Point bottomRight;
	Point paddleCenter;
	Point point1;
	Point point2;
	Point point3;
	Point point4;
	Point point5;
	Point point6;
	Point point7;
	Point point8;
	
	Circle circle1;
	Circle circle2;
	Circle circle3;
	Circle circle4;
	
	Vector speed1;
	Vector speed2;
	Vector speed3;
	Vector speed4;
	
	BlockState block1;
	BlockState block2;
	
	Rect rect1;
	Rect rect2;
	
	PaddleState paddle;

	
	@BeforeEach
	void setUp() throws Exception {
		bottomRight = new Point(10000,10000);
		
		// ALPHA 1
		point1 = new Point(1,1);
		circle1 =  new Circle(point1,1);
		speed1 = new Vector(5,6);
		alpha1 = new Alpha(circle1, speed1);
		
		// ALPHA 2
		point2 = new Point(3,4);
		circle2 =  new Circle(point2,1);
		speed2 = new Vector(2,3);
		alpha2 = new Alpha(circle2, speed2);
		
		// BALL 1
		point3 = new Point(2,5);
		circle3 =  new Circle(point3,1);
		speed3 = new Vector(4,3);
		ball1 = new NormalBall(circle3, speed3);
		
		// BALL 2
		point4 = new Point(6,7);
		circle4 =  new Circle(point4,1);
		speed4 = new Vector(6,4);
		ball2 = new NormalBall(circle4, speed4);
		
		// BLOCK 1
		point5 = new Point(2,3);
		point6 = new Point(3,4);
		rect1 =  new Rect(point5, point6);
		block1 = new NormalBlockState(rect1);
		
		// BLOCK 2
		point7 = new Point(7,9);
		point8 = new Point(8,10);
		rect2 =  new Rect(point7, point8);
		block2 = new NormalBlockState(rect2);
		
		// PADDLE
		paddleCenter = new Point(5000,7000);
		paddle = new NormalPaddleState(paddleCenter);
		
	}

	@Test
	void alphaTest1() {
		assertEquals(circle1, alpha1.getLocation());
		assertEquals(point1, alpha1.getCenter());
		assertEquals(speed1, alpha1.getVelocity());
		assertEquals(circle2, alpha2.getLocation());
		assertEquals(point2, alpha2.getCenter());
		assertEquals(speed2, alpha2.getVelocity());
		assertEquals(circle3, ball1.getLocation());
		assertEquals(point3, ball1.getCenter());
		assertEquals(speed3, ball1.getVelocity());
		assertEquals(circle4, ball2.getLocation());
		assertEquals(point4, ball2.getCenter());
		assertEquals(speed4, ball2.getVelocity());
		
		
	}
	
	@Test
	void normalBreakoutStateTest1() {
		assertNotEquals(alpha1.getVelocity() , alpha2.getVelocity());
		assertNotEquals(alpha1.getLocation() , alpha2.getLocation());
		assertNotEquals(ball1.getVelocity() , ball2.getVelocity());
		assertNotEquals(ball1.getLocation() , ball2.getLocation());
		
		Ball[] balls = {ball1, ball2};
		Alpha[] alphas = {alpha1, alpha2};
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		BlockState[] blocks = {block1,block2};
		BreakoutState state = new BreakoutState(alphas, balls, blocks, bottomRight, paddle);
		
		assertEquals(2, state.getAlphas().length);
		assertEquals(2, state.getBalls().length);
		assertEquals(3, state.getWalls().length);
		assertEquals(2, state.getBlocks().length);
		assertEquals(true, state.getBalls()[0].equalContent(balls[0]));
		assertEquals(true, state.getBalls()[1].equalContent(balls[1]));
		assertEquals(true, state.getAlphas()[0].equalContent(alphas[0]));
		assertEquals(true, state.getAlphas()[1].equalContent(alphas[1]));
		assertEquals(bottomRight, state.getBottomRight());
		assertEquals(paddle, state.getPaddle());
		assertEquals(true, state.getBlocks()[0].equals(blocks[0]));
		assertEquals(true, state.getBlocks()[1].equals(blocks[1]));
		
		
		
		 
	}
	
	
	@Test
	void normalBreakoutStateTest2() { 
		assertNotEquals(ball1.getVelocity() , ball2.getVelocity());
		assertNotEquals(ball1.getLocation() , ball2.getLocation());
		
		Ball[] balls = {ball1, ball2};

		BlockState[] blocks = {block1,block2};
		BreakoutState state = new BreakoutState(balls, blocks, bottomRight, paddle);
		

		assertEquals(2, state.getBalls().length);
		assertEquals(true, state.getBalls()[0].equalContent(balls[0]));
		assertEquals(true, state.getBalls()[1].equalContent(balls[1]));

		assertEquals(bottomRight, state.getBottomRight());
		assertEquals(paddle, state.getPaddle());
		assertEquals(true, state.getBlocks()[0].equals(blocks[0]));
		assertEquals(true, state.getBlocks()[1].equals(blocks[1]));
		
		
	}
	
	
	//
	@Test
	void tickTest1() {
		Ball[] balls = {ball1, ball2};
		BlockState[] blocks = {block1,block2};
		Alpha[] alphas = {alpha1, alpha2};
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		BreakoutState state = new BreakoutState(alphas, balls, blocks, bottomRight, paddle);
		BreakoutState stateWon = new BreakoutState(alphas, balls, new BlockState[] {}, bottomRight, paddle);
		BreakoutState stateDead = new BreakoutState(new Alpha[] {}, new Ball[] {}, blocks, bottomRight, paddle);
		
		state.tick(0, 1);
		assertEquals(state.getPaddle().getLocation(), paddle.getLocation());
		assertNotEquals(state.getBalls()[0].getLocation(), balls[0].getLocation());
		assertNotEquals(state.getBalls()[1].getLocation(), balls[1].getLocation());
		assertNotEquals(state.getAlphas()[0].getLocation(), alphas[0].getLocation());
		assertNotEquals(state.getAlphas()[1].getLocation(), alphas[1].getLocation());
		/*
		 * Deze throws worden niet ge�mplementeerd door de submissionTest
		assertThrows(IllegalArgumentException.class, 
				() -> state.tick(2, 1) );
		assertThrows(IllegalArgumentException.class, 
				() -> stateWon.tick(0, 1) );
		assertThrows(IllegalArgumentException.class, 
				() -> stateDead.tick(0, 1) );
				*/
	}
	// ball and alpha are at the bottom of the field
	@Test
	void tickTest2() {
		paddleCenter = new Point(5000,5000);
		PaddleState paddle = new NormalPaddleState(paddleCenter);
		Circle onderPaddle = new Circle(new Point(3000,9999), 3);
		Vector speed = new Vector(2,100);
		Ball ball1 = new NormalBall(circle1, speed1);
		Ball ball2 = new NormalBall(onderPaddle, speed);
		Ball[] balls = {ball1, ball2};
		Circle onderPaddleAlpha = new Circle(new Point(6000,9999), 3); 
		Alpha alpha2 = new Alpha(onderPaddleAlpha,speed);
		BlockState[] blocks = {block1,block2};
		Alpha[] alphas = {alpha1, alpha2};
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		BreakoutState state = new BreakoutState(alphas, balls, blocks, bottomRight, paddle);
		
		state.tick(0, 50);
		assertEquals(state.getPaddle().getLocation(), paddle.getLocation());
		assertEquals(1, state.getBalls().length);
		assertEquals(1, state.getAlphas().length);

	}
	
	// Ball & Alpha wall bounce test
		@Test
		void tickTest3() {
			Circle positionBall = new Circle(new Point(102,102), 200);
			Circle positionAlpha = new Circle(new Point(300,102), 200);
			Vector speed = new Vector(0,-5);
			
			Ball ball = new NormalBall(positionBall, speed);
			Alpha alpha = new Alpha(positionAlpha,speed);
			Ball[] balls = {ball};
			Alpha[] alphas = {alpha};
			ball.linkTo(alpha);
			BlockState[] blocks = {block1,block2};
			BreakoutState state = new BreakoutState(alphas, balls, blocks, bottomRight, paddle);
			
			state.tick(0, 1);
			assertEquals(state.getPaddle().getLocation(), paddle.getLocation());
			assertEquals(new Point(102,0), state.getBalls()[0].getLocation().getOutermostPoint(new Vector(0,-1)));
			
			assertEquals(true,state.getWalls()[0].contains(state.getBalls()[0].getLocation().getOutermostPoint(new Vector(0,-1))));
			ball = state.getBalls()[0];
			ball.changeVelocity(speed);
			alpha = state.getAlphas()[0];
			alpha.changeVelocity(speed);
			assertEquals(true, ball.collidesWith(state.getWalls()[0]));
			assertEquals(false, state.getBalls()[0].collidesWith(state.getWalls()[1]));	
			assertEquals(false, state.getBalls()[0].collidesWith(state.getWalls()[2]));
			
			assertEquals(true,state.getWalls()[0].contains(state.getAlphas()[0].getLocation().getOutermostPoint(new Vector(0,-1))));
			assertEquals(true, alpha.collidesWith(state.getWalls()[0]));
			assertEquals(false, state.getAlphas()[0].collidesWith(state.getWalls()[1]));	
			assertEquals(false, state.getAlphas()[0].collidesWith(state.getWalls()[2]));
			
		}
		
	// 	Ball bounce on Paddle
		@Test
		void tickTest4() {
			Circle positionBall = new Circle(new Point(2500,1648), 200);
			
			Vector speed = new Vector(0,5);
			
			Ball ball = new NormalBall(positionBall, speed);
			
			Ball[] balls = {ball};
			
			
			BlockState[] blocks = {block1,block2};
			paddleCenter = new Point(2500,2000); 
			// paddle y center = 1875
			// ball y center = 1772
			// straal ball = 100, snelheid = 5
			// 1772+100+5 > 1875
			// dus bounce?!
			paddle = new NormalPaddleState(paddleCenter);
			BreakoutState state = new BreakoutState(balls, blocks, bottomRight, paddle);
			
			state.tick(0,1);
			ball = state.getBalls()[0];
			ball.changeVelocity(speed);
			assertEquals(true, ball.collidesWith(paddle.getLocation()));
			
			paddle = new ReplicatingPaddleState(paddleCenter,4);
			state = new BreakoutState(balls, blocks, bottomRight, paddle);
			
			state.tick(0,1);
			ball = state.getBalls()[0];
			ball.changeVelocity(speed);
			assertEquals(true, ball.collidesWith(paddle.getLocation()));
			assertEquals(state.getBalls().length,4);
			
		}
		
	// Alpha bounce on Paddle
		@Test
		void tickTest5() {
			Circle positionAlpha = new Circle(new Point(2500,1648), 200);
			Circle positionBall = new Circle(new Point(300,102), 200);
			Vector speed = new Vector(0,5);
			
			Ball ball = new NormalBall(positionBall, speed);
			Alpha alpha = new Alpha(positionAlpha,speed);
			Ball[] balls = {ball};
			Alpha[] alphas = {alpha};
			ball.linkTo(alpha);
			BlockState[] blocks = {block1,block2};
			paddleCenter = new Point(2500,2000); 
			// paddle y center = 1875
			// alpha y center = 1772
			// straal alpha = 100, snelheid = 5
			// 1772+100+5 > 1875
			// dus bounce?!
			paddle = new NormalPaddleState(paddleCenter);
			BreakoutState state = new BreakoutState(alphas, balls, blocks, bottomRight, paddle);

			state.tick(0,1);
			alpha = state.getAlphas()[0];
			alpha.changeVelocity(speed);
			assertEquals(true, alpha.collidesWith(paddle.getLocation()));
		}
	
	// Ball bounce on Block
		
		@Test
		void tickTest6() {
			Circle positionAlpha = new Circle(new Point(300,102), 100);
			Circle positionBall = new Circle(new Point(1050,1898), 200);
			Rect positionBlock = new Rect(new Point(1000,2000), new Point(1100, 2100));
			Vector speed = new Vector(0,5);
			
			Ball ball = new NormalBall(positionBall, speed);
			Alpha alpha = new Alpha(positionAlpha,speed);
			Ball[] balls = {ball};
			Alpha[] alphas = {alpha};
			ball.linkTo(alpha);
			BlockState block1 = new NormalBlockState(positionBlock);
			BlockState[] blocks = {block1};
			paddleCenter = new Point(2500,3000); 
			
			paddle = new NormalPaddleState(paddleCenter);
			BreakoutState state = new BreakoutState(alphas, balls, blocks, bottomRight, paddle);

			state.tick(0,1);
			ball = state.getBalls()[0];
			ball.changeVelocity(speed);
			assertEquals(true, ball.collidesWith(block1.getLocation()));
			assertEquals(state.getBlocks().length,0);
			assertEquals(state.isWon(),true);
			
			
			Rect positionBlock2 = new Rect(new Point(1200,2100), new Point(1300, 2200));
			BlockState block2 = new NormalBlockState(positionBlock2);
			
			block1 = new SturdyBlockState(positionBlock,3);
			BlockState[] blocks1 = {block1,block2};
			state = new BreakoutState(alphas, balls, blocks1, bottomRight, paddle);
			state.tick(0,1);
			ball = state.getBalls()[0];
			ball.changeVelocity(speed);
			assertEquals(true, ball.collidesWith(block1.getLocation()));
			assertEquals(state.getBlocks().length,2);
			assertEquals(state.isWon(),false);
		}
			
	@Test
	void generalTest() {
		Vector centerV = new Vector(2500,2500);
		Point topLeft = Point.ORIGIN;
		Point center = topLeft.plus(centerV);


		int diameter = 700;
		Vector speed = new Vector(-200,-400);
		NormalBall NormalBall = new NormalBall(new Circle(center,diameter),speed);
		Point center2 = center.plus(centerV);
		Vector speed2 = new Vector(200,400);
		NormalBall NormalBall2 = new NormalBall(new Circle(center2, diameter), speed2);
		NormalBall[] listNormalBalls = {NormalBall,NormalBall2};

		Point topLeftBlock = new Point(1500,1500);
		Point botRightBlock = new Point(2500,2000);
		BlockState block = new NormalBlockState(new Rect(topLeftBlock,botRightBlock));
		Point topLeftBlock2 = new Point(2500,1500);
		Point botRightBlock2 = new Point(3500,2000);
		Point topLeftBlock3 = new Point(2500,2000);
		Point botRightBlock3 = new Point(3500,2500);
		BlockState block2 = new NormalBlockState(new Rect(topLeftBlock2,botRightBlock2));
		BlockState block3 = new NormalBlockState(new Rect(topLeftBlock3,botRightBlock3));
		BlockState[] listBlocks = {block,block2,block3};

		Point centerPaddle = new Point(2510,3500);
		Point bottomRight = new Point(10000,10000);
		PaddleState paddle = new NormalPaddleState(centerPaddle);
		BreakoutState state = new BreakoutState(listNormalBalls,listBlocks,bottomRight,paddle);


		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(null,listBlocks,bottomRight,paddle) );

		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(listNormalBalls,null,bottomRight,paddle) );
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(listNormalBalls,listBlocks,null,paddle) );
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(listNormalBalls,listBlocks,bottomRight,null) );
		NormalBall[] listNormalBallsNull = {NormalBall,null};
		BlockState[] listBlocksNull = {block,null};
		
		NormalBall NormalBallOffField = new NormalBall(new Circle(new Point(300,300), diameter), speed);
		BlockState blockOffField = new NormalBlockState(new Rect(new Point(-5,-5), new Point(-1,-1)));
		PaddleState paddleOffField = new NormalPaddleState(new Point(5000,100));
		NormalBall[] listNormalBallsOffField = {NormalBallOffField,NormalBall};
		BlockState[] listBlocksOffField = {blockOffField,block};
		
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(listNormalBallsNull,listBlocks,bottomRight,paddle) );
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(listNormalBalls,listBlocksNull,bottomRight,paddle) );
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(listNormalBalls,listBlocks,bottomRight,paddleOffField) );
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(listNormalBallsOffField,listBlocks,bottomRight,paddle) );
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(listNormalBalls,listBlocksOffField,bottomRight,paddle) );

		assertEquals(state.getBlocks()[0],listBlocks[0]);
		assertEquals(state.getBlocks()[1],listBlocks[1]);
		assertEquals(state.getBlocks()[2],listBlocks[2]);
		assertEquals(state.getPaddle(),paddle);
		assertEquals(state.getBottomRight(),bottomRight);
		state.movePaddleLeft(1);
		assertEquals(state.getPaddle().getCenter().getX(),paddle.getCenter().getX()-20);
		state.movePaddleRight(1);
		assert(state.getPaddle()!=paddle);
		assertEquals(state.getPaddle().getCenter(),paddle.getCenter());
		state.movePaddleRight(1);
		assertEquals(state.getPaddle().getCenter().getX(),paddle.getCenter().getX()+20);
	
		BreakoutState state4 = new BreakoutState(listNormalBalls,new BlockState[] {}, bottomRight, paddle);
		assertEquals(state4.isDead(),false);
		assertEquals(state4.isWon(),true);
		BreakoutState state5 = new BreakoutState(new NormalBall[] {},new BlockState[] {}, bottomRight, paddle);
		assertEquals(state5.isDead(),true);
		assertEquals(state5.isWon(),false);
		BreakoutState state6 = new BreakoutState(new NormalBall[] {},listBlocks, bottomRight, paddle);
		assertEquals(state6.isDead(),true);
		assertEquals(state6.isWon(),false);
	}

	
	@Test
	void nullBreakoutStateTest1() {
		Ball[] balls = null;
		BlockState[] blocks = {block1,block2};
		Alpha[] alphas = {alpha1, alpha2};
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(alphas, balls, blocks, bottomRight, paddle) );

	}
	@Test
	void nullBreakoutStateTest2() {
		Ball[] balls = {ball1, ball2};
		BlockState[] blocks = null;
		bottomRight = null;
		paddle = null;
		Alpha[] alphas = null;
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(alphas, balls, blocks, bottomRight, paddle) );

	}
	@Test
	void nullBreakoutStateTest3() {
		Ball[] balls = {ball1, ball2};
		BlockState[] blocks = {block1,block2};
		bottomRight = null;
		paddle = null;
		Alpha[] alphas = null;
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(alphas, balls, blocks, bottomRight, paddle) );

	}
	
	
	@Test
	void nullBreakoutStateTest4() {
		Ball[] balls = {ball1, ball2};
		BlockState[] blocks = {block1,block2};
		paddle = null;
		Alpha[] alphas = null;
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(alphas, balls, blocks, bottomRight, paddle) );

	}
	@Test
	void nullBreakoutStateTest5() {
		Ball[] balls = {ball1, ball2};
		BlockState[] blocks = {block1,block2};
		Alpha[] alphas = null;
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(alphas, balls, blocks, bottomRight, paddle) );

	}
	@Test
	void nullBreakoutStateTest6() {
		Ball[] balls = {ball1, ball2};
		BlockState[] blocks = {block1,block2};
		bottomRight = new Point(-10,-10);
		
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		Alpha[] alphas = {alpha1, alpha2};
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState( balls, blocks, bottomRight, paddle) );
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(alphas, balls, blocks, bottomRight, paddle) );

	}
	
	@Test
	void nullBreakoutStateTest7() {
		Ball[] balls = {ball1, ball2};
		BlockState[] blocks = {block1,block2};
		bottomRight = new Point(10000,10000);
		
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		Alpha[] alphas = {alpha1};
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState( balls, blocks, bottomRight, paddle) );
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(alphas, balls, blocks, bottomRight, paddle) );

	}
	
	@Test
	void nullBreakoutStateTest8() {
		Ball[] balls = {ball1, ball1};
		BlockState[] blocks = {block1,block2};
		bottomRight = new Point(10000,10000);
		
		Alpha[] alphas = {alpha1};
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState( balls, blocks, bottomRight, paddle) );
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(alphas, balls, blocks, bottomRight, paddle) );

	}
	@Test
	void nullBreakoutStateTest9() {
		Ball[] balls = {ball1, ball2};
		BlockState[] blocks = {block1,block2};
		bottomRight = new Point(10000,10000);
		
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		Alpha[] alphas = {alpha1,alpha1};
		
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(alphas, balls, blocks, bottomRight, paddle) );

	}
	@Test
	void nullBreakoutStateTest10() {
		Ball[] balls = {ball1,ball2};
		BlockState[] blocks = {block1,block2};
		bottomRight = new Point(10000,10000);
		
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		Alpha[] alphas = {alpha1};
		
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(alphas, balls, blocks, bottomRight, paddle) );

	}
	
	@Test
	void nullBreakoutStateTest11() {
		Ball[] balls = {null,ball2};
		BlockState[] blocks = {block1,block2};
		bottomRight = new Point(10000,10000);
		
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		Alpha[] alphas = {alpha1,alpha2};
		
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(alphas, balls, blocks, bottomRight, paddle) );

	
	}
	@Test
	void nullBreakoutStateTest12() {
		Ball[] balls = {ball1,ball2};
		BlockState[] blocks = {null,block2};
		bottomRight = new Point(10000,10000);
		
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		Alpha[] alphas = {alpha1,alpha2};
		
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(alphas, balls, blocks, bottomRight, paddle) );

	
	}
	@Test
	void nullBreakoutStateTest13() {
		Ball[] balls = {ball1,ball2};
		BlockState[] blocks = {block1,block2};
		bottomRight = new Point(10000,10000);
		
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		Alpha[] alphas = {null,alpha2};
		
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(alphas, balls, blocks, bottomRight, paddle) );

	
	}
	@Test
	void nullBreakoutStateTest14() {
		Circle centerOffField = new Circle(new Point(-10,-10),200);
		Vector speed = new Vector(0,5);
		ball1 = new NormalBall(centerOffField,speed);
		Ball[] balls = {ball1,ball2};
		BlockState[] blocks = {block1,block2};
		bottomRight = new Point(10000,10000);
		
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		Alpha[] alphas = {alpha1,alpha2};
		
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(alphas, balls, blocks, bottomRight, paddle) );

	
	}
	@Test
	void nullBreakoutStateTest15() {
		Circle centerOffField = new Circle(new Point(-10,-10),200);
		Vector speed = new Vector(0,5);
		alpha1 = new Alpha(centerOffField,speed);
		Ball[] balls = {ball1,ball2};
		BlockState[] blocks = {block1,block2};
		bottomRight = new Point(10000,10000);
		
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		Alpha[] alphas = {alpha1,alpha2};
		
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(alphas, balls, blocks, bottomRight, paddle) );

	
	}
	@Test
	void nullBreakoutStateTest16() {
		paddle = new NormalPaddleState(new Point(150,100));
		Ball[] balls = {ball1,ball2};
		BlockState[] blocks = {block1,block2};
		bottomRight = new Point(10000,10000);
		
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		Alpha[] alphas = {alpha1,alpha2};
		
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(alphas, balls, blocks, bottomRight, paddle) );

	
	}
	@Test
	void nullBreakoutStateTest17() {
		
		Ball[] balls = {ball1,ball2};
		block1 = new NormalBlockState(new Rect(new Point(-10,-10),new Point(10,10)));
		BlockState[] blocks = {block1,block2};
		bottomRight = new Point(10000,10000);
		
		ball1.linkTo(alpha1);
		ball2.linkTo(alpha2);
		Alpha[] alphas = {alpha1,alpha2};
		
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(alphas, balls, blocks, bottomRight, paddle) );

	
	}
	@Test
	void nullBreakoutStateTest18() {
		Ball[] balls = {ball1};
		BlockState[] blocks = {block1,block2};
		bottomRight = new Point(10000,10000);
		
		ball1.linkTo(alpha1);
		ball1.linkTo(alpha2);
		ball2.linkTo(alpha2);
		Alpha[] alphas = {alpha1,alpha2};
		
		assertThrows(IllegalArgumentException.class, 
				() -> new BreakoutState(alphas, balls, blocks, bottomRight, paddle) );

	}
}
