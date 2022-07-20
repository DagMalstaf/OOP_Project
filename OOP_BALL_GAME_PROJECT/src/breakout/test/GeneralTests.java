package breakout.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import breakout.utils.*;
import breakout.radioactivity.*;
import breakout.*;

class GeneralTests {

	@Test
	void testRect() {

		Point topleft = new Point(1000,1000);
		Point botright = new Point(2000,2000);
		int diameter = 200;

		Point center1 = new Point(1500,1500);
		Point center2 = new Point(950,1500);
		
		Circle ballposition = new Circle(center1, diameter);
		Rect location = new Rect(topleft,botright);
		Rect insideLocation = location.minusMargin(5);
		Rect insideLocation2 = location.minusMargin(5,10);
		assert(location.contains(ballposition));
		assert(location.contains(insideLocation));
		assert(location.contains(insideLocation2));

		Point outside = new Point(1500,900);
		Point close1 = location.constrain(outside);
		assertEquals(close1,new Point(1500,1000));

		Point inside = new Point(1500,1500);
		Point close2 = location.constrain(inside);
		assertEquals(close2,inside);

		assertEquals(location.getHeight(),1000);
		assertEquals(location.getWidth(),1000);

		Circle constrained = location.constrain(ballposition);
		assertEquals(constrained.getCenter(), ballposition.getCenter());
		assertEquals(constrained.getDiameter(), ballposition.getDiameter());

		Circle ballposition2 = new Circle(center2, diameter);
		Circle constrained2 = location.constrain(ballposition2);

		assertEquals(constrained2.getCenter(), new Point(1100,1500));


	}
	
	@Test
	void testCircle() {
		Point center = new Point(1000,1000);
		int diameter = 100;
		int radius = diameter/2;
		Circle cirkel = new Circle(center,diameter);
		assertEquals(center,cirkel.getCenter());
		assertEquals(diameter, cirkel.getDiameter());
		assertEquals(radius,cirkel.getRadius());
		assertEquals(center.plus(new Vector(-radius,0)),cirkel.getLeftmostPoint());
		assertEquals(center.plus(new Vector(0,-radius)),cirkel.getTopmostPoint());
		assertEquals(center.plus(new Vector(radius,0)),cirkel.getRightmostPoint());
		assertEquals(center.plus(new Vector(0,radius)),cirkel.getBottommostPoint());
		assertEquals(center.plus(new Vector(-radius,-radius)),cirkel.getTopLeftPoint());
		assertEquals(center.plus(new Vector(radius,radius)),cirkel.getBottomRightPoint());
		Point center2 = new Point(1000,1000);
		Circle cirkel2 = cirkel.withCenter(center2);
		assertEquals(center2,cirkel2.getCenter());
	}
	@Test
	void testFacade() {
		int diameter = 100;
		Vector speed = new Vector(0,5);

		Point normalballCenter = new Point(1000,1000);
		Point normalPaddleCenter = new Point(2000,2000);
		Point normalBlockCenter = new Point(3000,3000);
		Point superchargeBallCenter = new Point(4000,4000);
		//Point replicatorPaddleCenter = new Point(5000,5000);
		Point sturdyBlockCenter = new Point(6000,6000);
		Point replicatorBlockCenter = new Point(7000,7000);
		Point powerBlockCenter = new Point(8000,8000);
		Point bottomRight = new Point(15000,15000);

		BreakoutFacade facade = new BreakoutFacade();
		PaddleState paddleFacade = facade.createNormalPaddleState(normalPaddleCenter);
		PaddleState paddle = new NormalPaddleState(normalPaddleCenter);
		assertEquals(paddleFacade.getCenter(),paddle.getCenter());
		assert(paddleFacade instanceof NormalPaddleState );
		Ball ballFacade = facade.createNormalBall(normalballCenter,diameter,speed);
		Ball ball = new NormalBall(new Circle(normalballCenter,diameter),speed);
		assertEquals(ball.equalContent(ballFacade),true);
		assert(ballFacade instanceof NormalBall);
		Ball superBallFacade = facade.createSuperchargedBall(superchargeBallCenter, diameter, speed, 10000);
		Ball superBall = new SuperChargedBall(new Circle(superchargeBallCenter, diameter), speed, 10000);
		assertEquals(superBallFacade.equalContent(superBall),true);
		assert( superBallFacade instanceof SuperChargedBall);
		Ball superballFacade2 = facade.createSuperchargedBall(normalballCenter,diameter,speed,10000);
		assertNotEquals(ball,superballFacade2);
		BlockState normalBlockFacade = facade.createNormalBlockState(normalBlockCenter.plus(new Vector(-5,-5)), normalBlockCenter.plus(new Vector(5,5)));
		BlockState sturdyBlockFacade = facade.createSturdyBlockState(sturdyBlockCenter.plus(new Vector(-5,-5)), sturdyBlockCenter.plus(new Vector(5,5)),3);
		BlockState replicatorBlockFacade = facade.createReplicatorBlockState(replicatorBlockCenter.plus(new Vector(-5,-5)), replicatorBlockCenter.plus(new Vector(5,5)));
		BlockState powerBlockFacade = facade.createPowerupBallBlockState(powerBlockCenter.plus(new Vector(-5,-5)), powerBlockCenter.plus(new Vector(5,5)));
		BlockState normal = new NormalBlockState(new Rect(normalBlockCenter.plus(new Vector(-5,-5)), normalBlockCenter.plus(new Vector(5,5))));
		BlockState sturdy = new SturdyBlockState(new Rect(sturdyBlockCenter.plus(new Vector(-5,-5)), sturdyBlockCenter.plus(new Vector(5,5))),3);
		BlockState replicator = new ReplicatorBlockState(new Rect(replicatorBlockCenter.plus(new Vector(-5,-5)), replicatorBlockCenter.plus(new Vector(5,5))));
		BlockState powerBlock = new PowerupBallBlockState(new Rect(powerBlockCenter.plus(new Vector(-5,-5)), powerBlockCenter.plus(new Vector(5,5))));
		assertEquals(normalBlockFacade.getLocation(),normal.getLocation());
		assert(normalBlockFacade instanceof NormalBlockState);
		assertEquals(sturdyBlockFacade.getLocation(),sturdy.getLocation());
		assert(sturdyBlockFacade instanceof SturdyBlockState );
		assertEquals(replicatorBlockFacade.getLocation(),replicator.getLocation());
		assert(replicatorBlockFacade instanceof ReplicatorBlockState);
		assertEquals(powerBlockFacade.getLocation(),powerBlock.getLocation());
		assert(powerBlockFacade instanceof PowerupBallBlockState);

		Ball[] listBalls = {ball,superBall};
		BlockState[] listBlocks = {normal,sturdy,replicator,powerBlock};
		BreakoutState stateFacade = facade.createBreakoutState(listBalls, listBlocks,bottomRight, paddle);
		BreakoutState state = new BreakoutState(listBalls, listBlocks,bottomRight, paddle);
		assert(Arrays.asList(stateFacade.getBlocks()).stream().allMatch(e-> Arrays.asList(state.getBlocks()).stream().anyMatch(j-> e==j)));
		assert(stateFacade.getBalls()[0]!=state.getBalls()[0]);
		assert(stateFacade.getBalls()[0]!=ball);
		assert(state.getBalls()[0]!=ball);

		assertEquals(facade.getColor(ball), ball.getColor());
		assertEquals(facade.getColor(paddle),paddle.getColor() );
		assertEquals(facade.getColor(normal),normal.getColor());
		assertEquals(facade.getLocation(paddle), new Rect(paddle.getLocation().getTopLeft(),paddle.getLocation().getBottomRight()));
		assertEquals(facade.getLocation(powerBlock), new Rect(powerBlock.getLocation().getTopLeft(),powerBlock.getLocation().getBottomRight()));
		assertEquals(facade.getCenter(ball),ball.getCenter());
		assertEquals(facade.getDiameter(ball),ball.getLocation().getDiameter());
		assert(Arrays.asList(facade.getBalls(state)).stream().allMatch(e-> Arrays.asList(listBalls).stream().anyMatch(j->e.equalContent(j))));



	}
	
	@Test
	void testBreakout() {


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





		assertEquals(state.getBalls()[0].equalContent(listNormalBalls[0]),true);
		assertEquals(state.getBalls()[1].equalContent(listNormalBalls[1]),true);
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
		Point centerPaddle2 = new Point(8495,3500);
		PaddleState paddle2 = new NormalPaddleState(centerPaddle2);
		BreakoutState state2 = new BreakoutState(listNormalBalls,listBlocks,bottomRight,paddle2);
		
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

}
