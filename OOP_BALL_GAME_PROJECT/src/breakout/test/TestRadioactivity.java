package breakout.test;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import breakout.BreakoutState;
import breakout.NormalBall;
import breakout.radioactivity.Alpha;
import breakout.radioactivity.Ball;
import breakout.utils.*;

class TestRadioactivity {

	@Test
	void BallAlphaLink() {
		Point centerBall = new Point(1000,1000);
		int diameterBall = 200;
		Point centerAlpha = new Point(2000,2000);
		int diameterAlpha = 100;
		Circle locationBall = new Circle(centerBall,diameterBall);
		Circle locationAlpha = new Circle(centerAlpha, diameterAlpha);
		Vector speedBall = new Vector(5,0);
		Vector speedAlpha = new Vector(5,0);
		Point centerBall2 = new Point(3000,3000);
		int diameterBall2 = 200;
		Point centerAlpha2 = new Point(4000,4000);
		int diameterAlpha2 = 100;
		Circle locationBall2 = new Circle(centerBall2,diameterBall2);
		Circle locationAlpha2 = new Circle(centerAlpha2, diameterAlpha2);
		Vector speedBall2 = new Vector(5,0);
		Vector speedAlpha2 = new Vector(5,0);
		Point centerBall3 = new Point(5000,5000);
		int diameterBall3= 200;	
		Circle locationBall3 = new Circle(centerBall3,diameterBall3);
		Vector speedBall3 = new Vector(5,0);
		
		Ball ball = new NormalBall(locationBall,speedBall);
		Alpha alpha = new Alpha(locationAlpha,speedAlpha);
		Ball ball2 = new NormalBall(locationBall2,speedBall2);
		Alpha alpha2 = new Alpha(locationAlpha2,speedAlpha2);
		Ball ball3 = new NormalBall(locationBall3,speedBall3);
		
		assertEquals(ball.getAlphas().size(),0);
		assertEquals(ball2.getAlphas().size(),0);
		assertEquals(ball3.getAlphas().size(),0);
		assertEquals(alpha.getBalls().size(),0);
		assertEquals(alpha2.getBalls().size(),0);
		assertEquals(ball.getCenter(),centerBall);
		assertEquals(ball.getLocation(),locationBall);
		assertEquals(ball.getVelocity(),speedBall);
		assertEquals(alpha.getCenter(),centerAlpha);
		assertEquals(alpha.getLocation(),locationAlpha);
		assertEquals(alpha.getVelocity(),speedAlpha);
		assertEquals(ball.getECharge(),1);
		
		ball.linkTo(alpha);
		
		assertEquals(ball.getAlphas().size(),1);
		assertEquals(ball.getAlphas().contains(alpha),true);
		assertEquals(ball.getAlphas().stream().anyMatch(returnedAlpha-> returnedAlpha.equalContent(alpha)),true);
		
		assertEquals(alpha.getBalls().size(),1);
		assertEquals(alpha.getBalls().contains(ball),true);
		assertEquals(alpha.getBalls().stream().anyMatch(returnedBall-> returnedBall.equalContent(ball)),true);
		
		ball.linkTo(alpha2);
		ball2.linkTo(alpha);
		ball3.linkTo(alpha2);
		
		assertEquals(ball.getAlphas().size(),2);
		assertEquals(ball.getAlphas().contains(alpha),true);
		assertEquals(ball.getAlphas().contains(alpha2),true);
		assertEquals(ball.getECharge(),2);
		assertEquals(ball2.getECharge(),-2);
		assertEquals(ball.getAlphas().stream().anyMatch(returnedAlpha-> returnedAlpha.equalContent(alpha)),true);
		assertEquals(ball.getAlphas().stream().anyMatch(returnedAlpha-> returnedAlpha.equalContent(alpha2)),true);
		
		ball.unLink(alpha2);
		assertEquals(ball.getAlphas().size(),1);
		assertEquals(ball.getAlphas().contains(alpha),true);
		assertEquals(ball.getAlphas().contains(alpha2),false);
		assertEquals(ball.getECharge(),-2);
		assertEquals(ball3.getECharge(),-1);
		assertEquals(ball.getAlphas().stream().anyMatch(returnedAlpha-> returnedAlpha.equalContent(alpha)),true);
		assertEquals(ball.getAlphas().stream().anyMatch(returnedAlpha-> returnedAlpha.equalContent(alpha2)),false);
		
		ArrayList<Ball> listBalls = new ArrayList<Ball>();
		ArrayList<Alpha> listAlphas = new ArrayList<Alpha>();
		listBalls.add(ball);
		listBalls.add(ball2);
		listBalls.add(ball3);
		listAlphas.add(alpha);
		listAlphas.add(alpha2);

		DualList<Ball,Alpha> clonedGraph = BreakoutState.cloneGraphForTesting(listBalls, listAlphas);
		
		ArrayList<Ball> clonedBalls = clonedGraph.getList1();
		ArrayList<Alpha> clonedAlphas = clonedGraph.getList2();
		
		assertEquals(clonedBalls.stream().anyMatch(clone -> listBalls.contains(clone)),false);
		assertEquals(clonedAlphas.stream().anyMatch(clone -> listAlphas.contains(clone)),false);
		assertEquals(clonedBalls.size(),listBalls.size());
		assertEquals(clonedAlphas.size(), listAlphas.size());
		assertEquals(clonedBalls.stream().allMatch(clone-> listBalls.stream().anyMatch(original-> original.equalContent(clone))),true);
		assertEquals(clonedAlphas.stream().allMatch(clone-> listAlphas.stream().anyMatch(original-> original.equalContent(clone))),true);
		
		ball.unLink(alpha2);
		assertEquals(ball.getAlphas().size(),1);
		assertEquals(ball.getAlphas().contains(alpha),true);
		assertEquals(ball.getAlphas().contains(alpha2),false);
		assertEquals(ball.getECharge(),-2);
		assertEquals(ball2.getECharge(),-2);
		assertEquals(ball3.getECharge(),-1);
		assertEquals(ball.getAlphas().stream().anyMatch(returnedAlpha-> returnedAlpha.equalContent(alpha)),true);
		assertEquals(ball.getAlphas().stream().anyMatch(returnedAlpha-> returnedAlpha.equalContent(alpha2)),false);
		
		ball.unLink(alpha);
		assertEquals(ball.getAlphas().size(),0);
		assertEquals(ball.getAlphas().contains(alpha),false);
		assertEquals(ball.getAlphas().contains(alpha2),false);
		assertEquals(ball.getECharge(),1);
		assertEquals(ball2.getECharge(),-1);
		assertEquals(ball3.getECharge(),-1);
		assertEquals(ball.getAlphas().stream().anyMatch(returnedAlpha-> returnedAlpha.equalContent(alpha)),false);
		assertEquals(ball.getAlphas().stream().anyMatch(returnedAlpha-> returnedAlpha.equalContent(alpha2)),false);
		
		
		alpha.changeLocation(locationBall3);
		alpha.changeVelocity(speedBall3);
		assertEquals(locationBall3.getCenter(),alpha.getCenter());
		assertEquals(alpha.getVelocity(),speedBall3);
		assertEquals(alpha.getColor(),Color.red);
		
	}
	@Test
	void generalTest() {
		Point centerBall = new Point(1000,1000);
		int diameterBall = 200;
		Point centerAlpha = new Point(2000,2000);
		int diameterAlpha = 100;
		Circle locationBall = new Circle(centerBall,diameterBall);
		Circle locationAlpha = new Circle(centerAlpha, diameterAlpha);
		Vector speedBall = new Vector(5,0);
		Vector speedAlpha = new Vector(5,0);
		Point centerBall2 = new Point(3000,3000);
		int diameterBall2 = 200;
		Point centerAlpha2 = new Point(4000,4000);
		int diameterAlpha2 = 100;
		Circle locationBall2 = new Circle(centerBall2,diameterBall2);
		Circle locationAlpha2 = new Circle(centerAlpha2, diameterAlpha2);
		Vector speedBall2 = new Vector(5,0);
		Vector speedAlpha2 = new Vector(5,0);
		Point centerBall3 = new Point(5000,5000);
		int diameterBall3= 200;	
		Circle locationBall3 = new Circle(centerBall3,diameterBall3);
		Vector speedBall3 = new Vector(5,0);
		
		Ball ball = new NormalBall(locationBall,speedBall);
		Alpha alpha = new Alpha(locationAlpha,speedAlpha);
		Ball ball2 = new NormalBall(locationBall,speedBall);
		Alpha alpha2 = new Alpha(locationAlpha,speedAlpha);
		Alpha alpha3 = new Alpha(locationAlpha2,speedAlpha2);
		Ball ball3 = new NormalBall(locationBall3,speedBall3);
		
		Point topleft = new Point(5000,5000);
		Point botright = new Point(7000,7000);
		Rect rect = new Rect(topleft,botright);
		assertEquals(null,alpha.bounceOn(rect));
		assertEquals(null,ball.bounceOn(rect));
		
		
		ball.linkTo(alpha);
		ball3.linkTo(alpha);
		ball.linkTo(alpha2);
		// check for bijectie by connecting one particle to two particles with same content
		//and the other particle to one with same content en one with different content
		Ball ballContent = new NormalBall(locationBall,speedBall);
		ballContent.linkTo(alpha2);
		assertEquals(false,alpha2.equalContent(alpha));
		
		Alpha alphaContent = new Alpha(locationAlpha,speedAlpha);
		ball.unLink(alpha2);
		ball.linkTo(alpha3);
		ball2.linkTo(alphaContent);
		ball2.linkTo(alpha);
		assertEquals(false,ball2.equalContent(ball));
		
		
	}

}
