package main.java.circleBoard;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.java.board.ChessboardDisplay;
import main.java.board.ChessboardLayout;
import main.java.board.Square;

public class CircleBoardDisplay extends ChessboardDisplay {

	public Square activeSquare;
	public Image upDownLabel;
	public Image LeftRightLabel;
	public Point topLeftPoint;
	public int active_x_square;
	public int active_y_square;

	public static final int img_x = 5;// image x position (used in JChessView
										// class!)
	public static final int img_y = img_x;// image y position (used in
											// JChessView class!)
	public static final int img_widht = 480;// image width
	public static final int img_height = img_widht;// image height

	private ChessboardLayout board_layout;
	boolean renderLabels, upsideDown;
	Square[][] squares;
	CircleBoard board;

	public CircleBoardDisplay(Image upDownLabel, Image leftRightLabel, Point topLeft, CircleBoard board) {
		this.upDownLabel = upDownLabel;
		LeftRightLabel = leftRightLabel;
		this.topLeftPoint = topLeft;
		this.board_layout = board.board_layout;
		this.squares = board.initial.getSquares();
		this.board = board;
		activeSquare = null;
		active_x_square = -1;
		active_y_square = -1;

		this.setDoubleBuffered(true);
		drawLabels(get_square_height());
	}

	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (renderLabels) {
			if (topLeftPoint.x <= 0 && topLeftPoint.y <= 0) {
				drawLabels(get_square_height());
			}

			g2d.drawImage(this.upDownLabel, 0, 0, null);
			g2d.drawImage(this.upDownLabel, 0, board_layout.image.getHeight(null) + topLeftPoint.y, null);
			g2d.drawImage(this.LeftRightLabel, 0, 0, null);
			g2d.drawImage(this.LeftRightLabel, board_layout.image.getHeight(null) + topLeftPoint.x, 0, null);
		}
		g2d.drawImage(board_layout.image, topLeftPoint.x, topLeftPoint.y, null);
		drawPieces(g2d);
		drawHighlightedSquares(g2d);

	}/*--endOf-paint--*/

	private void drawPieces(Graphics g) {
		for (int i = 0; i < 24; i++) // drawPiecesOnSquares
		{
			for (int y = 0; y < 6; y++) {
				if (squares[i][y].piece != null) {
					squares[i][y].piece.draw(g);// draw image of Piece
				}
			}
		} // --endOf--drawPiecesOnSquares
	}

	public Point indexToCartesian(Point p1) {
		int xi = p1.x;
		int yi = p1.y;

		int ri = (int) (getRadius() - (yi + 1) * get_square_height());
		int rs = (int) (ri + get_square_height());

		int a1 = (6 - xi) * 15;
		int a2 = a1 - 15;
		int rm = (rs + ri) / 2;
		int am = (a1 + a2) / 2;

		int xm = (int) (topLeftPoint.x + getRadius() + (int) (rm * Math.cos(Math.toRadians(am)))
				- get_square_height() / 2);
		int ym = (int) (topLeftPoint.y + getRadius() - (int) (rm * Math.sin(Math.toRadians(am)))
				- get_square_height() / 2);

		return new Point(xm, ym);

	}

	public void drawHighlightedSquares(Graphics2D g2d) {

		if (activeSquare != null) {
			int xi = activeSquare.getPozX();
			int yi = activeSquare.getPozY();

			Point pm = indexToCartesian(new Point(xi, yi));

			int xm = pm.x;
			int ym = pm.y;

			Image tempImage = board_layout.orgSelSquare;
			BufferedImage resized = resizeImage(tempImage, get_square_height());
			board_layout.selSquare = resized.getScaledInstance(get_square_height(), get_square_height(), 0);
			g2d.drawImage(board_layout.selSquare, xm, ym, null);

			if (activeSquare.piece != null) {
				ArrayList<Square> moves = activeSquare.piece.allMoves(false);
				for (Square sq : moves) {
					Point p_sq = indexToCartesian(new Point(sq.getPozX(), sq.getPozY()));

					tempImage = board_layout.orgAbleSquare;
					resized = resizeImage(tempImage, get_square_height());
					board_layout.ableSquare = resized.getScaledInstance(get_square_height(), get_square_height(), 0);

					g2d.drawImage(board_layout.ableSquare, p_sq.x, p_sq.y, null);
				}
			}

		}
	}

	private BufferedImage resizeImage(Image tempImage, int height) {
		BufferedImage resized = new BufferedImage(height, height, BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D imageGr = (Graphics2D) resized.createGraphics();
		imageGr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		imageGr.drawImage(tempImage, 0, 0, height, height, null);
		imageGr.dispose();
		return resized;
	}

	public void resizeChessboard(int height) {
		BufferedImage resized = resizeImage(board_layout.orgImage, height);
		board_layout.image = resized.getScaledInstance(height, height, 0);

		int square_height = (height - height / 3) / 6;

		if (renderLabels) {
			height += 2 * (this.upDownLabel.getHeight(null));
		}

		this.setSize(height, height);
		this.drawLabels(square_height);
	}

	protected final void drawLabels(float square_height2) {
		// BufferedImage uDL = new BufferedImage(800, 800,
		// BufferedImage.TYPE_3BYTE_BGR);
		int min_label_height = 20;
		int labelHeight = (int) Math.ceil(square_height2 / 4);
		labelHeight = (labelHeight < min_label_height) ? min_label_height : labelHeight;
		int labelWidth = (int) Math.ceil(square_height2 * 8 + (2 * labelHeight));
		BufferedImage uDL = new BufferedImage(labelWidth + min_label_height, labelHeight, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D uDL2D = (Graphics2D) uDL.createGraphics();
		uDL2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		uDL2D.setColor(Color.white);

		uDL2D.fillRect(0, 0, labelWidth + min_label_height, labelHeight);
		uDL2D.setColor(Color.black);
		uDL2D.setFont(new Font("Arial", Font.BOLD, 12));
		int addX = (int) (square_height2 / 2);
		if (renderLabels) {
			addX += labelHeight;
		}

		String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h" };

		int j = 1;
		for (int i = letters.length; i > 0; i--, j++) {
			uDL2D.drawString(letters[i - 1], (square_height2 * (j - 1)) + addX, 10 + (labelHeight / 3));
		}

		uDL2D.dispose();
		this.upDownLabel = uDL;

		uDL = new BufferedImage(labelHeight, labelWidth + min_label_height, BufferedImage.TYPE_3BYTE_BGR);
		uDL2D = (Graphics2D) uDL.createGraphics();
		uDL2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		uDL2D.setColor(Color.white);
		// uDL2D.fillRect(0, 0, 800, 800);
		uDL2D.fillRect(0, 0, labelHeight, labelWidth + min_label_height);
		uDL2D.setColor(Color.black);
		uDL2D.setFont(new Font("Arial", Font.BOLD, 12));

		j = 1;
		for (int i = 8; i > 0; i--, j++) {
			uDL2D.drawString(new Integer(i).toString(), 3 + (labelHeight / 3), (square_height2 * (j - 1)) + addX);
		}

		uDL2D.dispose();
		this.LeftRightLabel = uDL;
	}

	public int getUpDownLabelHeight() {
		return upDownLabel.getHeight(null);
	}

	public int getHeight() {
		return board_layout.image.getHeight(null);
	}/*--endOf-get_height--*/

	public void draw() {
		this.getGraphics().drawImage(board_layout.image, this.getTopLeftPoint().x, this.getTopLeftPoint().y, null);// draw
																													// an
																													// Image
																													// of
																													// chessboard
		// this.drawLabels();
		this.repaint();
	}

	public int get_square_height() {
		return (getRadius() - getRadius() / 3) / 6;
	}

	public int getRadius() {
		return board_layout.image.getHeight(null) / 2;
	}

	@Override
	public Point getTopLeftPoint() {
		if (renderLabels) {
			return new Point(topLeftPoint.x + upDownLabel.getHeight(null),
					topLeftPoint.y + upDownLabel.getHeight(null));
		}
		return topLeftPoint;
	}

}
