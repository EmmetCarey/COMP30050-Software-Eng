/*	
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 * TaxProperty class
 * 		- Property class for tax squares for later sprint
*/

package Monopoly.properties;

public class TaxProperty extends Property {

	private int tax;
	public TaxProperty(int sideOfBoard, String title, int taxCost,boolean isPurchasable) {
		super(sideOfBoard, title, isPurchasable);
		tax = taxCost;
	}
	
	public int getTax(){
		return tax;
	}
}
