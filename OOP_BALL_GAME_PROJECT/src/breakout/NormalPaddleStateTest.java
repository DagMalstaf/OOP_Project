package breakout;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import breakout.utils.*;

class NormalPaddleStateTest {
	Point loc = new Point(2000,1000);
	Rect fieldRect = new Rect(new Point(0,0),new Point(10000,10000));
	PaddleState p; 

	@BeforeEach
	void setUp() throws Exception {
		p = new NormalPaddleState(loc);
	}

	@Test
	void testNumberOfBallsAfterHit() {
		assertEquals(1, p.numberOfBallsAfterHit());
	}

	@Test
	void testStateAfterHit() {
		assertEquals(p, p.stateAfterHit());
	}

	@Test
	void testGetColor() {
		assertEquals(new Color(0x99, 0xff, 0xff), p.getColor());
	}

	@Test
	void testMove() {
		Vector v = new Vector(10,0);
		assertEquals(NormalPaddleState.class, p.move(v, fieldRect).getClass());
		assertEquals(p.getCenter().plus(v), p.move(v, fieldRect).getCenter());	}

	@Test
	void testNormalPaddleState() {
		assertEquals(p.getCenter(), loc);
	}

}
