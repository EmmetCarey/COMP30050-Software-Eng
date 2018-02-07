/*
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 * LeftPanel - JPanel containing board 
 * 			 - layout for properties
*/

package Monopoly.UI;

import Monopoly.core.Main;
import Monopoly.properties.Property;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;

public class GameBoardPanel extends JPanel {

	private static int boardDimension;
	private final URL SMALL_BACKGROUND_PATH = getClass().getClassLoader().getResource("board/small400Board.png");
	private final URL MEDIUM_BACKGROUND_PATH = getClass().getClassLoader().getResource("board/medium740Board.png");
	private final URL BIG_BACKGROUND_PATH = getClass().getClassLoader().getResource("board/big1000Board.png");
	private BufferedImage backgroundImage;
	
	public GameBoardPanel() {
		/* GridBagLayout setup for Game Board cells */
		setLayout(new GridBagLayout());
		setBackground();
		buildPlayablePanels();
	}

	private void setBackground() {
		/* default background colour */
		setBackground(Color.black);
		boardDimension = this.getHeight();
	}

	/* Paints the board */
	@Override
	protected void paintComponent(Graphics graphics) {
		boardDimension = this.getHeight();

			/* Set the background Image */
			try {
				if(boardDimension < 510)
					backgroundImage = ImageIO.read(SMALL_BACKGROUND_PATH);
				else if (boardDimension < 750) {
					backgroundImage = ImageIO.read(MEDIUM_BACKGROUND_PATH);
				} else {
					backgroundImage = ImageIO.read(BIG_BACKGROUND_PATH);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		super.paintComponent(graphics);
		if (backgroundImage != null) {
			backgroundImage = resizeImage(backgroundImage, this.getWidth(), this.getHeight(), false);
		}
		graphics.drawImage(backgroundImage, 0, 0, this);
	}
	
	public static BufferedImage resizeImage(BufferedImage img, int toWidth, int toHeight, boolean card) {
		int width = img.getWidth();
		int height = img.getHeight();
		BufferedImage imageToResize = new BufferedImage(toWidth, toHeight, img.getType());
		Graphics2D g = imageToResize.createGraphics();

		if (card) {
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		}

		g.drawImage(img, 0, 0, toWidth, toHeight, 0, 0, width, height, null);
		g.dispose();
		return imageToResize;
	}

	private void buildPlayablePanels() {
		/* ------------------------------------------ */
		/* ------- Setting up the Game Board -------- */
		/* ------------------------------------------ */
		for ( int i = 0; i < 40; ++i ) {
			Property panel = Main.propertyList.get(i);

			/* panels are transparent */
			panel.setBackground(new Color(0, 0, 0, 0));

			/* Each panel has it's own GridBagLayout */
			panel.setLayout(new GridBagLayout());

			/* If the panel is on the TOP or BOTTOM row */
			if (i > 0 && i < 10 || i > 20 && i < 30) {
				panel.setMinimumSize(new Dimension(59, 90));
				panel.setPreferredSize(new Dimension(59,90));
			} else if (i > 10 && i < 20 || i > 30 && i < 40) {
				/* If the panel is on the LEFT or RIGHT column */
				panel.setMinimumSize(new Dimension(90,59));
				panel.setPreferredSize(new Dimension(90,59));
			} else {
				/* The panel is a CORNER panel */
				panel.setMinimumSize(new Dimension(90,90));
				panel.setPreferredSize(new Dimension(90,90));
			}
		}

		/* Add Property panels to the Game Board
			- Within each Property panel is a GridBagLayout for up to 6 players */

		/* Set GridBagLayout constraints */
		GridBagConstraints gbc = new GridBagConstraints();

		/* margin (external padding) */
		gbc.insets = new Insets(0, 0, 0, 0);

		/* Game board GridBagLayout x and y coordinates for next square insertion */
		gbc.gridx = 10;
		gbc.gridy = 10;

		/* component will fill it's display area in both x and y axis */
		gbc.fill = GridBagConstraints.BOTH;

		/* Set weight defaults */
		gbc.weightx = 0.4;
		gbc.weighty = 0.4;

		/* offsets used to guide coordinates of panel insertions into grid */
		int x = 0, y = 0;

		for (int i = 0; i < 40; ++i) {
			if (i < 10) {
				/* Populating the BOTTOM row */
				x = 1;
				y = 0;
			} else if (i < 20) {
				/* Populating the LEFT column */
				x = 0;
				y = 1;
			} else if (i < 30) {
				/* TOP row */
				x = -1;
				y = 0;
			} else if (i < 40) {
				/* RIGHT column */
				x = 0;
				y = -1;
			}

			/* Coordinates of the corner cells */
			if (i == 0 || i == 10 || i == 20 || i == 30) {
				/* Give extra weight to the corner cells to give them
					the bigger, square shape */
				gbc.weightx = 0.6;
				gbc.weighty = 0.6;
			}

			/* Add the Property panel to leftPanel */
			this.add(Main.propertyList.get(i), gbc);

			/* Reset weights (for non-corner squares) */
			gbc.weightx = 0.4;
			gbc.weighty = 0.4;

			/* Use offsets to guide next panel coordinates in the grid */
			gbc.gridx -= x;
			gbc.gridy -= y;
		}
	}
}