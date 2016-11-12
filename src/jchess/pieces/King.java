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

/**
 * Class to represent a chess pawn king. King is the most important
 * piece for the game. Loose of king is the and of game.
 * When king is in danger by the opponent then it's a Checked, and when have
 * no other escape then stay on a square "in danger" by the opponent
 * then it's a CheckedMate, and the game is over.
 *
 *       |_|_|_|_|_|_|_|_|7
        |_|_|_|_|_|_|_|_|6
        |_|_|_|_|_|_|_|_|5
        |_|_|X|X|X|_|_|_|4
        |_|_|X|K|X|_|_|_|3
        |_|_|X|X|X|_|_|_|2
        |_|_|_|_|_|_|_|_|1
        |_|_|_|_|_|_|_|_|0
        0 1 2 3 4 5 6 7
 */
import java.util.ArrayList;

import jchess.board.Chessboard;
import jchess.board.Square;
import jchess.game.Player;
import jchess.gui.GUI;

import java.awt.Image;

public class King extends Piece
{

    public boolean wasMotion = false;//maybe change to: 'wasMotioned'
    //public boolean checked     = false;
    public static short value = 99;
    private static final Image imageWhite = GUI.loadImage("King-W.png");
    private static final Image imageBlack = GUI.loadImage("King-B.png");

    public King(Chessboard chessboard, Player player)
    {
        super(chessboard, player);
        //this.setImages("King-W.png", "King-B.png");
        this.symbol = "K";
        this.setImage(imageBlack, imageWhite);
        //this.image = imageWhite;
    }

    

    /**
     * Annotation to superclass Piece changing pawns location
     * @return  ArrayList with new possition of piece
     */
    @Override
    public ArrayList<Square> allMoves()
    {
        ArrayList<Square> list = new ArrayList<Square>();
        Square sq;
        Square sq1;
        for (int i = this.getSquare().getPozX() - 1; i <= this.getSquare().getPozX() + 1; i++)
        {
            for (int y = this.getSquare().getPozY() - 1; y <= this.getSquare().getPozY() + 1; y++)
            {
                if (!this.isout(i, y))
                {//out of bounds protection
                    sq = this.getChessboard().squares[i][y];
                    if (this.getSquare() == sq)
                    {//if we're checking square on which is King
                        continue;
                    }
                    if (this.checkPiece(i, y))
                    {//if square is empty
                        if (isSafe(sq))
                        {
                            list.add(sq);
                        }
                    }
                }
            }
        }

        if (!this.wasMotion && !this.isChecked())
        {//check if king was not moved before


            if (getChessboard().squares[0][this.getSquare().getPozY()].piece != null
                    && getChessboard().squares[0][this.getSquare().getPozY()].piece.getName().equals("Rook"))
            {
                boolean canCastling = true;

                Rook rook = (Rook) getChessboard().squares[0][this.getSquare().getPozY()].piece;
                if (!rook.isWasMotion())
                {
                    for (int i = this.getSquare().getPozX() - 1; i > 0; i--)
                    {//go left
                        if (getChessboard().squares[i][this.getSquare().getPozY()].piece != null)
                        {
                            canCastling = false;
                            break;
                        }
                    }
                    sq = this.getChessboard().squares[this.getSquare().getPozX() - 2][this.getSquare().getPozY()];
                    sq1 = this.getChessboard().squares[this.getSquare().getPozX() - 1][this.getSquare().getPozY()];
                    if (canCastling && this.isSafe(sq) && this.isSafe(sq1))
                    { //can do castling when none of Sq,sq1 is checked
                        list.add(sq);
                    }
                }
            }
            if (getChessboard().squares[7][this.getSquare().getPozY()].piece != null
                    && getChessboard().squares[7][this.getSquare().getPozY()].piece.getName().equals("Rook"))
            {
                boolean canCastling = true;
                Rook rook = (Rook) getChessboard().squares[7][this.getSquare().getPozY()].piece;
                if (!rook.isWasMotion())
                {//if king was not moves before and is not checked
                    for (int i = this.getSquare().getPozX() + 1; i < 7; i++)
                    {//go right
                        if (getChessboard().squares[i][this.getSquare().getPozY()].piece != null)
                        {//if square is not empty
                            canCastling = false;//cannot castling
                            break; // exit
                        }
                    }
                    sq = this.getChessboard().squares[this.getSquare().getPozX() + 2][this.getSquare().getPozY()];
                    sq1 = this.getChessboard().squares[this.getSquare().getPozX() + 1][this.getSquare().getPozY()];
                    if (canCastling && this.isSafe(sq) && this.isSafe(sq1))
                    {//can do castling when none of Sq,sq1 is checked
                        list.add(sq);
                    }
                }
            }
        }
        return list;
    }

    /** Method to check is the king is checked
     *  @return bool true if king is not save, else returns false
     */
    public boolean isChecked()
    {
        return !isSafe(this.getSquare());
    }

    /** Method to check is the king is checked or stalemated
     *  @return int 0 if nothing, 1 if checkmate, else returns 2
     */
    public int isCheckmatedOrStalemated()
    {
        /*
         *returns: 0-nothing, 1-checkmate, 2-stalemate
         */
        if (this.allMoves().size() == 0)
        {
            for (int i = 0; i < 8; ++i)
            {
                for (int j = 0; j < 8; ++j)
                {
                    if (getChessboard().squares[i][j].piece != null
                            && getChessboard().squares[i][j].piece.getPlayer() == this.getPlayer()
                            && getChessboard().squares[i][j].piece.allMoves().size() != 0)
                    {
                        return 0;
                    }
                }
            }

            if (this.isChecked())
            {
                return 1;
            }
            else
            {
                return 2;
            }
        }
        else
        {
            return 0;
        }
    }

    /** Method to check is the king is checked by an opponent
     * @param s Squere where is a king
     * @return bool true if king is save, else returns false
     */
    private boolean isSafe(Square s) //A bit confusing code.
    {
        // Rook & Queen
        for (int i = s.getPozY() + 1; i <= 7; ++i) //up
        {
            if (this.getChessboard().squares[s.getPozX()][i].piece == null || this.getChessboard().squares[s.getPozX()][i].piece == this) //if on this sqhuare isn't piece
            {
                continue;
            }
            else if (this.getChessboard().squares[s.getPozX()][i].piece.getPlayer() != this.getPlayer()) //if isn't our piece
            {
                if (this.getChessboard().squares[s.getPozX()][i].piece.getName().equals("Rook")
                        || this.getChessboard().squares[s.getPozX()][i].piece.getName().equals("Queen"))
                {
                    return false;
                }
                else
                {
                    break;
                }
            }
            else
            {
                break;
            }
        }

        for (int i = s.getPozY() - 1; i >= 0; --i) //down
        {
            if (this.getChessboard().squares[s.getPozX()][i].piece == null || this.getChessboard().squares[s.getPozX()][i].piece == this) //if on this sqhuare isn't piece
            {
                continue;
            }
            else if (this.getChessboard().squares[s.getPozX()][i].piece.getPlayer() != this.getPlayer()) //if isn't our piece
            {
                if (this.getChessboard().squares[s.getPozX()][i].piece.getName().equals("Rook")
                        || this.getChessboard().squares[s.getPozX()][i].piece.getName().equals("Queen"))
                {
                    return false;
                }
                else
                {
                    break;
                }
            }
            else
            {
                break;
            }
        }

        for (int i = s.getPozX() - 1; i >= 0; --i) //left
        {
            if (this.getChessboard().squares[i][s.getPozY()].piece == null || this.getChessboard().squares[i][s.getPozY()].piece == this) //if on this sqhuare isn't piece
            {
                continue;
            }
            else if (this.getChessboard().squares[i][s.getPozY()].piece.getPlayer() != this.getPlayer()) //if isn't our piece
            {
                if (this.getChessboard().squares[i][s.getPozY()].piece.getName().equals("Rook")
                        || this.getChessboard().squares[i][s.getPozY()].piece.getName().equals("Queen"))
                {
                    return false;
                }
                else
                {
                    break;
                }
            }
            else
            {
                break;
            }
        }

        for (int i = s.getPozX() + 1; i <= 7; ++i) //right
        {
            if (this.getChessboard().squares[i][s.getPozY()].piece == null || this.getChessboard().squares[i][s.getPozY()].piece == this) //if on this sqhuare isn't piece
            {
                continue;
            }
            else if (this.getChessboard().squares[i][s.getPozY()].piece.getPlayer() != this.getPlayer()) //if isn't our piece
            {
                if (this.getChessboard().squares[i][s.getPozY()].piece.getName().equals("Rook")
                        || this.getChessboard().squares[i][s.getPozY()].piece.getName().equals("Queen"))
                {
                    return false;
                }
                else
                {
                    break;
                }
            }
            else
            {
                break;
            }
        }

        // Bishop & Queen
        for (int h = s.getPozX() - 1, i = s.getPozY() + 1; !isout(h, i); --h, ++i) //left-up
        {
            if (this.getChessboard().squares[h][i].piece == null || this.getChessboard().squares[h][i].piece == this) //if on this sqhuare isn't piece
            {
                continue;
            }
            else if (this.getChessboard().squares[h][i].piece.getPlayer() != this.getPlayer()) //if isn't our piece
            {
                if (this.getChessboard().squares[h][i].piece.getName().equals("Bishop")
                        || this.getChessboard().squares[h][i].piece.getName().equals("Queen"))
                {
                    return false;
                }
                else
                {
                    break;
                }
            }
            else
            {
                break;
            }
        }

        for (int h = s.getPozX() - 1, i = s.getPozY() - 1; !isout(h, i); --h, --i) //left-down
        {
            if (this.getChessboard().squares[h][i].piece == null || this.getChessboard().squares[h][i].piece == this) //if on this sqhuare isn't piece
            {
                continue;
            }
            else if (this.getChessboard().squares[h][i].piece.getPlayer() != this.getPlayer()) //if isn't our piece
            {
                if (this.getChessboard().squares[h][i].piece.getName().equals("Bishop")
                        || this.getChessboard().squares[h][i].piece.getName().equals("Queen"))
                {
                    return false;
                }
                else
                {
                    break;
                }
            }
            else
            {
                break;
            }
        }

        for (int h = s.getPozX() + 1, i = s.getPozY() + 1; !isout(h, i); ++h, ++i) //right-up
        {
            if (this.getChessboard().squares[h][i].piece == null || this.getChessboard().squares[h][i].piece == this) //if on this sqhuare isn't piece
            {
                continue;
            }
            else if (this.getChessboard().squares[h][i].piece.getPlayer() != this.getPlayer()) //if isn't our piece
            {
                if (this.getChessboard().squares[h][i].piece.getName().equals("Bishop")
                        || this.getChessboard().squares[h][i].piece.getName().equals("Queen"))
                {
                    return false;
                }
                else
                {
                    break;
                }
            }
            else
            {
                break;
            }
        }

        for (int h = s.getPozX() + 1, i = s.getPozY() - 1; !isout(h, i); ++h, --i) //right-down
        {
            if (this.getChessboard().squares[h][i].piece == null || this.getChessboard().squares[h][i].piece == this) //if on this sqhuare isn't piece
            {
                continue;
            }
            else if (this.getChessboard().squares[h][i].piece.getPlayer() != this.getPlayer()) //if isn't our piece
            {
                if (this.getChessboard().squares[h][i].piece.getName().equals("Bishop")
                        || this.getChessboard().squares[h][i].piece.getName().equals("Queen"))
                {
                    return false;
                }
                else
                {
                    break;
                }
            }
            else
            {
                break;
            }
        }

        // Knight
        int newX, newY;

        //1
        newX = s.getPozX() - 2;
        newY = s.getPozY() + 1;

        if (!isout(newX, newY))
        {
            if (this.getChessboard().squares[newX][newY].piece == null) //if on this sqhuare isn't piece
            {
            }
            else if (this.getChessboard().squares[newX][newY].piece.getPlayer() == this.getPlayer()) //if is our piece
            {
            }
            else if (this.getChessboard().squares[newX][newY].piece.getName().equals("Knight"))
            {
                return false;
            }
        }

        //2
        newX = s.getPozX() - 1;
        newY = s.getPozY() + 2;

        if (!isout(newX, newY))
        {
            if (this.getChessboard().squares[newX][newY].piece == null) //if on this sqhuare isn't piece
            {
            }
            else if (this.getChessboard().squares[newX][newY].piece.getPlayer() == this.getPlayer()) //if is our piece
            {
            }
            else if (this.getChessboard().squares[newX][newY].piece.getName().equals("Knight"))
            {
                return false;
            }
        }

        //3
        newX = s.getPozX() + 1;
        newY = s.getPozY() + 2;

        if (!isout(newX, newY))
        {
            if (this.getChessboard().squares[newX][newY].piece == null) //if on this sqhuare isn't piece
            {
            }
            else if (this.getChessboard().squares[newX][newY].piece.getPlayer() == this.getPlayer()) //if is our piece
            {
            }
            else if (this.getChessboard().squares[newX][newY].piece.getName().equals("Knight"))
            {
                return false;
            }
        }

        //4
        newX = s.getPozX() + 2;
        newY = s.getPozY() + 1;

        if (!isout(newX, newY))
        {
            if (this.getChessboard().squares[newX][newY].piece == null) //if on this sqhuare isn't piece
            {
            }
            else if (this.getChessboard().squares[newX][newY].piece.getPlayer() == this.getPlayer()) //if is our piece
            {
            }
            else if (this.getChessboard().squares[newX][newY].piece.getName().equals("Knight"))
            {
                return false;
            }
        }

        //5
        newX = s.getPozX() + 2;
        newY = s.getPozY() - 1;

        if (!isout(newX, newY))
        {
            if (this.getChessboard().squares[newX][newY].piece == null) //if on this sqhuare isn't piece
            {
            }
            else if (this.getChessboard().squares[newX][newY].piece.getPlayer() == this.getPlayer()) //if is our piece
            {
            }
            else if (this.getChessboard().squares[newX][newY].piece.getName().equals("Knight"))
            {
                return false;
            }
        }

        //6
        newX = s.getPozX() + 1;
        newY = s.getPozY() - 2;

        if (!isout(newX, newY))
        {
            if (this.getChessboard().squares[newX][newY].piece == null) //if on this sqhuare isn't piece
            {
            }
            else if (this.getChessboard().squares[newX][newY].piece.getPlayer() == this.getPlayer()) //if is our piece
            {
            }
            else if (this.getChessboard().squares[newX][newY].piece.getName().equals("Knight"))
            {
                return false;
            }
        }

        //7
        newX = s.getPozX() - 1;
        newY = s.getPozY() - 2;

        if (!isout(newX, newY))
        {
            if (this.getChessboard().squares[newX][newY].piece == null) //if on this sqhuare isn't piece
            {
            }
            else if (this.getChessboard().squares[newX][newY].piece.getPlayer() == this.getPlayer()) //if is our piece
            {
            }
            else if (this.getChessboard().squares[newX][newY].piece.getName().equals("Knight"))
            {
                return false;
            }
        }

        //8
        newX = s.getPozX() - 2;
        newY = s.getPozY() - 1;

        if (!isout(newX, newY))
        {
            if (this.getChessboard().squares[newX][newY].piece == null) //if on this sqhuare isn't piece
            {
            }
            else if (this.getChessboard().squares[newX][newY].piece.getPlayer() == this.getPlayer()) //if is our piece
            {
            }
            else if (this.getChessboard().squares[newX][newY].piece.getName().equals("Knight"))
            {
                return false;
            }
        }

        // King
        King otherKing;
        if (this == getChessboard().kingWhite)
        {
            otherKing = getChessboard().kingBlack;
        }
        else
        {
            otherKing = getChessboard().kingWhite;
        }

        if (s.getPozX() <= otherKing.getSquare().getPozX() + 1
                && s.getPozX() >= otherKing.getSquare().getPozX() - 1
                && s.getPozY() <= otherKing.getSquare().getPozY() + 1
                && s.getPozY() >= otherKing.getSquare().getPozY() - 1)
        {
            return false;
        }

        // Pawn
        if (this.getPlayer().isGoDown()) //check if player "go" down or up
        {//System.out.println("go down");
            newX = s.getPozX() - 1;
            newY = s.getPozY() + 1;
            if (!isout(newX, newY))
            {
                if (this.getChessboard().squares[newX][newY].piece == null) //if on this sqhuare isn't piece
                {
                }
                else if (this.getChessboard().squares[newX][newY].piece.getPlayer() == this.getPlayer()) //if is our piece
                {
                }
                else if (this.getChessboard().squares[newX][newY].piece.getName().equals("Pawn"))
                {
                    return false;
                }
            }
            newX = s.getPozX() + 1;
            if (!isout(newX, newY))
            {
                if (this.getChessboard().squares[newX][newY].piece == null) //if on this sqhuare isn't piece
                {
                }
                else if (this.getChessboard().squares[newX][newY].piece.getPlayer() == this.getPlayer()) //if is our piece
                {
                }
                else if (this.getChessboard().squares[newX][newY].piece.getName().equals("Pawn"))
                {
                    return false;
                }
            }
        }
        else
        {//System.out.println("go up");
            newX = s.getPozX() - 1;
            newY = s.getPozY() - 1;
            if (!isout(newX, newY))
            {
                if (this.getChessboard().squares[newX][newY].piece == null) //if on this sqhuare isn't piece
                {
                }
                else if (this.getChessboard().squares[newX][newY].piece.getPlayer() == this.getPlayer()) //if is our piece
                {
                }
                else if (this.getChessboard().squares[newX][newY].piece.getName().equals("Pawn"))
                {
                    return false;
                }
            }
            newX = s.getPozX() + 1;
            if (!isout(newX, newY))
            {
                if (this.getChessboard().squares[newX][newY].piece == null) //if on this sqhuare isn't piece
                {
                }
                else if (this.getChessboard().squares[newX][newY].piece.getPlayer() == this.getPlayer()) //if is our piece
                {
                }
                else if (this.getChessboard().squares[newX][newY].piece.getName().equals("Pawn"))
                {
                    return false;
                }
            }
        }

        return true;
    }

    /** Method to check will the king be safe when move
     *  @return bool true if king is save, else returns false
     */
    public boolean willBeSafeWhenMoveOtherPiece(Square sqIsHere, Square sqWillBeThere) //long name ;)
    {
        Piece tmp = sqWillBeThere.piece;
        sqWillBeThere.piece = sqIsHere.piece; // move without redraw
        sqIsHere.piece = null;

        boolean ret = isSafe(this.getSquare());

        sqIsHere.piece = sqWillBeThere.piece;
        sqWillBeThere.piece = tmp;

        return ret;
    }
}