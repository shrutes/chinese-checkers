/**
 * CIS 120 HW10
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.util.TreeSet;

/**
 * 
 * 
 * This class holds the primary game logic for how different objects interact
 * with one another.
 * 
 */
@SuppressWarnings("serial")
public class GameCourt extends JPanel {

	// the state of the game logic
	
	// The ImageIcons used for the display of the buttons
	private ImageIcon empty = addImageIcon(FieldValue.EMPTY);
	private ImageIcon red = addImageIcon(FieldValue.RED);
	private ImageIcon green = addImageIcon(FieldValue.GREEN);
	private ImageIcon possible = addImageIcon(FieldValue.POSSIBLE);
	private ImageIcon select = addImageIcon(FieldValue.SELECT);
	private ImageIcon outofbound = addImageIcon(FieldValue.OUT_OF_BOUNDS);
	
	/*The board containing all of the fields,
	including an Out_of_bound field at the beginning and end of each row + entire first and last row */
	private Field[][] board;
	
	//The player whose turn it is
	private FieldValue turn;
	
	//boolean is true if a player has selected a piece to move, false otherwise
	private boolean selected;
	
	//an int array containing the row and column of the selected field
	private int[] selectField;
	
	//an array with the length of each row = the length of each row in the playing field + one out_of_bound
	private final int[] rowLength = {2, 3, 4, 5, 6, 15, 14, 13, 12, 11, 12, 13, 14, 15, 6, 5, 4, 3, 2};
	public boolean playing = false; // whether the game is running
	private JLabel status; // Current status text (i.e. Running...)
	
	private int redScore; //Red's score
	private int greenScore; // Green's score
	private JLabel redScoreLabel; //Red's score label
	private JLabel greenScoreLabel; //Green's score label
	private JLabel winning; //who is on the win;

	// Game constants
	public static final int COURT_WIDTH = 1200;
	public static final int COURT_HEIGHT = 1200;
		
	//The FieldListener determines what happens when a player clicks on a Field
	class FieldListener implements ActionListener {
		  private final int row;
		  private final int column;
		  public FieldListener(int row, int column) {
			  this.row = row;
			  this.column = column;
			  }

		  @Override
		  public void actionPerformed(ActionEvent e) {
			  if (playing) {
				     if (selected && checkFieldValue(row, column).equals(FieldValue.POSSIBLE)) {
				    	 move(row, column);
				     }
				     else {
				    	 selection(row, column);
				     }
			  }
		}
	}

	//initiates the game court
	public GameCourt(JLabel status, JLabel redScore, JLabel greenScore, JLabel winning) {
		// creates border around the court area, JComponent method
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.status = status;
		this.redScoreLabel = redScore;
		this.greenScoreLabel = greenScore;
		this.winning = winning;
	}
	
	/*adds an imageIcon for the different colors. 
	These are stored in local variables and given as parameters 
	when the setIcon method is called on the Fields */
	public ImageIcon addImageIcon(FieldValue color) {
		String img_file;
		switch(color) {

		case EMPTY:
			img_file = "darblue.png";
			break;
		case OUT_OF_BOUNDS:
			img_file = "darblue.png";
			setEnabled(false);
			break;
		case POSSIBLE:
			img_file = "possible.png";
			break;
		case SELECT:
			img_file = "lightblue.png";
			break;
		case RED:
			img_file = "red.png";
			break;
		default:
			img_file = "green.png";
			break;
		}

				BufferedImage img = null;
				try {
					img = ImageIO.read(new File(img_file));
				} catch (IOException e) {
					System.out.println("Internal Error:" + e.getMessage());
				}
		return new ImageIcon(img);
	}
	
	/*Select a piece and display the possible moves for this piece.
	if the player selects a Field that does not contain one of his own pieces
	or selects a piece that cannot be moved, the status is updated with an appropriate message */
	public void selection(int row, int column) {
		System.out.println(turn.toString());
		if (checkFieldValue(row,column).equals(turn)) {
			changeToEmpty();
			TreeSet<int[]> possibleSet = possibleMoves(row, column);
			if (!possibleSet.isEmpty()) {
				for (int[] field: possibleSet) {
					System.out.println("Element of possible: row " + field[0] + "column " + field[1]);
					updateValue(checkField(field[0], field[1]), FieldValue.POSSIBLE);
				}
				selectField[0] = row;
				selectField[1] = column;
				selected = true;
				updateValue(checkField(row, column), FieldValue.SELECT);
				status.setText("Click to move your piece to an available spot");
			}
			else {
				status.setText("There are no valid moves for this piece");
			}
		}
		else if (checkFieldValue(row, column).equals(FieldValue.SELECT)) {
			status.setText("You already selected this piece.");
		}
		else if (selected) {
			status.setText("This is not a valid move.");
		}
		else status.setText("Please select one of your own pieces.");
	}
	
	/*this method returns a TreeSet with the coordinates of the possible moves with
	the given input coordinates */
	public TreeSet<int[]> possibleMoves(int row, int column) {
		TreeSet<int[]> possibleMoves = new TreeSet<int[]>(new ArrayComparator());

		int[][] check = checkArray(row, column);
		for (int[] elem: check) {
			FieldValue value = checkFieldValue(elem[0], elem[1]);
			switch(value) {
			
			case OUT_OF_BOUNDS:
				break;
				
			case EMPTY: case POSSIBLE:
				possibleMoves.add(elem);
				break;
				
			default:
				possibleMoves.addAll(checkHop(row, column, elem[0], elem[1], 0));
				break;
			}

		}
		return possibleMoves;
	}
	
	//returns the piece at the given position
	private Field checkField (int row, int column) {
		System.out.println(row);
		System.out.println(column);
		return board[row][column];
	}
	
	//returns the FieldValue of the field at the given position
	private FieldValue checkFieldValue(int row, int column) {
		return checkField(row,column).getValue();
	}
	
	/*returns the array that has to be checked with respect to 
	the given input coordinates. This is a helper function used by possibleMoves and checkHop */
	private int[][] checkArray(int row, int column) {
		int next_column = board[row +1].length - board[row].length;
		int corr_next = 0;
		if (next_column > 1) {
			corr_next = 4;
			next_column = 1;
		}
		else if (next_column < -1) {
			corr_next = -4;
			next_column = -1;
		}
		int previous_column = board[row -1].length - board[row].length;
		int corr_prev = 0;
		if (previous_column > 1) {
			corr_prev = 4;
			previous_column = 1;
		}
		else if (previous_column < -1) {
			corr_prev = -4;
			previous_column = -1;
		}
		return new int[][] {{row + 1, column + next_column + corr_next}, 
				{row +1, column + corr_next}, 
				{row, column -1},
				{row, column +1},
				{row - 1, column + previous_column + corr_prev}, 
				{row - 1, column + corr_prev} };
	}
	
	//checks the possible hops a piece can make if an adjacent Field contains a field 
	public TreeSet<int[]> checkHop(int start_r, int start_c, int hop_r, int hop_c, int iter) {
		if (iter > 10) {
			return new TreeSet<int[]>(new ArrayComparator());
		}
		TreeSet<int[]> possibleMoves = new TreeSet<int[]>(new ArrayComparator());
		int dir_row = hop_r - start_r;
		int new_r = hop_r + dir_row;
		int new_c = (int) (0.5 * board[start_r].length - board[hop_r].length + 0.5 * board[new_r].length
				- start_c + 2* hop_c);
		FieldValue value = checkFieldValue(new_r, new_c);
		if (value.equals(FieldValue.EMPTY)) {
			int[] new_field = new int[] {new_r, new_c};
			possibleMoves.add(new_field);
			int[][] check = checkArray(new_r, new_c);
			for (int[] elem: check) {
				FieldValue value_check;
				//The Out_of_bounds fields ensure that the algorithm will work for all rows,
				//except for row 4/5 and 13/14, where checking the hops will cause ArrayIndexOutofBoundsExceptions
				//We catch these and simply return Out_Of_Bounds FielValues,
				//since no further action is required in these cases anyway.
				try {
					value_check = checkFieldValue(elem[0], elem[1]);
				} catch (ArrayIndexOutOfBoundsException e) {
					value_check = FieldValue.OUT_OF_BOUNDS;
				}

				if(!elem.equals(new int[] {hop_r, hop_c})) {
					switch(value_check) {
					
						case OUT_OF_BOUNDS: case EMPTY: case POSSIBLE:
							break;
							
						default:
							possibleMoves.addAll(checkHop(new_r, new_c, elem[0], elem[1], iter+1));
							break;
					}			
				}
			}
		}
		return possibleMoves;
	}
	
	//moves the selected piece to the selected possible position
	public void move(int row, int column) {
		updateValue(checkField(row, column), turn);
		updateValue(checkField(selectField[0], selectField[1]), FieldValue.EMPTY);
		getScore(turn);
		changeToEmpty();
		if (playing) {
			if (turn.equals(FieldValue.RED)) {
				turn = FieldValue.GREEN;
			}
			else {
				turn = FieldValue.RED;
			}
			selected = false;
			status.setText("It's " + turn.toString() +"'s turn.");
		}

	}
	
	//changes the FieldValue of all Fields that are 'Selected' or 'Possible' back to empty/color
	public void changeToEmpty() {
		for (Field[] row: board) {
			for (Field piece: row) {
				if (piece.getValue().equals(FieldValue.POSSIBLE)) {
					updateValue(piece, FieldValue.EMPTY);
				}
				else if (piece.getValue().equals(FieldValue.SELECT)) {
					updateValue(piece, turn);
				}
			}
		}
	}
	
	//updates the score of the player who just made a move and updated the 'winning' label.
	//ends the game if one player has all his or her pieces on the other side.
	//when this happens the game stops and can be reset.
	//The 'winning' and 'status' panels are updated to communicate this to the players.
	public void getScore(FieldValue player) {
		int score = 0;
		switch(player) {
			case RED:
				for (int i = 1; i< 5; i++) {
					for (Field piece: board[i]) {
						if (piece.getValue().equals(FieldValue.RED)) {
							score = score + 1;
							}
						}
					}
				redScore = score;
				redScoreLabel.setText("Red has " + score + " pieces in place. ");
				break;
				
			case GREEN:
				for (int i = 14; i< 18; i++) {
					for (Field piece: board[i]) {
						if (piece.getValue().equals(FieldValue.GREEN)) {
							score = score + 1;
						}
					}
				}
				greenScoreLabel.setText("Green has " + score + " pieces in place. ");
				greenScore = score;
				break;
				
			default:
				break;
		}
		if (score == 10) {
			winning.setText("Game over. " + player.toString() + " has won!");
			status.setText("Click the reset button to start a new game.");
			playing = false;		
		}
		
		else if (greenScore > redScore) {
			winning.setText("Green is winning.");		
		}
		
		else if (redScore > greenScore) {
			winning.setText("Red is winning.");
		}
		
		else {
			winning.setText("The score is tied.");
		}
		
	}
	
	//updates the FielValue of the selected Field and sets the Icon of the button.
	public void updateValue(Field piece, FieldValue new_color) {
		piece.changeValue(new_color);
		switch(new_color) {
		
			case EMPTY:
				piece.setIcon(empty);
				break;
				
			case OUT_OF_BOUNDS:
				piece.setIcon(outofbound);
				break;
				
			case POSSIBLE:
				piece.setIcon(possible);
				break;
				
			case SELECT:
				piece.setIcon(select);
				break;
				
			case RED:
				piece.setIcon(red);
				break;
				
			case GREEN:
				piece.setIcon(green);
				break;
				
			default:
				break;					
		}
	}

	/**
	 * (Re-)set the game to its initial state.
	 */
	public void reset() {
		removeAll();
		selected = false;

		turn= FieldValue.RED;
		selectField = new int[2];
		board = new Field[19][];
		redScore = 0;
		greenScore = 0;
		
		int number_of_rows = 0;
		int position_in_row = 0;
		
		//adds Fields to the board
		for (int i = 0; i < rowLength.length; i++) {
			position_in_row = 0;
			Field[] row = new Field[rowLength[i]];
			row[0] = new Field(FieldValue.OUT_OF_BOUNDS, number_of_rows, position_in_row, outofbound);
			position_in_row = position_in_row +1;
			for (int j = 1; j < rowLength[i] - 1; j++) {
				row[j]= new Field(FieldValue.EMPTY, number_of_rows, position_in_row, empty);
				position_in_row = position_in_row + 1;
			}
			row[rowLength[i]-1] = new Field(FieldValue.OUT_OF_BOUNDS, number_of_rows, position_in_row, outofbound);
			board[i] = row;
			number_of_rows = number_of_rows + 1;
		}
		
		//sets the beginning position for green
		for (int i = 1; i< 5; i++) {
			for (Field piece: board[i]) {
				if (piece.getValue().equals(FieldValue.EMPTY)) {
					updateValue(piece, FieldValue.GREEN);
				}
			}
		}
		
		//sets the beginning position for red
		for (int i = 14; i< 18; i++) {
			for (Field piece: board[i]) {
				if (piece.getValue().equals(FieldValue.EMPTY)) {
					updateValue(piece, FieldValue.RED);
				}
			}
		}	
		
		//adds JPanels representing the different rows to the game court
		//we then add a Fieldlistener to all the Fields that are not out_of_bounds
		setLayout(new GridLayout(19,1, 0, 0));
		setBackground(Color.WHITE);
		JPanel[] panels = new JPanel[19];
		for (int x = 0; x < panels.length; x++) {
			JPanel new_panel = new JPanel();
			new_panel.setBackground(Color.WHITE);
			panels[x] = new_panel;
			for (Field piece: board[x]) {
				if(!piece.getValue().equals(FieldValue.OUT_OF_BOUNDS)) {
					piece.addActionListener( 
							new FieldListener(piece.getRow(), piece.getColumn()));
					panels[x].add(piece);
				}
			}
		}
		for (int i = 0; i < panels.length; i++) {
			add(panels[i]);
		}
		

		playing = true;
		status.setText("New Game - RED begins");
		redScoreLabel.setText("Red has 0 pieces in place.");
		greenScoreLabel.setText("Green has 0 pieces in place.");
		winning.setText("The score is tied.");

		// Make sure that this component has the keyboard focus
		requestFocusInWindow();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(COURT_WIDTH, COURT_HEIGHT);
	}
}

