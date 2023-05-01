import java.io.FileReader;   //  Read Unicode chars from a file.
import java.io.IOException;  //  In case there's IO trouble.

//  WORDS. Iterator. Read words, represented as STRINGs, from a text file. Each
//  word is the longest possible contiguous series of alphabetic ASCII CHARs.

class Words
{
  private int           ch;      //  Last CHAR from READER, as an INT.
  private FileReader    reader;  //  Read CHARs from here.
  private StringBuilder word;    //  Last word read from READER.

//  Constructor. Initialize an instance of WORDS, so it reads words from a file
//  whose pathname is PATH. Throw an exception if we can't open PATH.

  public Words(String path)
  {
    try
    {
      reader = new FileReader(path);
      ch = reader.read();
    }
    catch (IOException ignore)
    {
      throw new IllegalArgumentException("Cannot open '" + path + "'.");
    }
  }

//  HAS NEXT. Try to read a WORD from READER, converting it to lower case as we
//  go. Test if we were successful.

  public boolean hasNext()
  {
    word = new StringBuilder();
    while (ch > 0 && ! isAlphabetic((char) ch))
    {
      read();
    }
    while (ch > 0 && isAlphabetic((char) ch))
    {
      word.append(toLower((char) ch));
      read();
    }
    return word.length() > 0;
  }

//  IS ALPHABETIC. Test if CH is an ASCII letter.

  private boolean isAlphabetic(char ch)
  {
    return 'a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z';
  }

//  NEXT. If HAS NEXT is true, then return a WORD read from READER as a STRING.
//  Otherwise, return an undefined STRING.

  public String next()
  {
    return word.toString();
  }

//  READ. Read the next CHAR from READER. Set CH to the CHAR, represented as an
//  INT. If there are no more CHARs to be read from READER, then set CH to -1.

  private void read()
  {
    try
    {
      ch = reader.read();
    }
    catch (IOException ignore)
    {
      ch = -1;
    }
  }

//  TO LOWER. Return the lower case ASCII letter which corresponds to the ASCII
//  letter CH.

  private char toLower(char ch)
  {
    if ('a' <= ch && ch <= 'z')
    {
      return ch;
    }
    else
    {
      return (char) (ch - 'A' + 'a');
    }
  }

//  MAIN. For testing. Open a text file whose pathname is the 0th argument from
//  the command line. Read words from the file, and print them one per line.

  public static void main(String [] args)
  {
    Words words = new Words(args[0]);
    while (words.hasNext())
    {
      System.out.println("'" + words.next() + "'");
    }
  }
}
class AnagramTree
{
  private class TreeNode
  {
    private byte[] summary;
    private WordNode words;
    private TreeNode left;
    private TreeNode right;
    private TreeNode(byte[] summary, WordNode word)
    {
      this.summary = summary;
      this.words = word;
      this.left = null;
      this.right = null;
    }
  }
  private class WordNode
  {
    private String word;
    private WordNode next;
    private WordNode(String word)
    {
      this.word = word;
      this.next = null;
    }
  }
  private TreeNode head;
  public AnagramTree()
  {
    head = new TreeNode(new byte[26], null);
  }
  public void add(String word)
  {
    if (word == null || word.isEmpty())
    {
      return;
    }
    byte[] summary = stringToSummary(word);
    TreeNode current = head;
    TreeNode parent = null;
    int cmp = 0;
    while (current != null)
    {
      cmp = compareSummaries(summary, current.summary);
      if (cmp == 0)
      {
        break;
      }
      parent = current;
      if (cmp < 0)
      {
        current = current.left;
      }
      else
      {
        current = current.right;
      }
    }
    if (current == null)
    {
      current = new TreeNode(summary, null);
      if (parent == null)
      {
        head = current;
      }
      else if (cmp < 0)
      {
        parent.left = current;
      }
      else
      {
        parent.right = current;
      }
    }
    current.words = addWord(current.words, word);
  }
  private WordNode addWord(WordNode node, String word)
  {
    if (node == null)
    {
      return new WordNode(word);
    }
    if (node.word.equals(word))
    {
      return node;
    }
    node.next = addWord(node.next, word);
    return node;
  }
  public void anagrams()
  {
    if (head == null)
    {
      return;
    }
    traversalAnagram(head);
  }
  private void traversalAnagram(TreeNode node)
  {
    if (node == null)
    {
      return;
    }
    traversalAnagram(node.left);
    printWords(node.words);
    traversalAnagram(node.right);
  }
  private void printWords(WordNode node)
  {
    if (node == null || node.next == null)
    {
      return;
    }
    while (node != null)
    {
      System.out.print(node.word + " ");
      node = node.next;
    }
    System.out.println();
  }
  private int compareSummaries(byte[] left, byte[] right)
  {
    for (int index = 0; index < 26; index += 1)
    {
      if (left[index] < right[index])
      {
        return -1;
      }
      else if (left[index] > right[index])
      {
        return 1;
      }
    }
    return 0;
  }
  private byte[] stringToSummary(String word)
  {
    byte[] summary = new byte[26];
    for (int index = 0; index < word.length(); index += 1)
    {
      char c = word.charAt(index);
      summary[c - 'a'] += 1;
    }
    return summary;
  }
}
class Anagrammer
{
  public static void main(String[] args)
  {
    Words words = new Words("warAndPeace.txt");
    AnagramTree tree = new AnagramTree();
    while (words.hasNext())
    {
      String word = words.next();
      tree.add(word);
    }
    tree.anagrams();
  }
}