package breakout.radioactivity;
import breakout.*;
import breakout.utils.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import java.util.OptionalInt;
import logicalcollections.LogicalSet;


/**
 * Represents the state of a ball in the breakout game.
 * @mutable
 *  
 * @invar| getAlphas() != null && getAlphas().stream().allMatch(alpha -> alpha != null)
 * bidirectional association: 
 * @invar| getAlphas().stream().allMatch(alpha -> alpha.getBalls().contains(this))
 * @invar | getLocation() != null
 * @invar | getVelocity().getSquareLength() >0
 * @invar | getVelocity() != null
 *  
 * check no duplicates: dummy check, is guaranteed by the propertys of the interface Set
 * @invar | getAlphas().stream().distinct().count() ==  getAlphas().size()
 * 
 * 
 * check result not zero
 * @invar| getECharge() != 0
 * check result sign
 * @invar| (this.getAlphas().size() %2 == 0)? getECharge()>0: getECharge() <0
 * check value result
 * @invar| (this.getAlphas().size() == 0)? getECharge() == 1:this.getAlphas().stream().mapToInt(alpha -> alpha.getBalls().size()).max().equals(OptionalInt.of((int) Math.abs(getECharge())))
 */
public abstract class Ball {
	/**
	 * @invar| location != null
	 */
	private Circle location;
	/**
	 * @invar| velocity != null
	 * @invar| velocity.getSquareLength() >0
	 */
	private Vector velocity;
	/**
	 * 
	 * @invar| linkedAlphas != null && linkedAlphas.stream().allMatch(e -> e != null) // phase 1 invar
	 * @representationObject
	 * @representationObjects
	 * 
	 */
	private final Set<Alpha> linkedAlphas = new HashSet<Alpha>();
	
	/**
	 * The bidirectional association is not well-defined when this.linkedAlpas or any of the elements in this.linkedAlphas or alpha.getLinkedBallsInternal()
	 * is null. Therefore we need to guard this invar behind a phase one invar that checks for not null.
	 * Consequently we add a dummy invar to make sure the bidirectional associtation is a phase 5 invar.
	 * 
	 * bidirectional association: phase 5 invar
	 * @invar| getLinkedAlphasInternal().stream().allMatch(alpha -> alpha.getLinkedBallsInternal().contains(this))
	 * check not null
	 * @post| result != null && result.stream().allMatch(alpha -> alpha != null)
	 * @creates|result
	 * @peerObjects (package-level)
	 */
	Set<Alpha> getLinkedAlphasInternal (){
		return Set.copyOf(this.linkedAlphas);
	}
	
	/**
	 * @invar| echarge != 0
	 * check sign echarge
	 * @invar| (this.getAlphas().size() %2 == 0)? echarge>0: echarge <0
	 * check value echarge
	 * @invar| (this.getAlphas().size() == 0)? echarge == 1:this.getAlphas().stream().mapToInt(alpha -> alpha.getBalls().size()).max().equals(OptionalInt.of((int) Math.abs(echarge)))
	 */
	private int echarge = 1;
	
	
	/**
	 * @creates|result
	 * @peerObjects
	 * 
	 */
	public Set<Alpha> getAlphas(){
		return getLinkedAlphasInternal();
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


	/**
	 *@mutates_properties| this.getAlphas(), alpha.getBalls(), this.getECharge(), (... alpha.getBalls()).getECharge()
	 *inspects this and its peerobjects/ represetationobjects
	 *@inspects|this
	 *check not null
	 *@pre| alpha != null
	 *check length linked alphas
	 *@post| (old(getAlphas()).contains(alpha))? getAlphas().size() == old(getAlphas()).size(): getAlphas().size() == old(getAlphas()).size()+1
	 *check correct change linkedalphas
	 *@post|  getAlphas().equals(LogicalSet.plus(old(getAlphas()),alpha))
	 *check correct change alpha.linkedBalls
	 *@post| alpha.getBalls().equals(LogicalSet.plus(old(alpha.getBalls()),this))
	 *check correct echarge
	 *check sign echarge
	 * @post| alpha.getBalls().stream().allMatch(ball -> (ball.getAlphas().size() %2 == 0)? ball.getECharge()>0: ball.getECharge() <0)
	 * check value echarge
	 * @post| alpha.getBalls().stream().allMatch(ball-> (ball.getAlphas().size() == 0)? ball.getECharge() == 1:ball.getAlphas().stream().mapToInt(alphas -> alphas.getBalls().size()).max().equals(OptionalInt.of((int) Math.abs(ball.getECharge()))))
	 */
	public void linkTo(Alpha alpha) {
		if(this.linkedAlphas.add(alpha)) {
			alpha.linkTo(this);
			alpha.getBalls().forEach(linkedBall-> linkedBall.calculateECharge());

		}
	}
	/**
	 *@mutates_properties| this.getAlphas(), alpha.getBalls(), this.getECharge(), (... alpha.getBalls()).getECharge()
	 *inspects this and its peerobjects/ represetationobjects
	 *@inspects|this
	 *check not null
	 *@pre| alpha != null
	 *check length linked alphas
	 *@post| (!old(getAlphas()).contains(alpha))? getAlphas().size() == old(getAlphas()).size(): getAlphas().size() == old(getAlphas()).size()-1
	 *check correct change linkedalphas
	 *@post| getAlphas().equals(LogicalSet.minus(old(getAlphas()),alpha))
	 *check correct change alpha.linkedBalls
	 *@post| alpha.getBalls().equals(LogicalSet.minus(old(alpha.getBalls()),this))
	 *check correct echarge
	 *check sign echarge
	 * @post| (this.getAlphas().size() %2 == 0)? this.getECharge()>0: this.getECharge() <0
	 * @post| alpha.getBalls().stream().allMatch(ball -> (ball.getAlphas().size() %2 == 0)? ball.getECharge()>0: ball.getECharge() <0)
	 * check value echarge
	 * @post| (this.getAlphas().size() == 0)? this.getECharge() == 1:this.getAlphas().stream().mapToInt(alphas -> alphas.getBalls().size()).max().equals(OptionalInt.of((int) Math.abs(this.getECharge())))
	 * @post| alpha.getBalls().stream().allMatch(ball-> (ball.getAlphas().size() == 0)? ball.getECharge() == 1:ball.getAlphas().stream().mapToInt(alphas -> alphas.getBalls().size()).max().equals(OptionalInt.of((int) Math.abs(ball.getECharge()))))
	 */
	public void unLink(Alpha alpha) {
		if(this.linkedAlphas.remove(alpha)) {
			alpha.unLink(this);
			alpha.getBalls().forEach((linkedBall)-> linkedBall.calculateECharge());
			this.calculateECharge();
		}

	}
	/**
	 * the max() return an optinalInt class, this means that when invoking getAsInt() we either get the value or a exception. This
	 * exception however occurs when the stream was empty and this only happens when (this.getAlphas().size() ==0)
	 * In this case the getAsInt() is never invoked, so the exception is never thrown
	 * inspects this and its peerobjects/ represetationobjects
	 * @inspects|this
	 * @mutates_properties| this.getECharge()
	 * check sign echarge
	 * @post| (this.getAlphas().size() %2 == 0)? this.getECharge()>0: this.getECharge() <0
	 * check value echarge
	 * @post| (this.getAlphas().size() == 0)? this.getECharge() == 1:this.getAlphas().stream().mapToInt(alpha -> alpha.getBalls().size()).max().equals(OptionalInt.of((int) Math.abs(this.getECharge())))
	 * 
	 */
	private void calculateECharge() {

		// temp is always positive number -> size is always >= 0
		if(this.getAlphas().size() == 0) {
			this.echarge = 1;
		}
		else if(this.getAlphas().size() %2 == 0) {
			this.echarge = this.getAlphas().stream().mapToInt(alpha -> alpha.getBalls().size()).max().getAsInt();
			//assert(this.echarge >0);
		}else {
			this.echarge = -this.getAlphas().stream().mapToInt(alpha -> alpha.getBalls().size()).max().getAsInt();
			//assert(this.echarge <0);
		}

		//assert((this.getAlphas().size() == 0)? this.echarge == 1:this.getAlphas().stream().mapToInt(alpha -> alpha.getBalls().size()).max().equals(OptionalInt.of((int) Math.abs(this.echarge)))
		//		);

	}
	/**
	 * @inspects| this
	 * 
	 */
	public int getECharge() {
		return this.echarge;
	}

	
	
	private boolean One_On_One(Ball other) {
		ArrayList<Alpha> otherAlphas = new ArrayList<Alpha>(other.getAlphas());
		
		boolean result = this.getAlphas().stream().map(alpha ->{
			if(otherAlphas.stream().anyMatch(alphaCheck->
			alpha.equalContentNoBallCheck(alphaCheck)
					)) {
				otherAlphas.remove(otherAlphas.stream().filter(e-> e.equalContentNoBallCheck(alpha)).findAny().get());
				return true;
			}
			else {
				return false;
			}

		}).allMatch(e-> e);
		//assertEquals(otherAlphas.size(),0);
		return result;
	}
	
	/**
	 * @post| (this.getClass() == other.getClass() && this.getCenter().equals(other.getCenter()) && 
	 * 		|	this.getVelocity().equals(other.getVelocity()) && 
	 * 		|		this.getECharge() == other.getECharge() && 
	 * 		|		this.getAlphas().size() == other.getAlphas().size() &&
	 * 		|         old(One_On_One(other)) &&
	 * 		|	((other instanceof SuperChargedBall superBallOther)? 
	 * 		|		((SuperChargedBall)this).getLifetime() == superBallOther.getLifetime()	:true )
	 * 		|		
	 * 		| )? result: !result
	 *  
	 */
	// !this.getAlphas().stream().allMatch(thisAlpha-> other.getAlphas().stream().anyMatch(otherAlpha->
	//thisAlpha.equalContentNoBallCheck(otherAlpha)
	public boolean equalContent(Ball other) {
		if( this.getClass() != other.getClass()) {return false;}
		if( !this.getCenter().equals(other.getCenter())) {return false;}
		if( ! this.getVelocity().equals(other.getVelocity())) {return false;}


		if( this.getECharge() != other.getECharge()) {return false;}
		if( this.getAlphas().size() != other.getAlphas().size() ||
				
				// ensure one-on-one relationship
				!One_On_One(other))

		{return false;}

		if( other instanceof SuperChargedBall superBallOther) {
			if(((SuperChargedBall)this).getLifetime() != superBallOther.getLifetime()) {return false;}
		}
		
		return true;
	}



	/**
	 * @post| (this.getClass() == other.getClass() && this.getCenter().equals(other.getCenter()) && 
	 * 		|	this.getVelocity().equals(other.getVelocity()) && 
	 * 		|		this.getECharge() == other.getECharge() &&
	 * 		|	((other instanceof SuperChargedBall superBallOther)? 
	 * 		|		((SuperChargedBall)this).getLifetime() == superBallOther.getLifetime()	:true )
	 * 		|		
	 * 		| )? result: !result
	 * 		
	 * 
	 */
	public boolean equalContentNoAlphaCheck(Ball other) {
		if( this.getClass() != other.getClass()) {return false;}
		if( !this.getCenter().equals(other.getCenter())) {return false;}
		if( ! this.getVelocity().equals(other.getVelocity())) {return false;}


		if( this.getECharge() != other.getECharge()) {return false;}

		if( other instanceof SuperChargedBall superBallOther) {
			if(((SuperChargedBall)this).getLifetime() != superBallOther.getLifetime()) {return false;}
		}
		return true;
	}
	
	
	/**
	 * Construct a new ball at a given `location`, with a given `velocity`.
	 * 
	 * @pre | location != null
	 * @pre | velocity != null
	 * @post | getLocation().equals(location)
	 * @post | getVelocity().equals(velocity)
	 * @post| getAlphas().size() == 0
	 * @post| getECharge() == 1
	 */
	public Ball(Circle location, Vector velocity) {
		this.location = location;
		this.velocity = velocity;
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
	public abstract void move(Vector v, int elapsedTime);

	/**
	 * Update the BallState after hitting a block at a given location, taking into account whether the block was destroyed by the hit or not.
	 * 
	 * @pre | rect != null
	 * @pre | collidesWith(rect)
	 * 
	 * @mutates_properties| this.getVelocity()
	 */
	public abstract void hitBlock(Rect rect, boolean destroyed);

	/**
	 * Update the BallState after hitting a paddle at a given location.
	 * 
	 * @pre | rect != null
	 * @pre | collidesWith(rect)
	 * @pre | paddleVel != null
	 * 
	 * @mutates_properties| this.getVelocity()
	 */
	public abstract void hitPaddle(Rect rect, Vector paddleVel);

	/**
	 * Update the BallState after hitting a wall at a given location.
	 * 
	 * @pre | rect != null
	 * @pre | collidesWith(rect)
	 * 
	 * @mutates_properties| this.getVelocity()
	 */
	public abstract void hitWall(Rect rect);

	/**
	 * Return the color this ball should be painted in.
	 * 
	 * @post | result != null
	 * @inspects this
	 */
	public abstract Color getColor();

	/**
	 * Return this point's center.
	 * 
	 * @post | getLocation().getCenter().equals(result)
	 * @inspects this
	 */
	public Point getCenter() {
		return getLocation().getCenter();
	}

	/**
	 * Return a clone of this BallState with the given velocity.
	 * 
	 * @inspects this
	 * @creates result
	 * @post | result.getLocation().equals(getLocation())
	 * @post | result.getVelocity().equals(v)
	 */
	public abstract Ball cloneWithVelocity(Vector v);

	/**
	 * Return a clone of this BallState.
	 * 
	 * @inspects this
	 * @creates result
	 * @post | result.getLocation().equals(getLocation())
	 * @post | result.getVelocity().equals(getVelocity())
	 */
	public Ball clone() {
		return cloneWithVelocity(getVelocity());
	}


}

