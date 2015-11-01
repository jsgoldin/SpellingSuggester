import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;


public class Dictionary
{
	private HashMap<String, Integer> dictionaryData;
	
	/**
	 * Create a new dictionary and loads the the languageModel file
	 */
	public Dictionary()
	{
		dictionaryData = new HashMap<String, Integer>();
		// load dictionary

		BufferedReader br;
		String sCurrentLine;
		
		try
		{
			br = new BufferedReader(new FileReader("LanguageModel.txt"));

			while ((sCurrentLine = br.readLine()) != null)
			{
				String[] tempS = sCurrentLine.split("-");

				dictionaryData.put(tempS[0].toLowerCase(), Integer.parseInt(tempS[1]));
			}
		} catch (NumberFormatException | IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return dictionaryData.containsKey(word.toLowerCase());
	}

	

	/**
	 * Returns an array of the entries in the dictionary that are as close as
	 * possible to the parameter word. 
	 *
	 * If the passed in word exists in the dictionary, then an array of length 1 
	 * containing that word is returned.
	 * 
	 * The suggest works by generating all possible words that are one edit
	 * away from the original word and adds the ones that are valid words to
	 * suggestions. If more suggestions are needed all every one edit away word
	 * is passed back into my method that generated "one edit away words"
	 * resulting in all possible 2 edit away words. This process is repeated and
	 * generates all x edit words away every iteration until enough
	 * suggestions are found. However if the number of words that are going to
	 * be checked for in the dictionary is > 8000 the algorithm gives up,
	 * because it will take to long to check for all of those words.
	 * 
	 * Once an array of suggestions has been produced the words are sorted in
	 * order of the frequency they appear based on a language model generated
	 * from the American National Corpus which contains roughly 15 million
	 * words.
	 * 
	 * The sorted array of suggestions is returned.
	 * 
	 * 
	 * @param word
	 *            the word that will suggestions will be made for
	 * @param maxNumSuggestions
	 *            the maximum number of suggestions that will be returned
	 * @return a String array containing the similar words
	 */
	public String[] suggest(String word, int maxNumSuggestions)
	{
		if (check(word))
		{
			// if word is in dictionary return only that word
			return new String[] { word };
		} else
		{
			String[] resultSuggestions = getLikelySuggestions(word,
					maxNumSuggestions);
			
			sortSuggestions(resultSuggestions);
			return resultSuggestions;
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
			if (dictionaryData.get(sWords[i]) == null)
			{
				suggestz.add(new Suggestion(sWords[i], 1));
			} else
			{
				suggestz.add(new Suggestion(sWords[i], dictionaryData
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


	/**
	 * Returns all of the possible words 1 edit away from the original word. An
	 * edit can be: a single transpose(swap neighbor letter), a single deletion,
	 * a single insertion, or a single alteration (changing an existing letter).
	 * 
	 * @param word
	 *            will be used to generate the one edits away
	 * @param originalWord
	 *            make sure duplicates of the original word are not included
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
	 * calling getPossibleOneEdits repeatedly in this manner I generated the
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
