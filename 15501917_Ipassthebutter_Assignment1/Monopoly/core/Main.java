/*
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 * 'Main' for this project
 * 		- calls on GameLauncher to initially start game
 * 		- contains the game status variables which are depended on for game play
 * 		- also contains GameLooping method
*/

package Monopoly.core;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

import Monopoly.UI.Card;
import Monopoly.dataStrucs.TrieAutocomplete;
import Monopoly.init.GameLauncher;
import Monopoly.player.Player;
import Monopoly.properties.Property;
import Monopoly.properties.PurchasableProperty;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	/*Game status variables - Accessed mainly by GameLauncher and Commands */
	public static ArrayList<Player> playerList = new ArrayList<>();
	public static ArrayList<Property> propertyList = new ArrayList<>();
	public static HashMap<String, PurchasableProperty> purchasableProperties = new HashMap<>(37);
	public static HashMap<String, ArrayList<PurchasableProperty>> propertiesByColor = new HashMap<>(11);
	
	/* holds pointer to JLabel of houses and hotels on board */
	public static HashMap<String,ArrayList<JLabel>> mapOfBuildingIcons = new HashMap<>(); 
	
	public static TrieAutocomplete shortWords = new TrieAutocomplete();

	public static ArrayList<String[]> chanceCards = new ArrayList<>(16);
	public static ArrayList<String[]> communityCards = new ArrayList<>(16);
	public static Card currentCardOnBoard;

	public static int nextChanceCard = 0;
	public static int nextCommunityCard = 0;

	/* 1st element pertains to owned status of "Get Out of Jail Free"
	 card in the Chance deck, 2nd element to the Community deck
	  false = not currently owned -- true = currently owned */
	public static boolean[] getOutOfJailCardsOwned = {false, false};

	public static int numberOfPlayers = 6;
	public static int currentPlayersMove = 0;

	/* These are for the PlayerMovesListener to do it's job */
	public static int playerMovesLeft = 0;
	public static int currentLocation = 0;
	/* ---------------------------------------------------- */


	public static int currentPlayersPosition = 0;
	
	/* package-private variables */
	static boolean playerHasMoved = false;
	public static boolean hasToMakeChoice = false;
	static int maxNumHouses = 32;
	/* Movement delay in milliseconds */
	static final int MOVEMENT_DELAY = 100;
	private static final int GENERAL_DEBT = 3;

	private static final String[] colorArray = {"BLACK","BLUE","GREEN","PINK","RED","YELLOW"};
	
	/* statistics */
	public static int numDiceRolled = 0;
	public static int numDoublesRolled = 0;
	public static int numOfRentsPaid = 0;
	public static int numOfHousesBuilt = 0;
	public static int numOfHotelsBuilt = 0;
	public static int numOfSquaresTravelled = 0;
	
	
	
	public static void main(String[] args) {

		/* Attempting to unify the Theme */
		UIManager.put("nimbusBase", new Color(50, 50, 50));
		UIManager.put("nimbusBlueGrey", new Color(50, 50, 50));
		UIManager.put("control", new Color(50, 50, 50));
		UIManager.put("ComboBox.background", new Color(0, 0, 0));
		UIManager.put("textForeground", new Color(150, 150, 150));
		UIManager.put("nimbusLightBackground", new Color(50, 50, 50));
		UIManager.put("nimbusFocus", new Color(70, 70, 150));
		UIManager.put("info", new Color(70, 70, 70));
		UIManager.put("Button.background", new ColorUIResource(50, 50, 50));

		/* Set the look and feel of the UI */
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}

		/* Launch the game */
		new GameLauncher();
		startGameLoop();
	}

	/* package-private method */
	static void startGameLoop() {
			Commands.echoText("\n[??] Type 'help' to see a list of all valid commands\n\n", true, "cyan");

			Player player = playerList.get(currentPlayersMove);
			Commands.echoText(String.format(
					">> %s's (%s) go  Initial balance: %d  Get Out Of Jail Free Cards: %d\n", player.getName(), colorArray[player.getPlayerId()],player.getBalance(),player.getNumGetOutOfJailFreeCards()), true, "YELLOW");
			Commands.echoText("----------------------------------------------------------" +
								"------------------------\n", false, "green");
			Player aPlayer = playerList.get(currentPlayersMove);
			if(aPlayer.isInJail()) {

				/* add a night in jail, check if this is now their third turn in jail*/
				if(aPlayer.addTurnInJail() == 3) {
					Commands.payToGetOutOfJail(playerList.get(currentPlayersMove));
				} else {
						
					Commands.echoText("\n[!!] You're currently in Jail\n", true, "cyan");
					Commands.echoText("[!!] Before you roll:\n" +
									  "[$]   You can type 'pay' to pay $50 to get of jail next turn,\n" +
									  "[$]   you can type 'card' to use a Get Out Of Jail Free card (if you have one),\n" +
									  "[$]   or, try rolling doubles, if you succeed, you're free!", false, "cyan");
				}
				/* Player may have fallen into debt due to another players Chance/Community card */
			} else if (aPlayer.getBalance() < 0) {
				Commands.echoText("----------------------------------------------------------" +
						"------------------------\n", false, "red");
				Commands.echoText("\n[$$] You have fallen into debt and have a negative balance!", false, "yellow");
				Commands.echoText("[$$]\tThis can happen when another player draws a Chance/Community card\n" +
						"which compels you to pay them more than your funds allow.", false, "yellow");
				Commands.echoText("[$$]\tYou must discharge your debt if you can, or claim bankruptcy\n", false, "yellow");
				Commands.echoText("----------------------------------------------------------" +
						"------------------------\n", false, "red");
				Commands.payDebt(getCurrentPlayer(), null, GENERAL_DEBT);
			}
	}

	
	public static Player getCurrentPlayer(){
		return playerList.get(currentPlayersMove);
	}
	
	public static Property getCurrentProperty(){
		return propertyList.get(currentPlayersPosition);
	}
	
	public static String autoCompleteText(String wholeInput) {
		String pattern =
				"^\\s*" +
				"([A-Za-z]+)?" +
				"(?:\\s+([A-Za-z]+))?" +
				"(?:\\s+([A-Za-z]+))?" +
				"(?:\\s+([A-Za-z]+))?" +
				"\\s*$";
		Pattern regexPattern = Pattern.compile(pattern);
		Matcher regexMatcher = regexPattern.matcher(wholeInput);
		String noInput = "NULL";
		/* If we find any matches */
		if (regexMatcher.matches()) {

			/* These can be null, but if so, assign them a string "NULL"
			 * as a null value causes errors when using String.equals() */
			String arg0 = (regexMatcher.group(1) == null)? noInput : regexMatcher.group(1);
			String arg1 = (regexMatcher.group(2) == null)? noInput : regexMatcher.group(2);
			String arg2 = (regexMatcher.group(3) == null)? noInput : regexMatcher.group(3);
			String arg3 = (regexMatcher.group(4) == null)? noInput : regexMatcher.group(4);

			ArrayList<String> stringsToCheck = new ArrayList<>();

			String[] argsArray = {arg0, arg1, arg2, arg3};

			for (String arg : argsArray) {
				if (!arg.equals(noInput)) {
					stringsToCheck.add(arg);
				}
			}

			int numArguments = stringsToCheck.size();
			String fullPhrase;
			String phraseToCheck = "";
			wholeInput = wholeInput.trim();

			if((fullPhrase = shortWords.getWordAutocomplete(stringsToCheck.get(numArguments-1))) != null) {
				if(numArguments > 1 && (phraseToCheck = (shortWords.getWordAutocomplete(stringsToCheck.get(numArguments-2) + " " + fullPhrase))) != null) {
					fullPhrase = phraseToCheck;
					if(numArguments > 2 && (phraseToCheck = (shortWords.getWordAutocomplete(stringsToCheck.get(numArguments-3) + " " + fullPhrase))) != null) {
						fullPhrase = phraseToCheck;
						String originalWording = wholeInput.substring(0,(wholeInput.length()-(stringsToCheck.get(numArguments-1).length() + stringsToCheck.get(numArguments-2).length() + stringsToCheck.get(numArguments-3).length()+2)));
						return (originalWording + fullPhrase);
					} else {
						/*size of individual original arguments + 1 for the space in between them*/
						String originalWording = wholeInput.substring(0,(wholeInput.length()-(stringsToCheck.get(numArguments-1).length() + stringsToCheck.get(numArguments-2).length()+1)));
						return (originalWording + fullPhrase);
					}
				} else {
					String originalWording = wholeInput.substring(0,(wholeInput.length()-stringsToCheck.get(numArguments-1).length()));
					return (originalWording + fullPhrase);
				}
			} else {
				return wholeInput;
			}
		} else {
			return wholeInput;
		}
	}
}