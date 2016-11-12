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
package jchess.board;

import java.awt.*;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JPanel;

import jchess.JChessApp;
import jchess.game.MovesTable;
import jchess.game.Player;
import jchess.game.Settings;
import jchess.game.MovesTable.castling;
import jchess.game.Player.colors;
import jchess.game.Settings.gameTypes;
import jchess.gui.GUI;
import jchess.pieces.Bishop;
import jchess.pieces.King;
import jchess.pieces.Knight;
import jchess.pieces.Move;
import jchess.pieces.Pawn;
import jchess.pieces.Piece;
import jchess.pieces.Queen;
import jchess.pieces.Rook;

/** Class to represent chessboard. Chessboard is made from squares.
 * It is setting the squers of chessboard and sets the pieces(pawns)
 * witch the owner is current player on it.
 */
public class Chessboard extends JPanel
{

    public static final int top = 0;
    public static final int bottom = 7;
    public Square squares[][];//squares of chessboard
    private static final Image orgImage = GUI.loadImage("chessboard.png");//image of chessboard
    private static Image image = Chessboard.orgImage;//image of chessboard
    private static final Image org_sel_square = GUI.loadImage("sel_square.png");//image of highlited square
    private static Image sel_square = org_sel_square;//image of highlited square
    private static final Image org_able_square = GUI.loadImage("able_square.png");//image of square where piece can go
    private static Image able_square = org_able_square;//image of square where piece can go
    public Square activeSquare;
    private Image upDownLabel = null;
    private Image LeftRightLabel = null;
    private Point topLeft = new Point(0, 0);
    private int active_x_square;
    private int active_y_square;
    private float square_height;//height of square
    //public Graphics graph;
    public static final int img_x = 5;//image x position (used in JChessView class!)
    public static final int img_y = img_x;//image y position (used in JChessView class!)
    public static final int img_widht = 480;//image width
    public static final int img_height = img_widht;//image height
    private ArrayList moves;
    private Settings settings;
    public King kingWhite;
    public King kingBlack;
    //-------- for undo ----------
    private Square undo1_sq_begin = null;
    private Square undo1_sq_end = null;
    private Piece undo1_piece_begin = null;
    private Piece undo1_piece_end = null;
    private Piece ifWasEnPassant = null;
    private Piece ifWasCastling = null;
    private boolean breakCastling = false; //if last move break castling
    //----------------------------
    //For En passant:
    //|-> Pawn whose in last turn moved two square
    public Pawn twoSquareMovedPawn = null;
    public Pawn twoSquareMovedPawn2 = null;
    private MovesTable moves_history;

    /** Chessboard class constructor
     * @param settings reference to Settings class object for this chessboard
     * @param moves_history reference to Moves class object for this chessboard 
     */
    public Chessboard(Settings settings, MovesTable moves_history)
    {
        this.settings = settings;
        this.activeSquare = null;
        this.square_height = img_height / 8;//we need to devide to know height of field
        this.squares = new Square[8][8];//initalization of 8x8 chessboard
        this.active_x_square = 0;
        this.active_y_square = 0;
        for (int i = 0; i < 8; i++)
        {//create object for each square
            for (int y = 0; y < 8; y++)
            {
                this.squares[i][y] = new Square(i, y, null);
            }
        }//--endOf--create object for each square
        this.moves_history = moves_history;
        this.setDoubleBuffered(true);
        this.drawLabels((int) this.square_height);
    }/*--endOf-Chessboard--*/


    /** Method setPieces on begin of new game or loaded game
     * @param places string with pieces to set on chessboard
     * @param plWhite reference to white player
     * @param plBlack reference to black player
     */
    public void setPieces(String places, Player plWhite, Player plBlack)
    {

        if (places.equals("")) //if newGame
        {
            if (this.settings.upsideDown)
            {
                this.setPieces4NewGame(true, plWhite, plBlack);
            }
            else
            {
                this.setPieces4NewGame(false, plWhite, plBlack);
            }

        } 
        else //if loadedGame
        {
            return;
        }
    }/*--endOf-setPieces--*/


    /**
     *
     */
    private void setPieces4NewGame(boolean upsideDown, Player plWhite, Player plBlack)
    {

        /* WHITE PIECES */
        Player player = plBlack;
        Player player1 = plWhite;
        if (upsideDown) //if white on Top
        { 
            player = plWhite;
            player1 = plBlack;
        }
        this.setFigures4NewGame(0, player, upsideDown);
        this.setPawns4NewGame(1, player);
        this.setFigures4NewGame(7, player1, upsideDown);
        this.setPawns4NewGame(6, player1);
    }/*--endOf-setPieces(boolean upsideDown)--*/


    /**  method set Figures in row (and set Queen and King to right position)
     *  @param i row where to set figures (Rook, Knight etc.)
     *  @param player which is owner of pawns
     *  @param upsideDown if true white pieces will be on top of chessboard
     * */
    private void setFigures4NewGame(int i, Player player, boolean upsideDown)
    {

        if (i != 0 && i != 7)
        {
            System.out.println("error setting figures like rook etc.");
            return;
        }
        else if (i == 0)
        {
            player.setGoDown(true);
        }

        this.squares[0][i].setPiece(new Rook(this, player));
        this.squares[7][i].setPiece(new Rook(this, player));
        this.squares[1][i].setPiece(new Knight(this, player));
        this.squares[6][i].setPiece(new Knight(this, player));
        this.squares[2][i].setPiece(new Bishop(this, player));
        this.squares[5][i].setPiece(new Bishop(this, player));
        if (upsideDown)
        {
            this.squares[4][i].setPiece(new Queen(this, player));
            if (player.getColor() == Player.colors.white)
            {
                this.squares[3][i].setPiece(kingWhite = new King(this, player));
            }
            else
            {
                this.squares[3][i].setPiece(kingBlack = new King(this, player));
            }
        }
        else
        {
            this.squares[3][i].setPiece(new Queen(this, player));
            if (player.getColor() == Player.colors.white)
            {
                this.squares[4][i].setPiece(kingWhite = new King(this, player));
            }
            else
            {
                this.squares[4][i].setPiece(kingBlack = new King(this, player));
            }
        }
    }

    /**  method set Pawns in row
     *  @param i row where to set pawns
     *  @param player player which is owner of pawns
     * */
    private void setPawns4NewGame(int i, Player player)
    {
        if (i != 1 && i != 6)
        {
            System.out.println("error setting pawns etc.");
            return;
        }
        for (int x = 0; x < 8; x++)
        {
            this.squares[x][i].setPiece(new Pawn(this, player));
        }
    }

    /** method to get reference to square from given x and y integeres
     * @param x x position on chessboard
     * @param y y position on chessboard
     * @return reference to searched square
     */
    public Square getSquare(int x, int y)
    { 
        if ((x > this.get_height()) || (y > this.get_widht())) //test if click is out of chessboard
        {
            System.out.println("click out of chessboard.");
            return null;
        }
        if (this.settings.renderLabels)
        {
            x -= this.upDownLabel.getHeight(null);
            y -= this.upDownLabel.getHeight(null);
        }
        double square_x = x / square_height;//count which field in X was clicked
        double square_y = y / square_height;//count which field in Y was clicked

        if (square_x > (int) square_x) //if X is more than X parsed to Integer
        {
            square_x = (int) square_x + 1;//parse to integer and increment
        }
        if (square_y > (int) square_y) //if X is more than X parsed to Integer
        {
            square_y = (int) square_y + 1;//parse to integer and increment
        }
        //Square newActiveSquare = this.squares[(int)square_x-1][(int)square_y-1];//4test
        System.out.println("square_x: " + square_x + " square_y: " + square_y + " \n"); //4tests
        Square result;
        try
        {
            result = this.squares[(int) square_x - 1][(int) square_y - 1];
        }
        catch (java.lang.ArrayIndexOutOfBoundsException exc)
        {
            System.out.println("!!Array out of bounds when getting Square with Chessboard.getSquare(int,int) : " + exc);
            return null;
        }
        return this.squares[(int) square_x - 1][(int) square_y - 1];
    }

    /** Method selecting piece in chessboard
     * @param  sq square to select (when clicked))
     */
    public void select(Square sq)
    {
        this.activeSquare = sq;
        this.active_x_square = sq.getPozX() + 1;
        this.active_y_square = sq.getPozY() + 1;

        //this.draw();//redraw
        System.out.println("active_x: " + this.active_x_square + " active_y: " + this.active_y_square);//4tests
        repaint();

    }/*--endOf-select--*/


    /** Method set variables active_x_square & active_y_square
     * to 0 values.
     */
    public void unselect()
    {
        this.active_x_square = 0;
        this.active_y_square = 0;
        this.activeSquare = null;
        //this.draw();//redraw
        repaint();
    }/*--endOf-unselect--*/
    
    public int get_widht()
    {
        return this.get_widht(false);
    }
    
    public int get_height()
    {
        return this.get_height(false);
    }


    public int get_widht(boolean includeLables)
    {
        return this.getHeight();
    }/*--endOf-get_widht--*/


    public int get_height(boolean includeLabels)
    {
        if (this.settings.renderLabels)
        {
            return Chessboard.image.getHeight(null) + upDownLabel.getHeight(null);
        }
        return Chessboard.image.getHeight(null);
    }/*--endOf-get_height--*/


    public int get_square_height()
    {
        int result = (int) this.square_height;
        return result;
    }

    public void move(Square begin, Square end)
    {
        move(begin, end, true);
    }

    /** Method to move piece over chessboard
     * @param xFrom from which x move piece
     * @param yFrom from which y move piece
     * @param xTo to which x move piece
     * @param yTo to which y move piece
     */
    public void move(int xFrom, int yFrom, int xTo, int yTo)
    {
        Square fromSQ = null;
        Square toSQ = null;
        try
        {
            fromSQ = this.squares[xFrom][yFrom];
            toSQ = this.squares[xTo][yTo];
        }
        catch (java.lang.IndexOutOfBoundsException exc)
        {
            System.out.println("error moving piece: " + exc);
            return;
        }
        this.move(this.squares[xFrom][yFrom], this.squares[xTo][yTo], true);
    }

    public void move(Square begin, Square end, boolean refresh)
    {
        this.move(begin, end, refresh, true);
    }

    /** Method move piece from square to square
     * @param begin square from which move piece
     * @param end square where we want to move piece         *
     * @param refresh chessboard, default: true
     * */
    public void move(Square begin, Square end, boolean refresh, boolean clearForwardHistory)
    {

        castling wasCastling = MovesTable.castling.none;
        Piece promotedPiece = null;
        boolean wasEnPassant = false;
        if (end.piece != null)
        {
            end.piece.setSquare(null);
        }

        Square tempBegin = new Square(begin);//4 moves history
        Square tempEnd = new Square(end);  //4 moves history
        //for undo
        undo1_piece_begin = begin.piece;
        undo1_sq_begin = begin;
        undo1_piece_end = end.piece;
        undo1_sq_end = end;
        ifWasEnPassant = null;
        ifWasCastling = null;
        breakCastling = false;
        // ---

        twoSquareMovedPawn2 = twoSquareMovedPawn;

        begin.piece.setSquare(end);//set square of piece to ending
        end.piece = begin.piece;//for ending square set piece from beginin square
        begin.piece = null;//make null piece for begining square

        if (end.piece.getName().equals("King"))
        {
            if (!((King) end.piece).wasMotion)
            {
                breakCastling = true;
                ((King) end.piece).wasMotion = true;
            }

            //Castling
            if (begin.getPozX() + 2 == end.getPozX())
            {
                move(squares[7][begin.getPozY()], squares[end.getPozX() - 1][begin.getPozY()], false, false);
                ifWasCastling = end.piece;  //for undo
                wasCastling = MovesTable.castling.shortCastling;
                //this.moves_history.addMove(tempBegin, tempEnd, clearForwardHistory, wasCastling, wasEnPassant);
                //return;
            }
            else if (begin.getPozX() - 2 == end.getPozX())
            {
                move(squares[0][begin.getPozY()], squares[end.getPozX() + 1][begin.getPozY()], false, false);
                ifWasCastling = end.piece;  // for undo
                wasCastling = MovesTable.castling.longCastling;
                //this.moves_history.addMove(tempBegin, tempEnd, clearForwardHistory, wasCastling, wasEnPassant);
                //return;
            }
            //endOf Castling
        }
        else if (end.piece.getName().equals("Rook"))
        {
            if (!((Rook) end.piece).isWasMotion())
            {
                breakCastling = true;
                ((Rook) end.piece).setWasMotion(true);
            }
        }
        else if (end.piece.getName().equals("Pawn"))
        {
            if (twoSquareMovedPawn != null && squares[end.getPozX()][begin.getPozY()] == twoSquareMovedPawn.getSquare()) //en passant
            {
                ifWasEnPassant = squares[end.getPozX()][begin.getPozY()].piece; //for undo

                tempEnd.piece = squares[end.getPozX()][begin.getPozY()].piece; //ugly hack - put taken pawn in en passant plasty do end square

                squares[end.getPozX()][begin.getPozY()].piece = null;
                wasEnPassant = true;
            }

            if (begin.getPozY() - end.getPozY() == 2 || end.getPozY() - begin.getPozY() == 2) //moved two square
            {
                breakCastling = true;
                twoSquareMovedPawn = (Pawn) end.piece;
            }
            else
            {
                twoSquareMovedPawn = null; //erase last saved move (for En passant)
            }

            if (end.piece.getSquare().getPozY() == 0 || end.piece.getSquare().getPozY() == 7) //promote Pawn
            {
                if (clearForwardHistory)
                {
                    String color;
                    if (end.piece.getPlayer().getColor() == Player.colors.white)
                    {
                        color = "W"; // promotionWindow was show with pieces in this color
                    }
                    else
                    {
                        color = "B";
                    }

                    String newPiece = JChessApp.getJcv().showPawnPromotionBox(color); //return name of new piece

                    if (newPiece.equals("Queen")) // transform pawn to queen
                    {
                        Queen queen = new Queen(this, end.piece.getPlayer());
                        queen.setChessboard(end.piece.getChessboard());
                        queen.setPlayer(end.piece.getPlayer());
                        queen.setSquare(end.piece.getSquare());
                        end.piece = queen;
                    }
                    else if (newPiece.equals("Rook")) // transform pawn to rook
                    {
                        Rook rook = new Rook(this, end.piece.getPlayer());
                        rook.setChessboard(end.piece.getChessboard());
                        rook.setPlayer(end.piece.getPlayer());
                        rook.setSquare(end.piece.getSquare());
                        end.piece = rook;
                    }
                    else if (newPiece.equals("Bishop")) // transform pawn to bishop
                    {
                        Bishop bishop = new Bishop(this, end.piece.getPlayer());
                        bishop.setChessboard(end.piece.getChessboard());
                        bishop.setPlayer(end.piece.getPlayer());
                        bishop.setSquare(end.piece.getSquare());
                        end.piece = bishop;
                    }
                    else // transform pawn to knight
                    {
                        Knight knight = new Knight(this, end.piece.getPlayer());
                        knight.setChessboard(end.piece.getChessboard());
                        knight.setPlayer(end.piece.getPlayer());
                        knight.setSquare(end.piece.getSquare());
                        end.piece = knight;
                    }
                    promotedPiece = end.piece;
                }
            }
        }
        else if (!end.piece.getName().equals("Pawn"))
        {
            twoSquareMovedPawn = null; //erase last saved move (for En passant)
        }
        //}

        if (refresh)
        {
            this.unselect();//unselect square
            repaint();
        }

        if (clearForwardHistory)
        {
            this.moves_history.clearMoveForwardStack();
            this.moves_history.addMove(tempBegin, tempEnd, true, wasCastling, wasEnPassant, promotedPiece);
        }
        else
        {
            this.moves_history.addMove(tempBegin, tempEnd, false, wasCastling, wasEnPassant, promotedPiece);
        }
    }/*endOf-move()-*/


    public boolean redo()
    {
        return redo(true);
    }

    public boolean redo(boolean refresh)
    {
        if ( this.settings.gameType == Settings.gameTypes.local ) //redo only for local game
        {
            Move first = this.moves_history.redo();

            Square from = null;
            Square to = null;

            if (first != null)
            {
                from = first.getFrom();
                to = first.getTo();

                this.move(this.squares[from.getPozX()][from.getPozY()], this.squares[to.getPozX()][to.getPozY()], true, false);
                if (first.getPromotedPiece() != null)
                {
                    Pawn pawn = (Pawn) this.squares[to.getPozX()][to.getPozY()].piece;
                    pawn.setSquare(null);

                    this.squares[to.getPozX()][to.getPozY()].piece = first.getPromotedPiece();
                    Piece promoted = this.squares[to.getPozX()][to.getPozY()].piece;
                    promoted.setSquare(this.squares[to.getPozX()][to.getPozY()]);
                }
                return true;
            }
            
        }
        return false;
    }

    public boolean undo()
    {
        return undo(true);
    }

    public synchronized boolean undo(boolean refresh) //undo last move
    {
        Move last = this.moves_history.undo();


        if (last != null && last.getFrom() != null)
        {
            Square begin = last.getFrom();
            Square end = last.getTo();
            try
            {
                Piece moved = last.getMovedPiece();
                this.squares[begin.getPozX()][begin.getPozY()].piece = moved;

                moved.setSquare(this.squares[begin.getPozX()][begin.getPozY()]);

                Piece taken = last.getTakenPiece();
                if (last.getCastlingMove() != castling.none)
                {
                    Piece rook = null;
                    if (last.getCastlingMove() == castling.shortCastling)
                    {
                        rook = this.squares[end.getPozX() - 1][end.getPozY()].piece;
                        this.squares[7][begin.getPozY()].piece = rook;
                        rook.setSquare(this.squares[7][begin.getPozY()]);
                        this.squares[end.getPozX() - 1][end.getPozY()].piece = null;
                    }
                    else
                    {
                        rook = this.squares[end.getPozX() + 1][end.getPozY()].piece;
                        this.squares[0][begin.getPozY()].piece = rook;
                        rook.setSquare(this.squares[0][begin.getPozY()]);
                        this.squares[end.getPozX() + 1][end.getPozY()].piece = null;
                    }
                    ((King) moved).wasMotion = false;
                    ((Rook) rook).setWasMotion(false);
                    this.breakCastling = false;
                }
                else if (moved.getName().equals("Rook"))
                {
                    ((Rook) moved).setWasMotion(false);
                }
                else if (moved.getName().equals("Pawn") && last.wasEnPassant())
                {
                    Pawn pawn = (Pawn) last.getTakenPiece();
                    this.squares[end.getPozX()][begin.getPozY()].piece = pawn;
                    pawn.setSquare(this.squares[end.getPozX()][begin.getPozY()]);

                }
                else if (moved.getName().equals("Pawn") && last.getPromotedPiece() != null)
                {
                    Piece promoted = this.squares[end.getPozX()][end.getPozY()].piece;
                    promoted.setSquare(null);
                    this.squares[end.getPozX()][end.getPozY()].piece = null;
                }

                //check one more move back for en passant
                Move oneMoveEarlier = this.moves_history.getLastMoveFromHistory();
                if (oneMoveEarlier != null && oneMoveEarlier.wasPawnTwoFieldsMove())
                {
                    Piece canBeTakenEnPassant = this.squares[oneMoveEarlier.getTo().getPozX()][oneMoveEarlier.getTo().getPozY()].piece;
                    if (canBeTakenEnPassant.getName().equals("Pawn"))
                    {
                        this.twoSquareMovedPawn = (Pawn) canBeTakenEnPassant;
                    }
                }

                if (taken != null && !last.wasEnPassant())
                {
                    this.squares[end.getPozX()][end.getPozY()].piece = taken;
                    taken.setSquare(this.squares[end.getPozX()][end.getPozY()]);
                }
                else
                {
                    this.squares[end.getPozX()][end.getPozY()].piece = null;
                }

                if (refresh)
                {
                    this.unselect();//unselect square
                    repaint();
                }

            }
            catch (java.lang.ArrayIndexOutOfBoundsException exc)
            {
                return false;
            }
            catch (java.lang.NullPointerException exc)
            {
                return false;
            }

            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Method to draw Chessboard and their elements (pieces etc.)
     * @deprecated 
     */
    public void draw()
    {
        this.getGraphics().drawImage(image, this.getTopLeftPoint().x, this.getTopLeftPoint().y, null);//draw an Image of chessboard
        this.drawLabels();
        this.repaint();
    }/*--endOf-draw--*/


    /**
     * Annotations to superclass Game updateing and painting the crossboard
     */
    @Override
    public void update(Graphics g)
    {
        repaint();
    }

    public Point getTopLeftPoint()
    {
        if (this.settings.renderLabels)
        {
            return new Point(this.topLeft.x + this.upDownLabel.getHeight(null), this.topLeft.y + this.upDownLabel.getHeight(null));
        }
        return this.topLeft;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Point topLeftPoint = this.getTopLeftPoint();
        if (this.settings.renderLabels)
        {
            if(topLeftPoint.x <= 0 && topLeftPoint.y <= 0) //if renderLabels and (0,0), than draw it! (for first run)
            {
                this.drawLabels();
            }
            g2d.drawImage(this.upDownLabel, 0, 0, null);
            g2d.drawImage(this.upDownLabel, 0, Chessboard.image.getHeight(null) + topLeftPoint.y, null);
            g2d.drawImage(this.LeftRightLabel, 0, 0, null);
            g2d.drawImage(this.LeftRightLabel, Chessboard.image.getHeight(null) + topLeftPoint.x, 0, null);
        }
        g2d.drawImage(image, topLeftPoint.x, topLeftPoint.y, null);//draw an Image of chessboard
        for (int i = 0; i < 8; i++) //drawPiecesOnSquares
        {
            for (int y = 0; y < 8; y++)
            {
                if (this.squares[i][y].piece != null)
                {
                    this.squares[i][y].piece.draw(g);//draw image of Piece
                }
            }
        }//--endOf--drawPiecesOnSquares
        if ((this.active_x_square != 0) && (this.active_y_square != 0)) //if some square is active
        {
            g2d.drawImage(sel_square, 
                            ((this.active_x_square - 1) * (int) square_height) + topLeftPoint.x,
                            ((this.active_y_square - 1) * (int) square_height) + topLeftPoint.y, null);//draw image of selected square
            Square tmpSquare = this.squares[(int) (this.active_x_square - 1)][(int) (this.active_y_square - 1)];
            if (tmpSquare.piece != null)
            {
                this.moves = this.squares[(int) (this.active_x_square - 1)][(int) (this.active_y_square - 1)].piece.allMoves();
            }

            for (Iterator it = moves.iterator(); moves != null && it.hasNext();)
            {
                Square sq = (Square) it.next();
                g2d.drawImage(able_square, 
                        (sq.getPozX() * (int) square_height) + topLeftPoint.x,
                        (sq.getPozY() * (int) square_height) + topLeftPoint.y, null);
            }
        }
    }/*--endOf-paint--*/


    public void resizeChessboard(int height)
    {
        BufferedImage resized = new BufferedImage(height, height, BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics g = resized.createGraphics();
        g.drawImage(Chessboard.orgImage, 0, 0, height, height, null);
        g.dispose();
        Chessboard.image = resized.getScaledInstance(height, height, 0);
        this.square_height = (float) (height / 8);
        if (this.settings.renderLabels)
        {
            height += 2 * (this.upDownLabel.getHeight(null));
        }
        this.setSize(height, height);

        resized = new BufferedImage((int) square_height, (int) square_height, BufferedImage.TYPE_INT_ARGB_PRE);
        g = resized.createGraphics();
        g.drawImage(Chessboard.org_able_square, 0, 0, (int) square_height, (int) square_height, null);
        g.dispose();
        Chessboard.able_square = resized.getScaledInstance((int) square_height, (int) square_height, 0);

        resized = new BufferedImage((int) square_height, (int) square_height, BufferedImage.TYPE_INT_ARGB_PRE);
        g = resized.createGraphics();
        g.drawImage(Chessboard.org_sel_square, 0, 0, (int) square_height, (int) square_height, null);
        g.dispose();
        Chessboard.sel_square = resized.getScaledInstance((int) square_height, (int) square_height, 0);
        this.drawLabels();
    }

    protected void drawLabels()
    {
        this.drawLabels((int) this.square_height);
    }

    protected final void drawLabels(int square_height)
    {
        //BufferedImage uDL = new BufferedImage(800, 800, BufferedImage.TYPE_3BYTE_BGR);
        int min_label_height = 20;
        int labelHeight = (int) Math.ceil(square_height / 4);
        labelHeight = (labelHeight < min_label_height) ? min_label_height : labelHeight;
        int labelWidth =  (int) Math.ceil(square_height * 8 + (2 * labelHeight)); 
        BufferedImage uDL = new BufferedImage(labelWidth + min_label_height, labelHeight, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D uDL2D = (Graphics2D) uDL.createGraphics();
        uDL2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        uDL2D.setColor(Color.white);

        uDL2D.fillRect(0, 0, labelWidth + min_label_height, labelHeight);
        uDL2D.setColor(Color.black);
        uDL2D.setFont(new Font("Arial", Font.BOLD, 12));
        int addX = (square_height / 2);
        if (this.settings.renderLabels)
        {
            addX += labelHeight;
        }

        String[] letters =
        {
            "a", "b", "c", "d", "e", "f", "g", "h"
        };
        if (!this.settings.upsideDown)
        {
            for (int i = 1; i <= letters.length; i++)
            {
                uDL2D.drawString(letters[i - 1], (square_height * (i - 1)) + addX, 10 + (labelHeight / 3));
            }
        }
        else
        {
            int j = 1;
            for (int i = letters.length; i > 0; i--, j++)
            {
                uDL2D.drawString(letters[i - 1], (square_height * (j - 1)) + addX, 10 + (labelHeight / 3));
            }
        }
        uDL2D.dispose();
        this.upDownLabel = uDL;

        uDL = new BufferedImage(labelHeight, labelWidth + min_label_height, BufferedImage.TYPE_3BYTE_BGR);
        uDL2D = (Graphics2D) uDL.createGraphics();
        uDL2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        uDL2D.setColor(Color.white);
        //uDL2D.fillRect(0, 0, 800, 800);
        uDL2D.fillRect(0, 0, labelHeight, labelWidth + min_label_height);
        uDL2D.setColor(Color.black);
        uDL2D.setFont(new Font("Arial", Font.BOLD, 12));

        if (this.settings.upsideDown)
        {
            for (int i = 1; i <= 8; i++)
            {
                uDL2D.drawString(new Integer(i).toString(), 3 + (labelHeight / 3), (square_height * (i - 1)) + addX);
            }
        }
        else
        {
            int j = 1;
            for (int i = 8; i > 0; i--, j++)
            {
                uDL2D.drawString(new Integer(i).toString(), 3 + (labelHeight / 3), (square_height * (j - 1)) + addX);
            }
        }
        uDL2D.dispose();
        this.LeftRightLabel = uDL;
    }

    public void componentMoved(ComponentEvent e)
    {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void componentShown(ComponentEvent e)
    {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void componentHidden(ComponentEvent e)
    {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}