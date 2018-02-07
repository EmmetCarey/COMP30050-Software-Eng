/*
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 */


package Monopoly.core;

import java.util.Random;

public class Dice {

    private static int diceRoll;
    private static int doublesInARow = 0;
    private static boolean rolledDoubles = false;
    
    /* Rolling an individual die */
    public static int getRandNumber() {
        Random ranNum = new Random();
        return ranNum.nextInt(6) + 1;
    }

    public static int getDiceRoll() {
    	return diceRoll;
    }
    
    public static boolean rolledDoubles() {
    	return rolledDoubles;
    }
    
    public static void resetDoublesInARow(){
    	doublesInARow = 0;
    }
    
    /* Rolling the dice */
    static int rollDice() {
    	Main.numDiceRolled++;
    	rolledDoubles = false;
        int die1 = Dice.getRandNumber();
        int die2 = Dice.getRandNumber();

        Commands.echoText("\n[#] You Rolled: " + die1 + " and " + die2 + "\n", false, null);
       
        if (die1 == die2) {
        	Main.numDoublesRolled++;
        	rolledDoubles = true;
            doublesInARow++;
            if (doublesInARow == 3) {
                /* Go to Jail */
                doublesInARow = 0;
                diceRoll = -1;
                return diceRoll;
            }
        } else {
            doublesInARow = 0;
        }
		/* Return the total */
        diceRoll = die1 + die2;
        return die1 + die2;
    }
}
