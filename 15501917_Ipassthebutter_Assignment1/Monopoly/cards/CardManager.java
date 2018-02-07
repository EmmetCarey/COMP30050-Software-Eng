/*
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 */

package Monopoly.cards;


import Monopoly.UI.*;
import Monopoly.core.Commands;
import Monopoly.core.Main;
import Monopoly.properties.CardProperty;
import Monopoly.properties.Property;
import Monopoly.properties.PurchasableProperty;

import java.awt.*;

public class CardManager {

    private static Card currentCard = null;
    private static ChanceCard chanceCard = null;
	private static CommunityCard communityCard = null;

	private static final String CHANCE = "10";
	private static final String COMMUNITY = "20";

	private static boolean cardRemovedDueToResize = false;
	private static boolean cannotSelectJailCard = false;

    public static void displayCard(Property property) {

        clearCards();

    	cardRemovedDueToResize = false;
    	cannotSelectJailCard = false;
    	
        /* Draw the appropriate Property Card graphic to the game board */
        if (property instanceof PurchasableProperty) {
            /* Show property card */
            PurchasableProperty purchasableProperty = (PurchasableProperty) property;
            PropertyCard propertyCard = new PropertyCard();

            /* less than this size => not worth displaying the cards*/
            GridBagConstraints gbc = new GridBagConstraints();

            gbc.gridx = 1;
            gbc.gridy = 4;
            gbc.weightx = 0;
            gbc.weighty = 0;

            gbc.gridwidth = 6;
            gbc.gridheight = 6;
            gbc.ipadx = 10;
            gbc.ipady = 10;


            propertyCard.initialisePropertyCard(purchasableProperty);
            currentCard = propertyCard;
            getGameBoardPanel().add(currentCard, gbc);
            BuildWindow.repaintBoard();

            /* Draw an appropriate Chance or Community Chest card to the game board */
        } else if (property instanceof CardProperty) {

            /* Set up the parameters for drawing the card to the board */
            GridBagConstraints gbc = new GridBagConstraints();

            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.weightx = 0;
            gbc.weighty = 0;
            gbc.gridwidth = 8;
            gbc.gridheight = 4;

            String cardType = property.getTitle();
            String background;
            String text;
            String type;
            Integer newLocation;
            Integer code;
            Integer amount;

            switch (cardType) {
                case "Chance":
                    String[] chanceArgs = Main.chanceCards.get(Main.nextChanceCard);
                    Main.nextChanceCard = (Main.nextChanceCard + 1) % 16;

                    background = chanceArgs[0];
                    text = chanceArgs[1];
                    code = (chanceArgs[2].equals("")) ? null : Integer.valueOf(chanceArgs[2]);
                    amount = (chanceArgs[3].equals("")) ? null : Integer.valueOf(chanceArgs[3]);
                    newLocation = (chanceArgs[4].equals("")) ? null : Integer.valueOf(chanceArgs[4]);
                    type = chanceArgs[5];
                    chanceCard = new ChanceCard(background, text, code, amount, newLocation, type);
                    currentCard = chanceCard;
                    break;

                case "Community Chest":
                    String[] comChestArgs = Main.communityCards.get(Main.nextCommunityCard);
                    Main.nextCommunityCard = (Main.nextCommunityCard + 1) % 16;

                    background = comChestArgs[0];
                    text = comChestArgs[1];
                    code = (comChestArgs[2].equals("")) ? null : Integer.valueOf(comChestArgs[2]);
                    amount = (comChestArgs[3].equals("")) ? null : Integer.valueOf(comChestArgs[3]);
                    newLocation = (comChestArgs[4].equals("")) ? null : Integer.valueOf(comChestArgs[4]);
                    type = comChestArgs[5];
                    communityCard = new CommunityCard(background, text, code, amount, newLocation, type);
                    currentCard = communityCard;
                    break;
            }

            if (currentCard.getCode() == 5) {
                if ((currentCard.getType().equals(CHANCE) && Main.getOutOfJailCardsOwned[0])
                        || (currentCard.getType().equals(COMMUNITY)) && Main.getOutOfJailCardsOwned[1]) {
                    /* If we are here, that means we got a Get Out of Jail Free card, but they
                    are all already owned, so we pass over it and select a different card from
                    the deck!*/
                    cannotSelectJailCard = true;
                }
            }

            if (!cannotSelectJailCard) {

                getGameBoardPanel().add(currentCard, gbc);

                Main.currentCardOnBoard = currentCard;
                String color = "pink";
                Commands.echoText("\n----------------------------------------------------------" +
                        "------------------------\n", false, color);
                Commands.echoText(String.format(
                        "\n[?!] %s\n\n- %s\n\n", cardType, currentCard.getCardText()), false, color);
                Commands.echoText("----------------------------------------------------------" +
                        "------------------------\n", false, color);
            } else {
                displayCard(property);
            }
        }

        /* Repaint to display the current card */
        resizeComponents();
        BuildWindow.repaintBoard();
    }

    /* Some static methods for managing graphical card representations */

    protected static GameBoardPanel getGameBoardPanel() {
        return BuildWindow.getBoard();
    }

    public static void clearCards() {
        if (!(currentCard == null)) {
            getGameBoardPanel().remove(currentCard);
            currentCard = null;
        }

        BuildWindow.repaintBoard();
    }

    public static void resizeComponents() {

        if (currentCard != null || cardRemovedDueToResize) {
        	/* if board is less than this - what's the point of tiny text */
        	if(BuildWindow.getBoard().getHeight() < 710	) {
        		cardRemovedDueToResize = true;
        		currentCard.setVisible(false);
        	} else {
        		if(cardRemovedDueToResize) {
            		currentCard.setVisible(true);
        		}
        		currentCard.reSize();
        	}
        }
    }
}
