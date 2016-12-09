package main.java.movesInSquareBoard;

import java.util.ArrayList;

import main.java.board.IChessboard;
import main.java.board.IMove;
import main.java.board.Square;
import main.java.pieces.King;
import main.java.pieces.Piece;

public class PawnMoves implements IMove {
	
	public void regularMove(Piece piece, ArrayList<Square> list, int i) {
		int newY=0;
		if (piece.getPlayer().isGoDown()) {// check if player "go" down or up
			newY = piece.getSquare().getPozY() + i;// if yes, change value
		} else {
			newY = piece.getSquare().getPozY() -i;
													// (only in first move)
		}
		
		if (!piece.pieceBehaviour.isout(piece.getSquare().getPozX(), newY )) {
			Square moveSq = piece.getChessboard().getSquares()[piece.getSquare().getPozX()][newY];

			if (moveSq.piece == null && piece.myKing().willBeSafeWhenMoveOtherPiece(piece.getSquare(), moveSq)) {
				list.add(moveSq);

			}
		}
	}
	
	public void captureMove(Piece piece, ArrayList<Square> list, int newX){
		int newY=0;
		if (piece.getPlayer().isGoDown()) {// check if player "go" down or up
			newY = piece.getSquare().getPozY() + 1;// if yes, change value
		} else {
			newY = piece.getSquare().getPozY() -1;
													// (only in first move)
		}
		if (!piece.pieceBehaviour.isout(newX, newY)) {
			Square moveSq = piece.getChessboard().getSquares()[newX][newY];
		if (moveSq.piece != null) {// check if can hit left
			if (piece.getPlayer() != moveSq.piece.getPlayer() && !moveSq.piece.getName().equals("King")) {
					if (piece.myKing().willBeSafeWhenMoveOtherPiece(piece.getSquare(), moveSq)) {
						list.add(moveSq);
					}
				}
			}
		}
		
	}
	
	public void enPassantMove(Piece piece, ArrayList<Square> list, int newX, int i){
		int newY= piece.getSquare().getPozY();
		if (!piece.pieceBehaviour.isout(newX, newY + i)) {
			Square attSq = piece.getChessboard().getSquares()[newX][newY];
			Square moveSq = piece.getChessboard().getSquares()[newX][newY + i];

			if (attSq.piece != null && piece.getChessboard().getTwoSquareMovedPawn() != null
					&& attSq == piece.getChessboard().getTwoSquareMovedPawn().getSquare()) {
				// check if can hit left
				if (piece.getPlayer() != attSq.piece.getPlayer() && !attSq.piece.getName().equals("King")) {
					if (piece.myKing().willBeSafeWhenMoveOtherPiece(piece.getSquare(), moveSq)) {
						list.add(moveSq);

					}
				}
			}
		}
	}
	
	public ArrayList<Square> getMoves(Piece piece, boolean ignoreKing){
		ArrayList<Square> list = new ArrayList<>();
		regularMove(piece, list, 1);
		if ((piece.getPlayer().isGoDown() && piece.getSquare().getPozY() == 1)
				|| (!piece.getPlayer().isGoDown() && piece.getSquare().getPozY() == 6)) {
			regularMove(piece, list, 2);
		}
		
		captureMove(piece, list, piece.getSquare().getPozX()-1);
		enPassantMove(piece, list, piece.getSquare().getPozX()-1, -1);
		
		//right
		captureMove(piece, list, piece.getSquare().getPozX()+1);
		enPassantMove(piece, list, piece.getSquare().getPozX()+1, 1);
		
		
		return list;
	}
}
