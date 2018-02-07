/*
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 */

package Monopoly.UI;

import Monopoly.init.BadResourceException;
import Monopoly.init.GameLauncher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Card extends JPanel {

    private JLabel cardTextLabel;
    private Integer code;
    private Integer amount;
    private String type;
    private String cardText;
    private Integer newLocation;
    private BufferedImage backgroundImage;
    private final String resourcePath = "gameCards/";

    public Card() {

    }

    public Card(String image, String text, Integer code, Integer amount, Integer newLocation, String type) {
        cardText = text;
        this.type = type;
        this.code = code;
        this.amount = amount;
        this.newLocation = newLocation;

        try {
            backgroundImage = ImageIO.read(
                    GameLauncher.class.getClassLoader().getResource(resourcePath + image));
        } catch (IOException ex) {
            throw new BadResourceException("Error reading chanceTemplate.png");
        }

        setLayout(new BorderLayout());

        int width = (int) (getGameBoardPanel().getWidth() * 0.40);
        int height = (int) (getGameBoardPanel().getHeight() * 0.20);
        setMinimumSize(new Dimension(width, height));

        setCardText(cardText);
    }

	public void reSize(){
		cardTextLabel.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,(int) Math.floor(BuildWindow.getBoard().getHeight() * 0.023)));
	}

	public String getType() { return type; }

	public Integer getNewLocation() { return newLocation; }

	/* return sum of money to be payed or received
	according to Chance / Community card instructions */
	public Integer getAmount() {
	    return amount;
    }

	public Integer getCode() {
	    return code;
    }

    public String getCardText() {
	    return cardText;
    }

	private void setCardText(String cardText) {
        cardTextLabel = new JLabel();

        cardTextLabel.setForeground(Color.BLACK);
        cardTextLabel.setHorizontalAlignment(JLabel.CENTER);

        int width = (int) (getGameBoardPanel().getWidth() * 0.20);
        String text = String.format(
                "<html><div style=\"text-align:center;width:%dpx;\">%s</div><html>", width, cardText);
        cardTextLabel.setText(text);

		cardTextLabel.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,(int) Math.floor(BuildWindow.getBoard().getHeight() * 0.023)));
        add(cardTextLabel);
    }

    private static GameBoardPanel getGameBoardPanel() {
        return BuildWindow.getBoard();
    }
	
}
