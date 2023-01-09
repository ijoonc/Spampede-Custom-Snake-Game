package com.gradescope.spampede;

import java.awt.Color;

/**
 * Represents a single cell within a board.
 * 
 * @author CS60 instructors
 */
class BoardCell {

	/* ----------------------------- */
	/* Basic contents of a BoardCell */
	/* ----------------------------- */

	/** The row of this cell within the board (non-negative). */
	private final int row;

	/** The column of this cell within the board (non-negative). */
	private final int column;

	/** The current contents of this cell. */
	private CellType myCellType;

	/* ---------------------------- */
	/* Variables used during search */
	/* ---------------------------- */

	/** Has this cell been added to the search queue yet? */
	private boolean addedToSearchList = false;

	/** Where did we came from, when search first reached this BoardCell? */
	private BoardCell parent = null;

	/**
	 * Creates a new BoardCell.
	 * 
	 * @param inputRow    the row of this cell
	 * @param inputColumn the column of this cell
	 * @param type        the initial contents of this cell
	 */
	public BoardCell(int inputRow, int inputColumn, CellType type) {
		this.row = inputRow;
		this.column = inputColumn;
		this.myCellType = type;
	}

	/* ------------------------------------- */
	/* Access basic information about a cell */
	/* ------------------------------------- */

	/** Returns the row of this cell. */
	public int getRow() {
		return this.row;
	}

	/** Returns the column of this cell. */
	public int getColumn() {
		return this.column;
	}

	/** Returns true if this cell is a wall. */
	public boolean isWall() {
		return this.myCellType == CellType.WALL;
	}

	/** Returns true if this cell is open (not a wall or a snake body part). */
	public boolean isOpen() {
		return this.myCellType == CellType.OPEN || this.isSpam();
	}

	/** Returns true if this cell contains spam. */
	public boolean isSpam() {
		return this.myCellType == CellType.SPAM;
	}

	/** Returns true if this cell contains a snake body part (not the head). */
	public boolean isBody() {
		return this.myCellType == CellType.BODY;
	}

	/** Returns true if this cell contains the head of the snake. */
	public boolean isHead() {
		return this.myCellType == CellType.HEAD;
	}

	/** Returns the color for drawing this cell. */
	public Color getCellColor() {
		if (this.isWall()) {
			return Preferences.COLOR_WALL;
		} else if (this.isSpam()) {
			return Preferences.COLOR_SPAM;
		} else if (this.isOpen()) {
			return Preferences.COLOR_OPEN;
		} else if (this.isHead()) {
			return Preferences.COLOR_HEAD;
		} else if (this.isBody()) {
			return Preferences.COLOR_BODY;
		} else {
			return Preferences.COLOR_OPEN;
		}
	}

	/* ------------------------------ */
	/* Modify basic info about a cell */
	/* ------------------------------ */

	/** Marks this BoardCell as spam. */
	public void becomeSpam() {
		this.myCellType = CellType.SPAM;
	}

	/** Marks this BoardCell as open. */
	public void becomeOpen() {
		this.myCellType = CellType.OPEN;
	}

	/** Marks this BoardCell as the snake's head. */
	public void becomeHead() {
		this.myCellType = CellType.HEAD;
	}

	/** Marks this BoardCell as part of the snake's body. */
	public void becomeBody() {
		this.myCellType = CellType.BODY;
	}

	/* -------------------------- */
	/* Access and set search info */
	/* -------------------------- */

	/** Marks this cell as having been added to our BFS search queue. */
	public void setAddedToSearchList() {
		this.addedToSearchList = true;
	}

	/** Returns true if this cell has been added to our BFS search queue. */
	public boolean inSearchListAlready() {
		return this.addedToSearchList;
	}

	/** Clear the search-related info for this cell (to allow a new search). */
	public void clear_RestartSearch() {
		this.addedToSearchList = false;
		this.parent = null;
	}

	/** Sets the parent of this cell in our BFS search. */
	public void setParent(BoardCell p) {
		this.parent = p;
	}

	/** Returns the parent of this cell in our BFS search. */
	public BoardCell getParent() {
		return this.parent;
	}

	/* ---------------------------- */
	/* Helper functions for testing */
	/* ---------------------------- */

	/** Returns this cell as a string "[row, col, type]". */
	public String toString() {
		return "[" + this.row + ", " + this.column + ", " + this.toStringType() + "]";
	}

	/** Returns the contents of this cell, as a single character. */
	public String toStringType() {
		return this.myCellType.getDisplayChar();
	}

	/** Returns the parent of this cell, as a string "[null]" or "[row, col]". */
	public String toStringParent() {
		if (this.parent == null) {
			return "[null]";
		} else {
			return "[" + this.parent.row + ", " + this.parent.column + "]";
		}
	}

}
