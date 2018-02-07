/*
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 */

package Monopoly.UI;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Monopoly.init.BadResourceException;
import Monopoly.init.GameLauncher;

public class CommunityCard extends Card {
	
	BufferedImage backgroundImage;
    private final String resourcePath = "gameCards/";
    private String imageName;

    public CommunityCard(String image, String text, Integer code, Integer amount, Integer newLocation, String type) {
        super(image, text, code, amount, newLocation, type);
        imageName = image;
    }
    
    @Override
    public void reSize() {
    	 try {
             backgroundImage = ImageIO.read(
                     GameLauncher.class.getClassLoader().getResource(resourcePath + imageName));
         } catch (IOException ex) {
             throw new BadResourceException("Error reading comChestCardTemplate.png");
         }
    	 repaint();
    	 super.reSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return backgroundImage == null ?
                new Dimension(0, 0) :
                  new Dimension((int) Math.floor(BuildWindow.getBoard().getHeight()*.4),
                		  		(int) Math.floor(BuildWindow.getBoard().getHeight()*.2));
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (backgroundImage != null) {
            backgroundImage = GameBoardPanel.resizeImage(
                    backgroundImage, this.getWidth(), this.getHeight(), false);
        }
        g.drawImage(backgroundImage, 0, 0, this);

    }
}
