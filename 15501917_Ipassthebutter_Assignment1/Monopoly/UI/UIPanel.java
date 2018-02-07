/*
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 * Builds right panel - contains inner classes CommandInputPanel and InfoDisplayPanel
*/

package Monopoly.UI;

import Monopoly.core.Main;
import Monopoly.core.UserInput;
import Monopoly.dataStrucs.HistoryVector;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.util.Vector;

import javax.swing.*;
import javax.swing.text.*;

public class UIPanel extends JPanel {

	private JSplitPane verticalSplitPane;
	private InfoDisplayPanel infoDisplayPanel = new InfoDisplayPanel();
	private static CommandInputPanel commandInputPanel = new CommandInputPanel();

	/* InfoDisplayPanel contains textPane - displaying information -
	bottomPanel contains textField - for input of information and doButton
	These sit in the JSplitPane */

	public UIPanel() {

		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		setMinimumSize(new Dimension(400,750));
		verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, infoDisplayPanel, commandInputPanel);

		/* Set the split pane divider */
		verticalSplitPane.setDividerLocation(740 - 100);
		verticalSplitPane.setTopComponent(infoDisplayPanel);
		verticalSplitPane.setBottomComponent(commandInputPanel);

		/* pane is not resizeable */
		verticalSplitPane.setEnabled(false);
		add(verticalSplitPane);
		setFocus();
	}

	public void resizeComponents() {
		verticalSplitPane.setDividerLocation((int) (getHeight()-getInsets().top-getInsets().bottom-((this.getHeight()-getInsets().top-getInsets().bottom)*.13)));
		InfoDisplayPanel.resizeComponents();
		CommandInputPanel.resizeComponents();
	}

	public static void setDefaultKey() {
		commandInputPanel.setDefaultKey();
	}

	public static void appendText(String string, boolean bold, String colour) {
		InfoDisplayPanel.appendText(string, bold, colour);
		CommandInputPanel.resetTextField();
	}

	public static void toggleTextField() {
		CommandInputPanel.toggleTextField();
	}

	public static void setFocus() {
		CommandInputPanel.setFocus();
	}

	public static void clearText() {
		InfoDisplayPanel.clearText();
	}

/****************/
	public static void shoveTextUp(){
		InfoDisplayPanel.shoveTextUp();
	}
/*********************/
	
	
	
	/* This JPanel contains a JScrollPane which contains the
		JTextArea Info Panel */
	private static class InfoDisplayPanel extends JPanel {

		/* The Info Panel */
		private static final JTextPane textPane = new JTextPane();
		private static final JScrollPane scrollPane = new JScrollPane(textPane);

		/* Document into which we insert Strings */
		private static StyledDocument document = textPane.getStyledDocument();

		/* Key to use for text manipulation */
		private static final SimpleAttributeSet keyWord = new SimpleAttributeSet();

		public InfoDisplayPanel() {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			textPane.setEditable(false);
			textPane.setFont(new Font("monospaced", Font.PLAIN, 12));
			textPane.setBackground(Color.DARK_GRAY);
			textPane.setForeground(Color.LIGHT_GRAY);

			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

			/* add scrollPane. scrollPane contains textPane. */
			add(scrollPane);
		}

		public static void resizeComponents() {
			int size = (int) (BuildWindow.getBoard().getHeight() * 0.015);
			
			if(size < 12) {
				size = 12;
			}
			textPane.setFont(new Font("monospaced", Font.PLAIN, size));
		}
		
		public static void clearText() {
			textPane.setText("");
		}

/******************************/
		private static int endOfDocument = 0;
		private static int numOfEmptyLines = 0;
		private static int realNumOfLines = 0;
		private static boolean clearInitialised = false;
		
		
		public static void shoveTextUp(){
            if(clearInitialised) {
            	if(realNumOfLines < getMaxNumOfLinesInPane() ) {
		            for(int i = 0; i < realNumOfLines ; i++) {
		            	addEmptyLine();
		            }
            	} else {
            		for(int i = 0 ; i < getMaxNumOfLinesInPane() ; i++) {
            			addEmptyLine();
            		}
            	}
            } else {
            	for(int i = 0; i < getMaxNumOfLinesInPane(); i++) {
            		addEmptyLine();
            	}
            	clearInitialised = true;
            }
            
            realNumOfLines = 0;
            numOfEmptyLines = getMaxNumOfLinesInPane();
		}
		
		private static void addEmptyLine() {
			try {
				document.insertString(document.getLength(), "\n",keyWord );
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		
		
		private static int getMaxNumOfLinesInPane(){
			JViewport viewPort = scrollPane.getViewport();
			Rectangle viewRect = viewPort.getViewRect();
			int lineHeight = textPane.getFontMetrics(textPane.getFont()).getHeight();
            return viewRect.height/lineHeight;
		}
		
		private static int getNumOfNewLinesInString(String str) {
			int numOfNewLineChars = 0;
			for(char character: str.toCharArray()) {
				if(character == '\n') {
					numOfNewLineChars++;
				}
			}
			return numOfNewLineChars;
		}
		
		public static void appendText(String string, boolean bold, String colour) {

			if (bold) {
				StyleConstants.setBold(keyWord, true);
			}

			if (colour != null) {
				/* use String color to invoke a method on the Color class*/
				try {
					Field field = Color.class.getField(colour);
					Color color = (Color) field.get(null);
					StyleConstants.setForeground(keyWord, color);
				} catch (NoSuchFieldException  | IllegalAccessException ex) {
					ex.printStackTrace();
					StyleConstants.setForeground(keyWord, Color.LIGHT_GRAY);
				}
			}

			/* Append text with any Styles set */
			try {
				
				int numOfNewLineChars = getNumOfNewLinesInString(string);

				/*line to pad text pane */
				if(getMaxNumOfLinesInPane() == numOfEmptyLines) {
					numOfNewLineChars++;
				}
				
				realNumOfLines += numOfNewLineChars;
				
				if(numOfEmptyLines > numOfNewLineChars) {
					document.remove(endOfDocument, numOfNewLineChars);
					numOfEmptyLines -= numOfNewLineChars;
				} else {
					if(numOfEmptyLines > 0) {
						document.remove(endOfDocument,numOfEmptyLines);
						numOfEmptyLines = 0;
					}
				}
				document.insertString(endOfDocument,string,keyWord);
				endOfDocument += string.length();
				
			} catch (BadLocationException ex) {
				ex.printStackTrace();
			}

			/* Keep textPane scrolled to the bottom */
			textPane.setCaretPosition(textPane.getDocument().getLength());

			/* Reset any Styles that were set above */
			StyleConstants.setBold(keyWord, false);
			StyleConstants.setForeground(keyWord, Color.LIGHT_GRAY);
		}
	}
/*************************************/
		
		
		
		/* old version*/
//		public static void appendText(String string, boolean bold, String colour) {
//
//			if (bold) {
//				StyleConstants.setBold(keyWord, true);
//			}
//
//			if (colour != null) {
//				/* use String color to invoke a method on the Color class*/
//				try {
//					Field field = Color.class.getField(colour);
//					Color color = (Color) field.get(null);
//					StyleConstants.setForeground(keyWord, color);
//				} catch (NoSuchFieldException  | IllegalAccessException ex) {
//					ex.printStackTrace();
//					StyleConstants.setForeground(keyWord, Color.LIGHT_GRAY);
//				}
//			}
//
//			/* Append text with any Styles set */
//			try {
//				document.insertString(document.getLength(), string, keyWord);
//			} catch (BadLocationException ex) {
//				ex.printStackTrace();
//			}
//
//			/* Keep textPane scrolled to the bottom */
//			textPane.setCaretPosition(textPane.getDocument().getLength());
//
//			/* Reset any Styles that were set above */
//			StyleConstants.setBold(keyWord, false);
//			StyleConstants.setForeground(keyWord, Color.LIGHT_GRAY);
//		}
//	}

	/* This JPanel contains the JTextField command input and a button to execute
		any input commands */
	private static class CommandInputPanel extends JPanel implements ActionListener {

		protected static Vector<String> commandHistory = new HistoryVector<String>(12);
		private static int historyIndex = -1;
		private static String historyTempString = "";

		/* Command Input text field */
		private static final JTextField textField = new JTextField();
		JButton doButton = new JButton("Do");

		public CommandInputPanel() {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			doButton.addActionListener(this);

			textField.setBackground(Color.DARK_GRAY);
			textField.setForeground(Color.LIGHT_GRAY);
			textField.setCaretColor(Color.WHITE);
			textField.setFocusTraversalKeysEnabled(false);

			textField.setFont(new Font("monospaced", Font.PLAIN, 12));
			
			textField.addKeyListener(new KeyListener() {
				
				@Override
				public void keyPressed(KeyEvent e) {}

				@Override
				public void keyReleased(KeyEvent e) {
					String userInput = textField.getText();
					if(e.getKeyCode() == KeyEvent.VK_TAB && !(userInput.trim().equals(""))) {
						textField.setText(Main.autoCompleteText(userInput));
					} else if (e.getKeyCode() == KeyEvent.VK_UP) {
						if(historyIndex == -1) {
							historyTempString = textField.getText();
						}
						if(commandHistory.size()-1 > historyIndex) {
							textField.setText(commandHistory.get(++historyIndex));
						}
					} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
						if(historyIndex > 0) {
							textField.setText(commandHistory.get(--historyIndex));
						} else if(historyIndex == 0) {
							historyIndex = -1;
							textField.setText(historyTempString);
							
						}
					}
				}

				@Override
				public void keyTyped(KeyEvent e) {}
			});
			add(textField);
			add(doButton);
		}

		/* Doesn't have a rootpane until after because of the order we set new objects */
		private void setDefaultKey() {
			/* allows [ENTER] key to invoke the doButton also */
			this.getRootPane().setDefaultButton(doButton);
		}

		public static void resizeComponents() {
			int size = (int) (BuildWindow.getBoard().getHeight() * 0.016);
			
			if(size < 12) {
				size = 12;
			}
			textField.setFont(new Font("monospaced", Font.PLAIN, size));
		}
		
		public static void resetTextField() {
			textField.setText("");
		}

		public static void toggleTextField() {
			if(textField.isEnabled()) {
				textField.setEnabled(false);
			} else {
				textField.setEnabled(true);
				setFocus();
			}
		}

		public static void setFocus() {
			textField.grabFocus(); 
			textField.requestFocus();
		}

		/* What happens when each button is pressed */
		@Override
		public void actionPerformed(ActionEvent actionEvent) {

			/* The doButton was pressed */
			if (actionEvent.getSource() == doButton) {
				historyIndex = -1;
				String command = textField.getText();
				if (command.trim().length() > 0) {
					commandHistory.add(command);
				/* Call method in GameLauncher main class */
					UserInput.doButtonPressed(command);
				} else {
					textField.setText("");
				}
					
			}
		}
	}
}