/*
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 * Property class
 * 		- Contains basic property info 
*/

package Monopoly.properties;

import Monopoly.core.Main;

import java.awt.GridBagConstraints;
import javax.swing.*;

public class Property extends JPanel {

	private String title;
	private boolean purchasable;
	private int propertyNumber;

	public Property(int sideOfBoard, String title, boolean isPurchasable) {
		this.title = title;
		this.purchasable = isPurchasable;
		this.propertyNumber = sideOfBoard;
		/* if it's Go, free parking or jail */
	}

	public int getPropertyNumber() {
		return propertyNumber;
	}

	public boolean isPurchasable() {
		return purchasable;
	}

	public String getTitle() {
		return title;
	}

	public String getInfo() {
		String info = "\n";
		info += String.format("\t- %-20s: %s", "Title",  title);
		if(!(this instanceof PurchasableProperty)) {
			info += "\n";
		}
		return info;
	}

	public void addPlayer(int playerID) {
		GridBagConstraints gbc = new GridBagConstraints();
		/*along the bottom row*/
		if(propertyNumber >= 0 && propertyNumber < 10 ) {
			if (playerID % 2 == 0) {
				gbc.gridx = 0;
				gbc.gridy  = (playerID/2)+1;
			} else {
				gbc.gridx = 1;
				gbc.gridy = (playerID/2)+1;
			}
		}else if(propertyNumber >= 20 && propertyNumber <= 30) { /*we're on top row in*/
			if (playerID % 2 == 0) {
				gbc.gridx = 0;
				gbc.gridy  = playerID/2;
			} else {
				gbc.gridx = 1;
				gbc.gridy = playerID/2;
			}
		} else if(propertyNumber > 10 && propertyNumber < 20) { /*left side out*/
			if(playerID % 2 == 0) {
				gbc.gridx = playerID/2;
				gbc.gridy = 0;
			} else {
				gbc.gridx = playerID/2;
				gbc.gridy = 1;
			}
		} else if(propertyNumber > 30 && propertyNumber < 40) { /*right side now -shake it all about*/
			if(playerID % 2 == 0) {
				gbc.gridx = (playerID/2)+1;
				gbc.gridy = 0;
			} else {
				gbc.gridx = (playerID/2)+1;
				gbc.gridy = 1;
			}
		}
		this.add(Main.playerList.get(playerID),gbc);
	}

	public void removePlayer(int playerID) {
		remove(Main.playerList.get(playerID));
	}
}