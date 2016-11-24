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
package jchess.pieces;

import java.util.ArrayList;

import jchess.board.Chessboard;
import jchess.board.Square;
import jchess.game.Player;
import jchess.gui.GUI;

import java.awt.Image;

/**
 * Class to represent a chess pawn bishop Bishop can move across the chessboard
 *
 * |_|_|_|_|_|_|_|X|7 
 * |X|_|_|_|_|_|X|_|6 
 * |_|X|_|_| |X|_|_|5 
 * |_|_|X|_|X|_|_|_|4
 * |_|_|_|B|_|_|_|_|3 
 * |_| |X|_|X|_|_|_|2 
 * |_|X|_|_|_|X|_|_|1 
 * |X|_|_|_|_|_|X|_|0 
 *  0 1 2 3 4 5 6 7
 */
public class Bishop extends Piece {

	public static short value = 3;
	protected static final Image imageWhite = GUI.loadImage("Bishop-W.png");
	protected static final Image imageBlack = GUI.loadImage("Bishop-B.png");

	public Bishop(Chessboard chessboard, Player player) {
		super(chessboard, player); // call initializer of super type: Piece
		this.symbol = "B";
		this.setImage(imageBlack, imageWhite);
	}

	/**
	 * Annotation to superclass Piece changing pawns location
	 * 
	 * @return ArrayList with new position of piece
	 */

	public ArrayList<Square> allMoves(Chessboard chessboard) {
		ArrayList<Square> list = new ArrayList<Square>();
		King myKing = myKing(chessboard);
		int x = this.getSquare().getPozX();
		int y = this.getSquare().getPozY();

		for (int h = x - 1, i = y + 1; !pieceBehaviour.isout(h, i); --h, ++i) // left-up
		{
			if (this.pieceBehaviour.checkPiece(h, i)) // if on this square isn't
														// piece
			{
				if (myKing.willBeSafeWhenMoveOtherPiece(this.getSquare(),
						getChessboard().initial.squares[h][i])) {
					list.add(getChessboard().initial.squares[h][i]);
				}

				if (this.pieceBehaviour.otherOwner(h, i)) {
					break;
				}
			} else {
				break;// we've to break because we cannot go beside other
						// piece!!
			}
		}

		for (int h = x - 1, i = y - 1; !pieceBehaviour.isout(h, i); --h, --i) // left-down
		{
			if (this.pieceBehaviour.checkPiece(h, i)) // if on this square
														// isn't piece
			{
				if (myKing.willBeSafeWhenMoveOtherPiece(this.getSquare(),
						getChessboard().initial.squares[h][i])) {
					list.add(getChessboard().initial.squares[h][i]);
				}

				if (this.pieceBehaviour.otherOwner(h, i)) {
					break;
				}
			} else {
				break;// we've to break because we cannot go beside other
						// piece!!
			}
		}

		for (int h = x + 1, i = y + 1; !pieceBehaviour.isout(h, i); ++h, ++i) // right-up
		{
			if (this.pieceBehaviour.checkPiece(h, i)) // if on this square isn't
														// piece
			{
				if (myKing.willBeSafeWhenMoveOtherPiece(this.getSquare(),
						getChessboard().initial.squares[h][i])) {
					list.add(getChessboard().initial.squares[h][i]);
				}

				if (this.pieceBehaviour.otherOwner(h, i)) {
					break;
				}
			} else {
				break;// we've to break because we cannot go beside other
						// piece!!
			}
		}

		for (int h = x + 1, i = y - 1; !pieceBehaviour.isout(h, i); ++h, --i) // right-down
		{
			if (this.pieceBehaviour.checkPiece(h, i)) // if on this square
														// isn't piece
			{
				if (myKing.willBeSafeWhenMoveOtherPiece(this.getSquare(),
						getChessboard().initial.squares[h][i])) {
					list.add(getChessboard().initial.squares[h][i]);
				}

				if (this.pieceBehaviour.otherOwner(h, i)) {
					break;
				}
			} else {
				break;// we've to break because we cannot go beside other
						// piece!!
			}
		}

		return list;
	}
}
