/**
 * CIS 120 HW10
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

// imports necessary libraries for Java swing
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class Game implements Runnable {
	
	private void helpScreen() {
		JDialog dialog = new JDialog(new Frame(), "Help");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JTextArea text = new JTextArea(guide);
        text.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        text.setEditable(false);
        dialog.add(text);
        dialog.pack();
        dialog.setVisible(true);
	}
	public void run() {
		// NOTE : recall that the 'final' keyword notes inmutability
		// even for local variables.

		// Top-level frame in which game components live
		// Be sure to change "TOP LEVEL FRAME" to the name of your game
		final JFrame frame = new JFrame("CHINESE CHECKERS");
		frame.setLocation(1200, 1200);

		// Status panel
		final JPanel status_panel = new JPanel();
		final JPanel gamestats_panel = new JPanel();
		gamestats_panel.setLayout(new GridLayout(3,1));
		frame.add(status_panel, BorderLayout.SOUTH);
		frame.add(gamestats_panel, BorderLayout.EAST);
		final JLabel status = new JLabel("New Game - RED begins.");
		final JLabel redScoreLabel = new JLabel("Red has 0 pieces in place.");
		final JLabel greenScoreLabel = new JLabel("Green has 0 pieces in place.");
		final JLabel winning = new JLabel("The score is tied.");
		Font font = new Font("Serif", Font.BOLD, 25);
		status.setFont(font);
		redScoreLabel.setFont(font);
		greenScoreLabel.setFont(font);
		winning.setFont(font);
		status_panel.add(status);
		gamestats_panel.add(redScoreLabel);
		gamestats_panel.add(greenScoreLabel);
		gamestats_panel.add(winning);
		

		// Main playing area
		final GameCourt playingField = new GameCourt(status, redScoreLabel, greenScoreLabel, winning);
		frame.add(playingField, BorderLayout.CENTER);

		// Reset button
		final JPanel control_panel = new JPanel();
		frame.add(control_panel, BorderLayout.NORTH);

		// Note here that when we add an action listener to the reset
		// button, we define it as an anonymous inner class that is
		// an instance of ActionListener with its actionPerformed()
		// method overridden. When the button is pressed,
		// actionPerformed() will be called.
		final JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playingField.reset();
			}
		});
		control_panel.add(reset);
		
		final JButton help = new JButton("Help");
		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				helpScreen();
			}
		});
		control_panel.add(help);
		

		// Put the frame on the screen
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		// Start game
		playingField.reset();
	}

	
	private String guide = "This explains how the game is played.";
	/*
	 * Main method run to start and run the game Initializes the GUI elements
	 * specified in Game and runs it IMPORTANT: Do NOT delete! You MUST include
	 * this in the final submission of your game.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Game());
		
	}
}
