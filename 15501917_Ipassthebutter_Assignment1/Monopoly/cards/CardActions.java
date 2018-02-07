package Monopoly.cards;

import java.util.ArrayList;
import java.util.Map.Entry;

import Monopoly.UI.BuildWindow;
import Monopoly.UI.Card;
import Monopoly.core.Commands;
import Monopoly.core.Main;
import Monopoly.player.Player;
import Monopoly.properties.Property;
import Monopoly.properties.PurchasableProperty;
import Monopoly.properties.StreetProperty;

public class CardActions {

	static int amountToPay = 0;
	static Player currentPlayer;
	private static final String CHANCE = "10";
	
	
	/***********/
	boolean test = true;
	
    public static void performCardAction(Card card) {

        int cardCode = card.getCode();
//        int cardCode = 3;
        currentPlayer = Main.getCurrentPlayer();

        /* Perform the action according the cards code
	        1 = add balance
            2 = remove balance
            3 = teleport
            4 = send player to jail
            5 = get out of jail card
            6 = receive money from other players
            7 = pay upkeep on houses/hotels
            8 = remove balance or take chance card
         */
        switch (cardCode) {

            case 1:
                /* Add to balance */
                int amountToAdd = card.getAmount();
                currentPlayer.addToBalance(amountToAdd);
                Commands.echoText(String.format(
                        "\n[$$] Player %s received $%d!\n",
                        currentPlayer.getName(), amountToAdd), false, "cyan");
                break;

            case 2:
                /* Remove from balance */
                int amountToRemove = card.getAmount();

                if (!Commands.canPayWithBalance(currentPlayer, amountToRemove)) {
                    Commands.echoText(
                            "\n[$$] You do not have sufficient balance to pay your debt!\n\n", true, "cyan");

                    if (Commands.canPayDebt(currentPlayer, amountToRemove)) {
				        /* inform player they must sell assets to balance debt */
                        Commands.echoText(Commands.playerMustSellAssets(), false, "cyan");
                    } else {
                        Commands.echoText(Commands.playerMustDeclareBankruptcy(), false, "cyan");
                    }
                }

                currentPlayer.removeFromBalance(amountToRemove);
                Commands.echoText(String.format(
                        "\n[$$] Player %s was deducted $%d!\n",
                        currentPlayer.getName(), amountToRemove), false, "cyan");

                break;

            case 3:
                /* Teleport player to square */
                int newLocation = card.getNewLocation();
//                int newLocation = -3;
                int playerCoordinate = currentPlayer.getPlayerLocation();

                Main.numOfSquaresTravelled += newLocation-playerCoordinate;
                
                /* If card says to go backwards */
                if (newLocation < 0) {
                    if (newLocation == -3) {
                        newLocation = (playerCoordinate + 37) % 40;

                    } else {
                        /* Go to Old Kent Road */
                        newLocation = 1;
                    }
                }

                Main.currentLocation = newLocation;

                Property currentSquare = Main.getCurrentProperty();
                Property newSquare = Main.propertyList.get(newLocation);

                currentSquare.remove(currentPlayer);
                currentSquare.revalidate();

                newSquare.addPlayer(Main.currentPlayersMove);
                newSquare.revalidate();

                currentPlayer.updateLocation(newLocation);	
                Main.currentPlayersPosition = newSquare.getPropertyNumber();
                		
                Commands.echoText(String.format(
                        "\nPlayer %s was transported to %s\n",
                        currentPlayer.getName(), Main.propertyList.get(newLocation).getTitle()), false, "cyan");

                if(card.getNewLocation() < 0) {
                	Commands.checkSquareStatus(true);
                } else {
                	Commands.checkSquareStatus(false);
                }
                
                BuildWindow.repaintBoard();
                
                break;

            case 4:
                /* Send player to jail */
                Commands.sendToJail(currentPlayer);
                break;

            case 5:
                 /* Get out of jail free */
                currentPlayer.addGetOutOfJailFreeCard(card.getType());
                Commands.echoText(String.format(
                        "\n[##] Player %s now has a Get Out Of Jail Free card!\n",
                        currentPlayer.getName()), false, "cyan");

                break;

            case 6:
                /* Receive money from other players */
                amountToPay = card.getAmount();

                Commands.echoText("\n", false, null);
                for (Player player : Main.playerList) {
                    /* It's possible that a player goes into negative balance as a result */
                    /* This is dealt with when it is that players turn */
                    if (player != currentPlayer) {
                        player.transferFunds(amountToPay, currentPlayer);
                    }
                }

                Commands.echoText(String.format(
                        "\n[$$] Player %s received $%d from all players",
                        currentPlayer.getName(), card.getAmount()), false, "cyan");
                break;

            case 7:
                /* Pay upkeep on houses/hotels */
            	int upKeepOnBuildings = 0;
            	String parseAmount = card.getAmount().toString();
            	
            	int costForHouse = Integer.parseInt(parseAmount.substring(0,2));
            	int costForHotel = Integer.parseInt(parseAmount.substring(2));
            	
            	for(Entry<String, ArrayList<PurchasableProperty>> propertyList : currentPlayer.getPropertyArray().entrySet()) {
        			for(PurchasableProperty ownedProperty : propertyList.getValue()) {
        				PurchasableProperty  property = ownedProperty;
        				if (property instanceof StreetProperty) {
        					if (((StreetProperty) property).hasHotel()) {
        						upKeepOnBuildings += costForHotel;
        					} else {
        						upKeepOnBuildings += costForHouse * ((StreetProperty)property).numHousesBuilt();
        					}
        				}
        			}
        		}
            	
            	if(upKeepOnBuildings == 0) {
            		  Commands.echoText("\n[**] You do not have any buildings!\n", false, "cyan");
            	}
            	
                if (!Commands.canPayWithBalance(currentPlayer, upKeepOnBuildings)) {
                    Commands.echoText(
                            "\n[$$] You do not have sufficient balance to pay your debt!\n\n", true, "cyan");

                    if (Commands.canPayDebt(currentPlayer, upKeepOnBuildings)) {
				        /* inform player they must sell assets to balance debt */
                        Commands.echoText(Commands.playerMustSellAssets(), false, "cyan");
                    } else {
                        Commands.echoText(Commands.playerMustDeclareBankruptcy(), false, "cyan");
                    }
                }

                currentPlayer.removeFromBalance(upKeepOnBuildings);
                Commands.echoText(String.format(
                        "\n[$$] Player %s was deducted $%d!\n",
                        currentPlayer.getName(), upKeepOnBuildings), false, "cyan");

            	break;

            case 8:
                /* Remove balance OR take Chance card */
            	Commands.echoText("[!!]Type 'pay' to pay the bill or 'chance' to pick up a Chance Card\n", false, "cyan");
            	amountToPay = card.getAmount();
            	Main.hasToMakeChoice = true;

                break;
        }

    }
    
    public static boolean payUp() {
    	if(Commands.canPayWithBalance(currentPlayer, amountToPay)) {
    	     currentPlayer.removeFromBalance(amountToPay);
             Commands.echoText(String.format(
                     "\n[$$] Player %s was deducted $%d!\n",
                     currentPlayer.getName(), amountToPay), false, "cyan");
             return true;
    	} else {
    		Commands.echoText("[$$]Not enough in your bank to pay",false,"cyan");
    		Commands.echoText(Commands.playerMustSellAssets(),false,"cyan");
    	}
		return false;
    }
    
    public static void takeChance() {
    	CardManager.clearCards();
		Commands.echoText("[**]Picking up a chance card!",false,"cyan");
    	CardManager.displayCard(Main.propertyList.get(7));
		performCardAction(Main.currentCardOnBoard);
    }
}

