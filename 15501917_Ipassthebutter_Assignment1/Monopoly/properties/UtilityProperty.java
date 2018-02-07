/*	
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 * UtilityProperty class
 * 		- Class for electric and water company properties
*/

package Monopoly.properties;

import java.awt.image.BufferedImage;

import Monopoly.core.Dice;
import Monopoly.core.Main;

public class UtilityProperty extends PurchasableProperty {

	private int otherUtility;

	public UtilityProperty(int sideOfBoard, String title, String colour, int price, int mortgageValue, int[] rent) {
		super(sideOfBoard,title, colour,price, mortgageValue, rent);

		/*for checking owners to find rent*/
		if (sideOfBoard == 12) {
			otherUtility = 28;
		} else {
			otherUtility = 12;
		}
	}

	@Override
	public int getRent(){

		/* If player owns both properties - 10 times dice roll */
		if(super.getOwner() == ((PurchasableProperty) Main.propertyList.get(otherUtility)).getOwner()) {
			return (super.getRentArray()[1] * Dice.getDiceRoll());
		}

		/* If they don't own both, 4 times dice roll */
		return (super.getRentArray()[0] * Dice.getDiceRoll());
	}
}