/*	
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 * TrainProperty class
 * 		- Class only for train properties
*/


package Monopoly.properties;

import Monopoly.core.Main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class TrainProperty extends PurchasableProperty {

	private ArrayList<Integer> otherStations;

	public TrainProperty(int sideOfBoard, String title,String colour, int price, int mortgageValue, int[] rent) {
		super(sideOfBoard, title, colour, price, mortgageValue, rent);

		if(sideOfBoard == 5) {
			otherStations = new ArrayList<>(Arrays.asList(15,25,35));
		} else if (sideOfBoard == 15) {
			otherStations = new ArrayList<>(Arrays.asList(5,25,35));
		} else if (sideOfBoard == 25) { 
			otherStations = new ArrayList<>(Arrays.asList(5,15,35));
		} else {
			otherStations = new ArrayList<>(Arrays.asList(5,15,25));
		}
	}

	@Override
	public int getRent() {
		int numOfExtraStationsOwned = 0;
		for(Integer statPropIndex : otherStations) {
			if(((PurchasableProperty)(Main.propertyList.get(statPropIndex))).getOwner() == super.getOwner()) {
				numOfExtraStationsOwned++;
			}
		}
		return super.getRentArray()[numOfExtraStationsOwned];
	}
}
