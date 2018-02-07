/*
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 * Exception class thrown when
 * 		- There was an issue initiating the game
*/

package Monopoly.init;

public class BadStartException extends RuntimeException {

	public BadStartException() {
		this("Problem initializing game - please restart");
	}

	public BadStartException(String err) {
		super(err);
	}
}
