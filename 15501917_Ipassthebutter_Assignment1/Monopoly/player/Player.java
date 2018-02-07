/*	
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 * Player class
 * 		- contains data relevent to player
*/

package Monopoly.player;

import Monopoly.core.Commands;
import Monopoly.core.Main;
import Monopoly.properties.Property;
import Monopoly.properties.PurchasableProperty;
import Monopoly.properties.StreetProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Player extends JLabel {

	private int playerID;
	private int balance;
	private int numTurnsInJail;
	private boolean inJail;
	private int numGetOutOfJailFreeCards;
	private String ownedJailCardType;
	private String playerName;
	private final String CHANCE = "10";

	private TreeMap<String, ArrayList<PurchasableProperty>> properties = new TreeMap<>(new Comparator<String>()
    {
		int BROWN = 1;
		int STATION = 5;
		int LIGHT_BLUE = 6;
		int PINK = 11;
		int UTILITY = 12;
		int ORANGE = 16;
		int RED = 21;
		int YELLOW = 26;
		int GREEN = 31;
		int DARK_BLUE = 39;

		@Override
		public int compare(String arg1, String arg2){
        	ArrayList<String> colorList =  new ArrayList<>(
        			Arrays.asList(	((PurchasableProperty) Main.propertyList.get(UTILITY)).getColor(),
							((PurchasableProperty) Main.propertyList.get(STATION)).getColor(),
							((PurchasableProperty) Main.propertyList.get(BROWN)).getColor(),
							((PurchasableProperty) Main.propertyList.get(LIGHT_BLUE)).getColor(),
							((PurchasableProperty) Main.propertyList.get(PINK)).getColor(),
							((PurchasableProperty) Main.propertyList.get(ORANGE)).getColor(),
							((PurchasableProperty) Main.propertyList.get(RED)).getColor(),
							((PurchasableProperty) Main.propertyList.get(YELLOW)).getColor(),
							((PurchasableProperty) Main.propertyList.get(GREEN)).getColor(),
							((PurchasableProperty) Main.propertyList.get(DARK_BLUE)).getColor()));

        	if(colorList.indexOf(arg1) < colorList.indexOf(arg2)) {
            	return -1;
            } else if (colorList.indexOf(arg1) == colorList.indexOf(arg2)) {
            	return 0;
            } else {
            	return 1;
            }
        } 
	});

	/* Format <Square number(0 - 39)> <x coordinate within the square gridlayout><y coord> */
	private int boardLocation;

	public Player(int idNum, ImageIcon icon, String name, int initialBalance) {
		playerID = idNum;
		setIcon(icon);
		balance = initialBalance;
		playerName = name;
		boardLocation = 0;
		inJail = false;
		numTurnsInJail = 0;
		numGetOutOfJailFreeCards = 0;
	}

	public boolean hasGetOutOfJailFreeCard() {
		return numGetOutOfJailFreeCards > 0;
	}
	
	public void useGetOutOfJailFreeCard() {
		if (hasGetOutOfJailFreeCard()) {
			if (ownedJailCardType.equals(CHANCE)) {
				Main.getOutOfJailCardsOwned[0] = false;
			} else {
				Main.getOutOfJailCardsOwned[1] = false;
			}
			numGetOutOfJailFreeCards--;
		}
	}
	
	public void addGetOutOfJailFreeCard(String cardType) {
			ownedJailCardType = cardType;
			numGetOutOfJailFreeCards++;
	}
	
	public int getNumGetOutOfJailFreeCards() {
		return numGetOutOfJailFreeCards;
	}
	
	@Override
	public String getName() {
		return playerName;
	}
	
	public int getNumTurnsInJail() {
		return numTurnsInJail;
	}
	
	public int addTurnInJail() {
		return ++numTurnsInJail;
	}

	public void resetJailTime() {
		numTurnsInJail = 0;
	}

	public void putInJail() {
		inJail = true;
	}
	
	public void removeFromJail() {
		inJail = false;
	}
	
	public boolean isInJail() {
		return inJail;
	}
	
	/* update players location on the game board
	 * - Note: Inner Grid coordinate remains the same */
	public void updateLocation(int gridSquare) {
		boardLocation = gridSquare;
	}

	public int getPlayerId() {
		return playerID;
	}

	/*returns array with location data for player*/
	public int getPlayerLocation() {
		return boardLocation;
	}

	public int getBalance() {
		return balance;
	}

	public void addToBalance(int amount) {
		balance += amount;
	}

	public void removeFromBalance(int amount) {
		balance -= amount;
	}

	public void addProperty(Property property) {
		
		PurchasableProperty prop = (PurchasableProperty) property;
		String color = prop.getColor();
		
		ArrayList<PurchasableProperty> tempList = properties.get(color);
		
		if(tempList == null) {
			properties.put(color, tempList = new ArrayList<PurchasableProperty>());
		}
		
		tempList.add(prop);
//		properties.get(color).add(prop);
	}

	public int getWorth() {
		int worth = balance;

		for(Entry<String, ArrayList<PurchasableProperty>> mapEntry : properties.entrySet()) {
//		for(PurchasableProperty ownedProperty: properties) {
		ArrayList<PurchasableProperty> propertyList = mapEntry.getValue();
			for(PurchasableProperty ownedProperty : propertyList) {
				if (ownedProperty.isMortgaged()) {
					worth += ownedProperty.getPrice()/2;
				} else {
					worth += ownedProperty.getPrice();
					if(ownedProperty instanceof StreetProperty) {
						if(((StreetProperty) ownedProperty).hasHotel()) {
							worth += ((StreetProperty) ownedProperty).getHotelPrice();
						} else {
							worth += ((StreetProperty) ownedProperty).numHousesBuilt() * ((StreetProperty) ownedProperty).getHousePrice();
						}
					}
				}
			}
		}
		return worth;
	}

	/*
	 * A helper method for deciding upon a player's bankruptcy claim.
	 * Returns the worth of a player minus property values, which can't be sold
	 * in order to discharge a debt.
	 */
	public int getLiquidAssets() {
		int worth = balance;

		for(Entry<String, ArrayList<PurchasableProperty>> propertyList : properties.entrySet()) {
//		for (PurchasableProperty ownedProperty: properties) {
			/* Check if property is mortgaged, and if not, add mortgage value to
				potential player balance*/

			for(PurchasableProperty ownedProperty : propertyList.getValue()) {
				if (!ownedProperty.isMortgaged()) {
					worth += ownedProperty.getMortgageAmount();
				}
	
				/* Check if property has any buildings that can be sold */
				if (ownedProperty instanceof StreetProperty) {
					StreetProperty streetProperty = (StreetProperty) ownedProperty;
					if (streetProperty.hasHotel()) {
						worth += streetProperty.getHotelPrice() / 2;
					} else {
						worth += streetProperty.numHousesBuilt() * streetProperty.getHousePrice() / 2;
					}
				}
			}
		}

		return worth;
	}

	public TreeMap<String, ArrayList<PurchasableProperty>> getPropertyArray() {
		return properties;
	}

	public void getProperties() {
		if (properties.size() == 0) {
			Commands.echoText("[**] You own no properties\n" +
					"[??] You can buy properties with the 'buy' command\n" +
					"[??] In order to buy a property it must be both purchasable and available\n", false, "cyan");
		} else {
			Commands.echoText("\tYour properties:\n", false, null);

			for(Entry<String, ArrayList<PurchasableProperty>> propertyList : properties.entrySet()) {

				for(PurchasableProperty ownedProperty : propertyList.getValue()) {
					Commands.echoText(ownedProperty.getInfo() + "\n", false, null);
					Commands.echoText(//"----------------------------------------------------------" +
							"\t------------------------\n", false, "gray");
				}
			}
		}
	}

	/* Transfer funds from one player to another (when rent is owed etc.) */
	public void transferFunds(int amount, Player receiver) {
		removeFromBalance(amount);

		if (receiver != null) {
			receiver.addToBalance(amount);
			Commands.echoText(String.format("[$$] Transferred $%d to %s\n", amount, receiver.getName()), false, null);
		}
	}

	/* ------ TEST methods ------ */
	public void setBalance(int amount) {
		balance = amount;
	}
}