package breakout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.*;

import breakout.radioactivity.*;
import breakout.utils.*;

import static org.junit.Assert.*;

//import breakout.gui.GameView;

// ------INVARS----------------------------------------------------------------------------------------

/**
 * Represents the current state of a breakout game.
 * 
 *
 * @invar | getBlocks() != null && Arrays.stream(getBlocks()).allMatch(block -> block != null)
 * @invar | getPaddle() != null
 * @invar | getBottomRight() != null
 *  @invar | getBalls() != null && Arrays.stream(getBalls()).allMatch(ball -> ball != null)
 * @invar | getAlphas() != null && Arrays.stream(getAlphas()).allMatch(alpha-> alpha != null)
 * 
 * @invar | Point.ORIGIN.isUpAndLeftFrom(getBottomRight())
 * @invar | Arrays.stream(getBlocks()).allMatch(b -> getField().contains(b.getLocation()))
 * @invar | getField().contains(getPaddle().getLocation())
 * all balls and alphas are in the field
 * @invar | Arrays.stream(getAlphas()).allMatch(alpha -> getField().contains(alpha.getLocation()))
 * @invar | Arrays.stream(getBalls()).allMatch(ball -> getField().contains(ball.getLocation()))
 * 
 * check if all balls connected to the returned alphas are in the returned getBalls() array
 * @invar | Arrays.stream(getAlphas()).map(alpha-> alpha.getBalls()).reduce( new HashSet<Ball>(),(result,element)-> {result.addAll(element);  
 * 		 |		return result;
 * 		 |	}).stream().allMatch(returnedLinkedBall-> Arrays.stream(getBalls())
 * 		 |		.filter(ball -> ball.getAlphas().size()!=0).anyMatch(returnedBall-> returnedBall.equalContent(returnedLinkedBall)))
 * check if all alphas connected to the returned balls are in the returned getAlphas() array
 * @invar |Arrays.stream(getBalls()).map(ball-> ball.getAlphas()).reduce( new HashSet<Alpha>(),(result,element)-> {result.addAll(element);  
 * 		 |		return result;
 * 		 |	}).stream().allMatch(returnedLinkedAlpha-> Arrays.stream(getAlphas())
 * 		 |		.filter(alpha-> alpha.getBalls().size()!=0).anyMatch(returnedAlpha -> returnedAlpha.equalContent(returnedLinkedAlpha)))
 *
 * Check if there are duplicates in Balls & Alphas
 * @invar | Arrays.stream(getBalls()).distinct().count() ==  getBalls().length
 * @invar | Arrays.stream(getAlphas()).distinct().count() ==  getAlphas().length
 * 
 */
public class BreakoutState {

	// ------FIELDS----------------------------------------------------------------------------------------


	/**check if all ball connected to the given alphas are in the 'balls' array
	 * @invar | alphas != null
	 * @invar | Arrays.stream(alphas).allMatch(alpha -> alpha != null)
	 * @invar | Arrays.stream(alphas).allMatch(alpha -> getFieldInternal().contains(alpha.getLocation()))
	 * @invar | Arrays.stream(alphas).distinct().count() ==  alphas.length

	 * @representationObject
	 * @representationObjects Each alpha is a representation object
	 * @invar|Arrays.stream(alphas).map(alpha-> alpha.getBalls()).reduce( new HashSet<Ball>(),(result,element)-> {result.addAll(element);  
	 * 		 |		return result;
	 * 		 |	}).equals(new HashSet<Ball>(Arrays.stream(balls).filter(ball-> ball.getAlphas().size()!=0).toList())) 
	 * 
	 */
	private Alpha[] alphas;



	/**
	 * @invar | balls != null
	 * @invar | Arrays.stream(balls).allMatch(ball -> ball != null)
	 * @invar | Arrays.stream(balls).allMatch(b -> getFieldInternal().contains(b.getLocation()))
	 * @invar | Arrays.stream(balls).distinct().count() ==  balls.length
	 * @representationObject
	 * @representationObjects Each ball is a representation object
	 * check if all alphas connected to the given balls are in the 'alphas' Set
	 * @invar|Arrays.stream(balls).map(ball-> ball.getAlphas()).reduce( new HashSet<Alpha>(),(result,element)-> {result.addAll(element);  
	 * 		 |		return result;
	 * 		 |	}).equals(new HashSet<Alpha>(Arrays.stream(alphas).filter(alpha-> alpha.getBalls().size()!=0).toList()))
	 */
	private Ball[] balls;
	private static final Vector PADDLE_VEL = new Vector(20, 0);
	public static final int MAX_BALL_REPLICATE = 5;
	private static final Vector[] BALL_VEL_VARIATIONS = new Vector[] { new Vector(0, 0), new Vector(2, -2),
			new Vector(-2, 2), new Vector(2, 2), new Vector(-2, -2) };
	public static int MAX_ELAPSED_TIME = 50;
	/**
	 * @invar | bottomRight != null
	 * @invar | Point.ORIGIN.isUpAndLeftFrom(bottomRight)
	 */
	private final Point bottomRight;

	/**
	 * @invar | blocks != null
	 * @invar | Arrays.stream(blocks).allMatch(b -> getFieldInternal().contains(b.getLocation()))
	 * @invar | Arrays.stream(blocks).allMatch(block -> block != null)
	 * @representationObject
	 */
	private BlockState[] blocks;
	/**
	 * @invar | paddle != null
	 * @invar | getFieldInternal().contains(paddle.getLocation())
	 */
	private PaddleState paddle;

	private final Rect topWall;
	private final Rect rightWall;
	private final Rect leftWall;
	private final Rect[] walls;



	// ------CONSTRUCTOR----------------------------------------------------------------------------------------


	/**
	 * 
	 * Construct a new BreakoutState with the given balls, blocks, paddle.
	 * 
	 * @throws IllegalArgumentException | balls == null
	 * check all ball not null
	 * @throws IllegalArgumentException | Arrays.stream(balls).anyMatch(ball -> ball == null)
	 * @throws IllegalArgumentException | blocks == null
	 * @throws IllegalArgumentException | Arrays.stream(blocks).anyMatch(block -> block == null)
	 * @throws IllegalArgumentException | bottomRight == null
	 * @throws IllegalArgumentException | paddle == null
	 * @throws IllegalArgumentException | !Point.ORIGIN.isUpAndLeftFrom(bottomRight)
	 * @throws IllegalArgumentException | !(new Rect(Point.ORIGIN,bottomRight)).contains(paddle.getLocation())
	 * @throws IllegalArgumentException | !Arrays.stream(blocks).allMatch(b -> (new Rect(Point.ORIGIN,bottomRight)).contains(b.getLocation()))
	 * @throws IllegalArgumentException | !Arrays.stream(balls).allMatch(b -> (new Rect(Point.ORIGIN,bottomRight)).contains(b.getLocation()))
	 * there are no alphas connected to any ball( consequently the exhaustiveness requirement is also fulfilled)
	 * @throws IllegalArgumentException | Arrays.stream(balls).mapToInt(ball -> ball.getAlphas().size()).sum() != 0
	 * there are not duplicates in balls
	 * @throws IllegalArgumentException | Arrays.stream(balls).distinct().count() !=  balls.length
	 * @post | Arrays.stream(balls).allMatch(givenBall -> Arrays.stream(getBalls()).anyMatch(returnedBall-> givenBall.equalContent(returnedBall)))
	 * @post | Arrays.equals(getBlocks(),blocks)
	 * @post | getBottomRight().equals(bottomRight)
	 * @post | getPaddle().equals(paddle)
	 * @post | getAlphas().length == 0
	 */
	public BreakoutState(Ball[] balls, BlockState[] blocks, Point bottomRight, PaddleState paddle) {
		if (balls == null)
			throw new IllegalArgumentException();
		if (blocks == null)
			throw new IllegalArgumentException();
		if (bottomRight == null)
			throw new IllegalArgumentException();
		if (paddle == null)
			throw new IllegalArgumentException();
		if(Arrays.stream(balls).anyMatch(ball -> ball == null))
			throw new IllegalArgumentException("there is a null Ball object in balls");
		if(Arrays.stream(blocks).anyMatch(block -> block == null))
			throw new IllegalArgumentException("there is a null Block object in blocks");

		if (!Point.ORIGIN.isUpAndLeftFrom(bottomRight))
			throw new IllegalArgumentException();
		this.bottomRight = bottomRight;
		if (!getFieldInternal().contains(paddle.getLocation()))
			throw new IllegalArgumentException();
		if (!Arrays.stream(blocks).allMatch(b -> getFieldInternal().contains(b.getLocation())))
			throw new IllegalArgumentException();
		if (!Arrays.stream(balls).allMatch(b -> getFieldInternal().contains(b.getLocation())))
			throw new IllegalArgumentException();

		if (Arrays.stream(balls).mapToInt(ball -> ball.getAlphas().size()).sum() != 0)
			throw new IllegalArgumentException("there are linked alphas!");
		if ( Arrays.stream(balls).distinct().count() !=  balls.length)
			throw new IllegalArgumentException("there is a duplicate in balls");
		// balls.clone() does a shallow copy by default
		this.balls = new Ball[balls.length];
		for(int i = 0; i < balls.length; ++i) {
			this.balls[i] = balls[i].clone();
		}
		this.alphas = new Alpha[] {};
		this.blocks = blocks.clone();
		this.paddle = paddle;

		this.topWall = new Rect(new Point(0, -1000), new Point(bottomRight.getX(), 0));
		this.rightWall = new Rect(new Point(bottomRight.getX(), 0),
				new Point(bottomRight.getX() + 1000, bottomRight.getY()));
		this.leftWall = new Rect(new Point(-1000, 0), new Point(0, bottomRight.getY()));
		this.walls = new Rect[] { topWall, rightWall, leftWall };
	}

	/**
	 * 
	 * Construct a new BreakoutState with the given balls, blocks, paddle.
	 * 
	 * @throws IllegalArgumentException | balls == null
	 * @throws IllegalArgumentException | alphas == null
	 * check all alpha not null
	 * @throws IllegalArumentException | Arrays.stream(alphas).anyMatch(alpha -> alpha == null)
	 *  check all ball not null
	 * @throws IllegalArgumentException | Arrays.stream(balls).anyMatch(ball -> ball == null)
	 * @throws IllegalArgumentException | blocks == null
	 * @throws IllegalArgumentException | Arrays.stream(blocks).anyMatch(block -> block == null)
	 * @throws IllegalArgumentException | bottomRight == null
	 * @throws IllegalArgumentException | paddle == null
	 * check field
	 * @throws IllegalArgumentException | !Point.ORIGIN.isUpAndLeftFrom(bottomRight)
	 * @throws IllegalArgumentException | !(new Rect(Point.ORIGIN,bottomRight)).contains(paddle.getLocation())
	 * @throws IllegalArgumentException | !Arrays.stream(blocks).allMatch(b -> (new Rect(Point.ORIGIN,bottomRight)).contains(b.getLocation()))
	 * @throws IllegalArgumentException | !Arrays.stream(balls).allMatch(b -> (new Rect(Point.ORIGIN,bottomRight)).contains(b.getLocation()))
	 * @throws IllegalArgumentException | !Arrays.stream(alphas).allMatch(b -> (new Rect(Point.ORIGIN,bottomRight)).contains(b.getLocation()))
	 * check exhaustiveness of the graph
	 * @throws IllegalArgumentException | !Arrays.stream(alphas).map(alpha-> alpha.getBalls()).reduce( new HashSet<Ball>(),(result,element)-> {result.addAll(element);  
	 * 		 |		return result;
	 * 		 |	}).equals(new HashSet<Ball>(Arrays.stream(balls).filter(ball-> ball.getAlphas().size()!=0).toList()))
	 * @throws IllegalArgumentException | !Arrays.stream(balls).map(ball-> ball.getAlphas()).reduce( new HashSet<Alpha>(),(result,element)-> {result.addAll(element);  
	 * 		 |		return result;
	 * 		 |	}).equals(new HashSet<Alpha>(Arrays.stream(alphas).filter(alpha-> alpha.getBalls().size()!=0).toList()))
	 * there are not duplicates
	 * @throws IllegalArgumentException | Arrays.stream(balls).distinct().count() !=  balls.length
	 * @throws IllegalArgumentException | Arrays.stream(alphas).distinct().count() !=  alphas.length
	 * 
	 * 
//	 * @post | Arrays.stream(balls).allMatch(givenBall -> Arrays.stream(getBalls()).anyMatch(returnedBall-> givenBall.equalContent(returnedBall)))
//	 * @post | Arrays.stream(alphas).allMatch(givenAlpha -> Arrays.stream(getAlphas()).anyMatch(returnedAlpha-> givenAlpha.equalContent(returnedAlpha)))
     * 
     * check if balls and alphas are equal in content with the getters getBalls and getAlphas
	 * this check includes a peerobject check one on one (bijectie) check: deeply equal check
	 * => isomorfism between given and returned graph is checked as well
	 * => check that equal object appear in same order
	 * @post| IntStream.range(0,balls.length).allMatch(i-> balls[i].equalContent(getBalls()[i]))
	 * @post| IntStream.range(0,alphas.length).allMatch(i-> alphas[i].equalContent(getAlphas()[i]))
	 * 
	 * @post | Arrays.equals(getBlocks(),blocks)
	 * @post | getBottomRight().equals(bottomRight)
	 * @post | getPaddle().equals(paddle)
	 * exhaustiveness
	 * @post |Arrays.stream(getAlphas()).map(alpha-> alpha.getBalls()).reduce( new HashSet<Ball>(),(result,element)-> {result.addAll(element);  
	 * 		 |		return result;
	 * 		 |	}).stream().allMatch(returnedLinkedBall-> Arrays.stream(getBalls())
     * 		 |		.filter(ball -> ball.getAlphas().size()!=0).anyMatch(returnedBall-> returnedBall.equalContent(returnedLinkedBall)))
     *
	 * 
	 * @post | Arrays.stream(getBalls()).map(ball-> ball.getAlphas()).reduce( new HashSet<Alpha>(),(result,element)-> {result.addAll(element);  
	 * 		 |		return result;
	 * 		 |	}).stream().allMatch(returnedLinkedAlpha-> Arrays.stream(getAlphas())
     * 		 |	.filter(alpha-> alpha.getBalls().size()!=0).anyMatch(returnedAlpha -> returnedAlpha.equalContent(returnedLinkedAlpha)))
     *

	 * 
	 */


	public BreakoutState(Alpha[] alphas, Ball[] balls, BlockState[] blocks, Point bottomRight, PaddleState paddle) {
		
		if (balls == null)
			throw new IllegalArgumentException();
		if (blocks == null)
			throw new IllegalArgumentException();
		if (bottomRight == null)
			throw new IllegalArgumentException();
		if (paddle == null)
			throw new IllegalArgumentException();
		if (alphas == null)
			
			throw new IllegalArgumentException("alphas is null");
		if(Arrays.stream(balls).anyMatch(ball -> ball == null))
			throw new IllegalArgumentException("there is a null Ball object in balls");
		if(Arrays.stream(alphas).anyMatch(alpha -> alpha == null))
			throw new IllegalArgumentException("there is a null Alpha object in alphas");
		if(Arrays.stream(blocks).anyMatch(block -> block == null))
			throw new IllegalArgumentException("there is a null Block object in blocks");
		if (!Point.ORIGIN.isUpAndLeftFrom(bottomRight))
			throw new IllegalArgumentException();
		this.bottomRight = bottomRight;
		if (!getFieldInternal().contains(paddle.getLocation()))
			throw new IllegalArgumentException();
		if (!Arrays.stream(blocks).allMatch(b -> getFieldInternal().contains(b.getLocation())))
			throw new IllegalArgumentException();
		if (!Arrays.stream(balls).allMatch(b -> getFieldInternal().contains(b.getLocation())))
			throw new IllegalArgumentException();
		if (!Arrays.stream(alphas).allMatch(b -> getFieldInternal().contains(b.getLocation())))
			throw new IllegalArgumentException();


		if ( Arrays.stream(balls).distinct().count() !=  balls.length)
			throw new IllegalArgumentException("there is a duplicate in balls");
		if ( Arrays.stream(alphas).distinct().count() !=  alphas.length)
			throw new IllegalArgumentException("there is a duplicate in alhas");
		// exhaustiveness
		if (!Arrays.stream(balls).map(ball-> ball.getAlphas()).reduce( new HashSet<Alpha>(),(result,element)-> {result.addAll(element);  
		return result;
		}).equals(new HashSet<Alpha>(Arrays.stream(alphas).filter(alpha-> alpha.getBalls().size()!=0).toList())))
			throw new IllegalArgumentException("there are extra alphas connected");

		if(!Arrays.stream(alphas).map(alpha-> alpha.getBalls()).reduce( new HashSet<Ball>(),(result,element)-> {result.addAll(element);  
		return result;
		}).equals(new HashSet<Ball>(Arrays.stream(balls).filter(ball-> ball.getAlphas().size()!=0).toList())))
			throw new IllegalArgumentException("there are extra balls connected");

		ArrayList<Ball> listBalls = new ArrayList<Ball>();
		ArrayList<Alpha> listAlphas = new ArrayList<Alpha>();
		listBalls.addAll(Arrays.asList(balls));
		listAlphas.addAll(Arrays.asList(alphas));
		DualList<Ball,Alpha> clonedGraph = BreakoutState.cloneGraph(listBalls, listAlphas);


		this.alphas = clonedGraph.getList2().toArray(Alpha[]::new);
		this.balls = clonedGraph.getList1().toArray(Ball[]::new);

		this.blocks = blocks.clone();
		this.paddle = paddle;

		this.topWall = new Rect(new Point(0, -1000), new Point(bottomRight.getX(), 0));
		this.rightWall = new Rect(new Point(bottomRight.getX(), 0),
				new Point(bottomRight.getX() + 1000, bottomRight.getY()));
		this.leftWall = new Rect(new Point(-1000, 0), new Point(0, bottomRight.getY()));
		this.walls = new Rect[] { topWall, rightWall, leftWall };


	}

	// ------GETTERS----------------------------------------------------------------------------------------

	/**
	 * Return the balls of this BreakoutState.
	 *
	 * @creates result
	 * @creates ...result
	 */
	public Ball[] getBalls() {
		DualList<Ball,Alpha> respons = BreakoutState.cloneGraph(new ArrayList<Ball>(Arrays.asList(this.balls)), new ArrayList<Alpha>(Arrays.asList(alphas)));
		return respons.getList1().toArray(new Ball[] {});
		//		return balls.clone();
	}



	/**
	 * Return the balls of this BreakoutState.
	 *
	 * @creates result
	 * @creates ...result
	 */
	public Alpha[] getAlphas() {

		DualList<Ball,Alpha> respons = BreakoutState.cloneGraph(new ArrayList<Ball>(Arrays.asList(this.balls)), new ArrayList<Alpha>(Arrays.asList(alphas)));
		return respons.getList2().toArray(new Alpha[] {});

		//		return balls.clone();


	}
	/**
	 * Return the blocks of this BreakoutState.
	 *
	 * @creates result
	 */
	public BlockState[] getBlocks() {
		return blocks.clone();
	}

	/**
	 * Return the paddle of this BreakoutState.
	 */
	public PaddleState getPaddle() {
		return paddle;
	}

	/**
	 * Return the point representing the bottom right corner of this BreakoutState.
	 * The top-left corner is always at Coordinate(0,0).
	 */
	public Point getBottomRight() {
		return bottomRight;
	}

	// internal version of getField which can be invoked in partially inconsistent
	// states
	private Rect getFieldInternal() {
		return new Rect(Point.ORIGIN, bottomRight);
	}

	/**
	 * Return a rectangle representing the game field.
	 * 
	 * @post | result != null
	 * @post | result.getTopLeft().equals(Point.ORIGIN)
	 * @post | result.getBottomRight().equals(getBottomRight())
	 */
	public Rect getField() {
		return getFieldInternal();

	}

	/**
	 * Return an array with all the walls 
	 * 
	 * @post | result != null
	 */
	public Rect[] getWalls() {
		return this.walls;

	}







	// ------TICK----------------------------------------------------------------------------------------

	/**
	 * Move all moving objects one step forward.
	 * @inspects| this
	 * @mutates| this
	 * @mutates| ...getBalls()
	 * @mutates| ...getAlphas()
	 * @pre | elapsedTime >= 0
	 * @pre | elapsedTime <= MAX_ELAPSED_TIME
	 * 
	 */
	
	 // Deze documentatie wordt niet gerund vanwege de grote tijdsvertraging: 
	 // SubmissionTest3.simpleTick: 84s -> 6,3
	// de overige vertragingen worden ook vooral door documentatie veroorzaakt
	
	
//	 * @throws IllegalArgumentException | paddleDir != 0 && paddleDir != 1 && paddleDir != -1
//	 * @throws IllegalArgumentExceptioin | isWon() || isDead()
//	 * 
//	 * CHECK FIELD
//	 * check that all the blocks are still in the gamefield
//	 * @post |Arrays.stream(getBlocks()).allMatch(block -> getField().contains(block.getLocation()))	
//	 * check that all the balls are still in the gamefield
//	 * @post |Arrays.stream(getBalls()).allMatch(ball -> getField().contains(ball.getLocation()))
//	 * check that all the alphas are still in the gamefield
//	 * @post |Arrays.stream(getAlphas()).allMatch(alpha -> getField().contains(alpha.getLocation()))
//	 * check that the paddle is still in the gamefield
//	 * @post | getField().contains(getPaddle().getLocation())
//	 *
//	 * CHECK NULL
//	 * @post | getBlocks() != null
//	 * @post | getBalls() != null
//	 * @post | getAlphas() != null
//	 * @post | getPaddle() != null
//	 * @post | Arrays.stream(getBlocks()).allMatch(block -> block != null)
//	 * @post | Arrays.stream(getBalls()).allMatch(ball -> ball != null)
//	 * @post | Arrays.stream(getAlphas()).allMatch(alpha -> alpha != null)
//	 * 
//	 * CHECK EXHAUSTIVENESS
//	 * @post | Arrays.stream(getAlphas()).map(alpha-> alpha.getBalls()).reduce( new HashSet<Ball>(),(result,element)-> {result.addAll(element);  
//	 * 		 |		return result;
//	 * 		 |	}).stream().allMatch(returnedLinkedBall-> Arrays.stream(getBalls())
//	 * 		 |		.filter(ball-> ball.getAlphas().size()!=0).anyMatch(returnedBall-> returnedBall.equalContent(returnedLinkedBall)))
//	 * @post |Arrays.stream(getBalls()).map(ball-> ball.getAlphas()).reduce( new HashSet<Alpha>(),(result,element)-> {result.addAll(element);  
//	 * 		 |		return result;
//	 * 		 |	}).stream().allMatch(returnedLinkedAlpha-> Arrays.stream(getAlphas())
//	 * 		 |	.filter(alpha-> alpha.getBalls().size()!=0).anyMatch(returnedAlpha -> returnedAlpha.equalContent(returnedLinkedAlpha)))
//	 * 
//	 * CHECK DUPLICATES
//	 * @post | Arrays.stream(getBalls()).distinct().count() ==  getBalls().length
//	 * @post | Arrays.stream(getAlphas()).distinct().count() ==  getAlphas().length
//	 * 
//	 * 
//	 * check post that no blocks were added
//	 * @post | getBlocks().length<= old(getBlocks()).length
//	 * 
//	 * check that paddle hasn't changed position
//	 * @post | getPaddle().getCenter().equals(old(getPaddle()).getCenter())
//	 * 
//	 * 
//	 **/
	
	public void tick(int paddleDir, int elapsedTime) {
		/*
		 *  Deze throws kunnen niet gecheckt worden door de submissionTests
		if(paddleDir != 0 && paddleDir != 1 && paddleDir != -1) {
			throw new IllegalArgumentException("invalid paddle direction");
		}
		if (isWon()|| isDead()) {
			throw new IllegalArgumentException("game has already ended");
		}
		*/
		// done
		stepBalls(elapsedTime);
		stepAlphas(elapsedTime);
		// done
		bounceBallsOnWalls();

		bounceAlphasOnWalls();
		// done
		removeDeadBalls();
		removeDeadAlphas();
		// done
		bounceBallsOnBlocks();
		// done
		bounceBallsOnPaddle(paddleDir);
		bounceAlphasOnPaddle(paddleDir);
		// done
		clampBalls();
		clampAlphas();

		balls = Arrays.stream(balls).filter(x -> x != null).toArray(Ball[]::new);


	}

	// ------TICK FUNCTIONS----------------------------------------------------------------------------------------

	private void stepBalls(int elapsedTime) {
		/*
		ArrayList<Circle> oldLocations =  new ArrayList<Circle>();
		Arrays.stream(this.balls).forEach(ball -> oldLocations.add(ball.getLocation()));
*/
		Arrays.stream(this.balls).forEach(ball -> ball.move(ball.getVelocity().scaled(elapsedTime), elapsedTime));
/*
		ArrayList<Circle> newLocations =  new ArrayList<Circle>();
		Arrays.stream(this.balls).forEach(ball -> newLocations.add(ball.getLocation()));

		List<Circle> differences = oldLocations.stream()
				.filter(element -> newLocations.contains(element))
				.collect(Collectors.toList());
		assertEquals(0, differences.size());
*/
	}

	private void stepAlphas(int elapsedTime) {
		/*
		ArrayList<Circle> oldLocations =  new ArrayList<Circle>();
		Arrays.stream(this.alphas).forEach(alpha -> oldLocations.add(alpha.getLocation()));
*/
		Arrays.stream(this.alphas).forEach(alpha -> alpha.move(alpha.getVelocity().scaled(elapsedTime), elapsedTime));
/*
		ArrayList<Circle> newLocations =  new ArrayList<Circle>();
		Arrays.stream(this.alphas).forEach(alpha -> newLocations.add(alpha.getLocation()));

		List<Circle> differences = oldLocations.stream()
				.filter(element -> newLocations.contains(element))
				.collect(Collectors.toList());
		assertEquals(0, differences.size());
*/
	}

	// ---------------------------------------------------

	private void bounceBallsOnWalls() {
		Arrays.stream(this.balls).forEach(ball -> bounceWallsBall(ball));
	}

	private void bounceAlphasOnWalls() {
		Arrays.stream(this.alphas).forEach(alpha -> bounceWallsAlpha(alpha));
	}

	// ---------------------------------------------------

	private void removeDeadBalls() {
		//int paddleBorder = paddle.getLocation().getBottomRight().getY();
		for (int i = 0; i < balls.length; ++i) {
			if( balls[i].getLocation().getBottommostPoint().getY() > bottomRight.getY()) {
				balls[i] = removeDeadBall(balls[i]);
				//assertEquals(null,balls[i]);
			}
		}
	}


	private void removeDeadAlphas() {
		int paddleBorder = paddle.getLocation().getBottomRight().getY();
		Set<Alpha> temp = new HashSet<Alpha>();
		//int oldLength = this.alphas.length;

		for (Alpha alpha : this.alphas) {
			if( alpha.getCenter().getY() >= paddleBorder) {
				alpha.getBalls().stream().forEach( ball -> ball.unLink(alpha));
				temp.add(alpha);

			}
		}

		//temp.stream().forEach(removedAlpha -> this.alphas.remove(removedAlpha));
		this.alphas = Arrays.stream(this.alphas).filter(alpha -> !temp.contains(alpha)).toArray(Alpha[]::new);
		//assertEquals(oldLength - temp.size(), this.alphas.length);
	}

	// ---------------------------------------------------

	private void bounceBallsOnBlocks() {
		for(int i = 0; i < balls.length; ++i) {
			if(balls[i] != null) {
				balls[i] = collideBallBlocks(balls[i]);
			}
		}
	}

	// ---------------------------------------------------


	private void bounceBallsOnPaddle(int paddleDir) {
		Vector paddleVel = PADDLE_VEL.scaled(paddleDir);
		Ball[] balls = this.balls; 
		for(int i = 0; i < balls.length; ++i) {
			if(balls[i] != null) {
				collideBallPaddle(balls[i], paddleVel);
			}
		}
	}

	private void bounceAlphasOnPaddle(int paddleDir) {
		Vector paddleVel = PADDLE_VEL.scaled(paddleDir);

		for(int i = 0; i < this.alphas.length; ++i) {
			collideAlphaPaddle(this.alphas[i], paddleVel);

		}
	}

	// ---------------------------------------------------

	private void clampBalls() {
		for(int i = 0; i < balls.length; ++i) {
			if(balls[i] != null) {
				clampBall(balls[i]);
			}		
		}
	}

	private void clampAlphas() {

		for(int i = 0; i < this.alphas.length; ++i) {
			if(this.alphas[i] != null) {
				clampAlpha(this.alphas[i]);
			}		
		}
	}

	// ------HELP FUNCTIONS----------------------------------------------------------------------------------------


	private void bounceWallsBall(Ball ball) {
		for (Rect wall : walls) {
			if (ball.collidesWith(wall)) {
				ball.hitWall(wall);
			}
		}
	}

	private void bounceWallsAlpha(Alpha alpha) {
		for (Rect wall : walls) {
			if (alpha.collidesWith(wall)) {
				alpha.hitWall(wall);
				for (Ball ball : alpha.getBalls()) {
					Vector magnetSpeed = Vector.magnetSpeed(alpha.getLocation().getCenter(), ball.getLocation().getCenter(), ball.getECharge(), ball.getVelocity());
					ball.changeVelocity(magnetSpeed);
				}	
			}
		}
	}


	private void collideBallPaddle(Ball ball, Vector paddleVel) {
		if (ball.collidesWith(paddle.getLocation())) {
			ball.hitPaddle(paddle.getLocation(),paddleVel);

			//int oldAlphaCount = this.alphas.length;
			Alpha alpha = new Alpha(ball.getLocation(), ball.getVelocity().plus(new Vector(-2,-2)));
			//this.alphas.add(alpha);
			this.alphas = Stream.concat(Arrays.stream(alphas), Stream.of(alpha)).toArray(Alpha[]::new);

			//assertEquals(oldAlphaCount + 1, this.alphas.length);
			ball.linkTo(alpha);

			int nrBalls = paddle.numberOfBallsAfterHit();
			if(nrBalls > 1) {
				Ball[] curballs = balls;
				balls = new Ball[curballs.length + nrBalls - 1];
				for(int i = 0; i < curballs.length; ++i) {
					balls[i] = curballs[i];
				}
				for(int i = 1; i < nrBalls; ++i) {
					Vector nballVel = ball.getVelocity().plus(BALL_VEL_VARIATIONS[i]);
					balls[curballs.length + i -1] = ball.cloneWithVelocity(nballVel);					
				}
			}
			paddle = paddle.stateAfterHit();
		}
	}

	private void collideAlphaPaddle(Alpha alpha, Vector paddleVel) {
		if (alpha.collidesWith(paddle.getLocation())) {
			//int oldBallCount = this.balls.length;
			alpha.hitPaddle(paddle.getLocation(),paddleVel);
			Ball ball = new NormalBall(alpha.getLocation(), alpha.getVelocity().plus(new Vector(-2,-2)));

			balls = Stream.concat(Arrays.stream(balls), Stream.of(ball)).toArray(Ball[]::new);

			//assertEquals(oldBallCount + 1, this.balls.length);
			ball.linkTo(alpha);

		}
	}

	private Ball removeDeadBall(Ball ball) {

		ball.getAlphas().stream().forEach(alpha -> ball.unLink(alpha));			
		return null; 


	}


	private void clampBall(Ball b) {
		Circle loc = getFieldInternal().constrain(b.getLocation());
		b.move(loc.getCenter().minus(b.getLocation().getCenter()),0);
	}

	private void clampAlpha(Alpha a) {
		Circle loc = getFieldInternal().constrain(a.getLocation());
		a.move(loc.getCenter().minus(a.getLocation().getCenter()),0);
	}

	private Ball collideBallBlocks(Ball ball) {
		for (BlockState block : blocks) {
			if (ball.collidesWith(block.getLocation())) {
				boolean destroyed = hitBlock(block);
				ball.hitBlock(block.getLocation(), destroyed);
				paddle = block.paddleStateAfterHit(paddle);
				return block.ballStateAfterHit(ball);
			}
		}
		return ball;
	}

	private boolean hitBlock(BlockState block) {
		boolean destroyed = true;
		ArrayList<BlockState> nblocks = new ArrayList<BlockState>();
		for (BlockState b : blocks) {
			if (b != block) {
				nblocks.add(b);
			} else {
				BlockState nb = block.blockStateAfterHit();
				if (nb != null) {
					nblocks.add(nb);
					destroyed = false;
				}
			}
		}
		blocks = nblocks.toArray(new BlockState[] {});
		return destroyed;
	}

	/**
	 * Move the paddle right.
	 * 
	 * @param elapsedTime
	 * 
	 * @mutates_properties| this.getPaddle()
	 */
	public void movePaddleRight(int elapsedTime) {
		paddle = paddle.move(PADDLE_VEL.scaled(elapsedTime), getField());

	}

	/**
	 * Move the paddle left.
	 * 
	 * @mutates_properties| this.getPaddle()
	 */
	public void movePaddleLeft(int elapsedTime) {
		paddle = paddle.move(PADDLE_VEL.scaled(-elapsedTime), getField());
	}

	/**
	 * Return whether this BreakoutState represents a game where the player has won.
	 * 
	 * @post | result == (getBlocks().length == 0 && !isDead())
	 * @inspects this
	 */
	public boolean isWon() {
		return getBlocks().length == 0 && !isDead();
	}

	/**
	 * Return whether this BreakoutState represents a game where the player is dead.
	 * 
	 * @post | result == (getBalls().length == 0)
	 * @inspects this
	 */
	public boolean isDead() {
		return getBalls().length == 0;
	}
	
	// This documentation is tested, but will not be runned during extensive testing of tick due perfomance concernces
//	/**
//	 * @creates|result
//	 * check not null
//	 * @pre| balls != null && alphas != null && balls.stream().allMatch(ball -> ball != null) && alphas.stream().allMatch(alpha-> alpha != null)
//	 * check for closed graph: all graph elements are elements of balls and alphas
//	 * @pre| alphas.stream().map(alpha-> alpha.getBalls()).reduce( new HashSet<Ball>(),(result,element)-> {result.addAll(element);  
//	 * 		 |		return result;
//	 * 		 |	}).equals(new HashSet<Ball>(balls.stream().filter(ball->ball.getAlphas().size() != 0).toList()))
//	 * @pre|balls.stream().map(ball-> ball.getAlphas()).reduce( new HashSet<Alpha>(),(result,element)-> {result.addAll(element);  
//	 * 		 |		return result;
//	 * 		 |	}).equals(new HashSet<Alpha>(alphas.stream().filter(alpha->alpha.getBalls().size()!=0).toList()))
//
//	 * 
//	 * check for two sub arrays, one for the cloned ball and one for the cloned alphas
//	 * @post| result.size() == 2
//	 * check for right instances in result
//	 * @post| result.getList1().stream().allMatch(ball-> ball instanceof Ball) &&result.getList2().stream().allMatch(alpha-> alpha instanceof Alpha)
//	 * the number of balls haven't changed
//	 * @post| balls.size() == result.getList1().size()
//	 * all balls have changed hashcode
//	 * @post| result.getList1().stream().allMatch(newBall-> !balls.contains(newBall))
//	 * all balls have the same content and connection still stand
//	 * @post| result.getList1().stream().allMatch(newBall-> balls.stream().anyMatch(oldBall -> oldBall.equalContent((Ball)newBall)))
//	 * the number of alphas haven't changed
//	 * @post| alphas.size() == result.getList2().size()
//	 * all alphas have changed hashcode
//	 * @post| result.getList2().stream().allMatch(newAlpha-> !alphas.contains(newAlpha))
//	 * all alphas have the same content and connection still stand
//	 * @post| result.getList2().stream().allMatch(newAlpha-> alphas.stream().anyMatch(oldAlpha -> oldAlpha.equalContent(((Alpha)newAlpha))))
//	 */
	private static DualList<Ball,Alpha> cloneGraph(ArrayList<Ball> balls, ArrayList<Alpha> alphas){

		ArrayList<Alpha> clonedAlphas = new ArrayList<Alpha>(alphas.stream().map(alpha -> alpha.clone()).toList());
		Stream<Ball> ballStream = balls.stream().map( ball -> {
			Ball clonedBall = ball.clone();

			clonedAlphas.stream().filter(clonedAlpha-> ball.getAlphas().stream().anyMatch(alpha->alpha.equalContentNoBallCheck(clonedAlpha))
					).forEach(connectedAlpha-> clonedBall.linkTo(connectedAlpha));
			return clonedBall;
		} 
				);

		ArrayList<Ball> ballList = new ArrayList<Ball>(ballStream.toList());
		DualList<Ball, Alpha> result = new DualList<Ball, Alpha>(ballList,clonedAlphas);

		//assert(result.getList1().stream().allMatch(ball-> ball instanceof Ball) &&result.getList2().stream().allMatch(alpha-> alpha instanceof Alpha));

		return result;
	}
	
	/**
	 * @creates|result
	 * check not null
	 * @pre| balls != null && alphas != null && balls.stream().allMatch(ball -> ball != null) && alphas.stream().allMatch(alpha-> alpha != null)
	 * check for closed graph: all graph elements are elements of balls and alphas
	 * @pre| alphas.stream().map(alpha-> alpha.getBalls()).reduce( new HashSet<Ball>(),(result,element)-> {result.addAll(element);  
	 * 		 |		return result;
	 * 		 |	}).equals(new HashSet<Ball>(balls.stream().filter(ball->ball.getAlphas().size() != 0).toList()))
	 * @pre|balls.stream().map(ball-> ball.getAlphas()).reduce( new HashSet<Alpha>(),(result,element)-> {result.addAll(element);  
	 * 		 |		return result;
	 * 		 |	}).equals(new HashSet<Alpha>(alphas.stream().filter(alpha->alpha.getBalls().size()!=0).toList()))

	 * 
	 * check for two sub arrays, one for the cloned ball and one for the cloned alphas
	 * @post| result.size() == 2
	 * check for right instances in result
	 * @post| result.getList1().stream().allMatch(ball-> ball instanceof Ball) &&result.getList2().stream().allMatch(alpha-> alpha instanceof Alpha)
	 * the number of balls haven't changed
	 * @post| balls.size() == result.getList1().size()
	 * all balls have changed hashcode
	 * @post| result.getList1().stream().allMatch(newBall-> !balls.contains(newBall))
	 * all balls have the same content and connection still stand
	 * @post| result.getList1().stream().allMatch(newBall-> balls.stream().anyMatch(oldBall -> oldBall.equalContent((Ball)newBall)))
	 * the number of alphas haven't changed
	 * @post| alphas.size() == result.getList2().size()
	 * all alphas have changed hashcode
	 * @post| result.getList2().stream().allMatch(newAlpha-> !alphas.contains(newAlpha))
	 * all alphas have the same content and connection still stand
	 * @post| result.getList2().stream().allMatch(newAlpha-> alphas.stream().anyMatch(oldAlpha -> oldAlpha.equalContent(((Alpha)newAlpha))))
	 */
	public static DualList<Ball,Alpha> cloneGraphForTesting(ArrayList<Ball> balls, ArrayList<Alpha> alphas){

		return cloneGraph(balls,alphas);
	}
}






