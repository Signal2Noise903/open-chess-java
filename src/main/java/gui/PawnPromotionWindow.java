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
package main.java.gui;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;

/**
 * Class responsible for promotion of a pawn. When pawn reach the end of the
 * chessboard it can be change to rook, bishop, queen or knight. For what pawn
 * is promoted decideds player.
 * 
 * 
 */
public class PawnPromotionWindow extends JDialog implements ActionListener {

	JButton knightButton;
	JButton bishopButton;
	JButton rookButton;
	JButton queenButton;
	public String result;
	/**
	@param parent
	 *            Information about the current piece
	 * @param color
	 *            The player color
	*/
	public PawnPromotionWindow(Frame parent, String color) {
		super(parent);
		this.setTitle("Choose piece");
		this.setLayout(new GridLayout(1, 4));
		
		this.knightButton = new JButton(new ImageIcon(GUI.loadImage("Knight-" + color + ".png")));
		this.bishopButton = new JButton(new ImageIcon(GUI.loadImage("Bishop-" + color + ".png")));
		this.rookButton = new JButton(new ImageIcon(GUI.loadImage("Rook-" + color + ".png")));
		this.queenButton = new JButton(new ImageIcon(GUI.loadImage("Queen-" + color + ".png")));
		
		this.result = "";

		this.knightButton.addActionListener(this);
		this.bishopButton.addActionListener(this);
		this.rookButton.addActionListener(this);
		this.queenButton.addActionListener(this);

		this.add(queenButton);
		this.add(rookButton);
		this.add(bishopButton);
		this.add(knightButton);
	}

	/**
	 * Method setting the color fo promoted pawn
	 * 
	 * @param color
	 *            The players color
	 */
	public void setColor(String color) {
		this.knightButton.setIcon(new ImageIcon(GUI.loadImage("Knight-" + color + ".png")));
		this.bishopButton.setIcon(new ImageIcon(GUI.loadImage("Bishop-" + color + ".png")));
		this.rookButton.setIcon(new ImageIcon(GUI.loadImage("Rook-" + color + ".png")));
		this.queenButton.setIcon(new ImageIcon(GUI.loadImage("Queen-" + color + ".png")));
	}

	/**
	 * Method which is changing a pawn into queen, rook, bishop or knight
	 * 
	 * @param arg0
	 *            Capt information about performed action
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == queenButton) {
			result = "Queen";
		} else if (arg0.getSource() == rookButton) {
			result = "Rook";
		} else if (arg0.getSource() == bishopButton) {
			result = "Bishop";
		} else // knight
		{
			result = "Knight";
		}
		this.setVisible(false);
	}
}
