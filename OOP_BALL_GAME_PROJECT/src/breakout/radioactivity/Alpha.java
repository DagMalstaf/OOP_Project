package breakout.radioactivity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import breakout.BreakoutState;
import breakout.utils.Circle;
import breakout.utils.Point;
import breakout.utils.Rect;
import breakout.utils.Vector;
/**@mutable
 * check not null: phase 1 invariant, the not null invar for Ball is also a phase 1 invar->
 * the phase 2 bidirectional association is guarded and therefore well-defined
 * @invar| getBalls() != null && getBalls().stream().allMatch(ball -> ball != null) 
 * bidirectional association: phase 2 invariant
 * @invar| getBalls().stream().allMatch(ball -> ball.getAlphas().contains(this))
 * @invar | getLocation() != null
 * @invar | getVelocity().getSquareLength() >0
 * @invar | getVelocity() != null
 * 
 * check no duplicates:  dummy check, is guaranteed by the propertys of the interface Set
 * @invar |getBalls().stream().distinct().count() ==  getBalls().size()
 */
public class Alpha  {
	
	private static final Color ALPHA_COLOR = Color.red;
	/**
	 * @invar| location != null
	 */
	private Circle location;
	/**
	 * @invar| velocity != null
	 * @invar| velocity.getSquareLength() >0
	 * 
	 */
	private Vector velocity;
	
	
	/**
	 * @invar| linkedBalls != null && linkedBalls.stream().allMatch(e -> e != null)  // phase 4 invar
	 * @representationObject
	 * @representationObjects
	 */
	private final Set<Ball> linkedBalls = new HashSet<Ball>();
	
	/**
	 * @creates | result
	 * @peerObjects (package-level)
	 * The bidirectional association is not well-defined when this.linkedballs or any of the elements in this.linkedballs or ball.getLinkedAlphasInternal()
	 * is null. Therefore we need to guard this invar behind a phase one invar that checks for not null.
	 * Consequently we add a dummy invar to make sure the bidirectional associtation is a phase 5 invar.
	 * 
	 * bidirectional association: phase 5 invar
	 * @invar| getLinkedBallsInternal().stream().allMatch(ball -> ball.getLinkedAlphasInternal().contains(this))
	 * check not null
	 * @post| result != null && result.stream().allMatch(ball -> ball != null)
	 *
	 */
	Set<Ball> getLinkedBallsInternal (){
		return Set.copyOf(this.linkedBalls);
	}
	/**
	 * @creates|result
	 * @peerObjects
	 * 
	 */
	public Set<Ball> getBalls(){
		
		return this.getLinkedBallsInternal();
	}
	
	/**
	 * Construct a new ball at a given `location`, with a given `velocity`.
	 * 
	 * @pre | location != null
	 * @pre | velocity != null
	 * @post | getLocation().equals(location)
	 * @post | getVelocity().equals(velocity)
	 * @post| getBalls().size() == 0
	 */
	public Alpha(Circle location, Vector velocity) {
		this.location = location;
		this.velocity = velocity;
	}
	
	/**
	 *
	 * @mutates_properties| this.getBalls()
	 * @pre| ball != null
	 * can only be invoked when the ball is not already in the Set
	 * 
	 */
	void linkTo(Ball ball) {
		this.linkedBalls.add(ball);
		
	}
	/**
	 * @mutates_properties| this.getBalls()
	 * @pre| ball != null
	 * can only be invoked when the ball is already in the Set
	 * 
	 */
	void unLink(Ball ball) {
		this.linkedBalls.remove(ball);
		
	}
	private boolean One_On_One(Alpha other) {
		ArrayList<Ball> otherBalls = new ArrayList<Ball>(other.getBalls());
		boolean result= this.getBalls().stream().map(ball ->{
			if(otherBalls.stream().anyMatch(ballCheck->
			ball.equalContentNoAlphaCheck(ballCheck)
					)) {
				otherBalls.remove(otherBalls.stream().filter(e-> e.equalContentNoAlphaCheck(ball)).findAny().get()) ;
				return true;
			}
			else {
				return false;
			}

		}).allMatch(e-> e);
		//assertEquals(otherBalls.size(),0);
		return result;
	}
	
	/**
	 * @post| (this.getCenter().equals(other.getCenter()) && 
	 * 		|	this.getVelocity().equals(other.getVelocity()) && 
	 * 		|		this.getBalls().size() == other.getBalls().size() &&
	 * 		|		old(One_On_One(other))
	 * 		| )? result: !result
	 * 		
	 * 
	 */
	public boolean equalContent(Alpha other) {
		
		if( !this.getCenter().equals(other.getCenter())) {return false;}
		if( ! this.getVelocity().equals(other.getVelocity())) {return false;}
		if( this.getBalls().size() != other.getBalls().size() ||

				// ensure one-on-one relationship
				!One_On_One(other))
			{return false;}
		
		return true;
	}
	/**
	 * @post| (this.getCenter().equals(other.getCenter()) && 
	 * 		|	this.getVelocity().equals(other.getVelocity())
	 * 		|		 )? result: !result
	 * 		
	 * 
	 */
	public boolean equalContentNoBallCheck(Alpha other) {
		if( !this.getCenter().equals(other.getCenter())) {return false;}
		if( ! this.getVelocity().equals(other.getVelocity())) {return false;}
		
		return true;
	}
	/**
	 * Return this ball's location.
	 */
	public Circle getLocation() {
		return location;
	}

	/**
	 * Return this ball's velocity.
	 */
	public Vector getVelocity() {
		return velocity;
	}
	
	/**
	 * Return this point's center.
	 * 
	 * @post | getLocation().getCenter().equals(result)
	 * @inspects this
	 */
	public Point getCenter() {
		return this.getLocation().getCenter();
	}
	
	/**
	 * Check whether this ball collides with a given `rect` and if so, return the
	 * new velocity this ball will have after bouncing on the given rect.
	 * 
	 * @pre | rect != null
	 * @post | (rect.collideWith(getLocation()) == null && result == null) ||
	 *       | (rect.collideWith(getLocation()) != null && getVelocity().product(rect.collideWith(getLocation())) <= 0 && result == null) ||
	 *       | (rect.collideWith(getLocation()) != null && result.equals(getVelocity().mirrorOver(rect.collideWith(getLocation()))))
	 * @inspects this
	 */
	public Vector bounceOn(Rect rect) {
		Vector coldir = rect.collideWith(location);
		if (coldir != null && velocity.product(coldir) > 0) {
			return velocity.mirrorOver(coldir);
		}
		return null;
	}
	
	/**
	 * Check whether this ball collides with a given `rect`.
	 * 
	 * @pre | rect != null
	 * @post | result == ((rect.collideWith(getLocation()) != null) &&
	 *       |            (getVelocity().product(rect.collideWith(getLocation())) > 0))
	 * @inspects this
	 */
	public boolean collidesWith(Rect rect) {
		Vector coldir = rect.collideWith(getLocation());
		return coldir != null && (getVelocity().product(coldir) > 0);
	}
	/**
	 * Move this BallState by the given vector.
	 * 
	 * @pre | v != null
	 * @pre | elapsedTime >= 0
	 * @pre | elapsedTime <= BreakoutState.MAX_ELAPSED_TIME
	 * @post | getLocation().getCenter().equals(old(getLocation()).getCenter().plus(v))
	 * @post | getLocation().getDiameter() == old(getLocation()).getDiameter()
	 * @mutates_properties| this.getLocation()
	 */
	public void move(Vector v, int elapsedTime) {
		this.location = new Circle(getLocation().getCenter().plus(v), getLocation().getDiameter());

		
	}
	/**
	 * Update the BallState after hitting a paddle at a given location.
	 * 
	 * @pre | rect != null
	 * @pre | collidesWith(rect)
	 * @pre | paddleVel != null
	 * 
	 * @mutates_properties| this.getVelocity()
	 */
	public void hitPaddle(Rect rect, Vector paddleVel) {
		Vector nspeed = bounceOn(rect);
		this.velocity = nspeed.plus(paddleVel.scaledDiv(5));

		
	}
	/**
	 * Update the BallState after hitting a wall at a given location.
	 * 
	 * @pre | rect != null
	 * @pre | collidesWith(rect)
	 * 
	 *  @mutates_properties| this.getVelocity()
	 */
	public void hitWall(Rect rect) {
		this.velocity = bounceOn(rect);
		
	}
	/**
	 * Return the color this ball should be painted in.
	 * 
	 * @post | result != null
	 * @inspects this
	 */
	public Color getColor() {
		
		return ALPHA_COLOR;
	}
	
	/**
	 * Return a clone of this BallState.
	 * 
	 * @inspects this
	 * @creates result
	 * @post | result.getLocation().equals(getLocation())
	 * @post | result.getVelocity().equals(getVelocity())
	 */
	public Alpha clone() {
		return this.cloneWithVelocity(this.velocity);
	}
	
	/**
	 * Return a clone of this BallState with the given velocity.
	 * 
	 * @inspects this
	 * @creates result
	 * @post | result.getLocation().equals(getLocation())
	 * @post | result.getVelocity().equals(v)
	 */
	public Alpha cloneWithVelocity(Vector v) {
		return new Alpha(this.getLocation(), v);
	
	
	
		
	};
	/**
	 * extra layer of encapsulation not present in model solution to protect class invariants
	 * @mutates_properties| this.getLocation()
	 * @pre| location != null
	 * @post| this.getLocation() == location
	 * 
	 */
	
	
		
	public void changeLocation(Circle location) {
		this.location = location;
	}
	
	/**extra layer of encapsulation not present in model solution to protect class invariants

	 * @mutates_properties| this.getVelocity()
	 * @pre| speed != null
	 * @pre| speed.getSquareLength()>0
	 * @post| this.getVelocity() == speed
	 */
	public void changeVelocity(Vector speed) {
		this.velocity = speed;
	}
	

}
