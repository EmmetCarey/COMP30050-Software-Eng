/*
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 *
 * This class extends Thread in order to handle the player movement and
 * consequent re-validation of the appropriate components on the game board
 */

package Monopoly.player;

import Monopoly.UI.UIPanel;
import Monopoly.core.Commands;
import Monopoly.core.Main;

public class PlayerMoverThread extends Thread {

	/* The delay between moves */
	private int delay;

	public PlayerMoverThread(int delay) {
		this.delay = delay;
	}

	@Override
	public void run() {

		/* Disable the command input field to avoid command conflicts */
		UIPanel.toggleTextField();

		/* Visibly move the player a number of squares decided by their dice roll */
		while (Main.playerMovesLeft > 0) {
			Main.currentLocation = Commands.movePieceOneSquare();
			try {
				/* Delay between player moves (see Main.movementDelay) */
				Thread.sleep(delay);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}

			Main.playerMovesLeft--;
		}

		/* Check players situation / context on the square they landed on */
		Commands.checkSquareStatus(false);

		/* Re-enable the command input field */
		UIPanel.toggleTextField();
	}
}