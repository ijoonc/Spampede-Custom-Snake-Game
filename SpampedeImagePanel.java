package com.gradescope.spampede;

import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics;

/**
 * Implements low-level graphics work.
 * 
 * DO NOT MODIFY.
 * 
 * @author CS60 instructors
 */
public class SpampedeImagePanel extends JPanel {

	/** The image that this panel draws */
	Image myImage;

	/** Constructs a new SpampedeImagePanel */
	public SpampedeImagePanel(Image inputImage) {
		// store the image
		this.myImage = inputImage;

		// calculate the dimensions of the panel
		int height = inputImage.getHeight(null);
		int width = inputImage.getWidth(null);
		Dimension dimensions = new java.awt.Dimension(width, height);
		super.setPreferredSize(dimensions);
	}

	/** Draws the image on the panel */
	@Override
	public void paint(Graphics graphicsObj) {
		graphicsObj.drawImage(this.myImage, 0, 0, null);
	}

	/** Added to avoid a warning - not used! */
	private static final long serialVersionUID = 1L;
}
