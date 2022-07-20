package breakout;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import breakout.radioactivity.Ball;
import breakout.utils.*;
class NormalBallTest {
	Point p11;
	Point p05;
	Point p38;
	Point pm14;

	Rect r1138;
	Rect rm1438;

	Vector v1010;

	Circle c052;
	Circle c389;
	Ball b1;

	@BeforeEach
	void setUp() throws Exception {
		p11 = new Point(1, 1);
		p05 = new Point(0, 5);
		p38 = new Point(3, 8);
		pm14 = new Point(-1, 4);
		r1138 = new Rect(p11, p38);
		rm1438 = new Rect(pm14, p38);
		c052 = new Circle(p05, 2);
		c389 = new Circle(p38, 9);
		v1010 = new Vector(10, 10);
		b1 = new NormalBall(c052, v1010);
	}

	@Test
	void testBall() {
		assertEquals(p05, b1.getLocation().getCenter());
		assertEquals(2, b1.getLocation().getDiameter());
		assertEquals(v1010, b1.getVelocity());
	}

	@Test
	void testBounceOn() {
		assertEquals(new Vector(-10, 10), b1.bounceOn(r1138));
	}

	@Test
	void testHitBlockNotDestroyed() {
		b1.hitBlock(r1138, false);
		assertEquals(b1.getVelocity(),new Vector(-10,10));
		assertEquals(b1.getLocation(), c052);
	}

	@Test
	void testHitBlockDestroyed() {
		b1.hitBlock(r1138, true);
		assertEquals(b1.getVelocity(),new Vector(-10,10));
		assertEquals(b1.getLocation(), c052);
	}

	@Test
	void testHitWall() {
		b1.hitWall(r1138);
		assertEquals(b1.getVelocity(),new Vector(-10,10));
		assertEquals(b1.getLocation(), c052);
	}

	@Test
	void testHitPaddle() {
		b1.hitPaddle(r1138, new Vector(0,-10));
		assertEquals(b1.getVelocity(),new Vector(-10,8));
		assertEquals(b1.getLocation(), c052);
	}
	
	@Test
	void testMove() {
		b1.move(new Vector(0,-10), 40);
		assertEquals(b1.getVelocity(), v1010);
		assertEquals(new Point(0,-5), b1.getLocation().getCenter());
	}
	
	@Test
	void testGetColor() {
		assertEquals(Color.yellow, b1.getColor());
	}
}
