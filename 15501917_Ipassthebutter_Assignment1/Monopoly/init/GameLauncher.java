/*
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 * Initiates the game
 * 		- calls on buildGUI
 * 		- sets up players
 * 		- sets up properties
 * 		- sets up who goes first
*/

package Monopoly.init;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import Monopoly.UI.*;
import Monopoly.core.Commands;
import Monopoly.core.Dice;
import Monopoly.core.Main;
import Monopoly.player.Player;
import Monopoly.properties.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* The Game is initialised from this class */
public class GameLauncher {

	public static ArrayList<String> playerNames = new ArrayList<>();

	private static BuildWindow mainFrame;

	/* Initial player balance */
	private static final int INITIAL_BALANCE = 1500;

	/* Holds URL paths for resources - specifically player tokens */
	private final static URL BLACK_PLAYER_TOKEN_PATH = GameLauncher.class.getClassLoader().getResource("icons/blackPlayer.png");
	private final static URL BLUE_PLAYER_TOKEN_PATH = GameLauncher.class.getClassLoader().getResource("icons/bluePlayer.png");
	private final static URL GREEN_PLAYER_TOKEN_PATH = GameLauncher.class.getClassLoader().getResource("icons/greenPlayer.png");
	private final static URL PINK_PLAYER_TOKEN_PATH = GameLauncher.class.getClassLoader().getResource("icons/pinkPlayer.png");
	private final static URL RED_PLAYER_TOKEN_PATH = GameLauncher.class.getClassLoader().getResource("icons/redPlayer.png");
	private final static URL YELLOW_PLAYER_TOKEN_PATH = GameLauncher.class.getClassLoader().getResource("icons/yellowPlayer.png");

	private final static String PROPERTY_DATA_PATH = "miscResources/monopoly_street_data.xml";
	private final static String CARD_DATA_PATH = "miscResources/monopoly_card_data.xml";
	private final static String CHANCE_CARD_TEMPLATE = "smallChanceTemplate.png";
	private final static String COMMUNITY_CARD_TEMPLATE = "smallComChestTemplate.png";
	private final static URL SHORT_CUT_NAMES = GameLauncher.class.getClassLoader().getResource("miscResources/shortCutNames.txt");

	private static boolean rollForFirstPlayerDoneTwice = false;

	/* When this Constructor is finished, the game is ready to play */
	public GameLauncher() {

		Main.mapOfBuildingIcons.put("HOTEL", new ArrayList<JLabel>());
		Main.mapOfBuildingIcons.put("HOUSE", new ArrayList<JLabel>());
		
		setPlayers();
		setProperties();
		setCards();
		setShortWords();
		buildGUI();
		
		Main.currentPlayersMove = Main.playerList.indexOf(whoGoesFirst(Main.playerList));


	}
	
	public static ArrayList<URL> getPlayerIconURLArray(){
		return new ArrayList<URL>(Arrays.asList(BLACK_PLAYER_TOKEN_PATH,
												BLUE_PLAYER_TOKEN_PATH,
												GREEN_PLAYER_TOKEN_PATH,
												PINK_PLAYER_TOKEN_PATH,
												RED_PLAYER_TOKEN_PATH,
												YELLOW_PLAYER_TOKEN_PATH
												));
				
		
	}

	private static void setShortWords() {
		BufferedReader shortWordsIn;

		try {
			shortWordsIn = new BufferedReader(new InputStreamReader(SHORT_CUT_NAMES.openStream()));
			String aWord = shortWordsIn.readLine();
			while (aWord != null) {
				if (!aWord.equals("") && aWord.charAt(0) != '#') {
					Main.shortWords.insert(aWord);
				}
				aWord = shortWordsIn.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Create player JLabels with token images(coloured discs) and add to playerList */
	private static void setPlayers() {
		new GetPlayerNames();

		if (playerNames.size() == 0) {
			System.exit(0);
		}

		ArrayList<URL> iconPaths = new ArrayList<>(Main.numberOfPlayers);
		Main.numberOfPlayers = playerNames.size();

		iconPaths.add(BLACK_PLAYER_TOKEN_PATH);
		iconPaths.add(BLUE_PLAYER_TOKEN_PATH);
		iconPaths.add(GREEN_PLAYER_TOKEN_PATH);
		iconPaths.add(PINK_PLAYER_TOKEN_PATH);
		iconPaths.add(RED_PLAYER_TOKEN_PATH);
		iconPaths.add(YELLOW_PLAYER_TOKEN_PATH);

		for (int i = 0; i < playerNames.size(); i++) {
			Main.playerList.add(new Player(i, new ImageIcon(iconPaths.get(i)), playerNames.get(i), INITIAL_BALANCE));
		}
	}

	private void setProperties() {

		Main.propertiesByColor.put("BROWN", new ArrayList<PurchasableProperty>(2));
		Main.propertiesByColor.put("IRISH_GREEN", new ArrayList<PurchasableProperty>(4));
		Main.propertiesByColor.put("LIGHT_BLUE", new ArrayList<PurchasableProperty>(3));
		Main.propertiesByColor.put("PINK", new ArrayList<PurchasableProperty>(3));
		Main.propertiesByColor.put("LILAC", new ArrayList<PurchasableProperty>(2));
		Main.propertiesByColor.put("ORANGE", new ArrayList<PurchasableProperty>(3));
		Main.propertiesByColor.put("RED", new ArrayList<PurchasableProperty>(3));
		Main.propertiesByColor.put("YELLOW", new ArrayList<PurchasableProperty>(3));
		Main.propertiesByColor.put("GREEN", new ArrayList<PurchasableProperty>(3));
		Main.propertiesByColor.put("DARK_BLUE", new ArrayList<PurchasableProperty>(2));

		InputStream fXmlFile;

		try {
			fXmlFile =  GameLauncher.class.getClassLoader().getResourceAsStream(PROPERTY_DATA_PATH);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			NodeList nList = doc.getElementsByTagName("PropertyData");

			ArrayList<Integer> nonPurchasables = new ArrayList<>();
			nonPurchasables.add(0);
			nonPurchasables.add(10);
			nonPurchasables.add(20);
			nonPurchasables.add(30);
			ArrayList<Integer> cardProperties = new ArrayList<>();
			cardProperties.add(2);
			cardProperties.add(7);
			cardProperties.add(17);
			cardProperties.add(22);
			cardProperties.add(33);
			cardProperties.add(36);
			ArrayList<Integer> taxProperties = new ArrayList<>();
			taxProperties.add(4);
			taxProperties.add(38);
			ArrayList<Integer> utilityProperties = new ArrayList<>();
			utilityProperties.add(12);
			utilityProperties.add(28);
			ArrayList<Integer> trainStationProperties = new ArrayList<>();
			trainStationProperties.add(5);
			trainStationProperties.add(15);
			trainStationProperties.add(25);
			trainStationProperties.add(35);
			/*
			   <PropertyData>
				<NAME>			</NAME>
				<COLOR>			</COLOR>
				<PRICE>			</PRICE>
				<MORTGAGE>		</MORTGAGE>
				<RENT>			</RENT>
				<HOUSEPRICE>	</HOUSEPRICE>
				<HOTELCOST>		</HOTELCOST>
			   </PropertyData>
			*/

			for (int i = 0; i < 40; ++i) {

				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element anElement = (Element) nNode;
					
					/*
					* if nonPurchasable
					* 		if isJailProperty
					* 		else
					* else if cardProperty
					* else if taxProperty
					* else => purchasableProperty 
					* 		if utilityProperty
					* 		else if trainStationProperty
					*		else => streetProperty
					*/

					if (nonPurchasables.contains(i)) {
						if (i == 10) {
							Main.propertyList.add(new JailProperty(i,anElement.getElementsByTagName("NAME").item(0).getTextContent(),false));
						} else {
							Main.propertyList.add(new Property(i,anElement.getElementsByTagName("NAME").item(0).getTextContent(), false));
						}

					} else if (cardProperties.contains(i)) {
						String title = anElement.getElementsByTagName("NAME").item(0).getTextContent();
						CardProperty card = new CardProperty(i, title, false);
						Main.propertyList.add(card);

					} else if (taxProperties.contains(i)) {
						int cost = Integer.parseInt(anElement.getElementsByTagName("RENT").item(0).getTextContent());
						Main.propertyList.add(new TaxProperty(i,anElement.getElementsByTagName("NAME").item(0).getTextContent(),cost,false));

					} else {
						int mortgageValue = Integer.parseInt(anElement.getElementsByTagName("MORTGAGE").item(0).getTextContent());
						int propertyPrice = Integer.parseInt(anElement.getElementsByTagName("PRICE").item(0).getTextContent());
						PurchasableProperty newProperty;
						String rents = anElement.getElementsByTagName("RENT").item(0).getTextContent();
						
						if (utilityProperties.contains(i)) {
							/*int array for the relevant rent costs*/
							int[] intRents = new int[2];
							int previousIndex = 0;
							int rentsIndex = 0;
							/* breaking rent String (ex. 3,10,30,60) into ints */
							for (int j = 0; j < rents.length();j++) {
								/*comma breaks individual rent costs*/
								if (rents.charAt(j) == ',') {
									intRents[rentsIndex++] = Integer.parseInt(rents.substring(previousIndex,j));
									previousIndex = j+1;
								/*if we've reached end of string*/
								} else if (j == rents.length()-1) {
									intRents[rentsIndex++] = Integer.parseInt(rents.substring(previousIndex,j+1));
									previousIndex = j+1;
								}
							}
							newProperty = new UtilityProperty(i,anElement.getElementsByTagName("NAME").item(0).getTextContent(), anElement.getElementsByTagName("COLOR").item(0).getTextContent(), propertyPrice, mortgageValue,intRents);
						} else if (trainStationProperties.contains(i)) {
							int[] intRents = new int[4];
							int previousIndex = 0;
							int rentsIndex = 0;
							for (int j = 0; j < rents.length();j++) {
								if (rents.charAt(j) == ',') {
									intRents[rentsIndex++] = Integer.parseInt(rents.substring(previousIndex,j));
									previousIndex = j+1;
								} else if (j == rents.length()-1) {
									intRents[rentsIndex++] = Integer.parseInt(rents.substring(previousIndex,j+1));
									previousIndex = j+1;
								}
							}
							newProperty = new TrainProperty(i,anElement.getElementsByTagName("NAME").item(0).getTextContent(),anElement.getElementsByTagName("COLOR").item(0).getTextContent(), propertyPrice, mortgageValue,intRents);
						} else {
							//also need to parse hotel and house price
							int housePrice = Integer.parseInt(anElement.getElementsByTagName("HOUSEPRICE").item(0).getTextContent());
							int hotelPrice = Integer.parseInt(anElement.getElementsByTagName("HOTELPRICE").item(0).getTextContent());
							int[] intRents = new int[6];
							int previousIndex = 0;
							int rentsIndex = 0;
							for (int j = 0; j < rents.length();j++) {
								if (rents.charAt(j) == ',') {
									intRents[rentsIndex++] = Integer.parseInt(rents.substring(previousIndex,j));
									previousIndex = j+1;
								} else if (j == rents.length()-1) {
									intRents[rentsIndex++] = Integer.parseInt(rents.substring(previousIndex,j+1));
									previousIndex = j+1;
								}
							}
							newProperty = new StreetProperty(i,anElement.getElementsByTagName("NAME").item(0).getTextContent(), anElement.getElementsByTagName("COLOR").item(0).getTextContent(), propertyPrice, mortgageValue,intRents,housePrice,hotelPrice);
							newProperty = new StreetProperty(
									i,
									anElement.getElementsByTagName("NAME").item(0).getTextContent(),
									anElement.getElementsByTagName("COLOR").item(0).getTextContent(),
									propertyPrice,
									mortgageValue,
									intRents,
									housePrice,
									hotelPrice);
						}

						Main.propertyList.add(newProperty);
						Main.propertiesByColor.get(newProperty.getColor()).add(newProperty);
						Main.purchasableProperties.put(newProperty.getTitle(), newProperty);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadStartException("[!!] Can't read monopoly_street_data.xml");
		}
		
		Main.purchasableProperties.put("Kent", Main.purchasableProperties.get("Old Kent Road"));
		Main.purchasableProperties.put("Mall", Main.purchasableProperties.get("Pall Mall"));
		Main.purchasableProperties.put("Kings", Main.purchasableProperties.get("Kings Cross Station"));
		Main.purchasableProperties.put("Angel", Main.purchasableProperties.get("The Angel Islington"));
	}
	
	private void setCards() {
		InputStream fXmlFile;
		
		try {
			fXmlFile =  GameLauncher.class.getClassLoader().getResourceAsStream(CARD_DATA_PATH);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			NodeList nList = doc.getElementsByTagName("CardData");
			/*
			<CardData>
				<TYPE>		</TYPE>
				<CODE>		</CODE>
				<TITLE>		</TITLE>
				<AMOUNT>	</AMOUNT
			</CardData>
			 */

			/* parse the card data from the XML file and put CardGraphics instances into
				one of two lists based on whether the card is Chance or Community Chest */

			for (int i = 0; i < 32; ++i) {
				Node nNode = nList.item(i);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element anElement = (Element) nNode;

					String type = anElement.getElementsByTagName("TYPE").item(0).getTextContent();
					String codeStr = anElement.getElementsByTagName("CODE").item(0).getTextContent();
					String amountStr = anElement.getElementsByTagName("AMOUNT").item(0).getTextContent();
					String title = anElement.getElementsByTagName("TITLE").item(0).getTextContent();
					String newLocation = anElement.getElementsByTagName("MOVE").item(0).getTextContent();

					switch (type) {
						case "10":
							/* It's a CHANCE card */
							String[] chanceCardParameters = {CHANCE_CARD_TEMPLATE, title, codeStr, amountStr, newLocation, type};
							Main.chanceCards.add(chanceCardParameters);
							break;

						case "20":
							/* It's a COMMUNITY CHEST card */
							String[]  communityCardParameters = {COMMUNITY_CARD_TEMPLATE, title, codeStr, amountStr, newLocation, type};
							Main.communityCards.add(communityCardParameters);
							break;
					}
				}
			}

			/* Shuffle the Chance and Community Chest decks */
			long seed = System.nanoTime();
			Collections.shuffle(Main.chanceCards, new Random(seed));
			seed = System.nanoTime();
			Collections.shuffle(Main.communityCards, new Random(seed));

		} catch (Exception e) {
			e.printStackTrace();
			throw new BadStartException("[!!] Can't read monopoly_card_data.xml");
		}
	}

	/* Initialise/Set-up the GUI */
	public void buildGUI() {
		mainFrame = new BuildWindow();
		mainFrame.setResizable(true);
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/* The JTextField command input should have focus when game starts */
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent windowEvent) {
				UIPanel.setFocus();
			}
		});
	}

	private static Player whoGoesFirst(ArrayList<Player> playerList) {

		class PlayerRoll {
			String playerName = "";
			Player player = null;
			int rollNumber = 0;
		}

		Vector<PlayerRoll> highestRoll = new Vector<>(playerList.size());
		if (!rollForFirstPlayerDoneTwice) {
			Commands.echoText("----------------------------------------------------------" +
					"------------------------\n", false, "yellow");
			Commands.echoText("ROLLING TO DECIDE WHO GOES FIRST...\n\n", false, "white");
			Commands.echoText("                     Dice 1  Dice 2\n", false, "white");
		}

		for (Player player: playerList) {
			int i = 0;
			int a = Dice.getRandNumber();
			int b = Dice.getRandNumber();
			int rollValue = a + b;

			/* The first player is put at the top of the vector first - everything else is compared to it
			- if it's not the first player & the given player rolls higher they're put to top */
			if (player.equals(playerList.get(0)) || rollValue > highestRoll.get(0).rollNumber) {
				PlayerRoll playerRoll = new PlayerRoll();
				playerRoll.playerName = player.getName();
				playerRoll.player = player;
				playerRoll.rollNumber = rollValue;
				highestRoll.add(0, playerRoll);
			} else {
				/* otherwise put them in their respective rank */
				while(i < highestRoll.size()
						&& (highestRoll.get(i).rollNumber > rollValue
							|| rollValue == highestRoll.get(i).rollNumber)) {
					i++;
				}
				PlayerRoll temp = new PlayerRoll();
				temp.playerName = player.getName();
				temp.rollNumber = rollValue;
				temp.player = player;
				highestRoll.add(i, temp);
			}

			Commands.echoText(
					String.format(
							"%-8s"+" rolls "+"%10d"+" "+"%7d"+"     (%d)\n", player.getName(), a, b, rollValue), false, null);
		}
		/* if two people have the same highest score - call this again with the array of just those players */
		if (highestRoll.get(0).rollNumber == highestRoll.get(1).rollNumber) {
			rollForFirstPlayerDoneTwice = true;
			Commands.echoText("\nEqual numbers rolled! Rolling again...\n\n", false, "white");
			
			ArrayList<Player> temp = new ArrayList<Player>();
			for(PlayerRoll aRoll: highestRoll) {
				if(aRoll.rollNumber == highestRoll.get(0).rollNumber) {
					temp.add(aRoll.player);
				}
			}
			
			return whoGoesFirst(temp);
		}
		Commands.echoText(
				String.format(
						"\nPlayer %d: "+ "%s"+ " gets first roll!\n",
						playerNames.indexOf(highestRoll.get(0).playerName)+1,
						highestRoll.get(0).playerName), false, null);

		Commands.echoText("----------------------------------------------------------" +
				"------------------------\n", false, "yellow");

		return highestRoll.get(0).player;
	}
}