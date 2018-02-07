/*	
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 * StreetProperty class
 * 		- Class corresponding to the normal street properties you can buy & build
*/

package Monopoly.properties;

import Monopoly.UI.BuildWindow;
import Monopoly.UI.GameBoardPanel;
import Monopoly.core.Main;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class StreetProperty extends PurchasableProperty {

	private int housePrice;
	private int hotelPrice;
	private int numHouses = 0;
	private boolean hasHotel = false;
	private Vector<JLabel> buildingList = new Vector<JLabel>();

	public StreetProperty(int sideOfBoard, String title, String colour,
						  int price,int mortgageValue, int[] rent,
						  int housePrice,int hotelPrice) {
		super(sideOfBoard, title, colour, price, mortgageValue, rent);
		this.housePrice = housePrice;
		this.hotelPrice = hotelPrice;
	}

	@Override
	public int getRent() {
		int rentCost = 0;

		if (hasHotel) {
			rentCost = super.getRentArray()[5];
		} else {
			rentCost = super.getRentArray()[numHouses];
		}

		boolean ownsWholeColorGroup = true;
		ArrayList<PurchasableProperty> tempArray = Main.propertiesByColor.get(super.getColor());
		for (PurchasableProperty prop : tempArray) {
			if (prop.getOwner() != super.getOwner()) {
				ownsWholeColorGroup = false;
			}
		}
		
		/* if player owns the whole group and it's an unimproved site, double rent */
		return (ownsWholeColorGroup && !(hasHotel) && (numHouses == 0) ? rentCost * 2 : rentCost);
	}

	public int getHousePrice() {
		return housePrice;
	}

	public int getHotelPrice() {
		return hotelPrice;
	}

	public boolean hasHotel() {
		return hasHotel;
	}

	public int numHousesBuilt() {
		return numHouses;
	}

	public void demolishBuilding() {
		if (!buildingList.isEmpty()) {
			if(hasHotel) {
				Main.mapOfBuildingIcons.get("HOTEL").remove(buildingList.get(0));
			} else {
				Main.mapOfBuildingIcons.get("HOUSE").remove(buildingList.get(0));
			}
			remove(buildingList.remove(0));
			revalidate();
			BuildWindow.repaintBoard();
		}
		if (hasHotel) {
			hasHotel = false;
		} else {
			numHouses--;
		}
	}

	public void buildHotel() {
		if (hasHotel == false) {
			Main.numOfHotelsBuilt++;
			while (numHouses != 0) {
				demolishBuilding();
			}
			JLabel hotel = null;
			try {
				hotel = new JLabel(new ImageIcon
						(GameBoardPanel.resizeImage(ImageIO.read(StreetProperty.class.getClassLoader().getResource("icons/hotel.png")),
													(int)(BuildWindow.getBoard().getHeight() * 0.018),
													(int)(BuildWindow.getBoard().getHeight() * 0.018),
													false)));
			} catch (IOException e) {
				e.printStackTrace();
			}
			Main.mapOfBuildingIcons.get("HOTEL").add(hotel);
			build(hotel);
			hasHotel = true;
		}
	}

	public void buildHouse() {
		if (hasHotel == false && buildingList.size() < 4) {
			Main.numOfHousesBuilt++;
			JLabel house = null;
			try {
				house = new JLabel(new ImageIcon
						(GameBoardPanel.resizeImage(ImageIO.read(StreetProperty.class.getClassLoader().getResource("icons/house.png")),
													(int)(BuildWindow.getBoard().getHeight() * 0.018),
													(int)(BuildWindow.getBoard().getHeight() * 0.018),
													false)));
			} catch (IOException e) {
				e.printStackTrace();
			}
			build(house);
			Main.mapOfBuildingIcons.get("HOUSE").add(house);
			numHouses++;
		}
	}

	@Override
	public String getInfo() {
		String info = super.getInfo();
		info += String.format("\t- %-20s: %s\n", "House Price", housePrice);
		info += String.format("\t- %-20s: %s\n", "Hotel Price", hotelPrice);
		return info;
	}

	private void build(JLabel building) {
		int propertyNumber = super.getPropertyNumber();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.ipadx = 1;
		gbc.ipady = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		/*along the bottom row*/
		if (propertyNumber >= 0 && propertyNumber < 10 ) {
			gbc.weighty = 0.5;
			gbc.anchor = GridBagConstraints.NORTH;
			gbc.gridx = numHouses;
			gbc.gridy = 0;
		} else if(propertyNumber >= 20 && propertyNumber <= 30) { /*we're on top row in*/
			gbc.weighty = 0.5;
			gbc.anchor = GridBagConstraints.SOUTH;
			gbc.gridx = numHouses;
			gbc.gridy = 3;
		} else if(propertyNumber > 10 && propertyNumber < 20) { /*left side out*/
			gbc.weightx = 0.5;
			gbc.anchor = GridBagConstraints.EAST;
			gbc.gridx = 3;
			gbc.gridy = numHouses;
		} else if(propertyNumber > 30 && propertyNumber < 40) { /*right side now -shake it all about*/
			gbc.weightx = 0.5;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.gridx = 0;
			gbc.gridy = numHouses;
		}
		buildingList.add(0,building);
		add(buildingList.firstElement(),gbc);
		revalidate();
		BuildWindow.repaintBoard();
	}
}