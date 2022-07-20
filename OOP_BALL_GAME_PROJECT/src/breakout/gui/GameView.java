package breakout.gui;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import breakout.BlockState;
import breakout.BreakoutFacade;
import breakout.BreakoutState;
import breakout.PaddleState;
import breakout.radioactivity.Alpha;
import breakout.radioactivity.Ball;
import breakout.utils.Point;
import breakout.utils.Rect;
import breakout.utils.Vector;

@SuppressWarnings("serial")
public class GameView extends JPanel {
	public static final int ballMoveDelayMillis = 20;

	public BreakoutState breakoutState;
	private Timer ballTimer;
	private boolean leftKeyDown = false;
	private boolean rightKeyDown = false;

	long prevTimestamp = 0;
	
	private BreakoutFacade facade;

	private void gameChanged() {
		repaint(10);
	}

	private void startMovingBalls() {
		ballTimer = new Timer(ballMoveDelayMillis, actionEvent -> {
			long timestamp = System.currentTimeMillis();
			moveBalls(timestamp);
		});
		ballTimer.start();
	}

	/**
	 * Create a new GameView for playing breakout starting from a given
	 * breakoutState.
	 * 
	 * @param breakoutState initial state for the game.
	 */
	public GameView(BreakoutState breakoutState) {
		this.breakoutState = breakoutState;
		this.facade = new BreakoutFacade();

		setBackground(Color.black);

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_RIGHT -> {
					rightKeyDown = true;
					break;
				}
				case KeyEvent.VK_LEFT -> {
					leftKeyDown = true;
					break;
				}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_RIGHT -> {
					rightKeyDown = false;
					break;
				}
				case KeyEvent.VK_LEFT -> {
					leftKeyDown = false;
					break;
				}
				}
			}
		});
		startMovingBalls();
	}

	private void moveBalls(long timestamp) {
		if (prevTimestamp != 0) {
			int elapsedTime = (int) (timestamp - prevTimestamp);
			// very high elapsed times (for example during debugging) are annoying.
			elapsedTime = Math.min(elapsedTime, BreakoutState.MAX_ELAPSED_TIME);

			int curPaddleDir = 0;
			if (leftKeyDown && !rightKeyDown) {
				facade.movePaddleLeft(breakoutState, elapsedTime);
				curPaddleDir = -1;
			}
			if (!leftKeyDown && rightKeyDown) {
				facade.movePaddleRight(breakoutState, elapsedTime);
				curPaddleDir = 1;
			}
			facade.tick(breakoutState, curPaddleDir, elapsedTime);
			if (facade.isDead(breakoutState)) {
				JOptionPane.showMessageDialog(this, "Game over :-(");
				System.exit(0);
			}
			if (facade.isWon(breakoutState)) {
				JOptionPane.showMessageDialog(this, "Gewonnen!");
				System.exit(0);
			}
			gameChanged();
		}
		prevTimestamp = timestamp;
	}

	@Override
	public Dimension getPreferredSize() {
		Point size = toGUICoord(
				facade.getBottomRight(breakoutState).plus(new Vector(200, 200)));
				//breakoutState.getBottomRight().plus(new Vector(200, 200)));
		return new Dimension(size.getX(), size.getY());
	}

	@Override
	public boolean isFocusable() {
		return true;
	}

	// Convert point in the game coordinate system to the GUI coordinate system.
	private Point toGUICoord(Point loc) {
		return new Point(loc.getX() / 50, loc.getY() / 50).plus(new Vector(5, 5));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Point botRight = toGUICoord(
				facade.getBottomRight(breakoutState));
		g.setColor(Color.black);
		g.drawRect(0, 0, botRight.getX(), botRight.getY());

		paintBlocks(g);
		paintBalls(g);
		paintAlphas(g); //squished circles in principle.
		paintLinks(g);
		paintPaddle(g);
		
		// domi: this fixes a visual latency bug on my system...
		Toolkit.getDefaultToolkit().sync();
	}

	private void paintPaddle(Graphics g) {
		// paddle
		PaddleState paddle = facade.getPaddle(breakoutState);
		Rect loc = facade.getLocation(paddle);
		Color c = facade.getColor(paddle);
		Point tl = loc.getTopLeft();
		Point br = loc.getBottomRight();
		g.setColor(c);
		paintPaddle(g, tl, br);
	}

	private void paintPaddle(Graphics g, Point tlg, Point brg) {
		Point tl = toGUICoord(tlg);
		Point br = toGUICoord(brg);
		g.fillRect(tl.getX(), tl.getY(), br.getX() - tl.getX(), br.getY() - tl.getY());
	}

	private void paintBalls(Graphics g) {
		// ball
		for (Ball ball : facade.getBalls(breakoutState)) {
			Point center = facade.getCenter(ball);
			int diam = facade.getDiameter(ball);
			int radius = diam/2;
			Point tl = center.plus(new Vector(-radius,-radius));
			Point br = center.plus(new Vector(radius,radius));
			Color color = facade.getColor(ball);
			paintBall(g, color, tl, br);
		}
	}
	
	private void paintBall(Graphics g, Color color, Point tlg, Point brg) {
		g.setColor(color);
		Point tl = toGUICoord(tlg);
		Point br = toGUICoord(brg);
		g.fillOval(tl.getX(), tl.getY(), br.getX() - tl.getX(), br.getY() - tl.getY());
	}
	
	private void paintAlphas(Graphics g) {
		for (Alpha alpha : facade.getAlphas(breakoutState)) {
			Point center = facade.getCenter(alpha);
			int diam = facade.getDiameter(alpha);
			int radius = diam/2;
			Point tl = center.plus(new Vector(-radius,-radius / 2)); //alphas are squished ovals for now
			Color color = facade.getColor(alpha);
			paintAlpha(g, color, tl, diam, radius);
			
		}
	}
	
	private void paintAlpha(Graphics g, Color color, Point tlg , int width, int height) {
		g.setColor(color);
		Point tl = toGUICoord(tlg);
		g.fillOval(tl.getX(), tl.getY(), width/50 , height/50);
	}
	
	private void paintLinks(Graphics g) {
		for (Ball ball : facade.getBalls(breakoutState) ) { //deep copy
			for (Alpha alpha : facade.getAlphas(ball)) { //shallow cop
				Point start = toGUICoord(facade.getCenter(ball));
				Point end = toGUICoord(facade.getCenter(alpha));
				g.setColor(Color.red);
				g.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
			}
		}
	}

	

	private void paintBlock(Graphics g, Point tlg, Point brg) {
		Point tl = toGUICoord(tlg);
		Point br = toGUICoord(brg);
		g.fillRect(tl.getX(), tl.getY(), br.getX() - tl.getX(), br.getY() - tl.getY());
	}

	private void paintBlocks(Graphics g) {
		// blocks
		for (BlockState block : facade.getBlocks(breakoutState)) {
			g.setColor(facade.getColor(block));
			Rect loc = facade.getLocation(block);
			Point tl = loc.getTopLeft();
			Point br = loc.getBottomRight();
			paintBlock(g, tl, br);
		}
	}

}
