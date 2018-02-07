/*
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15725891
 * Commands 
 * 		- Contains methods that users can call throughout game
*/

package Monopoly.core;

import Monopoly.UI.BuildWindow;
import Monopoly.UI.UIPanel;
import Monopoly.cards.CardActions;
import Monopoly.cards.CardManager;
import Monopoly.player.Player;
import Monopoly.player.PlayerMoverThread;
import Monopoly.properties.*;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Map.Entry;

public class Commands {

	/* Convenience variables for readability */
	private static final int JAIL = 10;
	private static final int RENT = 1;
	private static final int TAX = 2;
	private static final int GENERAL_DEBT = 3;


	/* Helper functions for readability */
	public static Player getCurrentPlayer() {
		return Main.getCurrentPlayer();
	}

	public static Property getCurrentProperty() {
		return Main.getCurrentProperty();
	}
	/* -------------------------------- */

	public static boolean payToGetOutOfJail(Player currentPlayer) {
		
		/* if they have the balance */
		if(canPayWithBalance(currentPlayer, 50)) {
			/* forcing player to leave jail after 3 turns */
			if(currentPlayer.getNumTurnsInJail() == 3) {
				Commands.echoText("\n[!!] This is your third turn in jail, you must pay $50 to leave\n", true, "cyan");
			} 
			currentPlayer.transferFunds(50,null);
			currentPlayer.removeFromJail();
		} else {
			/* if they have the liquid assets to pay out */
			if(canPayDebt(currentPlayer,50)) {
				/* if being forced out, deduct 50 => negative balance, print playerMustSellAssets info */
				if(currentPlayer.getNumTurnsInJail() == 3) {
					currentPlayer.transferFunds(50, null);
					echoText(playerMustSellAssets(),true, "cyan");
				} else {
					/* else give player opportunity to wait another turn or simply sell assets before typing pay again */
					Commands.echoText("\n[!!] You must sell assets before you can pay to get out of jail!\n", true, "cyan");
					return false;
				}
			/* if player must leave jail and they don't have assets => must declare bankrupt */
			} else {
				if(currentPlayer.getNumTurnsInJail() == 3) {
					currentPlayer.transferFunds(50,null);
					echoText(playerMustDeclareBankruptcy(),true,"cyan");
				} else {
					echoText("\n[!!]You don't have the balance or assets to pay to get out of jail!\n[!!] You must wait until you do to get out!\n",true,"cyan");
				}
			}
			
		}
		return true;
	}

	public static void echoText(String string, boolean bold, String colour) {
		if (string.length() > 0)
			UIPanel.appendText(string, bold, colour);
	}


	public static boolean useCardToGetOutOfJail(Player currentPlayer) {
		if(currentPlayer.hasGetOutOfJailFreeCard()) {
			currentPlayer.useGetOutOfJailFreeCard();
			currentPlayer.removeFromJail();
			return true;
		} else {
			echoText("[**] You don't have any Get Out Of Jail Free cards!\n", false, "cyan");
			return false;
		}
	}
	
	protected static boolean demolishOnProperty(StreetProperty property, int numberOfBuildingsToDemolish) {
		Player playerDemolishingBuilding = getCurrentPlayer();

		if (numberOfBuildingsToDemolish < 0) {
			echoText("[**] You can't demolish negative buildings!\n", false, "cyan");
			return false;
		} else if (numberOfBuildingsToDemolish == 0) {
			echoText("[**] You want to demolish 0 buildings?!\n", false, "cyan");
			return false;
		} else if (numberOfBuildingsToDemolish > 4) {
			echoText("[**] You can't even have more than 4 buildings on a property!\n", false, "cyan");
			return false;
		}

		/*
		 * check if they own the given property
		 * if there's no hotel (ie. only houses) check that the number to demolish isn't greater than the number built
		 * if there's a hotel, number to demolish must be one
		 */

		if (property.getOwner() != playerDemolishingBuilding) {
			echoText("[**] You can only demolish buildings on properties you own.\n", false, "cyan");
			return false;
		}

		if (!property.hasHotel() && property.numHousesBuilt() < numberOfBuildingsToDemolish) {
			echoText("[**] You don't have that many buildings on this property.\n", false, "cyan");
			return false;
		}

		if (property.hasHotel() && numberOfBuildingsToDemolish != 1) {
			echoText("[**] You don't have that many buildings on this property.\n", false, "cyan");
			return false;
		}

		if (property.hasHotel()) {
			property.demolishBuilding();
			playerDemolishingBuilding.addToBalance(property.getHotelPrice()/2);
		} else {
			for(int i = 0; i < numberOfBuildingsToDemolish;i++) {
				property.demolishBuilding();
			}
			Main.maxNumHouses += numberOfBuildingsToDemolish;
			playerDemolishingBuilding.addToBalance((property.getHousePrice()/2)*numberOfBuildingsToDemolish);
		}
		return true;
	}

	private static boolean playerOwnWholeColorGroup(Player currentPlayer, String color) {

		ArrayList<PurchasableProperty> tempArray = Main.propertiesByColor.get(color);
		
		for (PurchasableProperty propCheck: tempArray) {
			if (propCheck.getOwner() != currentPlayer) {
				echoText("[**] You must own the whole color group to build here.", false, "cyan");
				return false;
			}
		}
		return true;
	}
	
	private static boolean noPropertiesInColorGroupMortgaged(String color) {

		ArrayList<PurchasableProperty> tempArray = Main.propertiesByColor.get(color);
		
		for (PurchasableProperty propCheck: tempArray) {
			if (propCheck.isMortgaged()) {
				echoText("[**] No property of the colour group can be mortgaged to build.", false, "cyan");
				return false;
			}
		}
		return true;
	}
	
	protected static boolean buildOnProperty(StreetProperty property, int numberOfBuildingsToBuild) {
		Player currentPlayer = getCurrentPlayer();

		/*
		 * check if they own the given property
		 * check it's not mortgaged
		 * check that the owner owns all the color group
		 * check that it's not already full
		 * check that they have enough money
		 * check that they can build that many buildings
		 */

		if (property.getOwner() != currentPlayer) {
			echoText("[**] You can only build on properties you own.", false, "cyan");
			return false;
		}

		if (property.isMortgaged()) {
			echoText("[**] You can only build on properties that are not mortgaged.", false, "cyan");
			return false;
		}

		if(!playerOwnWholeColorGroup(currentPlayer, property.getColor())) {
			echoText("[**] You must own the whole color group to build here.", false, "cyan");
			return false;
		}
		
		if(!noPropertiesInColorGroupMortgaged(property.getColor())) {
			echoText("[**] No property of the colour group can be mortgaged if you want to build here.", false, "cyan");
			return false;
		}
		

		if (property.hasHotel()) {
			echoText("[**] You can't build any more on this property.", false, "cyan");
			return false;
		}
		/*4 houses + hotel - if property currently has 0 buildings, if user types build <street> 5 - build hotel
		 * anymore buildings - throw error
		 */
		int numberAlreadyBuilt = property.numHousesBuilt();
		if (property.hasHotel() || (numberAlreadyBuilt + numberOfBuildingsToBuild) > 5) {
			echoText("[**] You can not build more than 4 houses or a single hotel.", false, "cyan");
			return false;
		}
		int costToBuild;
		/*user wants to build a hotel*/
		if (numberAlreadyBuilt+numberOfBuildingsToBuild == 5) {
			/* maximum 32 houses per game - if there aren't enough in the bank, user cannot build a hotel */
			if (numberOfBuildingsToBuild-1 > Main.maxNumHouses) {
				echoText("[**] There are not enough houses available to be built (Limit of 32 houses per game).", false, "cyan");
				return false;
			}
			costToBuild = property.getHotelPrice();
			costToBuild += property.getHousePrice()*(numberOfBuildingsToBuild-1);
			if (costToBuild > currentPlayer.getBalance()) {
				echoText(String.format("[**] You don't have enough money in your account to build %d buildings.", numberOfBuildingsToBuild), false, "cyan");
				return false;
			}
			/* return 4 houses */
			Main.maxNumHouses += 4;
			property.buildHotel();
		} else {
			if (numberOfBuildingsToBuild > Main.maxNumHouses) {
				echoText("[**] There are not enough houses available to be built (Limit of 32 houses per game).", false, "cyan");
				return false;
			}
			costToBuild = property.getHousePrice()*numberOfBuildingsToBuild;
			if (costToBuild > currentPlayer.getBalance()) {
				echoText(String.format("[**] You don't have enough money in your account to build %d buildings.", numberOfBuildingsToBuild), false, "cyan");
				return false;
			}
			/*build x many houses */
			for (int i = 0; i<numberOfBuildingsToBuild;i++) {
				property.buildHouse();
			}
			Main.maxNumHouses -= numberOfBuildingsToBuild;
		}
		currentPlayer.removeFromBalance(costToBuild);
		return true;
	}

	protected static boolean redeemProperty(String title, Player currentPlayer) {
		PurchasableProperty propertyToRedeem = Main.purchasableProperties.get(title);
		if (propertyToRedeem == null) {
			echoText("%s is not a purchasable property", true, "cyan");
			return false;
		}
		Player propertyOwner = propertyToRedeem.getOwner();
		if (propertyOwner == null) {
			echoText(String.format("[**] %s has not been bought by anyone yet", title), true, "cyan");
			return false;
		} else if (currentPlayer == propertyOwner) {
			if (propertyToRedeem.isMortgaged()) {
				int redeemAmount = propertyToRedeem.getMortgageAmount();
				redeemAmount += redeemAmount * 0.10;
				if (currentPlayer.getBalance() >= redeemAmount) {
					currentPlayer.removeFromBalance(redeemAmount);
				} else {
					echoText(String.format("[$$] Your balance is too low to redeem %s", title), true, "cyan");
					echoText(String.format("[$$] Mortgage amount: %20d", redeemAmount), false, "cyan");
					echoText(String.format("[$$] Balance: %20d", currentPlayer.getBalance()), false, "cyan");
					return false;
				}
				propertyToRedeem.setMortgaged(false);
				echoText(String.format(
						"[$$] %s Redeemed. Cost: $%d", propertyToRedeem.getTitle(), redeemAmount), false, "cyan");
				return true;
			} else {
				echoText(String.format("[**] Property %s is not mortgaged", title), true, "cyan");
				return false;
			}
		} else {
			echoText(String.format("[**] Only the owner, %s, can redeem %s", propertyOwner.getName(), title), true, "cyan");
			return false;
		}
	}

	protected static boolean mortgageProperty(String title, Player currentPlayer) {
		PurchasableProperty propertyToMortgage = Main.purchasableProperties.get(title);
		if (propertyToMortgage == null) {
			echoText(String.format("[**] %s is not a purchasable property", title), true, "cyan");
			return false;
		}
		Player propertyOwner = propertyToMortgage.getOwner();
		if (propertyOwner == null) {
			echoText(String.format("[**] %s has not been bought by anyone yet", title), true, "cyan");
			return false;
		}
		if (currentPlayer == propertyOwner) {
			if (propertyToMortgage.isMortgaged()) {
				echoText(String.format("[**] %s is already mortgaged!", title), false, "cyan");
				return false;
			}
			if (propertyToMortgage instanceof StreetProperty) {
				/* Check whether player owns the entire color group */
				boolean ownsWholeColorGroup = true;
				String color = propertyToMortgage.getColor();
				ArrayList<PurchasableProperty> colorGroupArray = Main.propertiesByColor.get(color);
				for (PurchasableProperty property : colorGroupArray) {
					if (property.getOwner() != propertyToMortgage.getOwner()) {
						ownsWholeColorGroup = false;
					}
				}
				/* Check whether property has any buildings */
				if (((StreetProperty) propertyToMortgage).numHousesBuilt() > 0
						|| ((StreetProperty) propertyToMortgage).hasHotel()) {
					echoText("[**] Cannot mortgage a property with buildings on it!\n\n", false, "cyan");
					if (ownsWholeColorGroup) {
						echoText(String.format("\t- You own all properties in the %s color group.\n", color), false, "cyan");
						echoText(String.format("\t- You must sell all buildings on each property\n" +
										"\t  in the group before you can mortgage %s.\n\n", title), false, "cyan");
					}
					echoText("[**] You can sell buildings at half price with 'demolish'\n\n", false, "cyan");
					echoText("\t - Usage: demolish <property> <units(1-5)>\n", false, "cyan");
					return false;
				}
			}
			/* At this point, a mortgage can go ahead */
			int mortgageAmount = propertyToMortgage.getMortgageAmount();
			currentPlayer.addToBalance(mortgageAmount);
			propertyToMortgage.setMortgaged(true);
			echoText(String.format(
					"[$$] Mortgaged %s for $%d", propertyToMortgage.getTitle(), mortgageAmount), false, "cyan");
			return true;
		} else {
			echoText(String.format("[**] Only the owner, %s, can mortgage %s", propertyOwner.getName(), title), true, "cyan");
			return false;
		}
	}

	/* Roll the dice and set the player in motion across the appropriate number of squares
	 * A thread separate to the main UI thread is used to achieve this */
	public static void movePlayer() {
		
		if (Dice.getDiceRoll() == -1) {
	           Player currentPlayer = getCurrentPlayer();
               echoText("\n\n[!!] You rolled doubles three times and are being sent to jail!\n", false, "cyan");
               sendToJail(currentPlayer);
               return;
		}
		/*
			We spin the task of moving the player and updating the
			UI to reflect this movement into a separate thread.
		 */
		PlayerMoverThread playerMoverThread = new PlayerMoverThread(Main.MOVEMENT_DELAY);
		playerMoverThread.start();
	}

	/* Advance the player one square
	*  This method is called from a separate Thread - i.e. NOT the main UI thread 
	*/
	public static int movePieceOneSquare() {
		Main.numOfSquaresTravelled++;
		/* For readability */
		final int playerID = Main.currentPlayersMove;

		/* get location data of given player */
		int playerCoordinate = Main.playerList.get(playerID).getPlayerLocation();

		Main.currentPlayersPosition = ((playerCoordinate + 1) % 40);

		/* invokeLater is a method used to execute the code within run()
			on the main UI Thread. This is necessary because this method(checkSquareStatus)
			is called from a separate thread. This allows us to update the main UI thread
			and visibly re-validate the square involved in player movement */

		/* For readability */
		final Property currentSquare = Main.propertyList.get(playerCoordinate);
		final Property nextSquare = Main.propertyList.get((playerCoordinate + 1) % 40);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				/* Remove player from their current square */
				currentSquare.remove(Main.playerList.get(playerID));

				/* Revalidated layout of the square */
				currentSquare.revalidate();

				/* Add player to next square mod 40 (always in range 0 - 39) */
				nextSquare.addPlayer(playerID);
				nextSquare.revalidate();
			}
		});

		/* update new location of player on board */
		Main.playerList.get(playerID).updateLocation(((playerCoordinate + 1) % 40));
		int updatedLocation = Main.playerList.get(playerID).getPlayerLocation();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				BuildWindow.repaintBoard();
			}
		});

		return updatedLocation;
	}

	/*
	 * This method assesses the current players situation on landing on a new
	 * square. It checks whether the new square is owned, and whether the player
	 * owes rent or tax on the square. It also checks to see whether the player has actually
	 * passed GO by landing on the square. Technically, landing ON GO counts as passing GO,
	 * which doesn't adversely affect game play
	 */
	public static void checkSquareStatus(boolean forced) {


		Property currentProperty = getCurrentProperty();
		int playerPosition = Main.currentPlayersPosition;
		Player currentPlayer = getCurrentPlayer();


		/* Output info on the square the player landed on */
		echoText("[#] You landed on: \n", false, null);
		echoText(currentProperty.getInfo(), false, null);

		/* If rent is owed */
		if (currentProperty.isPurchasable()) {
			/* Show property card on Game Board */
			CardManager.displayCard(currentProperty);

			PurchasableProperty property = (PurchasableProperty) getCurrentProperty();

			/* If property is owned by someone else
				&& it's not mortgaged, then rent must be paid */
			if (
					(property.getOwner() != null)
					&& (property.getOwner() != currentPlayer)
					&& (!property.isMortgaged())) {

				payDebt(currentPlayer, property, RENT);
			}
		}

		/* If tax has to be paid */
		if (currentProperty instanceof TaxProperty) {
			/* Check if player owes tax on the current property */
			TaxProperty taxProperty = (TaxProperty) currentProperty;
			payDebt(currentPlayer, taxProperty, TAX);
		}

		/* If player has passed go */
		if (!forced && playerPosition < ((40 + playerPosition - Dice.getDiceRoll()) % 40) || playerPosition == 0) {
			echoText("\n[#] You passed go! Collect $200\n", false, "cyan");
			currentPlayer.addToBalance(200);

		}

		/* if player has landed on GO TO JAIL */
		if (playerPosition == 30) {
			echoText("\n\n[!!] You have landed on Go To Jail!\n", false, "cyan");
			sendToJail(currentPlayer);
		}

		/* If player has landed on 'Chance' or 'Community Chest' */
		if (currentProperty instanceof CardProperty) {
			CardManager.displayCard(currentProperty);
			CardActions.performCardAction(Main.currentCardOnBoard);
		}
	}

	/* int typOfDebt -> 1 = rent, 2 = tax */
	protected static void payDebt(Player player, Property property, int typeOfDebt) {

		int amountOwed = 0;
		Player receiver = null;
		String message = "";

		switch (typeOfDebt) {
			case RENT:
				PurchasableProperty purchasableProperty = (PurchasableProperty) property;
				amountOwed = purchasableProperty.getRent();
				receiver = purchasableProperty.getOwner();
				String owner = purchasableProperty.getOwner().getName();
				echoText(String.format(
						"\n[$$] This property is owned by %s. You owe $%d in rent!\n", owner, amountOwed), true, "cyan");
				break;

			case TAX:
				amountOwed = ((TaxProperty) property).getTax();
				message = String.format("\n[$$] $%d tax deducted\n", amountOwed);
				break;

			case GENERAL_DEBT:
				/* This type of debt can be incurred by having to pay another player
				due to instructions on a Chance or Community card, and has already been
				paid. However, player then starts their go with a negative balance
				 */
				amountOwed = getCurrentPlayer().getBalance() * -1;
				break;
		}

		if (canPayWithBalance(player, amountOwed)) {
			Main.numOfRentsPaid++;
			player.transferFunds(amountOwed, receiver);
			if (message != null) {
				echoText(message, false, "cyan");
			}
		} else {
			echoText("\n[$$] You do not have sufficient balance to pay your debt!\n\n", true, "cyan");
			if (canPayDebt(player, amountOwed)) {
				Main.numOfRentsPaid++;
				/* Automatically transfer funds from current player to Site owner
					*** This may make the current players balance negative temporarily */
				player.transferFunds(amountOwed, receiver);
				/* inform player they must sell assets to balance debt */
				echoText(playerMustSellAssets(), false, "cyan");
				if (typeOfDebt == GENERAL_DEBT) {
					if (message != null)
						echoText(message, false, "cyan");
				}
			} else {
				/* Player must declare bankruptcy */
				player.transferFunds(amountOwed, receiver);
				echoText(playerMustDeclareBankruptcy(), false, "cyan");
			}

			if (typeOfDebt != GENERAL_DEBT) {
				echoText(message, false, "cyan");
			}
		}
	}

	public static boolean canPayWithBalance(Player player, int amount) {
		return (player.getBalance() >= amount);
	}

	public static String playerMustSellAssets() {
		return  "[$$] A calculation of your assets shows that you CAN pay\n" +
				"[$$] your debt if you sell buildings and/or mortgage properties";
	}

	public static String playerMustDeclareBankruptcy() {
		return  "[$$] A calculation of your current assets shows that you\n" +
				"[$$] can NEVER pay off your debt! You must claim bankruptcy!\n" +
				"\tUsage: type 'bankrupt'\n\n";
	}

	/* Check whether player could pay their debt by selling/mortgaging */
	public static boolean canPayDebt(Player player, int debt) {
		int potentialBalance = player.getLiquidAssets();
		return (potentialBalance >= debt);
	}

	public static void sendToJail(Player currPlayer) {

		Main.playerHasMoved = true;

		/* move player label/icon into Jail */
		getCurrentProperty().remove(currPlayer);
		((JailProperty) Main.propertyList.get(JAIL)).addPlayerToJail(currPlayer);


		/* update position and status of player */
		currPlayer.updateLocation(JAIL);
		Main.currentPlayersPosition = JAIL;
		currPlayer.putInJail();

		/* reset player's jail time to 0 */
		currPlayer.resetJailTime();


		echoText("[$]    You can type 'pay' to pay $50 to get of jail next turn,\n" +
				 "[$]    you can type 'card' to use a Get Out Of Jail Free card (if you have one),\n" +
				 "[$]    or you can wait, if you roll doubles next turn you'll be free!\n\n", false, "cyan");
		

		/* Update UI to reflect player is in Jail now */
		BuildWindow.repaintBoard();

	}

	protected static void buyProperty(PurchasableProperty property) {

		Player currentPlayer = getCurrentPlayer();

		/* Check whether player has the money to buy the property */
		if (currentPlayer.getBalance() >= property.getPrice()) {
			property.setOwner(currentPlayer);
			currentPlayer.removeFromBalance(property.getPrice());
			currentPlayer.addProperty(getCurrentProperty());
			echoText("[$$] Bought property: \t" + "- " + property.getTitle() + "\n", false, "cyan");
		} else {
			echoText(String.format("[$$] You do not have sufficient funds to buy %s\n", property.getTitle()), true, "cyan");
			echoText(String.format("\n\t- Balance: %d", currentPlayer.getBalance()), false, "cyan");
			echoText(String.format("\n\t- Price: %d\n", property.getPrice()), false, "cyan");
		}
	}

	protected static void endTurn() {
		Dice.resetDoublesInARow();
		/* Increment to the next player */
		Main.currentPlayersMove = (Main.currentPlayersMove + 1) % Main.numberOfPlayers;
		Main.playerHasMoved = false;
		/* Clear the info panel */

/*********************************/
		UIPanel.shoveTextUp();
		
		/* remove all cards from the board if any */
		CardManager.clearCards();
		
		Main.startGameLoop();
	}

	/* Outputting the full list of commands */
	protected static void displayHelp() {
		String helpOutput = "\t[#] The following is a list of valid commands\n\n" +
							String.format("\t[?] %-25s %s\n", "help", "- Displays help information") +
							String.format("\t[?] %-25s %s\n", "roll", "- Rolls the dice") +
							String.format("\t[?] %-25s %s\n", "buy", "- Buys the current property") +
							String.format("\t[?] %-25s %s\n", "property", "- Outputs property stats") +
							String.format("\t[?] %-25s %s\n", "mortgage <title>", "- Mortgage a property") +
							String.format("\t[?] %-25s %s\n", "build <title> <units>", "- Build <units> houses on a property") +
							String.format("\t[?] %-25s %s\n", "balance", "- Outputs the players current balance") +
							String.format("\t[?] %-25s %s\n", "pay rent", "- Pays the rent of the current property") +
							String.format("\t[?] %-25s %s\n", "bankrupt", "- Claim bankruptcy, sell assets, quit") +
							String.format("\t[?] %-25s %s\n", "done", "- Ends player turn") +
		 					String.format("\t[?] %-25s %s\n", "quit", "- End game, output summary and winner")+
							String.format("\t[?] %-25s %s\n", "TAB key", "- To auto-complete non-ambiguous words")+
							String.format("\t[?] %-25s %s\n", "UP/DOWN Arrow keys", "- To traverse previously inputted commands");
							
		echoText(helpOutput, false, null);
	}

	protected static boolean declareBankruptcy() {
		Player playerToRemove = getCurrentPlayer();

//		/* If they can pay their debts, don't let them bankrupt */
//		if (playerToRemove.getLiquidAssets() > Main.amountOwed ) {
//			echoText("\n[#] You have assets you can sell to pay your debts!\n", false, "cyan");
//			return false;
//		}

		if(playerToRemove.getLiquidAssets() > 0) {
			echoText("\n[#] You have assets you can sell to pay your debts!\n", false, "cyan");
			return false;
		}
		
		/* demolish all properties owned by player and remove player from game. */
		echoText("\n[##] Removing all assets...\n", false, "cyan");
//		for (PurchasableProperty property: playerToRemove.getPropertyArray()) {
		for(Entry<String, ArrayList<PurchasableProperty>> propertyList : playerToRemove.getPropertyArray().entrySet()) {
			for(PurchasableProperty ownedProperty : propertyList.getValue()) {
				PurchasableProperty  property = ownedProperty;
				if (property instanceof StreetProperty) {
					if (((StreetProperty) property).hasHotel()) {
						((StreetProperty) property).demolishBuilding();
					} else {
						for (int i = ((StreetProperty) property).numHousesBuilt(); i > 0 ; i--) {
							((StreetProperty) property).demolishBuilding();
							Main.maxNumHouses++;
						}
					}
				}
			property.setOwner(null);
			}
		}

		echoText("[##] Removing player from the game.\n", false, "cyan");
		getCurrentProperty().remove(playerToRemove);
		BuildWindow.repaintBoard();
		Main.playerList.remove(playerToRemove);
		Main.numberOfPlayers--;
		return true;
	}

	protected static void quitGame() {
		Player winningPlayer = null;
		int winningPlayerOverAllWorth = -100;
		boolean gameIsADraw = false;

		for (Player aPlayer: Main.playerList) {
			if (aPlayer.getWorth() > winningPlayerOverAllWorth) {
				winningPlayer = aPlayer;
				winningPlayerOverAllWorth = aPlayer.getWorth();
			} else {
				if (aPlayer.getWorth() == winningPlayerOverAllWorth)
					gameIsADraw = true;
			}
		}

		String winnerOutput = ("\n\n[**] Player: " + winningPlayer.getName() + " wins!\n" +
				String.format("\t %-5s %s\n", "Total assets: " , winningPlayerOverAllWorth));

		if (gameIsADraw) {
			winnerOutput = ("\n\n[**] Game is a draw!\n\n");
			for (Player aPlayer: Main.playerList) {
				if (aPlayer.getWorth() == winningPlayerOverAllWorth) {
					winnerOutput += ("\t\t\t" + aPlayer.getName()  +"\n");
				}
			}
			winnerOutput += ("\nAll have equal assets totalling: $" + winningPlayerOverAllWorth);
		}
		
		winnerOutput += ("\nStats:  " +
						"\n        Number of dice rolled : " + Main.numDiceRolled * 2 + "\n" +
						"\n        Number of doubles rolled : " + Main.numDoublesRolled + "\n" +
						"\n        Number of rents paid : " + Main.numOfRentsPaid + "\n" +
						"\n        Number of houses built: " + Main.numOfHousesBuilt + "\n" +
						"\n        Number of hotels built: " + Main.numOfHotelsBuilt + "\n" +
						"\n        Number of squares travelled: " + Main.numOfSquaresTravelled);
		echoText(winnerOutput,false,"cyan");
		UIPanel.toggleTextField();
	}
}