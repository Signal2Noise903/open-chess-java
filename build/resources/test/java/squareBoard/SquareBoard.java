/*
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Authors:
 * Mateusz Sławomir Lach ( matlak, msl )
 * Damian Marciniak
 */
package main.java.squareBoard;

import java.awt.Point;
import java.util.ArrayList;

import main.java.JChessApp;
import main.java.board.ChessboardDisplay;
import main.java.board.ChessboardLayout;
import main.java.board.IChessboard;
import main.java.board.IMove;
import main.java.board.Square;
import main.java.game.MovesTable;
import main.java.game.MovesTable.castling;
import main.java.movesInSquareBoard.KnightMoves;
import main.java.game.Player;
import main.java.game.Settings;
import main.java.movesInSquareBoard.BishopMoves;
import main.java.movesInSquareBoard.RookMoves;
import main.java.pieces.Bishop;
import main.java.pieces.King;
import main.java.pieces.Knight;
import main.java.pieces.Move;
import main.java.pieces.Pawn;
import main.java.pieces.Piece;
import main.java.pieces.Queen;
import main.java.pieces.Rook;

/**
 * Class to represent a SquareChessboard from 8x8 Squares for a two player Chess game. 
 */
public class SquareBoard implements IChessboard {

	public static final int top = 0;
	public static final int bottom = 7;

	// public Graphics graph;
	ArrayList<Square> moves;
	private Settings settings;
	// -------- for undo ----------
	private Square undo1_sq_begin = null;
	private Square undo1_sq_end = null;
	private Piece undo1_piece_begin = null;
	public Piece undo1_piece_end;
	public Piece ifWasEnPassant;
	public Piece ifWasCastling;
	public boolean breakCastling;
	public MovesTable moves_history;
	// ----------------------------
	// For En passant:
	// |-> Pawn whose in last turn moved two square
	public static Pawn twoSquareMovedPawn = null;
	public static Pawn twoSquareMovedPawn2 = null;

	ChessboardLayout board_layout = new ChessboardLayout("chessboard.png", "sel_square.png", "able_square.png");
	public SquareBoardInitialization initial;
	private SquareBoardDisplay display;

	/**
	 * Chessboard class constructor
	 * 
	 * @param settings
	 *            reference to Settings class object for this chessboard
	 * @param moves_history
	 *            reference to MovesTable class object for this chessboard
	 */
	public SquareBoard(Settings settings, MovesTable moves_history) {
		this.settings = settings;
		initial = new SquareBoardInitialization(settings.upsideDown, this);
		display = new SquareBoardDisplay(null, null, new Point(0, 0), settings.renderLabels, settings.upsideDown, this);
		this.moves_history = moves_history;

	}/*--endOf-Chessboard--*/

	/**
	 * method to get reference to square from given x and y integeres
	 * 
	 * @param x
	 *            x position on chessboard
	 * @param y
	 *            y position on chessboard
	 * @return reference to searched square
	 */
	public Square getSquare(int x, int y) {
		if ((x > this.get_height()) || (y > this.get_widht())) // test if click
																// is out of
																// chessboard
		{
			System.out.println("click out of chessboard.");
			return null;
		}
		if (this.settings.renderLabels) {
			x -= this.display.upDownLabel.getHeight(null);
			y -= this.display.upDownLabel.getHeight(null);
		}
		double square_x = (int) (x / display.square_height);// count which field in X
													// was clicked
		double square_y = (int) (y / display.square_height);// count which field in Y
													// was clicked
		/**
		if (square_x > (int) square_x) // if X is more than X parsed to Integer
		{
			square_x = (int) square_x + 1;// parse to integer and increment
		}
		if (square_y > (int) square_y) // if X is more than X parsed to Integer
		{
			square_y = (int) square_y + 1;// parse to integer and increment
		}
		**/
		// Square newActiveSquare =
		// this.squares[(int)square_x-1][(int)square_y-1];//4test
		System.out.println("square_x: " + square_x + " square_y: " + square_y + " \n"); // 4tests

		try {
			return initial.getSquares()[(int) square_x][(int) square_y];
		} catch (java.lang.ArrayIndexOutOfBoundsException exc) {
			System.out.println("!!Array out of bounds when getting Square with Chessboard.getSquare(int,int) : " + exc);
			return null;
		}
	}

	/**
	 * Method selecting piece in chessboard
	 * 
	 * @param sq
	 *            square to select (when clicked))
	 */
	public void select(Square sq) {
		this.display.activeSquare = sq;
		this.display.active_x_square = sq.getPozX();
		this.display.active_y_square = sq.getPozY();

		// this.draw();//redraw
		System.out.println("active_x: " + this.display.active_x_square + " active_y: " + this.display.active_y_square);// 4tests
		display.repaint();

	}/*--endOf-select--*/

	/**
	 * Method set variables active_x_square & active_y_square to 0 values.
	 */
	public void unselect() {
		this.display.active_x_square = -1;
		this.display.active_y_square = -1;
		this.display.activeSquare = null;
		// this.draw();//redraw
		display.repaint();
	}/*--endOf-unselect--*/

	public int get_widht() {
		return this.get_widht(false);
	}

	public int get_height() {
		return this.get_height(false);
	}

	public int get_widht(boolean includeLables) {
		return this.display.getHeight();
	}/*--endOf-get_widht--*/

	public int get_height(boolean includeLabels) {
		if (this.settings.renderLabels) {
			return board_layout.image.getHeight(null) + display.upDownLabel.getHeight(null);
		}
		return board_layout.image.getHeight(null);
	}/*--endOf-get_height--*/

	public int get_square_height() {
		return (int) this.display.square_height;
	}

	public void move(Square begin, Square end) {
		move(begin, end, true);
	}

	/**
	 * Method to move piece over chessboard
	 * 
	 * @param xFrom
	 *            from which x move piece
	 * @param yFrom
	 *            from which y move piece
	 * @param xTo
	 *            to which x move piece
	 * @param yTo
	 *            to which y move piece
	 */
	public void move(int xFrom, int yFrom, int xTo, int yTo) {

		try {
			Square fromSQ = this.initial.getSquares()[xFrom][yFrom];
			Square toSQ = this.initial.getSquares()[xTo][yTo];
			this.move(fromSQ, toSQ, true);
		} catch (java.lang.IndexOutOfBoundsException exc) {
			System.out.println("error moving piece: " + exc);
		}
	}

	public void move(Square begin, Square end, boolean refresh) {
		this.move(begin, end, refresh, true);
	}

	/**
	 * Method move piece from square to square
	 * 
	 * @param begin
	 *            square from which move piece
	 * @param end
	 *            square where we want to move piece *
	 * @param refresh
	 *            chessboard, default: true
	 */
	public void move(Square begin, Square end, boolean refresh, boolean clearForwardHistory) {

		castling wasCastling = MovesTable.castling.none;
		Piece promotedPiece = null;
		boolean wasEnPassant = false;
		
		if (end.piece != null) {
			end.piece.setSquare(null);
		}

		Square tempBegin = new Square(begin);// 4 moves history
		Square tempEnd = new Square(end); // 4 moves history
		// for undo
		
		undo1_piece_begin = begin.piece;
		undo1_sq_begin = begin;
		undo1_piece_end = end.piece;
		undo1_sq_end = end;
		ifWasEnPassant = null;
		ifWasCastling = null;
		breakCastling = false;
		// ---

		twoSquareMovedPawn2 = twoSquareMovedPawn;

		begin.piece.setSquare(end);// set square of piece to ending
		end.piece = begin.piece;// for ending square set piece from beginin
								// square
		begin.piece = null;// make null piece for begining square

		if (end.piece.getName().equals("King")) {
			if (!((King) end.piece).wasMotion) {
				breakCastling = true;
				((King) end.piece).wasMotion = true;
			}

			// Castling
			if (begin.getPozX() + 2 == end.getPozX()) {
				move(initial.getSquares()[7][begin.getPozY()], initial.getSquares()[end.getPozX() - 1][begin.getPozY()],
						false, false);
				ifWasCastling = end.piece; // for undo
				wasCastling = MovesTable.castling.shortCastling;
				// this.moves_history.addMove(tempBegin, tempEnd,
				// clearForwardHistory, wasCastling, wasEnPassant);
				// return;
			} else if (begin.getPozX() - 2 == end.getPozX()) {
				move(initial.getSquares()[0][begin.getPozY()], initial.getSquares()[end.getPozX() + 1][begin.getPozY()],
						false, false);
				ifWasCastling = end.piece; // for undo
				wasCastling = MovesTable.castling.longCastling;
				// this.moves_history.addMove(tempBegin, tempEnd,
				// clearForwardHistory, wasCastling, wasEnPassant);
				// return;
			}
			// endOf Castling
		} else if (end.piece.getName().equals("Rook")) {
			if (!((Rook) end.piece).wasMotion()) {
				breakCastling = true;
				((Rook) end.piece).setWasMotion(true);
			}
		} else if (end.piece.getName().equals("Pawn")) {
			if (twoSquareMovedPawn != null
					&& initial.getSquares()[end.getPozX()][begin.getPozY()] == twoSquareMovedPawn.getSquare()) // en
																												// passant
			{
				ifWasEnPassant = initial.getSquares()[end.getPozX()][begin.getPozY()].piece; // for
																								// undo

				tempEnd.piece = initial.getSquares()[end.getPozX()][begin.getPozY()].piece; // ugly
																							// hack
																							// -
																							// put
																							// taken
																							// pawn
																							// in
																							// en
																							// passant
																							// plasty
																							// do
																							// end
																							// square

				initial.getSquares()[end.getPozX()][begin.getPozY()].piece = null;
				wasEnPassant = true;
			}

			if (begin.getPozY() - end.getPozY() == 2 || end.getPozY() - begin.getPozY() == 2) // moved
																								// two
																								// square
			{
				breakCastling = true;
				twoSquareMovedPawn = (Pawn) end.piece;
			} else {
				twoSquareMovedPawn = null; // erase last saved move (for En
											// passant)
			}

			if (end.piece.getSquare().getPozY() == 0 || end.piece.getSquare().getPozY() == 7) // promote
																								// Pawn
			{
				if (clearForwardHistory) {
					String color;
					if (end.piece.getPlayer().getColor() == Player.colors.white) {
						color = "W"; // promotionWindow was show with pieces in
										// this color
					} else {
						color = "B";
					}

					String newPiece = JChessApp.getJcv().showPawnPromotionBox(color); // return
																						// name
																						// of
																						// new
																						// piece

					if (newPiece.equals("Queen")) // transform pawn to queen
					{
						ArrayList <IMove> queenMoves = new ArrayList<>();
						queenMoves.add(new RookMoves());
						queenMoves.add(new BishopMoves());
						
						Queen queen = new Queen(this, end.piece.getPlayer(),queenMoves);
						queen.setChessboard(end.piece.getChessboard());
						queen.setPlayer(end.piece.getPlayer());
						queen.setSquare(end.piece.getSquare());
						end.piece = queen;
					} else if (newPiece.equals("Rook")) // transform pawn to
														// rook
					{
						ArrayList <IMove> rookMoves = new ArrayList<>();
						rookMoves.add(new RookMoves());
						Rook rook = new Rook(this, end.piece.getPlayer(), rookMoves);
						rook.setChessboard(end.piece.getChessboard());
						rook.setPlayer(end.piece.getPlayer());
						rook.setSquare(end.piece.getSquare());
						end.piece = rook;
					} else if (newPiece.equals("Bishop")) // transform pawn to
															// bishop
					{	ArrayList <IMove> bishopMoves = new ArrayList<>();
						bishopMoves.add(new BishopMoves());
						Bishop bishop = new Bishop(this, end.piece.getPlayer(), bishopMoves);
						bishop.setChessboard(end.piece.getChessboard());
						bishop.setPlayer(end.piece.getPlayer());
						bishop.setSquare(end.piece.getSquare());
						end.piece = bishop;
					} else // transform pawn to knight
					{	ArrayList <IMove> knightMoves = new ArrayList<>();
						knightMoves.add(new KnightMoves());
						Knight knight = new Knight(this, end.piece.getPlayer(), knightMoves);
						knight.setChessboard(end.piece.getChessboard());
						knight.setPlayer(end.piece.getPlayer());
						knight.setSquare(end.piece.getSquare());
						end.piece = knight;
					}
					promotedPiece = end.piece;
				}
			}
		} else if (!end.piece.getName().equals("Pawn")) {
			twoSquareMovedPawn = null; // erase last saved move (for En passant)
		}
		// }

		if (refresh) {
			this.unselect();// unselect square
			display.repaint();
		}

		if (clearForwardHistory) {
			moves_history.clearMoveForwardStack();
			moves_history.addMove(tempBegin, tempEnd, true, wasCastling, wasEnPassant, promotedPiece);
		} else {
			moves_history.addMove(tempBegin, tempEnd, false, wasCastling, wasEnPassant, promotedPiece);
		}
	}/* endOf-move()- */

	public boolean redo() {
		return redo(true);
	}

	public boolean redo(boolean refresh) {
		if (this.settings.gameType == Settings.gameTypes.local) // redo only for
																// local game
		{
			Move first = moves_history.redo();

			Square from = null;
			Square to = null;

			if (first != null) {
				from = first.getFrom();
				to = first.getTo();

				this.move(this.initial.getSquares()[from.getPozX()][from.getPozY()],
						this.initial.getSquares()[to.getPozX()][to.getPozY()], true, false);
				if (first.getPromotedPiece() != null) {
					Pawn pawn = (Pawn) this.initial.getSquares()[to.getPozX()][to.getPozY()].piece;
					pawn.setSquare(null);

					this.initial.getSquares()[to.getPozX()][to.getPozY()].piece = first.getPromotedPiece();
					Piece promoted = this.initial.getSquares()[to.getPozX()][to.getPozY()].piece;
					promoted.setSquare(this.initial.getSquares()[to.getPozX()][to.getPozY()]);
				}
				return true;
			}

		}
		return false;
	}

	public boolean undo() {
		return undo(true);
	}

	public synchronized boolean undo(boolean refresh) // undo last move
	{
		Move last = moves_history.undo();

		if (last != null && last.getFrom() != null) {
			Square begin = last.getFrom();
			Square end = last.getTo();
			try {
				Piece moved = last.getMovedPiece();
				this.initial.getSquares()[begin.getPozX()][begin.getPozY()].piece = moved;

				moved.setSquare(this.initial.getSquares()[begin.getPozX()][begin.getPozY()]);

				Piece taken = last.getTakenPiece();
				if (last.getCastlingMove() != castling.none) {
					Piece rook = null;
					if (last.getCastlingMove() == castling.shortCastling) {
						rook = this.initial.getSquares()[end.getPozX() - 1][end.getPozY()].piece;
						this.initial.getSquares()[7][begin.getPozY()].piece = rook;
						rook.setSquare(this.initial.getSquares()[7][begin.getPozY()]);
						this.initial.getSquares()[end.getPozX() - 1][end.getPozY()].piece = null;
					} else {
						rook = this.initial.getSquares()[end.getPozX() + 1][end.getPozY()].piece;
						this.initial.getSquares()[0][begin.getPozY()].piece = rook;
						rook.setSquare(this.initial.getSquares()[0][begin.getPozY()]);
						this.initial.getSquares()[end.getPozX() + 1][end.getPozY()].piece = null;
					}
					((King) moved).wasMotion = false;
					((Rook) rook).setWasMotion(false);
					breakCastling = false;
				} else if (moved.getName().equals("Rook")) {
					((Rook) moved).setWasMotion(false);
				} else if (moved.getName().equals("Pawn") && last.wasEnPassant()) {
					Pawn pawn = (Pawn) last.getTakenPiece();
					this.initial.getSquares()[end.getPozX()][begin.getPozY()].piece = pawn;
					pawn.setSquare(this.initial.getSquares()[end.getPozX()][begin.getPozY()]);

				} else if (moved.getName().equals("Pawn") && last.getPromotedPiece() != null) {
					Piece promoted = this.initial.getSquares()[end.getPozX()][end.getPozY()].piece;
					promoted.setSquare(null);
					this.initial.getSquares()[end.getPozX()][end.getPozY()].piece = null;
				}

				// check one more move back for en passant
				Move oneMoveEarlier = moves_history.getLastMoveFromHistory();
				if (oneMoveEarlier != null && oneMoveEarlier.wasPawnTwoFieldsMove()) {
					Piece canBeTakenEnPassant = this.initial.getSquares()[oneMoveEarlier.getTo()
							.getPozX()][oneMoveEarlier.getTo().getPozY()].piece;
					if (canBeTakenEnPassant.getName().equals("Pawn")) {
						this.twoSquareMovedPawn = (Pawn) canBeTakenEnPassant;
					}
				}

				if (taken != null && !last.wasEnPassant()) {
					this.initial.getSquares()[end.getPozX()][end.getPozY()].piece = taken;
					taken.setSquare(this.initial.getSquares()[end.getPozX()][end.getPozY()]);
				} else {
					this.initial.getSquares()[end.getPozX()][end.getPozY()].piece = null;
				}

				if (refresh) {
					this.unselect();// unselect square
					display.repaint();
				}

			} catch (java.lang.ArrayIndexOutOfBoundsException exc) {
				return false;
			} catch (java.lang.NullPointerException exc) {
				return false;
			}

			return true;
		} else {
			return false;
		}
	}

	public ChessboardDisplay getDisplay() {
		return display;
	}

	public void setPieces( Player[] players) {
		initial.setPieces(players[0], players[1]);
	}

	public Square[][] getSquares() {
		return initial.getSquares();
	}

	public SquareBoardInitialization getInitial() {
		return initial;
	}

	public Square getActiveSquare() {
		return display.activeSquare;
	}

	public void setActiveSquare(Square sq) {
		display.activeSquare = sq;
	}

	public King getKing(Player player) {
		if (player.getColor().equals(Player.colors.white)) {
			return initial.kingWhite;
		} else if (player.getColor().equals(Player.colors.black)) {
			return initial.kingBlack;
		}
		return null;
	}

	public Piece getTwoSquareMovedPawn() {
		return twoSquareMovedPawn;
	}
}