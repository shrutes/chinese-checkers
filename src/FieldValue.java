//An enumeration of all the possible values a field can have.
//in addition to the different pieces, a field can be:
//'empty' if it contains no piece
//'out of bounds' to create the edges of the field
//'possible' to highlights fields that the 'select'ed piece can move to
public enum FieldValue {
	GREEN,
	BLUE,
	YELLOW,
	ORANGE,
	PURPLE,
	RED,
	SELECT,
	EMPTY,
	POSSIBLE,
	OUT_OF_BOUNDS;
}
