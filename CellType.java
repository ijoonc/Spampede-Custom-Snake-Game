package com.gradescope.spampede;

/**
 * The type of a board cell (wall, open, spam, head, body).
 * 
 * @author Isaac Chung with the help of professors
 */
enum CellType {
	WALL("*"), OPEN(" "), SPAM("X"), HEAD("H"), BODY("B");

	private final String displayChar;

	private CellType(String inputChar) {
		this.displayChar = inputChar;
	}

	/** Returns a String representing the CellType */
	public String getDisplayChar() {
		return this.displayChar;
	}

}
