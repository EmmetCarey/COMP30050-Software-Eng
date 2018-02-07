package Monopoly.Interfaces;

public interface Ownable extends Identifiable,Locatable {
	
	public Float getPrice();
	public String getColour();
	public String getOwner();	
	
}
