/*
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 * Purchasable property
 * 		- Inherits Property
*/

package Monopoly.properties;

import Monopoly.core.Main;
import Monopoly.player.Player;

public class PurchasableProperty extends Property {

	private int price;
	private int[] rent = new int[6];
	private int mortgageValue;
	private Player owner;
	private String color;
	private boolean mortgaged;
	
	public PurchasableProperty(int sideOfBoard, String title, String color, int price,int mortgageValue, int[] rent) {

		/* Set title and isPurchasable in Property class constructor */
		super(sideOfBoard,title, true);

		this.color = color;
		this.price = price;
		this.rent = rent;
		this.mortgageValue= mortgageValue;
		owner = null;
		mortgaged = false;
	}

	/*This should never be called*/
	public int getRent(){
		return 0;
	}

	public int[] getRentArray() {
		/*this should never be called, as rent is very specific to houses, number of colours owned etc.*/
		return rent;
	}

	public String getColor() {
		return color;
	}

	private String getRentString() {
		String rentValues = "";
		for(int i = 0; i < rent.length; i++) {
			rentValues += (rent[i] + " "); 
		}
		return rentValues;
	}

	public Player getOwner() {
		return owner;
	}

	public boolean isOwned() { return owner != null; }

	public void setOwner(Player newOwner) {
		owner = newOwner;
	}

	public int getPrice() {
		return price;
	}

	public boolean isMortgaged() {
		return mortgaged;
	}

	public void setMortgaged(boolean bool) {
		mortgaged = bool;
	}

	public int getMortgageAmount() {
		return mortgageValue;
	}

	@Override
	public String getInfo() {
		String info = super.getInfo() + "\n";
		if (this instanceof StreetProperty) {
			info += String.format("\t- %-20s: %s\n", "Color", getColor());
		}
		String currentOwner = (getOwner() == null) ? "" : getOwner().getName();
		if (currentOwner.equals(Main.playerList.get(Main.currentPlayersMove).getName())) {
			currentOwner = "You!";
		}
		info += String.format("\t- %-20s: %s\n", "Price", getPrice());
		info += String.format("\t- %-20s: %s\n", "Rent", getRentString());
		info += String.format("\t- %-20s: %s\n", "Owned by", (currentOwner.equals("") ? "Available" : currentOwner));
		info += String.format("\t- %-20s: %s\n", "Mortgage amount", getMortgageAmount());
		if (isOwned()) {
			info += String.format("\t- %-20s: %s\n", "Mortgaged", (isMortgaged() ? "True" : "False"));
		}
		return info;
	}
}