import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Data structure for storing words.
 */
public class Dictionary
{
	private Node root;
	private HashMap<String, Integer> wordFrequency;

	/**
	 * Create a new empty dictionary
	 */
	public Dictionary()
	{
		wordFrequency = new HashMap<String, Integer>();
		// initialize wordFrequency

		BufferedReader br;
		String sCurrentLine;

		try
		{
			br = new BufferedReader(new FileReader("LanguageModel.txt"));

			while ((sCurrentLine = br.readLine()) != null)
			{
				String[] tempS = sCurrentLine.split("-");

				wordFrequency.put(tempS[0], Integer.parseInt(tempS[1]));
			}
		} catch (NumberFormatException | IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Create a new dictionary from words in a text file. The text file should
	 * contain one word per line.
	 * 
	 * @param fileName
	 *            name of text file containing words that will be added to the
	 *            new dictionary
	 */
	public Dictionary(String fileName)
	{
		wordFrequency = new HashMap<String, Integer>();
		BufferedReader br = null;
		String sCurrentLine;

		try
		{
			br = new BufferedReader(new FileReader(fileName));

			// load Dictionary
			while ((sCurrentLine = br.readLine()) != null)
			{
				add(sCurrentLine);
			}

			// initialize word frequency
			br = new BufferedReader(new FileReader("LanguageModel.txt"));
			while ((sCurrentLine = br.readLine()) != null)
			{
				String[] tempS = sCurrentLine.split("-");
				wordFrequency.put(tempS[0], Integer.parseInt(tempS[1]));
			}
		} catch (FileNotFoundException e)
		{
			System.out.println("File: " + fileName + " does not exist!");
		} catch (IOException e)
		{
			System.out.println("Error reading file: " + fileName);
		}
	}

	/**
	 * Add a word to the dictionary.
	 * 
	 * @param word
	 *            the word that will be added to the dictionary
	 */
	public void add(String word)
	{
		root = addHelper(word, root);
	}

	/**
	 * Add helper method that recurses through the dictionary to the proper
	 * location inserts the word in the appropriate way.
	 * 
	 * @param word
	 *            the word that will be inserted
	 * @param currentNode
	 *            the current node of that the method is on as it recurses
	 *            through the tree
	 */
	private Node addHelper(String word, Node currentNode)
	{
		// empty tree
		if (currentNode == null)
		{
			Node temp = new Node(word);
			temp.setSuffixFlag(true);
			currentNode = temp;
			return currentNode;
		} else if (currentNode.getWord().equals(word)
				&& currentNode.isSuffix() == false)
		{
			// if word being inserted exists as a prefix, set the existing
			// prefix node to be a suffix
			currentNode.setSuffixFlag(true);
			return currentNode;
		} else if (currentNode.getWord().equals(word)
				&& currentNode.isSuffix() == true)
		{
			// word being inserted already exists in the tree
			// do nothing
			return currentNode;
		} else
		{
			// Recursive case

			// 2 cases
			// 1. the currentNode is the prefix of the word being inserted
			// 2. the currentWord is not a prefix of the word being inserted
			String commonPrefix = getLongestCommonPrefix(word,
					currentNode.getWord());
			if (word.startsWith(currentNode.getWord()))
			{
				// currentWord is a prefix of word being inserted

				// remove the common prefix from the word being inserted and
				// recursively insert it into the
				// current node

				String suffixWord = word.substring(commonPrefix.length(),
						word.length());

				currentNode.setChild(
						suffixWord.charAt(0),
						addHelper(suffixWord,
								currentNode.getChild(suffixWord.charAt(0))));
				return currentNode;
			} else
			{
				// currentWord is not a prefix of the word being inserted
				// find a common prefix and make that the parent of the two

				Node tempPrefixNode = new Node(commonPrefix);
				tempPrefixNode.setSuffixFlag(false);

				String suffix = currentNode.getWord().substring(
						commonPrefix.length(), currentNode.getWord().length());
				String suffixNewWord = word.substring(commonPrefix.length(),
						word.length());

				currentNode.setWord(suffix);

				// set the new commonPrefixNode to have a child pointing at the
				// old currentNode's suffix
				tempPrefixNode.setChild(suffix.charAt(0), currentNode);

				// now we need to insert the new suffix
				if (suffixNewWord.equals(""))
				{
					// if the new suffix an empty string then the new parent is
					// the suffix
					// and their is nothing else to insert
					tempPrefixNode.setSuffixFlag(true);
					return tempPrefixNode;
				} else
				{
					tempPrefixNode.setChild(
							suffixNewWord.charAt(0),
							addHelper(suffixNewWord, tempPrefixNode
									.getChild(suffixNewWord.charAt(0))));

					return tempPrefixNode;
				}
			}
		}
	}

	/**
	 * Check to see if a word exists in the dictionary. The check is case
	 * insensitive.
	 * 
	 * @param word
	 *            the word to check for
	 * @return <code>true</code> if a word exists; <code>false</code> otherwise
	 */
	public boolean check(String word)
	{
		return checkHelper(word.toLowerCase(), root);
	}

	/**
	 * Check helper method that recursively looks for a word in the dictionary.
	 * 
	 * @param word
	 *            the word to check for
	 * @param currentNode
	 *            the currentNode the method is on as it recursively goes
	 *            through the dictionary
	 * @return <code>true</code> if a word exists; <code>false</code> otherwise
	 */
	private boolean checkHelper(String word, Node currentNode)
	{
		if (currentNode == null)
		{
			return false;
		} else if (!word.startsWith(currentNode.getWord().toLowerCase()))
		{
			// if word does not start with the currentWord
			// it is not the currentNode or any of its children
			return false;
		} else if (word.equals(currentNode.getWord().toLowerCase())
				&& currentNode.isSuffix())
		{
			// if the word == currentWord and it is a suffix
			// then we found the word
			return true;
		} else if (word.equals(currentNode.getWord().toLowerCase())
				&& !currentNode.isSuffix())
		{
			// the string we are looking for is a prefix and not a word
			return false;
		} else
		{
			// currentWord is a valid prefix of the word we are searching for
			// call check helper with the currentNode and common prefix
			// compliment of the word

			String commonPrefix = this.getLongestCommonPrefix(word, currentNode
					.getWord().toLowerCase());
			String subWord = word.substring(commonPrefix.length(),
					word.length());

			return checkHelper(subWord, currentNode.getChild(subWord.charAt(0)));
		}
	}

	/**
	 * Checks if a word exists in the dictionary that starts with a certain in
	 * prefix.
	 * 
	 * @param prefix
	 *            prefix to check for
	 * @return <code>true</code> if a word exists that starts with the prefix;
	 *         <code>false</code> otherwise
	 */
	public boolean checkPrefix(String prefix)
	{
		return checkPrefixHelper(prefix.toLowerCase(), root);
	}

	public boolean checkPrefixHelper(String prefix, Node currentNode)
	{
		if (currentNode == null)
		{
			return false;
		} else if (currentNode.getWord().toLowerCase().startsWith(prefix))
		{
			return true;
		} else
		{
			// recurse
			// check children for prefix compliment
			String prefixComp = prefix.substring(currentNode.getWord()
					.toLowerCase().length(), prefix.length());
			return checkPrefixHelper(prefixComp,
					currentNode.getChild(prefixComp.charAt(0)));
		}
	}

	/**
	 * Print all the words in the dictionary in alphabetical order.
	 */
	public void print()
	{
		printHelper(root, "");
	}

	/**
	 * Helper print method that recursively traverses the tree in alphabetical
	 * order.
	 * 
	 * @param currentNode
	 *            the node the method is on as it recursively traverses the tree
	 * @param prefixSoFar
	 *            the prefix up until this node in the tree
	 */
	private void printHelper(Node currentNode, String prefixSoFar)
	{
		if (currentNode != null)
		{
			if (currentNode.isSuffix())
			{
				System.out.println(prefixSoFar + currentNode.getWord());
			}

			prefixSoFar += currentNode.getWord();

			for (int x = 0; x < 26; x++)
			{
				printHelper(currentNode.getChild(x), prefixSoFar);
			}
		}
	}

	/**
	 * Returns an array of the entries in the dictionary that are as close as
	 * possible to the parameter word. If the word passed in is in the
	 * dictionary, then an array of length 1 that contains only that word is
	 * returned. If numSuggestions is larger than the amount of words in the
	 * dictionary the returned array will be at most be the length of the amount
	 * of words in the dictionary.
	 * 
	 * 
	 * There are 2 suggest methods:
	 * 
	 * 1. The first simply chops of the last letter of the passed in word and
	 * looks for words that start with the new smaller word. That process is
	 * repeated until enough words are found.
	 * 
	 * 2. The other one works by generating all possible words that are one edit
	 * away from the original word and adds the ones that are valid words to
	 * suggestions. If more suggestions are needed all every one edit away word
	 * is passed back into my method that generated "one edit away words"
	 * resulting in all possible 2 edit away words. This process is repeated and
	 * generates all x edit words away every iteration until enough
	 * suggestions are found. However if the number of words that are going to
	 * be checked for in the dictionary is > 8000 the algorithm gives up,
	 * because it will take to long to check for all of those words.
	 * 
	 * My suggest method first tries my second implementation. But if that fails
	 * to find enough suggestions the 1st simpler less accurate one is used
	 * since it's guaranteed to give enough suggestions, unless more words are
	 * asked for than exist in the dictionary, in which case an array containing
	 * all the words in the dictionary will be returned.
	 * 
	 * 
	 * Once an array of suggestions has been produced the words are sorted in
	 * order of the frequency they appear based on a language model generated
	 * from the American National Corpus which contains roughly 15 million
	 * words.
	 * 
	 * Sorting with the language model will really only work on words that exist
	 * in the given dictionary text file because it was generated to have only
	 * those words. Future words that are added that are not in original
	 * dictionary will be assigned a default rank of 1 since infeasible to
	 * regenerate the language model every time a new word is added.
	 * 
	 * The final sorted array is returned.
	 * 
	 * 
	 * @param word
	 *            the word that will suggestions will be made for
	 * @param numSuggestions
	 *            the number of suggestions that will be returned
	 * @return a String array containing the similar words
	 */
	public String[] suggest(String word, int numSuggestions)
	{
		if (check(word))
		{
			// if word is in dictionary return only that word
			return new String[] { word };
		} else
		{

			// first try second implementation
			String[] resultSuggestions = getLikelySuggestions(word,
					numSuggestions);

			if (resultSuggestions.length >= numSuggestions)
			{
				sortSuggestions(resultSuggestions);

				return resultSuggestions;
			} else
			{
				// first failed, try first implementation
				suggestions = new LinkedHashSet<String>();

				// rescans the entire tree every time
				// it needs more suggestions and widens the search by chopping
				// of the last letter of the word being searched for.
				// The suggestions are stored in a set so duplicates can't exist

				// A call to suggest with an empty string would
				// fill suggestions with every valid word in the tree

				// The returned array can at most only be as big
				// as the number of valid words in the tree,
				// regardless of what numSuggestions is
				while (suggestions.size() < numSuggestions)
				{
					findSuggestions(root, "", word, numSuggestions);
					if (word.equals(""))
					{
						break;
					}
					word = word.substring(0, word.length() - 1);
				}

				// convert the set to String array
				String[] temp = new String[suggestions.size()];

				int index = 0;
				for (String x : suggestions)
				{
					temp[index] = x;
					index++;
				}

				sortSuggestions(temp);
				return temp;
			}
		}
	}

	/**
	 * Private inner class used for sorting suggestions.
	 */
	private class Suggestion implements Comparable<Suggestion>
	{
		String word;
		int rank;

		public Suggestion(String word, int rank)
		{
			this.word = word;
			this.rank = rank;
		}

		@Override
		public int compareTo(Suggestion otherS)
		{
			return otherS.rank - this.rank;
		}

	}

	/**
	 * Sorts suggestions in order of importance established by the language
	 * model.
	 * 
	 * @param sWords
	 *            array of unsorted suggestions
	 */
	private void sortSuggestions(String[] sWords)
	{
		List<Suggestion> suggestz = new ArrayList<Suggestion>();
		for (int i = 0; i < sWords.length; i++)
		{
			if (wordFrequency.get(sWords[i]) == null)
			{
				suggestz.add(new Suggestion(sWords[i], 1));
			} else
			{
				suggestz.add(new Suggestion(sWords[i], wordFrequency
						.get(sWords[i])));
			}
		}

		Collections.sort(suggestz);

		int index = 0;
		for (Suggestion x : suggestz)
		{
			sWords[index] = x.word;
			index++;
		}

	}

	// global variables used by suggestion methods
	private LinkedHashSet<String> suggestions;

	/**
	 * Suggestion helper method that recursively traverses the tree looking for
	 * valid words to add to the suggestions set. Words are added to the
	 * suggestion set when they begin the searchPrefix.
	 * 
	 * @param currentNode
	 *            the node the method is currently on as it traverses through
	 *            the tree
	 * @param prefixSoFar
	 *            the prefix up until this node in the tree
	 * @param searchPrefix
	 *            prefix that words will be checked to begin with
	 * @param numSuggestions
	 *            numSuggestions the amount of suggestions to look for
	 */
	private void findSuggestions(Node currentNode, String prefixSoFar,
			String searchPrefix, int numSuggestions)
	{
		if (currentNode == null)
		{
			// empty tree
			return;
		} else
		{

			// this conditional determines if the word should be added to the
			// suggestion set
			if ((prefixSoFar + currentNode.getWord()).startsWith(searchPrefix)
					&& currentNode.isSuffix()
					&& suggestions.size() < numSuggestions)
			{
				// the currentNode's word is valid and begins with the
				// searchPrefix
				// it is now added to the suggestions set
				suggestions.add(prefixSoFar + currentNode.getWord());
			}

			// this conditional determines if the currentNode's children
			// should be traversed
			if (searchPrefix.startsWith(prefixSoFar + currentNode.getWord())
					|| (prefixSoFar + currentNode.getWord())
							.startsWith(searchPrefix))
			{
				// if the currentWord starts with the searchPrefix or the
				// searchPrefix starts with the currentWord
				// keep trying to recurse

				for (int x = 0; x < 26; x++)
				{
					findSuggestions(currentNode.getChild(x), prefixSoFar
							+ currentNode.getWord(), searchPrefix,
							numSuggestions);
				}
			}
		}
	}

	/**
	 * Print out a representation of the structure of the tree.
	 */
	public void printTree()
	{
		childDepth = -1;
		printTreeHelper(root);
	}

	// global variable used by printTreeHelper
	int childDepth;

	/**
	 * printTree helper method.
	 */
	private void printTreeHelper(Node currentNode)
	{
		if (currentNode != null)
		{
			// just so dummy root is not printed
			if (!currentNode.getWord().equals(""))
			{
				childDepth++;
				String temp = currentNode.getWord();

				if (currentNode.isSuffix())
				{
					temp += "<T>";
				}

				// pad to left with childDepth * tabs
				String padding = "";
				for (int x = 0; x < childDepth; x++)
				{
					padding += "  ";
				}

				System.out.println(padding + temp);
			}

			// scan through every letter
			for (int x = 0; x < 26; x++)
			{
				printTreeHelper(currentNode.getChild(x));
			}
			childDepth--;
		}
	}

	/**
	 * @param x
	 *            the first string
	 * @param y
	 *            the seconds string
	 * @return the longest common prefix of the two strings
	 */
	private String getLongestCommonPrefix(String x, String y)
	{
		int index = 0;
		String prefixTemp = "";

		while (index < x.length() && index < y.length()
				&& x.charAt(index) == y.charAt(index))
		{
			prefixTemp += x.charAt(index);
			index++;
		}

		return prefixTemp;
	}

	/**
	 * Returns all of the possible words 1 edit away from the original word. An
	 * edit can be: a single transpose(swap neighbor letter), a single deletion,
	 * a single insertion, or a single alteration (changing an existing letter).
	 * 
	 * @param word
	 *            will be used to generate the one edits away
	 * @param originalWord
	 *            make sure duplicates of the orgininal word are not included
	 * @return array of possible one edit away words
	 */
	private ArrayList<String> getPossibleOneEdits(String word,
			String originalWord)
	{
		ArrayList<String> possibleEdits = new ArrayList<String>();

		String temp;

		// get possible 1 deletions
		for (int i = 0; i < word.length(); i++)
		{
			temp = (word.substring(0, i) + word.substring(i + 1, word.length()));

			if (!temp.equals(originalWord))
			{
				possibleEdits.add(temp);
			}
		}

		// get possible transposes
		for (int i = 0; i < word.length() - 1; i++)
		{
			char[] tempChars = word.toCharArray();

			char tempChar = tempChars[i];
			tempChars[i] = tempChars[i + 1];
			tempChars[i + 1] = tempChar;

			temp = new String(tempChars);
			if (!temp.equals(originalWord))
			{
				possibleEdits.add(new String(tempChars));
			}

		}

		// get possible replaces
		char[] tempChars;
		for (int i = 0; i < word.length(); i++)
		{
			tempChars = word.toCharArray();
			for (int letterI = 0; letterI < 26; letterI++)
			{
				tempChars[i] = Character.toChars(letterI + 97)[0];
				String tempS = new String(tempChars);
				if (!tempS.equals(originalWord))
				{
					possibleEdits.add(tempS);
				}
			}
		}

		// get possible insertions
		for (int i = 0; i < word.length() + 1; i++)
		{
			for (int letterI = 0; letterI < 26; letterI++)
			{
				temp = (word.substring(0, i)
						+ Character.toChars(letterI + 97)[0] + word.substring(
						i, word.length()));
				if (!temp.equals(originalWord))
				{
					possibleEdits.add(temp);
				}
			}
		}

		return possibleEdits;
	}

	/**
	 * Returns a list of likely suggestions.
	 * 
	 * Works by placing a call to getPossibleOneEdits, the result is pruned for
	 * valid words in the dictionary, the valid words are added to suggestions.
	 * 
	 * If more suggestions are needed all of the words in the result array
	 * generated from getPossibleOneEdits are passed into getPossibleOnedEdits
	 * and the process is repeated until enough suggestions are found. By
	 * calling getPossibleOneEdits repeatedly in this manner I can generated the
	 * results of x possible edits away from the original word.
	 * 
	 * If the number of possible edits is greater than 8000 the algorithm gives
	 * up because it will take to long to try all of them.
	 * 
	 * @param word
	 *            the word that similar words will be looked for
	 * @param numSuggestions
	 *            the max number of suggestions to be returned
	 * @return a String array of the suggestions found
	 */
	public String[] getLikelySuggestions(String word, int numSuggestions)
	{
		// set used too exclude duplicates
		LinkedHashSet<String> finalSuggestions = new LinkedHashSet<String>();

		ArrayList<String> editsToPrune = new ArrayList<String>();
		ArrayList<String> tempEdits;

		editsToPrune.addAll(getPossibleOneEdits(word, word));
		for (String edit : editsToPrune)
		{
			if (check(edit))
			{
				finalSuggestions.add(edit);
			}

			if (finalSuggestions.size() >= numSuggestions)
			{
				return finalSuggestions.toArray(new String[0]);
			}
		}

		while (finalSuggestions.size() < numSuggestions)
		{

			if (editsToPrune.size() > 8000)
			{
				// give up, this algorithm will take to long to find suggestions
				// return what was found so far
				break;
			}

			// do the next iteration
			// every element in editsToPrune is pass into getPossiblOneEdits,
			// resulting in possible 1 + x edits words
			tempEdits = new ArrayList<String>();
			for (String temp : editsToPrune)
			{
				tempEdits.addAll(getPossibleOneEdits(temp, word));
			}

			// reset editsToPrune and set it to be equal to tempEdits
			editsToPrune = new ArrayList<String>();
			editsToPrune.addAll(tempEdits);

			// prune and add to final suggestions
			for (String edit : editsToPrune)
			{
				if (check(edit))
				{
					finalSuggestions.add(edit);
				}

				if (finalSuggestions.size() >= numSuggestions)
				{
					return finalSuggestions.toArray(new String[0]);
				}
			}
		}

		return finalSuggestions.toArray(new String[0]);
	}

}
