package com.gradescope.spampede;

import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;

/**
 * Provides back-end for SpampedeBrain and SpampedeDisplay.
 * 
 * 
 * @author Isaac Chung with the help of professors
 */
public abstract class SpampedeBrainParent extends JApplet implements ActionListener, KeyListener, Runnable {

	/** The off-screen buffer of image */
	public Image image;

	/** The buffer's graphical tools */
	public Graphics screen;

	/** Buttons */
	private JButton newGameButton;
	private JButton pauseButton;
	private JButton startButton;

	/** Drop-down menu */
	private JMenu gameMenu;
	private JMenuItem newGameItem;
	private JMenuItem pauseItem;
	private JMenuItem startItem;

	/** Fun sounds (optional) */
	public AudioClip audioSpam; // spam sound
	public AudioClip audioCrunch; // crunch sound
	public AudioClip audioMeow; // meow sound

	/* ---------------------------------------------------------------------------- */
	/* Methods to initialize the applet and register listeners for user interaction */
	/* ---------------------------------------------------------------------------- */
	
	/**
	 * Initializes this applet. This method is called when Spampede is started (not
	 * per game)!
	 */
	@Override
	public void init() {

		// register w/the applet (i.e. parent) so it calls our keyPressed method
		this.addKeyListener(this);

		// set positions
		this.setLayout(new BorderLayout());

		// initialize controls
		this.initializeButtons();
		this.initializeMenu();

		// set up the (off-screen) buffer for drawing, named image
		this.image = this.createImage(this.getSize().width, Preferences.GAMEBOARDHEIGHT);
		this.screen = this.image.getGraphics(); // screen holds the drawing routines

		// add a central panel which holds the buffer (the game board)
		this.add(new SpampedeImagePanel(image), BorderLayout.CENTER);

		// example of loading images and audio
		try {
			URL url = this.getCodeBase();
			this.audioSpam = this.getAudioClip(url, "Spam.au");
			this.audioCrunch = this.getAudioClip(url, "crunch.au");
			this.audioMeow = this.getAudioClip(url, "cat.au");
			SpampedeDisplay.imageSpam = this.getImage(url, "spam.gif");
			System.out.println("successful loading of audio/images!");
		} catch (Exception e) {
			System.out.println("problem loading audio/images!");
			this.audioSpam = null;
			this.audioCrunch = null;
			this.audioMeow = null;
			SpampedeDisplay.imageSpam = null;
		}
		this.startNewGame(); // set up the game internals!
		super.repaint(); // re-render the environment to the screen
	}

	/**
	 * Initializes all buttons.
	 */
	private void initializeButtons() {
		// add a panel for buttons
		JPanel buttonPane = new JPanel(new FlowLayout());
		buttonPane.setBackground(Preferences.COLOR_BACKGROUND);
		this.add(buttonPane, BorderLayout.PAGE_START);

		this.newGameButton = new JButton("New Game"); // the text in the button
		this.newGameButton.addActionListener(this); // watch for button presses
		this.newGameButton.addKeyListener(this); // listen for key presses here
		buttonPane.add(this.newGameButton); // add button to the panel

		this.pauseButton = new JButton("Pause"); // a second button
		this.pauseButton.addActionListener(this);
		this.pauseButton.addKeyListener(this);
		buttonPane.add(this.pauseButton);

		this.startButton = new JButton("Start"); // a third button
		this.startButton.addActionListener(this);
		this.startButton.addKeyListener(this);
		buttonPane.add(this.startButton);
	}

	/**
	 * Initializes all menu items.
	 */
	private void initializeMenu() {
		// set up the menu bar
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);

		// add a menu to contain items
		this.gameMenu = new JMenu("Game"); // the menu name
		menuBar.add(gameMenu); // add the menu to the menu bar

		this.newGameItem = new JMenuItem("New Game"); // the text in the menu
		this.newGameItem.addActionListener(this); // watch for button presses
		this.newGameItem.addKeyListener(this); // listen for key presses here
		this.gameMenu.add(this.newGameItem); // add the item to the menu

		this.pauseItem = new JMenuItem("Pause"); // a second menu item
		this.pauseItem.addActionListener(this);
		this.pauseItem.addKeyListener(this);
		this.gameMenu.add(this.pauseItem);

		this.startItem = new JMenuItem("Start"); // a third menu item
		this.startItem.addActionListener(this);
		this.startItem.addKeyListener(this);
		this.gameMenu.add(this.startItem);
	}
	
	/**
	 * Processes buttons and menu items.
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		if (source == this.newGameButton || source == this.newGameItem) {
			this.startNewGame();
			this.go();
		}
		if (source == this.pauseButton || source == this.pauseItem) {
			this.pause();
		}
		if (source == this.startButton || source == this.startItem) {
			this.go();
		}
		this.requestFocus(); // makes sure this applet keeps keyboard focus
	}

	@Override
	public void keyReleased(KeyEvent evt) {
		// Not used
	}

	@Override
	public void keyTyped(KeyEvent evt) {
		// Not used
	}

	/* --------------------------------------------------------------- */
	/* Fields and methods are used to implement the Runnable interface */
	/* and to support pausing and resuming the applet. */
	/* --------------------------------------------------------------- */

	Thread thread; // the thread controlling the updates
	boolean threadSuspended; // whether or not the thread is suspended
	boolean running; // whether or not the thread is stopped

	/**
	 * Called to run this applet.
	 */
	@Override
	public void run() {
		// calls the "cycle()" method every so often (every sleepTime milliseconds)
		while (this.running) {
			try {
				if (this.thread != null) {
					Thread.sleep(Preferences.SLEEP_TIME);
					synchronized (this) {
						while (this.threadSuspended) {
							this.wait(); // sleeps until notify() wakes it up
						}
					}
				}
			} catch (InterruptedException e) {
				;
			}

			this.cycle(); // this represents 1 update cycle for the environment
		}
		this.thread = null;
	}

	/**
	 * Called when the "Start" button is pressed.
	 */
	public synchronized void go() {
		if (this.thread == null) {
			this.thread = new Thread(this);
			this.running = true;
			this.thread.start();
			this.threadSuspended = false;
		} else {
			this.threadSuspended = false;
		}
		this.notify(); // wakes up the call to wait(), above
	}

	/**
	 * Called when the "Pause" button is pressed.
	 */
	void pause() {
		if (this.thread == null) {
			;
		} else {
			this.threadSuspended = true;
		}
	}

	/**
	 * Called when the user leaves the page that contains the applet. It stops the
	 * thread altogether.
	 */
	public synchronized void stop() {
		this.running = false;
		this.notify();
	}

	/* ----------------------------------------------------------------- */
	/* Methods that will be overridden to provide Spampede functionality */
	/* ----------------------------------------------------------------- */

	/** Cycles a game through one "step" */
	abstract void cycle();

	/** Starts a new game */
	abstract void startNewGame();

	@Override
	public abstract void keyPressed(KeyEvent evt);

	private static final long serialVersionUID = 1L;
}
