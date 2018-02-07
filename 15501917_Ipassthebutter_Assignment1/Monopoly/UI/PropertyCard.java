/*
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 */

package Monopoly.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Monopoly.init.BadResourceException;
import Monopoly.init.GameLauncher;
import Monopoly.properties.PurchasableProperty;
import Monopoly.properties.StreetProperty;
import Monopoly.properties.TrainProperty;
import Monopoly.properties.UtilityProperty;

public class PropertyCard extends Card {
	
	private BufferedImage train;
	private BufferedImage electric;
	private BufferedImage water;
	
	private JLabel currentImageOnCard = null;
	private String typeOfImageOnCard = "";
	
	private JPanel titlePanel;
	private JLabel title;

	private JLabel rents;
	private JLabel extraInfo;
	
	private PurchasableProperty property = null;
	
	public PropertyCard() {
		
		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);
		
		
		try {
			train = ImageIO.read(GameLauncher.class.getClassLoader().getResource("icons/train.jpg"));
			electric = ImageIO.read(GameLauncher.class.getClassLoader().getResource("icons/electric.jpg"));
			water = ImageIO.read(GameLauncher.class.getClassLoader().getResource("icons/water.jpg"));
		} catch (IOException e) {
			throw new BadResourceException("Can't read in property image icon.");
		}
		
		initialiseTitle();
		initialiseRents();
		initialiseExtraInfo();
		reSize();
		
		/* black border looks ~~cool~~ */
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
	
	@Override
	public void reSize() {
		
		int boardSize = BuildWindow.getBoard().getHeight();
		title.setFont(new Font(Font.SANS_SERIF,Font.PLAIN, (int)Math.floor(boardSize * 0.019)));
		rents.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,(int) Math.floor(boardSize * 0.017)));
		extraInfo.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,(int) Math.floor(boardSize * 0.014)));

		if(currentImageOnCard != null) {
			switch(typeOfImageOnCard) {
				case "train":
					currentImageOnCard.setIcon(new ImageIcon(GameBoardPanel.resizeImage(train,(int) (BuildWindow.getBoard().getHeight()*0.1),(int) (BuildWindow.getBoard().getHeight()*0.1),true)));
					break;
				case "electric":
					currentImageOnCard.setIcon(new ImageIcon(GameBoardPanel.resizeImage(electric,(int) (BuildWindow.getBoard().getHeight()*0.08),(int) (BuildWindow.getBoard().getHeight()*0.08),true)));
					break;
				case "water":
					currentImageOnCard.setIcon(new ImageIcon(GameBoardPanel.resizeImage(water,(int) (BuildWindow.getBoard().getHeight()*0.08),(int) (BuildWindow.getBoard().getHeight()*0.08),true)));
					break;
			}
		}
		
		revalidate();
		repaint();
	}	
	
	/* info name of property and it's color */
	private void initialiseTitle() {
		GridBagConstraints gbc = new GridBagConstraints();

		titlePanel = new JPanel();
		titlePanel.setLayout(new GridBagLayout());
		
		title = new JLabel("");
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setVerticalAlignment(JLabel.CENTER);
		title.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,20));
		
		/* adding title label to colored label */
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		titlePanel.add(title,gbc);
		
		/* adding colored label to overall panel, insets provide nice white border */
		gbc.ipady = 20;
		gbc.weighty = 0.2;
		gbc.gridy = 1;
		gbc.insets = new Insets(5,5,0,5);
		add(titlePanel,gbc);
	}
	
	/* info RE rents of property */
	private void initialiseRents() {
		/* no Panel here */
		rents = new JLabel("");
		rents.setForeground(Color.BLACK);
		rents.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,16));
		
		GridBagConstraints gbc = new GridBagConstraints();	
		gbc.weightx = 0;
		gbc.weighty = 0.6;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.ipady = 10;
		add(rents,gbc);
	}
	
	/* info RE mortgage, house/hotel prices */
	private void initialiseExtraInfo() {
		extraInfo = new JLabel("");
		extraInfo.setForeground(Color.BLACK);
		extraInfo.setFont(new Font("Arial Black",Font.PLAIN,12));
		extraInfo.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,12));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0.1;
		gbc.weighty = 0.1;
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.ipady = 20;
		add(extraInfo,gbc);
	}
	
	/* called when we want to set this to represent a specific property */
	public void initialisePropertyCard(PurchasableProperty currentProperty) {

		/* in case previous card was utility => it had image of tap or light bulb */
		if (currentImageOnCard != null) {
			remove(currentImageOnCard);
			currentImageOnCard = null; 
			typeOfImageOnCard = "";
		}
		
		property = currentProperty;
		setTitle();
		setRents();
		setExtraInfo();
		reSize();
	}
	
	private void setTitle() {
		/* 
		 * Magical HTML here to save the day -> centers the text,
		 * max lines is three for this version, this ensures the JLabel has three lines, and any extra are added
		 * to the top to force the title to sit on the bottom of the colored panel
		 */
		String startLine = 	"<html><center>";
		String titleToPrint = "";
		int numOfLines = 3;
		for(char aCharacter: property.getTitle().toCharArray()) {
			if(aCharacter == ' ') {
				titleToPrint += "<br>";
				numOfLines--;
			} else {
				titleToPrint += aCharacter;
			}
		} 
		
		String extraLineSpaces = "";
		for(int i = 0;i<numOfLines-1;i++) {
			extraLineSpaces += "<br>";
		}
		String endLine = "</center></html>";
		
		title.setText(startLine + extraLineSpaces + titleToPrint + endLine);

		/* setting colour of panel if it's a StreetProperty */
		title.setForeground(Color.WHITE);
		if(property instanceof StreetProperty) {
			switch	(property.getColor()) {
				case "BROWN" :
					titlePanel.setBackground(new Color(139,69,19));
					break;
				case "LIGHT_BLUE":
					titlePanel.setBackground(new Color(0,191,255));
					break;
				case "PINK":
					titlePanel.setBackground(new Color(255,20,147));
					break;
				case "ORANGE":
					titlePanel.setBackground(new Color(255,165,0));
					break;
				case "RED":
					titlePanel.setBackground(new Color(255,0,0));
					break;
				case "YELLOW":
					/*Yellowis bright => black text instead of white */
					title.setForeground(Color.BLACK);
					titlePanel.setBackground(new Color(255,255,0));
					break;
				case "GREEN":
					titlePanel.setBackground(new Color(0,128,0));
					break;
				case "DARK_BLUE":
					titlePanel.setBackground(new Color(0,0,205));
					break;
			}
		

		} else {
			/* in case of Train Station or Utility */
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = 0;
			gbc.weighty = 0;
			
			/* no colour */
			titlePanel.setBackground(Color.WHITE);
			titlePanel.setForeground(Color.BLACK);
			
			/* make title black */
			title.setForeground(Color.BLACK);
			
			/* loading nice image to put on top of card */
			if (property instanceof TrainProperty) {
				currentImageOnCard = new JLabel(new ImageIcon(GameBoardPanel.resizeImage(train,(int) (BuildWindow.getBoard().getHeight()*0.1),(int) (BuildWindow.getBoard().getHeight()*0.1),true)));
				typeOfImageOnCard = "train";
			} else if (property.getPropertyNumber() == 12) {
				/* this is water works */
				currentImageOnCard = new JLabel(new ImageIcon(GameBoardPanel.resizeImage(water,(int) (BuildWindow.getBoard().getHeight()*0.08),(int) (BuildWindow.getBoard().getHeight()*0.08),true)));
				typeOfImageOnCard = "water";
				gbc.ipady = 10;
			} else if (property.getPropertyNumber() == 28) {
				/* this is electric company */
				typeOfImageOnCard = "electric";
				currentImageOnCard = new JLabel(new ImageIcon(GameBoardPanel.resizeImage(electric,(int) (BuildWindow.getBoard().getHeight()*0.08),(int) (BuildWindow.getBoard().getHeight()*0.08),true)));
				gbc.ipady = 10;
			}
			add(currentImageOnCard,gbc);
		}
	}

	private void setRents() {
		String rentsToPrint = "<html><center>";

		/* cards roughly follow this structure 
		 * 	   	____________________________
		 *		|    						|
		 * 		|		   TITLE			|
		 *		|							|
		 *		|        Rent $X			|
		 *		|     with 1 house   $y		|
		 *		|     with 2 house   $z		|
		 *		|     with 3 house   $a		|
		 *		|     with 4 house   $b		|
		 *		|	    With hotel  $c		|
		 *		|							|
		 *		|    Mortgage value $x		|
		 *		|   Houses cost $x each		|
		 *		| hotel, $y plus 4 houses 	|
		 *		|___________________________|
		 *
		 *	     ___________________________
		 *		|							|
		 * 		|	  <Image of train>		|
		 *		|         TITLE  			|
		 *		|							|
		 *		| Rent		  		   $y	|
		 *		| If 2 R.Rs are owned  $z	|
		 *		| If 3  "    "    "    $a	|
		 *		| If 4  "    "    "    $b	|
		 *		|	    	|
		 *		|							|
		 *		|    Mortgage value $x		|
		 *		|   Houses cost $x each		|
		 *		| 							|
		 *		|___________________________|
		 *
		 *   	 ___________________________
		 *		|							|
		 * 		|  	<Image of utility>		|
		 *		|          		  			|
		 *		|		  TITLE 			|
		 *		| 							|
		 *		|  If one utility is owned,	|
		 *		|   rent is 4 times amount	|
		 *		|      shown on dice		|
		 *		|	    					|
		 *		|If both utilities are owned|
		 *		|   rent is 10 times amount	|
		 *		|      shown on dice		|
		 *		| 							|
		 *		|___________________________|
		 */
		
		if(property instanceof StreetProperty) {
			rentsToPrint+= "&nbsp;&nbsp;Rent ";
			int[] temp = property.getRentArray();
			for(int i = 0; i < temp.length; i++ ) {
				if(i == 0) {
					rentsToPrint+= "$" + Integer.toString(temp[i])+ "&nbsp;&nbsp;</center>";
				} else {
					if(i == 1) {
						rentsToPrint += "&nbsp;&nbsp;&nbsp;With 1 House &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$" + Integer.toString(temp[i]) +"&nbsp;&nbsp;&nbsp;<br>" ;
					} else if (i == 5) {
						rentsToPrint += "<center>With Hotel &nbsp;&nbsp;&nbsp;$" + temp[i] + "</center>";
					} else {
						rentsToPrint += "&nbsp;&nbsp;&nbsp;With " + i + " Houses &nbsp;&nbsp;&nbsp;&nbsp;$" + Integer.toString(temp[i]) + "&nbsp;&nbsp;&nbsp;<br>";
					}
				}
			}
			
		} else if (property instanceof TrainProperty) {
			rentsToPrint += "&nbsp;&nbsp;Rent ";
			int[] temp = property.getRentArray();
			for(int i = 0; i < temp.length; i++ ) {
				if(i == 0) {
					rentsToPrint+= "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$" + Integer.toString(temp[i])+ "&nbsp;&nbsp;<br>";
				} else {
					if(i == 1) {
						rentsToPrint += "If 2 R.R.s are owned&nbsp;&nbsp;&nbsp;$" + Integer.toString(temp[i]) +"<br>" ;
					} else {
						rentsToPrint += "If " + (i+1) + " &nbsp;&nbsp;&nbsp;\"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$" + Integer.toString(temp[i]) + "<br>";
						
					}
				}
			}
		} else if (property instanceof UtilityProperty) {
			int[] temp = property.getRentArray();
			for(int i = 0; i < temp.length; i++ ) {
				if(i == 0) {
					rentsToPrint += "If one Utility is owned,<br> rent is " + temp[i] + " times amount<br>shown on dice.<br><br>";
				} else {
					rentsToPrint += "&nbsp;&nbsp;If both Utilities are owned,&nbsp;&nbsp;<br> rent is " + temp[i] + " times amount <br>shown on dice.<br>";
				}
			}
		}
			
		rents.setText(rentsToPrint+ "</html>");
	}
	
	/* extra info contains mortgage info and houses if applicable */
	private void setExtraInfo() {
		String extraInfoToAdd = "<html><center>";
		if(property instanceof StreetProperty) {
			
			
			extraInfoToAdd += "Mortgage Value $" + property.getMortgageAmount() + "<br>";
			
			extraInfoToAdd += "Houses Cost  $" + ((StreetProperty)property).getHousePrice()	+ " Each<br>";
			
			extraInfoToAdd += "Hotel, $" + ((StreetProperty) property).getHotelPrice() + " plus 4 Houses";
			
			extraInfoToAdd += "<br><br></center></html>";
			
		} else if (property instanceof TrainProperty) {
			extraInfoToAdd += "Mortgage Value $" + property.getMortgageAmount() + "<br><br></center></html>";
		} else {
			extraInfoToAdd = "";
		}
		
		extraInfo.setText(extraInfoToAdd);
	}
	

	
}
