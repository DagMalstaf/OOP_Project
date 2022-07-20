package breakout;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import breakout.utils.*;

class ReplicatingPaddleStateTest {
	Point loc = new Point(2000,1000);
	Rect fieldRect = new Rect(new Point(0,0),new Point(10000,10000));
	ReplicatingPaddleState p1; 
	ReplicatingPaddleState p2; 
	ReplicatingPaddleState p3; 

	@BeforeEach
	void setUp() throws Exception {
		p2 = new ReplicatingPaddleState(loc, 2);
		p3 = new ReplicatingPaddleState(loc, 3);
	}

	@Test
	void testNumberOfBallsAfterHit() {
		assertEquals(3, p3.numberOfBallsAfterHit());
	}

	@Test
	void testStateAfterHit() {
		assertEquals(ReplicatingPaddleState.class, p3.stateAfterHit().getClass());
		assertEquals(2, ((ReplicatingPaddleState)p3.stateAfterHit()).getCount());
		assertEquals(p3.getLocation(), p3.stateAfterHit().getLocation());
		assertEquals(NormalPaddleState.class, p2.stateAfterHit().getClass());
		assertEquals(p2.getLocation(), p2.stateAfterHit().getLocation());
	}

	@Test
	void testGetColor() {
		assertEquals(new Color(0xff, 0xff, 0x00), p3.getColor());
	}

	@Test
	void testMove() {
		Vector v = new Vector(10,0);
		assertEquals(ReplicatingPaddleState.class, p3.move(v, fieldRect).getClass());
		assertEquals(p3.getCenter().plus(v), p3.move(v, fieldRect).getCenter());	}

	@Test
	void testNormalPaddleState() {
		assertEquals(p3.getCenter(), loc);
	}

}
