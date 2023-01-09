package com.gradescope.spampede;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

/**
 * The "view" in MVC that is responsible for drawing the board on the screen.
 * 
 * @author Isaac Chung with the help of professors
 */
class SpampedeDisplay {

	/** The board/spampede data being drawn. */
	private final SpampedeData theData;

	/** The display where the board is drawn. */
	private final Graphics theScreen;

	/** The width of the display in pixels. */
	private final int width;

	/** The height of the display in pixels. */
	private final int height;

	/** A picture of a can of spam. */
	public static Image imageSpam;

	/**
	 * Creates a new SpampedeDisplay.
	 * 
	 * @param theBoard  the data being displayed
	 * @param theScreen the display on which to draw the board
	 * @param width     the width of the display (in pixels)
	 * @param height    the height of the display (in pixels)
	 */
	public SpampedeDisplay(SpampedeData theBoard, Graphics theScreen, int width, int height) {
		this.theScreen = theScreen;
		this.theData = theBoard;
		this.height = height;
		this.width = width;
	}

	/* -------------------- */
	/* Displaying the board */
	/* -------------------- */

	/**
	 * Re-draws the board, spam, and snake (but not the buttons).
	 */
	public void updateGraphics() {
		// Draw the background -- DO NOT REMOVE!
		this.clear();

		// Draw the title
		this.displayTitle();

		// Draw the board
		int xStart = (this.width - Preferences.CELL_SIZE * Preferences.NUM_CELLS_WIDE) / 2;
		int yStart = this.height / Preferences.CELL_SIZE;
		int numRows = this.theData.getNumRows();
		int numCols = this.theData.getNumColumns();

		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				this.drawSquare(xStart + (Preferences.CELL_SIZE * col), yStart + (Preferences.CELL_SIZE * row),
						this.theData.getCellColor(row, col));
			}
		}

		// Display an image, just for fun
		if (SpampedeDisplay.imageSpam != null) { // centered the spam image!
			this.theScreen.drawImage(SpampedeDisplay.imageSpam, Preferences.TITLE_X + 150, 370, null);
		}

		// Draw the game-over message, if appropriate
		if (this.theData.getGameOver()) {
			this.displayGameOver();
		}

	}

	/**
	 * Draws a cell-sized square with its upper-left corner at the specified pixel
	 * coordinates (i.e. x pixels to the right and y pixels below the upper-left
	 * corner) on the display.
	 * 
	 * @param x         the x-coordinate, between 0 and this.width-1 inclusive
	 * @param y         the y-coordinate, between 0 and this.height-1 inclusive
	 * @param cellColor the color of the square being drawn
	 */
	private void drawSquare(int x, int y, Color cellColor) {
		this.theScreen.setColor(cellColor);
		this.theScreen.fillRect(x, y, Preferences.CELL_SIZE, Preferences.CELL_SIZE);
	}

	/**
	 * Draws the background. DO NOT MODIFY!
	 */
	private void clear() {
		this.theScreen.setColor(Preferences.COLOR_BACKGROUND);
		this.theScreen.fillRect(0, 0, this.width, this.height);
		this.theScreen.setColor(Preferences.TITLE_COLOR);
		this.theScreen.drawRect(0, 0, this.width - 1, Preferences.GAMEBOARDHEIGHT - 1);
	}

	/* ------------ */
	/* Text Display */
	/* ------------ */

	/**
	 * Displays the title of the game.
	 */
	private void displayTitle() { // centered the title to the middle of the board!
		this.theScreen.setFont(Preferences.TITLE_FONT);
		this.theScreen.setColor(Preferences.TITLE_COLOR);
		this.theScreen.drawString(Preferences.TITLE, Preferences.TITLE_X + 125,

				Preferences.TITLE_Y);
	}

	/**
	 * Displays the game-over message.
	 */
	private void displayGameOver() {
		this.theScreen.setFont(Preferences.GAME_OVER_FONT);
		this.theScreen.setColor(Preferences.GAME_OVER_COLOR);
		this.theScreen.drawString(Preferences.GAME_OVER_TEXT, Preferences.GAME_OVER_X, Preferences.GAME_OVER_Y);
	}

}
