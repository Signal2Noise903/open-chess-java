package test.java.movesInCircleBoard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import main.java.board.Square;
import main.java.circleBoard.CircleBoard;
import main.java.circleBoard.CircleBoardInitialization;
import main.java.game.Player;
import main.java.movesInCircleBoard.DragonMovesInCircleBoard;
import main.java.pieces.Piece;
import main.java.pieces.PieceFactory;
import main.java.pieces.PieceFactory.PieceType;

public class DragonMovesInCircleBoardTest {
	CircleBoardInitialization board_squares;
	CircleBoard board;
	DragonMovesInCircleBoard dm;
	
	Piece pawn, rook;
	Player p1;
	Player p2;
	
	// Pawn position
	int x1 = 4;
	int y1 = 3;
	
	// Rook position
	int x2 = 3;
	int y2 = 3;

	@Before
	public void setUp() throws Exception {
		// Initialize board
		board= new CircleBoard();		
		p1 = new Player("Player1", "white");
		p2 = new Player("Player1", "black");

		// Set Pieces
		rook = PieceFactory.createSpecificPieceForCircleBoard(board, p1, PieceType.Rook);
		pawn = PieceFactory.createSpecificPieceForCircleBoard(board, p2, PieceType.Pawn); 
		board.getSquares()[x1][y1].setPiece(rook);
		board.getSquares()[x2][y2].setPiece(pawn);
		board.move(board.getSquares()[x1][y1], board.getSquares()[x2][y2], false);
		dm = new DragonMovesInCircleBoard();
	}

	@Test
	public final void releaseTheDragon() {
		PieceType dragon = board.getSquares()[x2][y2].getPiece().getType();
		assertEquals(PieceType.Dragon, dragon);
	}

	@Test
	public final void theDragonFireBlasterInnerCircle() {
		int x = x2;
		int y = y2;
		
		// Set pieces around the Dragon
		addPiecesAround(pawn, x, y, 1);

		// Expected set of moves
		ArrayList<Square> expected = addToExpectedAround(x, y, 1);
		
		// Obtained set of moves 
		ArrayList<Square> obtained = dm.getMoves(board, board.getSquares()[x2][y2].getPiece(), true);
		
		assertTrue(obtained.containsAll(expected));
	}
	
	@Test
	public final void testIncreaseFireBlasterMiddleCircle() {
		// Blast fire
		dm.increaseFireLoader();
		int fire = dm.getFireLoader();
		int x = x2;
		int y = y2;
		
		// Set pieces around the Dragon
		addPiecesAround(pawn, x, y, 1);
		addPiecesAround(pawn, x, y, 2);

		// Expected set of moves
		ArrayList<Square> expected = addToExpectedAround(x, y, 1);
		expected.addAll(addToExpectedAround(x, y, 2));
		
		// Obtained set of moves ;
		ArrayList<Square> obtained = dm.getMoves(board, board.getSquares()[x][y].getPiece(), true);
		
		assertTrue(fire > 0);
		assertTrue(obtained.containsAll(expected));
	}
	
	@Test
	public final void testKingAroundDragon() {
		// Blast fire
		int x = y2;
		int y = x2;
		
		// Set King around the Dragon
		Piece king = PieceFactory.createSpecificPieceForCircleBoard(board, p2, PieceType.King);
		board.getSquares()[x + 1][y].setPiece(king);
		board.getSquares()[x - 1][y].setPiece(pawn);

		// Expected set of moves including KING
		ArrayList<Square> expected = new ArrayList<Square>();
		expected.add(board.getSquares()[x + 1][y]);
		expected.add(board.getSquares()[x - 1][y]);

		// Obtained set of moves by Algorithm
		ArrayList<Square> obtained = dm.getMoves(board, board.getSquares()[x][y].getPiece(), true);
	
		assertFalse(obtained.contains(board.getSquares()[x + 1][y])); // FALSE, that it CAN move where the KING IS.
		
	}
	
	void addPiecesAround(Piece piece, int x, int y, int a) {
		board.getSquares()[x	][y + a ].setPiece(piece);
		board.getSquares()[x	][y - a ].setPiece(piece);
		board.getSquares()[x + a][y + a ].setPiece(piece);
		board.getSquares()[x + a][y - a ].setPiece(piece);
		board.getSquares()[x - a][y + a ].setPiece(piece);
		board.getSquares()[x - a][y - a ].setPiece(piece);
		board.getSquares()[x + a][y		].setPiece(piece);
		board.getSquares()[x - a][y		].setPiece(piece);
	}
	
	ArrayList<Square> addToExpectedAround(int x, int y, int a) {
		ArrayList<Square> expected = new ArrayList<Square>();
		expected.add(board.getSquares()[x	 ][y + a]);
		expected.add(board.getSquares()[x	 ][y - a]);
		expected.add(board.getSquares()[x + a][y + a]);
		expected.add(board.getSquares()[x + a][y - a]);
		expected.add(board.getSquares()[x - a][y + a]);
		expected.add(board.getSquares()[x - a][y - a]);
		expected.add(board.getSquares()[x + a][y	]);
		expected.add(board.getSquares()[x - a][y	]);
		return expected;
	}

}