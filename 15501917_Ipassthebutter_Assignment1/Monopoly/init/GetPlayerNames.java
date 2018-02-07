package Monopoly.init;

import Monopoly.init.GameLauncher;
import Monopoly.core.Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GetPlayerNames extends JDialog implements ActionListener {

	private static ArrayList<JTextField> playerNameInputs = new ArrayList<>();
	private static ArrayList<PlayerInputPanel> addedInputPanels = new ArrayList<>();

	/* possible number of players */
	private static JComboBox<Integer> numberOfPlayersGetter = new JComboBox<>(new Integer[] {2,3,4,5,6});

	/* initially 2 players */
	private static int currentNumberOfPlayers = 2;
	private static int defaultNumberOfPlayers = 2;

	/*JDialog consists of numberPanel on top - with ComboBox, dialogPanel center - where player inputs the name, buttonPanel bottom - containing OK and cancel button*/
	private static JPanel dialogPanel = new JPanel();
	private static JPanel numberPanel = new JPanel();
	private static JPanel buttonPanel = new JPanel();

	private static JButton ok = new JButton("Ok");
	private static JButton cancel = new JButton("Cancel");

	/* Inner class constructor */
	public GetPlayerNames() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("Player Names");
		setSize(new Dimension(350, 500));

		/*Board won't be set up until this dialog is dealt with*/
		setModal(true);
		setModalityType(ModalityType.DOCUMENT_MODAL);

		setUpPanels();

		/*Putting dialog in centre screen */
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point middle = new Point(screenSize.width / 2, screenSize.height / 2);
		Point newLocation = new Point(middle.x - (350/2),
									  middle.y - ((180+(6*35))/2));
		setLocation(newLocation);
		this.getRootPane().setDefaultButton(ok);

		setVisible(true);
		pack();
	}

	private void setUpPanels() {
		setButtonPanel();
		setComboBoxPanel();
		setDialogPanel();

		add(dialogPanel,BorderLayout.CENTER);
		add(numberPanel,BorderLayout.NORTH);
		add(buttonPanel,BorderLayout.SOUTH);
	}

	/*dialogPanel consists of between 2 & 6 PlayerInputPanels - instantiates them and adds them to the array for reference later*/
	private PlayerInputPanel buildInputPanel() {
		addedInputPanels.add(new PlayerInputPanel());
		return (addedInputPanels.get(addedInputPanels.size()-1));
	}

	private void setDialogPanel() {
		dialogPanel.setPreferredSize(new Dimension(280, (106+(6*35))));
		dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.PAGE_AXIS));
		/* initially adding two player name input panels */
		for (int i = 0; i < defaultNumberOfPlayers; i++) {
			dialogPanel.add(buildInputPanel());
		}
	}

	private void setComboBoxPanel() {
		/* numberOfPlayersGetter is the JComboBox - drop down menu between 2 and 6 */
		numberOfPlayersGetter.addActionListener(this);
		numberOfPlayersGetter.setBackground(Color.WHITE);
		numberPanel.add(new JLabel("Number of Players: "), BorderLayout.LINE_START);
		numberPanel.add(numberOfPlayersGetter, BorderLayout.LINE_END);
		numberPanel.setBorder(BorderFactory.createEmptyBorder(20, 5, 10, 5));
	}

	private void setButtonPanel() {
		buttonPanel.setLayout(new BorderLayout(100,50));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 60, 20, 60));
		ok.setPreferredSize(new Dimension(100,28));
		ok.addActionListener(this);
		cancel.setPreferredSize(new Dimension(100,28));
		cancel.addActionListener(this);
		JLabel requiredField = new JLabel("* Indicates required field.");
		requiredField.setForeground(Color.RED);
		buttonPanel.add(requiredField, BorderLayout.NORTH);
		buttonPanel.add(ok, BorderLayout.WEST);
		buttonPanel.add(cancel, BorderLayout.EAST);
	}

	/** Inner Inner class **/
	/* class extending JPanel containing "Player X <JTextField>*, errorText" */
	private class PlayerInputPanel extends JPanel implements FocusListener {
		
		JTextField playerTextField = new JTextField();
		JLabel errorDisplay = new JLabel(" ");
		
		public PlayerInputPanel() {
			setUpTextField();
			buildPanel();
		}

		private void setUpTextField() {

			playerTextField.setBackground(Color.WHITE);
			playerTextField.setPreferredSize(new Dimension(190, 30));

			/* Foreground colour initially light_grey,
			 makes for the transparent looking 'hint' text saying "enter
			 name here" - changes with focuslistener*/
			playerTextField.setForeground(Color.LIGHT_GRAY);
			playerTextField.setText("Enter player name here");
			playerTextField.addFocusListener(this);

			/*Add the textField to the inputs arrayList*/
			playerNameInputs.add(playerTextField);
		}

		private void buildPanel() {
			/* Our lord & saviour - GridBagLayout */
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();

			/* Adding Player X: before the field */
			gbc.gridheight = 1;
			gbc.gridwidth = 2;
			gbc.gridx = 0;
			gbc.gridy = 0;
			add(new JLabel("Player " + (addedInputPanels.size()+1) + ":  "), gbc);

			/* adding text input slot*/
			gbc.gridx = 2;
			add(playerNameInputs.get(playerNameInputs.size()-1), gbc);

			/* adding asterisk right after input slot */
			gbc.gridwidth = 1;
			gbc.gridx = 4;
			JLabel asterisk = new JLabel("*");
			asterisk.setFont(new Font("monospaced", Font.PLAIN, 22));
			asterisk.setForeground(Color.red);
			add(asterisk, gbc);

			/* Adding error label below textField */
			gbc.gridx = 2;
			gbc.gridy = 1;
			gbc.gridwidth = 5;
			gbc.anchor = GridBagConstraints.WEST;
			errorDisplay.setFont(new Font("DejaVu", Font.PLAIN,11));
			errorDisplay.setForeground(Color.red);
			add(errorDisplay, gbc);
		}

		/*If problem with the input, put error here*/
		public void setProblem(String s) {
			errorDisplay.setText(" " + s);
		}

		/*when problem's rectified*/
		public void removeProblem() {
			/*single space so the label doesn't collapse*/
			errorDisplay.setText(" ");
		}

		@Override
		public void focusGained(FocusEvent e) {
			/*If the foreground (text) colour is black, user text is there, don't reset it*/
			if (playerTextField.getForeground() == Color.BLACK) {
				//do nothing
			} else {
				playerTextField.setForeground(Color.BLACK);
				playerTextField.setText(null);
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
			/*If user hasn't inputted any text, put back the prompt*/
			if(playerTextField.getText().trim().equals("")) {
				playerTextField.setForeground(Color.LIGHT_GRAY);
				playerTextField.setText("Enter player name here");
			}
		}
	}

	private void updateNumOfPlayers() {

		/*for addition of inputPanels*/
		if (Main.numberOfPlayers > currentNumberOfPlayers) {
			for (; currentNumberOfPlayers < Main.numberOfPlayers; currentNumberOfPlayers++) {
				dialogPanel.add(buildInputPanel());
			}
		} else {
			/* for removal of inputPanels */
			if (Main.numberOfPlayers < currentNumberOfPlayers) {
				for (; currentNumberOfPlayers > Main.numberOfPlayers; currentNumberOfPlayers--) {
					dialogPanel.remove(addedInputPanels.size()-1);
					addedInputPanels.remove(addedInputPanels.size()-1);
				}
			}
		}

		/*Updating Dialog*/
		repaint();
		dialogPanel.revalidate();
		dialogPanel.repaint();
	}

	/*Checking if user inputted names are valid*/
	private static boolean checkNames() {
		int i;
		boolean allNamesAreOkay = true;
		ArrayList<String> acceptedPlayerNames = new ArrayList<>();
		/*Checks all textFields for problems */
		for (i = 0; i < playerNameInputs.size(); ++i) {
			/* If inputted name is longer than 16 characters - problem */
			if (playerNameInputs.get(i).getForeground() == Color.BLACK && playerNameInputs.get(i).getText().trim().length() > 16) {
				addedInputPanels.get(i).setProblem("Name is over 16 characters");
				allNamesAreOkay = false;
			/*If input field is empty*/
			} else if (playerNameInputs.get(i).getForeground() == Color.LIGHT_GRAY) {
				addedInputPanels.get(i).setProblem("Player field must not be empty");
				allNamesAreOkay = false;
			} else {
				/*If no problem with user inputted data
				- remove any possible problem
				- possibly wasteful but will only iterate at most 6 times*/
				if(playerNameInputs.get(i).getForeground() == Color.BLACK) {
					addedInputPanels.get(i).removeProblem();
				}
			}
		}

		/*Iterating through the user inputted names - adding them to acceptedPlayerNames one by one to be compared for repeats*/
		acceptedPlayerNames.add(playerNameInputs.get(0).getText().trim().toUpperCase());
		i = 1;
		do {
			/*If player inputted & contained in the acceptedPlayerNames list*/
			if (playerNameInputs.get(i).getForeground() == Color.BLACK
					&& acceptedPlayerNames.contains(playerNameInputs.get(i).getText().trim().toUpperCase())) {
				acceptedPlayerNames.add(playerNameInputs.get(i).getText().trim().toUpperCase());
				addedInputPanels.get(i).setProblem("Repeated names not allowed");
				allNamesAreOkay = false;
			}
			i++;
		} while (i < playerNameInputs.size()-1);
		return allNamesAreOkay;
	}

	private static void getNames() {
		/* iterate through inputs and add them to ultimate playerName list */
		for(JTextField nameInput : playerNameInputs) {
			GameLauncher.playerNames.add(nameInput.getText().trim());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		/* getting the number of players - when changed */
		if (e.getSource() == numberOfPlayersGetter) {
			Main.numberOfPlayers = (int) numberOfPlayersGetter.getSelectedItem();
			updateNumOfPlayers();
		/* if cancel is hit - close game */
		} else if (e.getSource() == cancel) {
			System.exit(0);

		/* if OK is hit - check the names - if they're all good, getNames */
		} else if (e.getSource() == ok) {
			//checkNames() returns true if names given are acceptable
			if (checkNames()) {
				getNames();
				dispose();
			}
		}
	}
}