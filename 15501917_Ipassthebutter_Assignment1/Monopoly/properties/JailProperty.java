/*	
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 * JailProperty class
 * 		- solely for Jail property to be used in later sprint - very specific addPlayer methods
*/

package Monopoly.properties;

import Monopoly.core.Main;
import Monopoly.player.Player;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class JailProperty extends Property {

	public JailProperty(int sideOfBoard, String title, boolean isPurchasable) {
		super(sideOfBoard, title, isPurchasable);
	}

	@Override
	public void addPlayer(int playerID){
		GridBagConstraints gbc = new GridBagConstraints();
		if(playerID == 0 || playerID == 1 || playerID == 2 ) {
			gbc.anchor = GridBagConstraints.WEST;
			gbc.weightx = 1;
			gbc.weighty =0;
			gbc.gridx = 0;
			gbc.gridy = playerID;
		} else {
			gbc.anchor = GridBagConstraints.SOUTH;
			gbc.weightx = 0;
			gbc.weighty =1;
			gbc.gridx = playerID - 2;
			gbc.gridy = 3;
		}
		add(Main.playerList.get(playerID),gbc);
	}

	public void addPlayerToJail(Player player) {
		int playerID = player.getPlayerId();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 0, 0, 0);

		if (playerID == 0 || playerID == 1 || playerID == 2) {
			gbc.anchor = GridBagConstraints.FIRST_LINE_END;
			gbc.gridx = playerID + 1;
			gbc.gridy  = 0;
		} else {
			gbc.anchor = GridBagConstraints.LINE_END;
			gbc.gridx =  playerID - 2;
			gbc.gridy = 1;
		}

		add(player,gbc);
	}
}
