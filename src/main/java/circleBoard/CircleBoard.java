package main.java.circleBoard;

import java.awt.Point;
import main.java.board.ChessboardDisplay;
import main.java.board.ChessboardLayout;
import main.java.board.IChessboard;
import main.java.board.Square;
import main.java.game.MovesTable;
import main.java.game.Player;
import main.java.game.Settings;
import main.java.pieces.King;
import main.java.pieces.Pawn;

/**
 * Class to represent a Circle Chessboard for a three player Chess game.
 * It contains 24 x 6 Squares 
 */

public class CircleBoard implements IChessboard {

	public static final int top = 0;
	public static final int bottom = 7;

	public MovesTable moves_history;

	ChessboardLayout board_layout = new ChessboardLayout("circle_chessboard.png", "sel_circle.png", "able_circle.png");
	public CircleBoardInitialization initial;
	private CircleBoardDisplay display;

	public CircleBoard(Settings settings, MovesTable moves_history) {

		settings.renderLabels = false;
		initial = new CircleBoardInitialization(this);
		display = new CircleBoardDisplay(null, null, new Point(0, 0), settings.renderLabels, settings.upsideDown, this);
		this.moves_history = moves_history;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Use transformation of x and y to polar coordinates to obtain the x and y index of the Square. 
	 * The angle is calculated based on the corresponding quadrant
	 */
	
	public Square getSquare(int x, int y) {

		if (x > 2 * getRadius() || y > 2 * getRadius()) // test if click is out
														// of chessboard
		{
			System.out.println("click out of chessboard.");
			return null;
		}

		int cx = getRadius(), cy = getRadius(), hi = get_square_height();
		double ri = Math.sqrt(Math.pow((cx - x), 2) + Math.pow((cy - y), 2));

		double ai = 0;
		// Calculate the angle depending on the quadrant
		if (x > cx && y < cy) {
			ai = Math.toDegrees(Math.asin((x - cx) / ri));
		} else if (x > cx && y > cy) {
			ai = 90 + Math.toDegrees(Math.acos((x - cx) / ri));
		} else if (x < cx && y > cy) {
			ai = 180 + Math.toDegrees(Math.asin((cx - x) / ri));
		} else if (x < cx && y < cy) {
			ai = 270 + Math.toDegrees(Math.acos((cx - x) / ri));
		}

		double square_x = (ai / 15);

		double square_y = (cy - ri) / hi;
		
		Square result;
		try {
			result = initial.squares[(int) square_x][(int) square_y];
			return result;

		} catch (java.lang.ArrayIndexOutOfBoundsException exc) {
			System.out.println("!!Array out of bounds when getting Square with Chessboard.getSquare(int,int) : " + exc);
			return null;
		}
	}

	public void select(Square sq) {
		this.display.activeSquare = sq;
		this.display.active_x_square = sq.getPozX();
		this.display.active_y_square = sq.getPozY();
		System.out.println("active_x: " + this.display.active_x_square + " active_y: " + this.display.active_y_square);
		display.repaint();
	}

	public void unselect() {
		this.display.active_x_square = -1;
		this.display.active_y_square = -1;
		this.display.activeSquare = null;
		display.repaint();
	}

	public void setPieces(Player[] players) {

		initial.setPieces(players);

	}

	public int get_height() {

		return board_layout.image.getHeight(null);
	}

	public Square[][] getSquares() {
		return initial.squares;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * If the piece is a pawn checks if it has passed the center so that the regular move is changed from forward to backward in the y axis. 
	 * 
	 */
	public void move(Square begin, Square end) {
		// Check if pawn passed the center
		if (begin.piece instanceof Pawn) {
			Pawn movedPawn = (Pawn) begin.piece;
			if (movedPawn.getSquare().getPozY() == 5 && end.getPozY() == 5)
				movedPawn.passedCenter = true;
		}

		begin.piece.setSquare(end);// set square of piece to ending
		end.piece = begin.piece;// for ending square set piece from beginning
								// square

		begin.piece = null;// make null piece for beginning square
		this.unselect();// unselect square
		display.repaint();

	}
	/**
	 * {@inheritDoc}
	 * 
	 * Not implemented 
	 * 
	 */
	@Override
	public boolean undo() {
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * {@inheritDoc}
	 * 
	 * Not implemented 
	 *  
	 */
	@Override
	public boolean redo() {
		// TODO Auto-generated method stub
		return false;
	}

	public Square getActiveSquare() {
		return display.activeSquare;
	}

	public King getKing(Player player) {
		if (player.getColor().equals(Player.colors.white)) {
			return initial.kingWhite;
		} else if (player.getColor().equals(Player.colors.black)) {
			return initial.kingBlack;
		} else if (player.getColor().equals(Player.colors.blue)) {
			return initial.kingBlue;
		}
		return null;
	}

	public ChessboardDisplay getDisplay() {
		return display;
	}

	/**
	 * Method calculating the square height of the circle board, based on the actual radius.
	 * 
	 * @return the square height of the CircleBoard
	 *  
	 */
	public int get_square_height() {
		return (getRadius() - getRadius() / 3) / 6;
	}
	/**
	 * Method calculating the radius of the circle board, based on the board layout height.
	 * 
	 * @return the radius of the CircleBoard
	 *  
	 */
	public int getRadius() {
		return board_layout.image.getHeight(null) / 2;
	}

}
