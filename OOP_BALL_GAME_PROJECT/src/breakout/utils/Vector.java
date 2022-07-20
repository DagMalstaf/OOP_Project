package breakout.utils;

/**
 * Represents a 2-dimensional integer vector.
 *
 * @immutable
 */
public class Vector {

	private final int x;
	private final int y;

	public static final Vector DOWN = new Vector(0, 1);
	public static final Vector UP = new Vector(0, -1);
	public static final Vector RIGHT = new Vector(1, 0);
	public static final Vector LEFT = new Vector(-1, 0);

	/**
	 * Return a new Coordinate with given x and y coordinates.
	 * 
	 * @post | getX() == x
	 * @post | getY() == y
	 */
	public Vector(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector other = (Vector) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	/** Return this vector's x coordinate */
	public int getX() {
		return x;
	}

	/** Return this vector's y coordinate */
	public int getY() {
		return y;
	}

	/**
	 * Return the Coordinate obtained by scaling this coordinate with a given
	 * factor.
	 * 
	 * @post | result != null
	 * @post | result.getX() == getX() * factor
	 * @post | result.getY() == getY() * factor
	 */
	public Vector scaled(int factor) {
		return new Vector(x * factor, y * factor);
	}

	/**
	 * Return the Coordinate obtained by adding this vector with vector `other`.
	 * 
	 * @pre | other != null
	 * @post | result != null
	 * @post | result.getX() == getX() + other.getX()
	 * @post | result.getY() == getY() + other.getY()
	 */
	public Vector plus(Vector other) {
		return new Vector(x + other.x, y + other.y);
	}

	/**
	 * Return the Coordinate obtained by subtracting vector `other` from this
	 * vector.
	 * 
	 * @pre | other != null
	 * @post | result != null
	 * @post | result.getX() == getX() - other.getX()
	 * @post | result.getY() == getY() - other.getY()
	 */
	public Vector minus(Vector other) {
		return new Vector(x - other.x, y - other.y);
	}

	/**
	 * Return a string representation of this vector.
	 * 
	 * @post | result != null
	 */
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	/**
	 * Compute the dot product of this vector with vector `other`.
	 * 
	 * @pre | v != null
	 * @post | result == getX() * v.getX() + getY() * v.getY()
	 */
	public int product(Vector v) {
		return v.x * x + v.y * y;
	}

	/**
	 * Return the square of the length of this vector.
	 * 
	 * @post | result == this.product(this)
	 */
	public int getSquareLength() {
		return this.product(this);
	}
	
	/**
	 * length /!\ double
	 * 
	 * //@post | result >= 0
	 */
	public double getLength() {
		return Math.sqrt( getSquareLength() );
	}

	/**
	 * Mirror this vector over a given normal vector and return the result.
	 * 
	 * @pre | m != null
	 * @pre | m.getSquareLength() == 1
	 * @post | result != null
	 * @post | result.equals(this.minus(m.scaled(2*this.product(m))))
	 */
	public Vector mirrorOver(Vector m) {
		return this.minus(m.scaled(2 * this.product(m)));
	}

	/**
	 * Scale this vector down by dividing its coordinates by the given factor.
	 * 
	 * @pre | d != 0
	 * @post | result != null
	 * @post | result.getX() == getX() / d
	 * @post | result.getY() == getY() / d
	 */
	public Vector scaledDiv(int d) {
		return new Vector(getX() / d, getY() / d);
	}
	
	/**
	 * 
	 * returns new speed of the ball after magnetism occurs.
	 */
	public static Vector magnetSpeed(Point alphaPos, Point ballPos, int ballEcharge, Vector ballSpeed) {
		Vector ballToAlpha = alphaPos.minus(ballPos);
		double ballToAlphaLength = ballToAlpha.getLength();
		double ballSpeedNLength = ballSpeed.getLength();
		//increase ball speed by max 50%. eCharges > 4 act as eCharges == 4.
		int chargeVal = Math.abs(ballEcharge);
		if (chargeVal == 0) {throw new IllegalArgumentException();}
		if (chargeVal == 2) {ballSpeedNLength = ballSpeedNLength * (1 + (0.166));}
		if (chargeVal == 3) {ballSpeedNLength = ballSpeedNLength * (1 + (0.333));}
		if (chargeVal >= 4) {ballSpeedNLength = ballSpeedNLength * (1 + (0.5));}
		if (ballToAlphaLength > ballSpeedNLength &&  ballSpeedNLength  >= 0) {
			double downFactor = ballSpeedNLength / ballToAlphaLength;
			assert 0 < downFactor && downFactor < 1;
			Vector res = ballToAlpha.floatScale(downFactor);
			int chargeSign;
			if (ballEcharge < 0) {chargeSign = -1;}
			else {chargeSign = 1;}
			res = res.scaled(- chargeSign); //either attracted or repelled by alpha
			return res;
		}
		else return ballSpeed;
	}
	
	public int floorCeil(double value) {
        return (int) (value >= 0 ? Math.ceil(value) : Math.floor(value));
	}
   
	public Vector floatScale(double factor) {
	        return new Vector(floorCeil( x * factor ) , floorCeil( y * factor ));
	}
		
//	public Vector floatScale(double factor) {
//		return new Vector((int) Math.ceil( x * factor ) , (int) Math.ceil( y * factor ));
//	}
}
