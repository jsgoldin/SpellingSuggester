/**
 * This class defines a node object, which populates the Dictionary.
 */
public class Node
{
	private String word;
	private Node[] children;
	private boolean suffixFlag;

	/**
	 * Creates a node
	 * 
	 * @param word the word value that the node will contain
	 */
	public Node(String word)
	{
		// initially set isPrefix to false;
		suffixFlag = false;

		// initially set word to passed in word
		this.word = word;

		// set up child array
		// initially has no children
		children = new Node[26];
	}

	/**
	 * Update the nodes prefix flag value
	 * <p>
	 * true = is a prefix<br>
	 * false = is a suffix<br>
	 * 
	 * @param flag the new value of the flag
	 * 
	 */
	public void setSuffixFlag(boolean flag)
	{
		suffixFlag = flag;
	}

	/**
	 * Update the nodes word value.
	 * 
	 * @param word the nodes new word value
	 */
	public void setWord(String word)
	{
		this.word = word;
	}

	/**
	 * Update the nodes children.
	 * 
	 * @param letter specifies the child to be updated
	 * @param newChild the node that will be set as the new child
	 */
	public void setChild(char letter, Node newChild)
	{
		children[Character.getNumericValue(letter) - 10] = newChild;
	}

	/**
	 * Returns the child Node associated with with the passed in letter.
	 * 
	 * @param letter specifies which child will be returned
	 * @return the child associated with the passed in letter
	 */
	public Node getChild(char letter)
	{
		return children[Character.getNumericValue(letter) - 10];
	}

	/**
	 * Returns the child Node associated with with the passed in index.
	 * 
	 * @param index specifies which child will be returned
	 * @return the child associated with the passed in index
	 */
	public Node getChild(int index)
	{
		return children[index];
	}

	/**
	 * @return <code>true</code> if suffix <code>false</code> otherwise
	 */
	public boolean isSuffix()
	{
		return suffixFlag;
	}

	/**
	 * @return word associated with the node
	 */
	public String getWord()
	{
		return word;
	}

}
