package com.gradescope.spampede;

import java.awt.Color;
import java.lang.Math;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.event.CellEditorListener;

/**
 * The "model" in MVC that is responsible for storing all the data for the
 * board.
 * 
 * @author CS60 instructors
 */
class SpampedeData {
	/**
	 * The collection of all the BoardCells in the program, indexed by row and
	 * column.
	 * 
	 * <p>
	 * All BoardCells needed by the program are created by the SpampedeData
	 * constructor, so you do not need to create any new BoardCells in your code.
	 * Instead, you will pass around (references to) existing cells, and change the
	 * contents of some of these cells.
	 * </p>
	 */
	private final BoardCell[][] boardCells2D;

	/**
	 * The number of non-wall cells in the initial board.
	 */
	private int freeSpots = 0;

	/**
	 * The current movement "mode" of the snake, i.e. whether it is headed in a
	 * particular direction or in AI mode.
	 */
	private SnakeMode currentMode = SnakeMode.GOING_EAST;

	/**
	 * A list of (references to) cells that currently contain spam, ordered from
	 * oldest (first) to youngest (last).
	 */
	private LinkedList<BoardCell> spamCells = new LinkedList<BoardCell>();

	/**
	 * A list of (references to) the cells that contain the snake. The head is the
	 * last element of the list.
	 */
	private LinkedList<BoardCell> snakeCells = new LinkedList<BoardCell>();

	/**
	 * Whether the game is over.
	 */
	private boolean gameOver = false;

	/* -------------------------------------- */
	/* Constructor and initialization methods */
	/* -------------------------------------- */

	/**
	 * Creates a new "board" with walls on the boundary and open in the interior.
	 */
	public SpampedeData() {
		int height = Preferences.NUM_CELLS_TALL;
		int width = Preferences.NUM_CELLS_WIDE;
		this.boardCells2D = new BoardCell[height][width];

		// Place walls around the outside
		this.addWalls();

		// Fill the remaining cells not already filled!
		this.fillRemainingCells();
	}

	/**
	 * Adds walls around the edges of this board.
	 */
	private void addWalls() {
		int height = this.getNumRows();
		int width = this.getNumColumns();

		// Add left and right walls
		for (int row = 0; row < height; row++) {
			this.boardCells2D[row][0] = new BoardCell(row, 0, CellType.WALL);
			this.boardCells2D[row][width - 1] = new BoardCell(row, width - 1, CellType.WALL);
		}
		// Add top and bottom walls
		for (int column = 0; column < width; column++) {
			this.boardCells2D[0][column] = new BoardCell(0, column, CellType.WALL);
			this.boardCells2D[height - 1][column] = new BoardCell(height - 1, column, CellType.WALL);
		}
	}

	/**
	 * Adds open cells to the interior of this board.
	 */
	private void fillRemainingCells() {
		int height = this.getNumRows();
		int width = this.getNumColumns();

		this.freeSpots = 0;
		for (int row = 0; row < height; row++) {
			for (int column = 0; column < width; column++) {
				if (this.boardCells2D[row][column] == null) {
					this.boardCells2D[row][column] = new BoardCell(row, column, CellType.OPEN);
					this.freeSpots++;
				}
			}
		}
	}

	/**
	 * Puts the snake in the upper-left corner of the walls, facing east.
	 */
	public void placeSnakeAtStartLocation() {
		BoardCell body = this.getCell(1, 1);
		BoardCell head = this.getCell(1, 2);
		this.snakeCells.addLast(body);
		this.snakeCells.addLast(head);
		head.becomeHead();
		body.becomeBody();
	}

	/* ---------------------------------------------- */
	/* Methods to access information about this board */
	/* ---------------------------------------------- */

	/**
	 * Returns true if we are in AI mode.
	 */
	public boolean inAImode() {
		return this.currentMode == SnakeMode.AI_MODE;
	}

	/**
	 * Returns the height of this board (including walls) in cells.
	 */
	public int getNumRows() {
		return this.boardCells2D.length;
	}

	/**
	 * Returns the width of this board (including walls) in cells.
	 */
	public int getNumColumns() {
		return this.boardCells2D[0].length;
	}

	/**
	 * Accesses a cell at a particular location.
	 * 
	 * <p>
	 * This method should really be private. We make it public to allow our unit
	 * tests to use it, but it should not be called from SpampedeBrain or
	 * SpampedeDisplay.
	 * </p>
	 * 
	 * @param r - the row to access, between 0 and numRows-1 inclusive
	 * @param c - the column to access, between 0 and numCols-1 inclusive
	 * @return the cell in row r and column c
	 */
	protected BoardCell getCell(int r, int c) {
		if (r >= this.getNumRows() || c >= this.getNumColumns() || r < 0 || c < 0) {
			System.err.println("Trying to access cell outside of the Board:");
			System.err.println("row: " + r + " col: " + c);
			System.exit(0);
		}
		return this.boardCells2D[r][c];
	}

	/* ------------------------------ */
	/* Helper method used by the view */
	/* ------------------------------ */

	/**
	 * Gets the color of the cell at a particular location.
	 * 
	 * @param r - the row to access, between 0 and numRows-1 inclusive
	 * @param c - the column to access, between 0 and numCols-1 inclusive
	 * @return the color of cell at row r and column c
	 */
	public Color getCellColor(int row, int col) {
		BoardCell cell = getCell(row, col);
		return cell.getCellColor();
	}

	/* ---------------- */
	/* Game over status */
	/* ---------------- */

	/**
	 * Sets the game status as game over.
	 */
	public void setGameOver() {
		this.gameOver = true;
	}

	/**
	 * Returns true if the game over message should be displayed.
	 */
	public boolean getGameOver() {
		return this.gameOver;
	}

	/* -------------------- */
	/* Spam-related methods */
	/* -------------------- */

	/**
	 * Returns true if there is zero spam.
	 */
	public boolean noSpam() {
		return this.spamCells.isEmpty();
	}

	/**
	 * Adds spam to a random open spot.
	 */
	public void addSpam() {
		// Pick a random cell
		int row = (int) (this.getNumRows() * Math.random());
		int column = (int) (this.getNumColumns() * Math.random());
		BoardCell cell = this.getCell(row, column);

		if (cell.isOpen()) {
			// If the random cell is open, put spam there
			cell.becomeSpam();
			spamCells.addLast(cell);
		} else {
			// If the random cell is occupied and this board is not already
			// too full of spam, try to place spam again
			double totalSize = this.getNumColumns() * this.getNumRows();
			double currentFreeSpots = this.freeSpots - this.snakeCells.size() - this.spamCells.size();
			double ratioFree = currentFreeSpots / totalSize;
			if (ratioFree < 0.2) {
				System.err.println("Not adding more spam");
			} else {
				addSpam();
			}
		}
	}

	/**
	 * Removes the oldest piece of un-eaten spam.
	 * 
	 * <p>
	 * The function is not used in the given code, but it might be useful if you
	 * want to extend the game.
	 * </p>
	 */
	@SuppressWarnings("unused")
	private void removeSpam() {
		if (!spamCells.isEmpty()) {
			spamCells.peekFirst().becomeOpen();
			spamCells.removeFirst();
		}
	}

	/* -------------------- */
	/* Snake access methods */
	/* -------------------- */

	/**
	 * Returns the cell containing the snake's head.
	 */
	public BoardCell getSnakeHead() {
		return this.snakeCells.peekLast();
	}

	/**
	 * Returns the cell containing the snake's tail.
	 */
	public BoardCell getSnakeTail() {
		return this.snakeCells.peekFirst();
	}

	/**
	 * Returns the cell contains the snake body adjacent to the head.
	 */
	public BoardCell getSnakeNeck() {
		int lastSnakeCellIndex = this.snakeCells.size() - 1;
		return this.snakeCells.get(lastSnakeCellIndex - 1);
	}

	/* ------------------------------------------ */
	/* Methods to set the snake's (movement) mode */
	/* ------------------------------------------ */

	/**
	 * Makes the snake head north.
	 */
	public void setDirectionNorth() {
		this.currentMode = SnakeMode.GOING_NORTH;
	}

	/**
	 * Makes the snake head south.
	 */
	public void setDirectionSouth() {
		this.currentMode = SnakeMode.GOING_SOUTH;
	}

	/**
	 * Makes the snake head east.
	 */
	public void setDirectionEast() {
		this.currentMode = SnakeMode.GOING_EAST;
	}

	/**
	 * Makes the snake head west.
	 */
	public void setDirectionWest() {
		this.currentMode = SnakeMode.GOING_WEST;
	}

	/**
	 * Makes the snake switch to AI mode.
	 */
	public void setMode_AI() {
		this.currentMode = SnakeMode.AI_MODE;
	}

	/**
	 * Picks an initial movement mode for the snake.
	 */
	public void setStartDirection() {
		this.setDirectionEast();
	}

	/* ---------------------- */
	/* Snake movement methods */
	/* ---------------------- */
	/**
	 * Moves the snake's head to the given cell. If the cell is spam, the snake eats
	 * the spam and grows by one cell.
	 * 
	 * @param cell The next cell
	 */
	public void moveSnakeForward(BoardCell cell) {
		this.updateSnakeCellsForSpam(cell);
	}

	/* -------------------------------------- */
	/* Methods to support movement without AI */
	/* -------------------------------------- */

	/**
	 * These methods should really be private. We make them public to allow access
	 * by our unit tests, but the methods should not be called from SpampedeBrain or
	 * SpampedeDisplay.
	 */

	/**
	 * Updates the snakeCells LinkedList depending on whether the snake head has a
	 * spam or not. Regardless of there being a spam or not, we add the cell to
	 * snakeCells. If there was a spam, we leave snakeCells as is. If not, we remove
	 * the first element of snakeCells.
	 */
	private void updateSnakeCellsForSpam(BoardCell cell) {
		snakeCells.addLast(cell);

		if (!cell.isSpam()) { // remove tail if no spam was eaten
			this.getSnakeTail().becomeOpen();
			snakeCells.removeFirst();
		}

		this.getSnakeNeck().becomeBody(); // we just change CellType if spam is eaten
		snakeCells.peekLast().becomeHead();
	}

	/**
	 * Returns the cell north of the specified cell, which must not be on the
	 * boundary.
	 */
	protected BoardCell getNorthNeighbor(BoardCell cell) {
		int row = cell.getRow();
		int col = cell.getColumn();
		return this.getCell(row - 1, col);
	}

	/**
	 * Returns the cell south of the specified cell, which must not be on the
	 * boundary.
	 */
	protected BoardCell getSouthNeighbor(BoardCell cell) {
		int row = cell.getRow();
		int col = cell.getColumn();
		return this.getCell(row + 1, col);
	}

	/**
	 * Returns the cell east of the specified cell, which must not be on the
	 * boundary.
	 */
	protected BoardCell getEastNeighbor(BoardCell cell) {
		int row = cell.getRow();
		int col = cell.getColumn();
		return this.getCell(row, col + 1);
	}

	/**
	 * Returns the cell west of the specified cell, which must not be on the
	 * boundary.
	 */
	protected BoardCell getWestNeighbor(BoardCell cell) {
		int row = cell.getRow();
		int col = cell.getColumn();
		return this.getCell(row, col - 1);
	}

	/**
	 * Returns the cell north of the snake's head.
	 */
	protected BoardCell getNorthNeighbor() {
		return this.getNorthNeighbor(this.getSnakeHead());
	}

	/**
	 * Returns the cell south of the snake's head.
	 */
	protected BoardCell getSouthNeighbor() {
		return this.getSouthNeighbor(this.getSnakeHead());
	}

	/**
	 * Returns the cell east of the snake's head.
	 */
	protected BoardCell getEastNeighbor() {
		return this.getEastNeighbor(this.getSnakeHead());
	}

	/**
	 * Returns the cell west of the snake's head.
	 */
	protected BoardCell getWestNeighbor() {
		return this.getWestNeighbor(this.getSnakeHead());
	}

	/**
	 * Returns the cell north, south, east, or west of the snake head based on the
	 * current direction of travel. This method should not be called when in AI
	 * mode, though Java requires the method to return a value regardless.
	 */
	public BoardCell getNextCellInDir() {
		switch (this.currentMode) { // another way of writing if-then-else statements!
		case GOING_WEST:
			return this.getWestNeighbor();
		case GOING_NORTH:
			return this.getNorthNeighbor();
		case GOING_SOUTH:
			return this.getSouthNeighbor();
		default:
			return this.getEastNeighbor();
		}
	}

	/* -------------------------------------- */
	/* Methods to support movement with AI */
	/* -------------------------------------- */

	/**
	 * Searches for the spam closest to the snake head using BFS.
	 * 
	 * @return the cell to move the snake head to, if the snake moves *one step*
	 *         along the shortest path to (the nearest) spam cell
	 */
	public BoardCell getNextCellFromBFS() {
		// initialize the search
		this.resetCellsForNextSearch();

		// initialize the cellsToSearch queue with the snake head;
		// as with any cell, we mark the head cells as having been added
		// to the queue
		Queue<BoardCell> cellsToSearch = new LinkedList<BoardCell>();
		BoardCell snakeHead = this.getSnakeHead();
		snakeHead.setAddedToSearchList();
		cellsToSearch.add(snakeHead);

		// search!
		while (!cellsToSearch.isEmpty()) { // follow the queue method shown on the slides!
			BoardCell frontOfQueue = cellsToSearch.remove();

			if (frontOfQueue.isSpam()) { // we have found our spam!
				return this.getFirstCellInPath(frontOfQueue);
			}

			BoardCell[] neighbors = this.getNeighbors(frontOfQueue);
			for (BoardCell neighbor : neighbors) {
				if (!neighbor.inSearchListAlready() && neighbor.isOpen()) { // no cycles!
					cellsToSearch.add(neighbor);
					neighbor.setAddedToSearchList();
					neighbor.setParent(frontOfQueue);
				}
			}
		}

		// if the search fails, just move somewhere
		return this.getRandomNeighboringCell(snakeHead);
	}
	/**
	 * Follows the traceback pointers from the closest spam cell to decide where the
	 * head should move. Specifically, follows the parent pointers back from the
	 * spam until we find the cell whose parent is the snake head (and which must
	 * therefore be adjacent to the previous snake head location).
	 * 
	 * @param start - the cell from which to start following pointers, typically the
	 *              location of the spam closest to the snake head
	 * @return the cell to move the snake head to, which should be a neighbor of the
	 *         head
	 */
	private BoardCell getFirstCellInPath(BoardCell start) {
		// recursive or looping solutions are possible
		BoardCell checkCell = start;

		if (this.noSpam()) { // if there are no spam, there is no automatic path to a spam...
			return null;
		}

		if (checkCell == this.getSnakeHead()) { // if the spam is on snakeHead, just return snakeHead
			return checkCell;
		}

		while (checkCell.getParent() != this.getSnakeHead()) { // the shortest path to spam!
			checkCell = checkCell.getParent();
		}

		return checkCell;
	}

	/* -------------------------------------------------- */
	/* Public methods to get all or one (random) neighbor */
	/* -------------------------------------------------- */

	/**
	 * Returns an array of the four neighbors of the specified cell.
	 */
	public BoardCell[] getNeighbors(BoardCell center) {
		BoardCell[] neighborsArray = { getNorthNeighbor(center), getSouthNeighbor(center), getEastNeighbor(center),
				getWestNeighbor(center) };
		return neighborsArray;
	}

	/**
	 * Returns a random open neighbor of the specified cell (or some other neighbor
	 * if there are no open neighbors).
	 */
	public BoardCell getRandomNeighboringCell(BoardCell start) {
		BoardCell[] neighborsArray = getNeighbors(start);
		for (BoardCell mc : neighborsArray) {
			if (mc.isOpen()) {
				return mc;
			}
		}
		// if we did not find an open space, return the first neighbor
		return neighborsArray[0];
	}

	/* ---------------------------- */
	/* Helper method(s) for reverse */
	/* ---------------------------- */

	/**
	 * Reverses the snake back-to-front and updates the movement mode appropriately.
	 */
	public void reverseSnake() {
		// Step 1: unlabel the head
		this.getSnakeHead().becomeBody();

		// Step 2: reverse the body parts
		int snakeSize = snakeCells.size(); // we don't want snakeSize to change as we remove cells
		LinkedList<BoardCell> newSnakeCells = new LinkedList<BoardCell>();
		for (int i = 0; i < snakeSize; i++) {
			newSnakeCells.addFirst(snakeCells.remove());
		}

		this.snakeCells = newSnakeCells;

		// Step 3: relabel the head
		this.snakeCells.peekLast().becomeHead();

		// Step 4: calculate the new direction after reversing!
		// We check the position of the new head and neck!
		// We base the snake's movement based on the position of the head relative to
		// its neck.
		if (this.getSnakeHead().getRow() == this.getSnakeNeck().getRow()) {
			if (this.getSnakeHead().getColumn() < this.getSnakeNeck().getColumn()) {
				this.setDirectionWest();
			}

			if (this.getSnakeHead().getColumn() > this.getSnakeNeck().getColumn()) {
				this.setDirectionEast();
			}
		}

		if (this.getSnakeHead().getColumn() == this.getSnakeNeck().getColumn()) {
			if (this.getSnakeHead().getRow() > this.getSnakeNeck().getRow()) {
				this.setDirectionSouth();
			}

			if (this.getSnakeHead().getRow() < this.getSnakeNeck().getRow()) {
				this.setDirectionNorth();
			}
		}
	}

	/* ------------------------------------- */
	/* Methods to reset the model for search */
	/* ------------------------------------- */

	/**
	 * Clears the search-related fields in all the cells, in preparation for a new
	 * breadth-first search.
	 */
	public void resetCellsForNextSearch() {
		for (BoardCell[] row : this.boardCells2D) {
			for (BoardCell cell : row) {
				cell.clear_RestartSearch();
			}
		}
	}

	/* -------------------------------------------------------------------- */
	/* Testing Infrastructure - You do not need to understand these methods */
	/* -------------------------------------------------------------------- */

	/**
	 * Pictures of test boards at http://tinyurl.com/spampedeTestBoards
	 */

	// Constructor used exclusively for testing!
	public SpampedeData(TestGame gameNum) {
		this.boardCells2D = new BoardCell[6][6];
		this.addWalls();
		this.fillRemainingCells();
		if (gameNum.snakeAtStart()) {
			this.testing_snakeAtStartLocation(gameNum);
			this.setDirectionEast();
		} else {
			this.testing_snakeNotAtStartLocation(gameNum);
		}

	}

	private void testing_snakeAtStartLocation(TestGame gameNum) {
		this.placeSnakeAtStartLocation();
		if (gameNum == TestGame.G1) {
			this.getCell(1, 3).becomeSpam();
		} else if (gameNum == TestGame.G2) {
			this.getCell(2, 2).becomeSpam();
		} else if (gameNum == TestGame.G3) {
			this.getCell(1, 4).becomeSpam();
		} else if (gameNum == TestGame.G4) {
			this.getCell(2, 1).becomeSpam();
		} else if (gameNum == TestGame.G5) {
			this.getCell(4, 1).becomeSpam();
		} else if (gameNum == TestGame.G6) {
			this.getCell(1, 3).becomeSpam();
			this.getCell(3, 1).becomeSpam();
		} else if (gameNum == TestGame.G7) {
			this.getCell(2, 2).becomeSpam();
			this.getCell(1, 4).becomeSpam();
		} else if (gameNum == TestGame.G8) {
			this.getCell(1, 4).becomeSpam();
			this.getCell(4, 2).becomeSpam();
		} else if (gameNum == TestGame.G9) {
			this.getCell(2, 1).becomeSpam();
			this.getCell(2, 4).becomeSpam();
		} else if (gameNum == TestGame.G10) {
			this.getCell(4, 1).becomeSpam();
			this.getCell(4, 4).becomeSpam();
		} else if (gameNum == TestGame.G11) {
			// No spam :)
		}
		// Add all spam to the spam cells
		int height = this.getNumRows();
		int width = this.getNumColumns();
		for (int row = 0; row < height; row++) {
			for (int column = 0; column < width; column++) {
				BoardCell cell = this.getCell(row, column);
				if (cell.isSpam()) {
					this.spamCells.add(cell);
				}
			}
		}
	}

	private void testing_snakeNotAtStartLocation(TestGame gameNum) {
		if (gameNum == TestGame.G12) {
			BoardCell body2 = this.getCell(2, 3);
			BoardCell body1 = this.getCell(2, 2);
			BoardCell head = this.getCell(2, 1);
			this.snakeCells.add(body2);
			this.snakeCells.add(body1);
			this.snakeCells.add(head);
			head.becomeHead();
			body2.becomeBody();
			body1.becomeBody();
		} else if (gameNum == TestGame.G13) {
			BoardCell body2 = this.getCell(3, 2);
			BoardCell body1 = this.getCell(2, 2);
			BoardCell head = this.getCell(2, 1);
			this.snakeCells.add(body2);
			this.snakeCells.add(body1);
			this.snakeCells.add(head);
			head.becomeHead();
			body2.becomeBody();
			body1.becomeBody();
		} else if (gameNum == TestGame.G14) {
			BoardCell body2 = this.getCell(2, 2);
			BoardCell body1 = this.getCell(3, 2);
			BoardCell head = this.getCell(3, 1);
			this.snakeCells.add(body2);
			this.snakeCells.add(body1);
			this.snakeCells.add(head);
			head.becomeHead();
			body2.becomeBody();
			body1.becomeBody();
		} else if (gameNum == TestGame.G15) {
			BoardCell body2 = this.getCell(3, 2);
			BoardCell body1 = this.getCell(3, 3);
			BoardCell head = this.getCell(3, 4);
			this.snakeCells.add(body2);
			this.snakeCells.add(body1);
			this.snakeCells.add(head);
			head.becomeHead();
			body2.becomeBody();
			body1.becomeBody();
		}
	}

	public String toString() {
		String result = "";
		for (int r = 0; r < this.getNumRows(); r++) {
			for (int c = 0; c < this.getNumColumns(); c++) {
				BoardCell cell = this.getCell(r, c);
				result += cell.toStringType();
			}
			result += "\n";
		}
		return result;
	}

	public String toStringParents() {
		String result = "";
		for (int r = 0; r < this.getNumRows(); r++) {
			for (int c = 0; c < this.getNumColumns(); c++) {
				BoardCell cell = this.getCell(r, c);
				result += cell.toStringParent() + "\t";
			}
			result += "\n";
		}
		return result;
	}

}
