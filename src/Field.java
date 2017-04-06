import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

//This class represents one clickable field on the board
public class Field extends JButton{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FieldValue color;
	private final int row;
	private final int column;
	
	//initiates a Field and gives it the layout.
	public Field (FieldValue color, int row, int column, ImageIcon img) {
		super(img);
		setBorder(BorderFactory.createEmptyBorder());
		setMargin(new Insets(0, 0, 0, 0));
		setContentAreaFilled(false);
		//setPreferredSize(new Dimension(30,30));
		this.color = color;
		this.row = row;
		this.column = column;		
	}

	public FieldValue getValue() {
		return color;
	}
	
	public void changeValue (FieldValue new_color) {
		color = new_color;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getColumn() {
		return column;
	}
	
}
