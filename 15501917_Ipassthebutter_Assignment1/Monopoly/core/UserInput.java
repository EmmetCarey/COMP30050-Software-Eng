/*
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 */


package Monopoly.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Monopoly.UI.UIPanel;
import Monopoly.cards.CardActions;
import Monopoly.cards.CardManager;
import Monopoly.player.Player;
import Monopoly.properties.Property;
import Monopoly.properties.PurchasableProperty;
import Monopoly.properties.StreetProperty;

public class UserInput {
	/* Regular Expression settings to capture property names in commands
	 * and check user input for validity */
	private static String pattern =
			"^\\s*" +
			"(roll|buy|property|pay|card|done|mortgage|balance|help|quit|redeem|build|demolish|bankrupt|clear|chance)" +
			"(?:\\s+([A-Za-z]+))?" +
			"(?:\\s+([A-Za-z]+))?" +
			"(?:\\s+([A-Za-z]+))?" +
			"(?:\\s+([0-9]))?" +
			"\\s*$";

	/* Compile regex pattern */
	private static Pattern regexPattern = Pattern.compile(pattern);

	/* Matcher Object for Regular Expressions */
	private static Matcher regexMatcher;

	private static String command;
	private static String argument = "";

	private static int unitValue = -1;

	/* What happens when the "Do" button is pressed */
	public static void doButtonPressed(String commandInput) {

		if (commandInput.length() > 0) {

		/* Check commandInput for matches */
			regexMatcher = regexPattern.matcher(commandInput);

		/* If we find any matches */
			if (regexMatcher.matches()) {

			/* parse the command input using a regular expression */
				parseCommandInput();

			/* Check input for errors */
				if (!inputHasErrors(commandInput)) {
				/* If none are found, execute the command */
					executeCommand(commandInput);
				}

			} else {
			/* No regex matches were found - input was either blank or otherwise invalid */
				invalidInputError(commandInput, null);
			}
		}
	}

	/* Parse the command input in separate 'tokens', the main command followed
	 * by any command arguments. This is done using a regular expression
	 */
	private static void parseCommandInput() {

		String noInput = "NULL";
		/* If there are matches, group 1 definitely captured something */
		argument = "";
		command = regexMatcher.group(1);

		/* These can be null, but if so, assign them a string "NULL"
		 * as a null value causes errors when using String.equals()
		 */
		String arg1 = (regexMatcher.group(2) == null)? noInput : regexMatcher.group(2);
		String arg2 = (regexMatcher.group(3) == null)? noInput : regexMatcher.group(3);
		String arg3 = (regexMatcher.group(4) == null)? noInput : regexMatcher.group(4);
		String arg4 = (regexMatcher.group(5) == null)? noInput : regexMatcher.group(5);

		String[] argsArray = {arg1, arg2, arg3, arg4};

		/* Piece args together into the format "<command> <title/arg1>(if any) <units>(if any)" */
		for (String arg : argsArray) {
			if (arg.matches("^[0-9]$")) {
				unitValue = Integer.parseInt(arg);
			} else if (arg.matches("[A-Za-z]+") && !arg.equals(noInput)) {
				argument += arg + " ";
			}
		}

		/* Trim off trailing or leading whitespace */
		argument = argument.trim();
	}

	/* Check the command input for superfluous, invalid or missing arguments
	 * return true if errors are found, else false */
	private static boolean inputHasErrors(String commandInput) {
		/* Make sure that appropriate number of arguments was entered
			for the matched command
		*/
		if (	(  command.equals("roll")
				|| command.equals("buy")
				|| command.equals("property")
				|| command.equals("done")
				|| command.equals("balance")
				|| command.equals("bankrupt")
				|| command.equals("help")
				|| command.equals("quit") )
				&& (!argument.equals(""))
				) {
			/* There is a superfluous command argument */
			invalidInputError(commandInput, "Superfluous command argument.\nPlease type 'help' to see valid commands.\n");
			return true;

		} else if (command.equals("mortgage") || command.equals("redeem")) {
			if (argument.equals("")) {
				/* There is a missing command argument */
				invalidInputError(commandInput, "Missing command argument");
				return true;

			} else if ((command.equals("mortgage") || command.equals("redeem"))
						&& !Main.purchasableProperties.containsKey(argument)) {
				/* Invalid property title */
				invalidInputError(commandInput, "Invalid property title.\n\n\tUsage: mortgage/redeem <property title>\n");
				return true;

			}

		} else if (command.equals("build") || command.equals("demolish")) {
			if (argument.equals("")) {

				invalidInputError(commandInput, "Missing command argument.\n\n\tUsage: build/demolish <property> <units(1-5)>\n");
				return true;

			} else if (unitValue == -1 || (unitValue > 5 || unitValue < 1)) {

				invalidInputError(commandInput, "Missing or Invalid unit value.\n\n\tUsage: build/demolish <property> <units(1-5)>\n");
				return true;

			} else if (!Main.purchasableProperties.containsKey(argument)) {

				invalidInputError(commandInput, "Invalid property title");
				return true;
			}
		}

		/* No input errors found */
		return false;
	}
	
	/* Once the command input has been parsed and checked for errors, it can be
	 * processed and executed here */
	private static void executeCommand(String commandInput) {

		Player currentPlayer = Main.getCurrentPlayer();
		Property currentProperty = Main.getCurrentProperty();

		echoText("\n\n[>>>] " + commandInput + "\n\n", true, null);

		/* Switch with each case of command input and their functions */
		switch (command) {

			case "help":
				// Output all valid commands
				Commands.displayHelp();
				break;

			case "roll":
				/* Rolling dice */
				if (Main.hasToMakeChoice) {
					echoText("[**] You still have to make a choice: Pay or pick up a chance card!\n", false, "cyan");
				} else if (Main.playerHasMoved) {
					if(currentPlayer.isInJail()) {
						echoText("[**] You've already rolled the dice and you're in jail!\n", false, "cyan");
					} else {
						echoText("[**] You've already rolled the dice!\n", false, "cyan");
					}
				} else {
					if (currentPlayer.getBalance() < 0 ) {
						echoText("[!!] You have debts to pay!\n", false, "cyan");
					} else if(currentPlayer.isInJail()) {
						echoText("[#] Rolling dice...", false, null);
						Main.playerMovesLeft  = Dice.rollDice();
						if (Dice.rolledDoubles()) {
							echoText("[**] You managed to roll doubles!\n", false, null);
							currentPlayer.removeFromJail();
							Commands.movePlayer();
						}  else {
							echoText("[**] Unlucky, maybe you'll roll doubles next time!\n", false, null);
						}

			            Main.playerHasMoved = true;
					} else {

						CardManager.clearCards();
						echoText("[#] Rolling dice...", false, null);
						Main.playerMovesLeft = Dice.rollDice();
						
						Commands.movePlayer();

				   		/* Checking for doubles */
				        if (Dice.rolledDoubles() && !currentPlayer.isInJail()) {
				        	echoText("[#] You rolled doubles, you can roll again!\n", false, "cyan");
				        } else {
				            Main.playerHasMoved = true;
				        }
					}
				}
				break;

			case "buy":
				/* Buy the property currently on (if not owned) and if sufficient funds */
				if (currentProperty.isPurchasable()) {
					if (((PurchasableProperty) currentProperty).isOwned()) {
						echoText("[**] This property is already owned\n", false, "cyan");
					} else {

						Commands.buyProperty((PurchasableProperty) currentProperty);
					}
				} else {
					echoText("[**] This property is not purchasable\n", false, "cyan");
				}
				break;

			case "pay":
				if (currentPlayer.isInJail()) {
					if (Commands.payToGetOutOfJail(currentPlayer)) {
						echoText("[**] Paid $50 to get out of jail!\n", false, "cyan");
						Main.playerHasMoved = true;
					}
				} else if (Main.hasToMakeChoice) {
					if(CardActions.payUp()) {
						echoText("[**] Paid $10 to avoid the chance card!\n", false, "cyan");
						Main.hasToMakeChoice = false;
					} 
				}
				
				else {
					if (currentPlayer.getBalance() < 0 ) {
						echoText("[!!] You owe the bank money, mortgage your properties or demolish some houses!",true,"cyan");
					} else {
						echoText("[**] You don't owe anyone anything!\n", false, "cyan");
					}
				}
				break;
				
			case "card":
				if (currentPlayer.isInJail()) {
					if (Commands.useCardToGetOutOfJail(currentPlayer)) {
						echoText("[**] Get Out Of Jail Free card used to get out of jail!\n", false, "cyan");
						Main.playerHasMoved = true;
					}
				} else {
					echoText("[**] You're not in jail!\n", false, "cyan");
				}
				
				break;
			case "property":
				/* Output the property owned by the player */
				currentPlayer.getProperties();
				break;

			case "balance":
				// Output the player current balance
				echoText("[$$] Your current balance is: " + currentPlayer.getBalance(), false, null);
				echoText("\n",false,null);
				break;

			case "mortgage":
				if (!Commands.mortgageProperty(argument, currentPlayer)) {
					echoText("\n[**] Mortgage failed..\n", true, "red");
				}
				break;

			case "redeem":
				if (!Commands.redeemProperty(argument, currentPlayer)) {
					echoText("\n[**] Redeem Failed..\n", true, "red");
				}
				break;

			case "build":
				Property propToBuildOn = Main.propertyList.get(Main.purchasableProperties.get(argument).getPropertyNumber());
				if (propToBuildOn instanceof StreetProperty) {
					if (Commands.buildOnProperty((StreetProperty) propToBuildOn, unitValue)) {
						echoText("[^^] Building successful!\n", false, null);
					} else {
						echoText("\n[**] Building failed..\n", true, "red");
					}
				} else {
					echoText("[**] You can only build on street properties.", false, "cyan");
					echoText("\n[**] Building failed..\n", true, "red");
				}
				break;

			case "demolish":
				Property propToDemolish = Main.propertyList.get(Main.purchasableProperties.get(argument).getPropertyNumber());
				if (propToDemolish instanceof StreetProperty) {
					if (Commands.demolishOnProperty((StreetProperty) propToDemolish, unitValue)) {
						echoText(String.format("[^^] Demolished %d buildings on %s\n", unitValue, argument), false, null);
					} else {
						echoText("\n[**] Demolition failed..\n", true, "red");
					}
				} else {
					echoText("[**] You can only build/demolish on street properties.", false, "cyan");
					echoText("\n[**] Demolition failed..\n", true, "red");
				}
				break;

			case "done":
				// End players turn
				if(Main.hasToMakeChoice) {
					echoText("[**] You still have to make a choice: Pay or pick up a chance card!\n", false, "cyan");
				} else if (!Main.playerHasMoved) {
					echoText("[**] You still have to roll!\n", false, "cyan");
				} else {
					if (currentPlayer.getBalance() < 0 ) {
						echoText("[**] You must sell any assets you have or declare bankruptcy!!\n", false, "cyan");
					} else {
						echoText("[#] Ending turn\n", false, null);
						echoText("----------------------------------------------------------" +
								"------------------------\n", false, "red");
						Commands.endTurn();
					}
				}
				break;

			case "bankrupt":
				// Declare player bankrupt and remove from game.
				echoText("[#] Declaring bankruptcy", false, null);
				if(Commands.declareBankruptcy()) {
					/*  Check if there is one player left in game, if so just quit the game.*/
					if (Main.numberOfPlayers == 1) {
						echoText("[##] Only one player left ending game.\n",false, "cyan");
						Commands.quitGame();
					} else {

						echoText("[##] Player has left the game.\n",false, "cyan");
						Main.currentPlayersMove = (Main.currentPlayersMove - 1) % Main.numberOfPlayers;
						Commands.endTurn();
					}
				} else {
					echoText("[**] Bankruptcy aborted!\n", false, "cyan");
				}
				break;


			case "quit":
				// End the game
				echoText("[#] Ending game, calculating assets and displaying the winner.", false, null);
				Commands.quitGame();
				break;
				
			case "clear":
				UIPanel.shoveTextUp();
				break;
				
			case "chance":
				CardActions.takeChance();
				Main.hasToMakeChoice = false;
				break;
				
			default:
				/* input matched the regex pattern but wasn't a valid command */
				invalidInputError(commandInput, null);
		}
	}

	private static void echoText(String string, boolean bold, String colour) {
		if (string.length() > 0)
			UIPanel.appendText(string, bold, colour);
	}
	
	private static void invalidInputError(String invalidInput, String errorMsg) {
		echoText("\n\n[!!!] " + invalidInput + "\n\n", true, "red");
		if (errorMsg == null) {
			echoText("[**] Invalid command, type 'help' to list all valid commands.\n", true, "cyan");
		} else {
			echoText(String.format("[**] %s\n", errorMsg), true, "cyan");
		}
	}
}