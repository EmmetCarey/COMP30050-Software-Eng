/*	
 * Team - IPassTheButter
 * Deaglan Connolly Bree - 15511107
 * Michael Kehoe - 15703545
 * Brian McDermott - 15730891
 * TrieAutocomplete class
 * 		- Tree structure allowing for auto complete of words
*/

package Monopoly.dataStrucs ;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class TrieAutocomplete {

	private TrieNode root;
	/*Node inner class*/
	private class TrieNode {
		private char value;
		private boolean isEndOfWordNode;
		private boolean isCapitalisedWord = false;
		private HashMap<Character, TrieNode> childNodes = new HashMap<Character, TrieNode>();

		public char getValue() {
			return value;
		}

		public TrieNode() {}

		public TrieNode(char arg) {
			value = arg;
		}

		public boolean isEndOfWordNode() {
			return isEndOfWordNode;
		}

		public boolean isCapitalisedWord() {
			return isCapitalisedWord;
		}

		public void setCapitalisedWord() {
			isCapitalisedWord = true;
		}

		public void setEndOfWordNode() {
			isEndOfWordNode = true;
		}
	}

	public TrieAutocomplete() {
		root = new TrieNode();
	}

	public void insert(String word) {
		HashMap<Character, TrieNode> childNodes = root.childNodes;
		for (int i=0; i < word.length(); i++) {
			char aLetter = word.charAt(i);
			TrieNode temp;
			if (childNodes.containsKey(Character.toUpperCase(aLetter))) {
				temp = childNodes.get(Character.toUpperCase(aLetter));
			} else {
				temp = new TrieNode(aLetter);
				childNodes.put(Character.toUpperCase(aLetter), temp);
			}
 
			childNodes = temp.childNodes;
 
			/*set the node to EndOfWordNode*/
			if (i==word.length()-1) {
				temp.setEndOfWordNode();
				/*if the first letter is capital*/
				if (word.charAt(0) == Character.toUpperCase(word.charAt(0))) {
					temp.setCapitalisedWord();
				}
			}
		}
	}

	public String getWordAutocomplete(String word) {
		HashMap<Character, TrieNode> childNodes = root.childNodes;
		String wordToReturn = "";
		for (int i = 0; i< word.length(); ++i) {
			char aLetter = word.charAt(i);
			TrieNode temp;
			temp = childNodes.get(Character.toUpperCase(aLetter));
			/*HashMap returns null if key isn't present in Map*/
			if (temp == null) {
				return null;
			}

			wordToReturn += temp.getValue();
			/*if at end of word - and it's end of word node (this part is redundant I guess*/
			if (i == word.length()-1 && temp.isEndOfWordNode()) {
				if (temp.isCapitalisedWord) {
					return (Character.toUpperCase(wordToReturn.charAt(0)) + wordToReturn.substring(1, wordToReturn.length()));
				} else {
					return (Character.toLowerCase(wordToReturn.charAt(0)) + wordToReturn.substring(1, wordToReturn.length()));
				}
			}

			/*if we're at end of word to search for - and there's only one child node - for auto-complete*/
			if (i == word.length()-1) {
				/*if the number of children is only 1 - we can try and fetch the full word*/
				if (temp.childNodes.size()==1) {
					/*While there's only 1 child*/
					while (temp.childNodes.size() <= 1) {
						/*Collection -> ArrayList nonsense*/
						List<TrieNode> theList = new ArrayList<TrieNode>(temp.childNodes.values());
						temp = theList.get(0);
						wordToReturn += temp.getValue();
						if (temp.childNodes.size() > 1) {
							return null;
						} else if (temp.isEndOfWordNode()) {
							if (temp.isCapitalisedWord) {
								return (Character.toUpperCase(wordToReturn.charAt(0)) + wordToReturn.substring(1, wordToReturn.length()));
							}
							return (Character.toLowerCase(wordToReturn.charAt(0)) + wordToReturn.substring(1, wordToReturn.length()));
						}
					}
				} 
			}
			childNodes = temp.childNodes;
		}
		return null;
	}
}
