/*
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 * Building of window  -
 * 		- setting main frame
 * 		- initially sets all player tokens on go
 */

package Monopoly.UI;

import Monopoly.cards.CardManager;
import Monopoly.core.Commands;
import Monopoly.core.Main;
import Monopoly.init.GameLauncher;
import Monopoly.player.Player;
import Monopoly.properties.StreetProperty;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class BuildWindow extends JFrame {

	/* URL paths for resources */
	private static JSplitPane horizontalSplitPane;					// split the window into gameBoardPanel and uiPanel
	private static UIPanel uiPanel = new UIPanel();					// contains splitPaneTwo
	static GameBoardPanel gameBoardPanel = new GameBoardPanel();	// contains the board image

	private static final int WINDOW_WIDTH = 1366;
	private static final int WINDOW_HEIGHT = 768;

	public static GameBoardPanel getBoard(){
		return gameBoardPanel;
	}
	
	public BuildWindow() {

		
		setTitle("Monopoly");
		setMinimumSize(new Dimension(800,480));
		setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		setVisible(true);

		addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				resizeComponents();
			}

			@Override
			public void componentHidden(ComponentEvent arg0) {}

			@Override
			public void componentMoved(ComponentEvent arg0) {}

			@Override
			public void componentShown(ComponentEvent arg0) {}

		});

		/* Set up horizontalSplitPane, the larger JSplitPane that splits
		 the GUI into a game board on the left and UI on the right */
		horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gameBoardPanel, uiPanel);

		/* horizontalSplitPane fills the whole JFrame */
		add(horizontalSplitPane);
		horizontalSplitPane.setEnabled(false);

		addPlayersToGo();
		UIPanel.setDefaultKey();
		UIPanel.setFocus();
		printInfo();
		pack();
	}
	
	private void printInfo() {
		String startText = "Welcome!\nThis game is still a work in progress " +
				"so please report any bugs to us @IPassTheButter. " +
				"You can use tab to auto complete game specific words and use" +
				" arrows to traverse command history, and type 'clear' to clear the" +
				" this panel of info.\n\n";
		
		/* What a cool ascii text image thing! (it looks better printed, a few backslash issues appear here)*/
		String s = 	"\n/  \\     /  |                                                  /  |          \n" +
					"$$  \\   /$$ |  ______   _______    ______    ______    ______  $$ | __    __ \n" +
					"$$$  \\ /$$$ | /      \\ /       \\  /      \\  /      \\  /      \\ $$ |/  |  /  |\n" +
					"$$$$  /$$$$ |/$$$$$$  |$$$$$$$  |/$$$$$$  |/$$$$$$  |/$$$$$$  |$$ |$$ |  $$ |\n" +
					"$$ $$ $$/$$ |$$ |  $$ |$$ |  $$ |$$ |  $$ |$$ |  $$ |$$ |  $$ |$$ |$$ |  $$ |\n" +
					"$$ |$$$/ $$ |$$ \\__$$ |$$ |  $$ |$$ \\__$$ |$$ |__$$ |$$ \\__$$ |$$ |$$ \\__$$ |\n" +
					"$$ | $/  $$ |$$    $$/ $$ |  $$ |$$    $$/ $$    $$/ $$    $$/ $$ |$$    $$ |\n" +
					"$$/      $$/  $$$$$$/  $$/   $$/  $$$$$$/  $$$$$$$/   $$$$$$/  $$/  $$$$$$$ |\n" +
					"                                           $$ |                    /  \\__$$ |\n" +
					"                                           $$ |                    $$    $$/ \n" +
					"                                           $$/                      $$$$$$/  \n";
					
		Commands.echoText(String.format("%s" + "\n" + "%s",s,startText), false, "cyan");
					
	}

	public void resizeComponents() {

		if (this.getHeight() > this.getWidth() - 300) {
			setSize(new Dimension(this.getWidth(), (int) (this.getHeight()*0.53)));
		}

		int heightOfBoard = this.getHeight() - (this.getInsets().top - this.getInsets().bottom);
		if (heightOfBoard > 0) {

			if (horizontalSplitPane != null) {
				horizontalSplitPane.setDividerLocation(heightOfBoard);
				gameBoardPanel.setSize(new Dimension(heightOfBoard, heightOfBoard));
				uiPanel.resizeComponents();
				CardManager.resizeComponents();
				reSizePlayerIcons();
				reSizeBuildingIcons();
				repaintBoard();
			}

		}
	}
	
	private static void reSizeBuildingIcons() {
		for(JLabel hotel: Main.mapOfBuildingIcons.get("HOTEL")) {
			try {
				hotel.setIcon(
						new ImageIcon(
								GameBoardPanel.resizeImage(
										ImageIO.read(StreetProperty.class.getClassLoader().getResource("icons/hotel.png")),
											(int)(getBoard().getHeight() * 0.018),
											(int)(getBoard().getHeight() * 0.018),
											false)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for(JLabel house: Main.mapOfBuildingIcons.get("HOUSE")) {
			try {
				house.setIcon(new ImageIcon(
						GameBoardPanel.resizeImage(
								ImageIO.read(StreetProperty.class.getClassLoader().getResource("icons/house.png")),
								(int)(getBoard().getHeight() * 0.018),
								(int)(getBoard().getHeight() * 0.018),
								false)));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void reSizePlayerIcons() {
		ArrayList<URL> iconURLs = GameLauncher.getPlayerIconURLArray();
		int i = 0;
		for(Player aPlayer: Main.playerList) {
			try {
				aPlayer.setIcon(new ImageIcon(
						GameBoardPanel.resizeImage(
								ImageIO.read(iconURLs.get(i++)),
								(int)(gameBoardPanel.getHeight()*.025),
								(int)(gameBoardPanel.getHeight()*.025),
								false)));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void repaintBoard() {
		gameBoardPanel.repaint();
		gameBoardPanel.revalidate();
	}

	/* Add player tokens to the Go(first) square */
	private static void addPlayersToGo() {
		for(Player aPlayer: Main.playerList) {
			Main.propertyList.get(0).addPlayer(aPlayer.getPlayerId());
		}
	}
}