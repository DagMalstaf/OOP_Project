package breakout;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import breakout.radioactivity.*;
import breakout.utils.*;

class SturdyBlockStateTest {
	Point p11;
	Point p05;
	Point p38;
	Point pm14;

	Rect r1138;
	Rect rm1438;

	SturdyBlockState b1;
	SturdyBlockState b2;
	SturdyBlockState b3;

	@BeforeEach
	void setUp() throws Exception {
		p11 = new Point(1, 1);
		p05 = new Point(0, 5);
		p38 = new Point(3, 8);
		pm14 = new Point(-1, 4);
		r1138 = new Rect(p11, p38);
		rm1438 = new Rect(pm14, p38);
		b3 = new SturdyBlockState(r1138,3);
		b2 = new SturdyBlockState(r1138,2);
		b1 = new SturdyBlockState(r1138,1);
	}

	@Test
	void testBlock() {
		assertEquals(r1138, b3.getLocation());
		assertEquals(3, b3.getLivesLeft());
	}

	@Test
	void testBlockStateAfterHitNotDead() {
		assertEquals(SturdyBlockState.class, b3.blockStateAfterHit().getClass());
		assertEquals(2, ((SturdyBlockState)b3.blockStateAfterHit()).getLivesLeft());
		assertEquals(b3.getLocation(), ((SturdyBlockState)b3.blockStateAfterHit()).getLocation());
	}

	@Test
	void testBlockStateAfterHitDead() {
		assertEquals(null, b1.blockStateAfterHit());
	}

	@Test
	void testBallStateAfterHit() {
		Ball b = new NormalBall(new Circle(p05,2), new Vector(10,10)); 
		assertEquals(b, b3.ballStateAfterHit(b));
	}

	@Test
	void testPaddleStateAfterHit() {
		PaddleState p = new NormalPaddleState(pm14); 
		assertEquals(p, b3.paddleStateAfterHit(p));
	}

	@Test
	void testGetColor() {
		assertEquals(new Color(0x80, 0x00, 0xff), b1.getColor());
		assertEquals(new Color(0x80, 0x00, 0xcf), b2.getColor());
		assertEquals(new Color(0x80, 0x00, 0x9f), b3.getColor());
		
	}
}
