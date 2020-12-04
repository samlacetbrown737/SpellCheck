import java.util.*;
import java.io.*;
public class SpellChecker {
	public ArrayList<String> suggest = new ArrayList<String>();
	public char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	public SeparateChainingHashST<String, String> wordBank = new SeparateChainingHashST();

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		SpellChecker sc = new SpellChecker();
		sc.hashWords();
		System.out.print("Word to check: ");
		String word = input.next();
		sc.checkWord(word);
	}

	public void checkWord(String word) {
		if(check(word)) {
			System.out.println("No mistakes found");
		} else {
			addCharStart(word);
			addCharEnd(word);
			rmCharStart(word);
			rmCharEnd(word);
			exchChars(word);
			int suggestions = suggest.size();
			if(suggestions != 0) {
				if(suggestions == 1) {
					System.out.println("1 suggestion: ");
				} else {
					System.out.println(suggestions + " suggestions: ");
				}
				for(int i = 0; i < suggestions; i++) {
					System.out.println(suggest.get(i));
				}
			} else {
				System.out.println("No suggestions");
			}
		}
	}

	public boolean check(String word) {
		boolean match = false;
		if(wordBank.contains(word)) {
			suggest.add(word);
			match = true;
		}
		return match;
	}

	public void hashWords() {
		String filePath = "words.txt";
		try {
		    BufferedReader lineReader = new BufferedReader(new FileReader(filePath));
		    String lineText = null;
		 
		    while ((lineText = lineReader.readLine()) != null) {
		        wordBank.put(lineText, lineText);
		    }
		 
		    lineReader.close();
		} catch (IOException ex) {
		    System.err.println(ex);
		}
	}

	public void addCharStart(String word) {
		for(int i = 0; i < 26; i++) {
			StringBuilder sb = new StringBuilder(word);
    		sb.insert(0, alphabet[i]);
    		check(sb.toString());
		}
	}

	public void addCharEnd(String word) {
		int len = word.length();
		for(int i = 0; i < 26; i++) {
			StringBuilder sb = new StringBuilder(word);
    		sb.insert(len, alphabet[i]);
    		check(sb.toString());
		}
	}

	public void rmCharStart(String word) {
		check(word.substring(1));
	}

	public void rmCharEnd(String word) {
		int len = (word.length() - 1);
		check(word.substring(0, len));
	}

	public void exchChars(String word) {
		for (int i = 0; i < word.length(); i++) {
			for (int j = i + 1; j < word.length(); j++) {
				exch(word, i, j);
			}
		}
	}

	public void exch(String word, int a, int b) {
		StringBuilder sb = new StringBuilder(word); 
        sb.setCharAt(a, word.charAt(b)); 
        sb.setCharAt(b, word.charAt(a)); 
        check(sb.toString()); 
	}
}

class SeparateChainingHashST<Key, Value> {
    private static final int INIT_CAPACITY = 4;
    private int n;
    private int m;
    private SequentialSearchST<Key, Value>[] st;

    public SeparateChainingHashST() {
        this(INIT_CAPACITY);
    } 

    public SeparateChainingHashST(int m) {
        this.m = m;
        st = (SequentialSearchST<Key, Value>[]) new SequentialSearchST[m];
        for (int i = 0; i < m; i++)
            st[i] = new SequentialSearchST<Key, Value>();
    } 

    private void resize(int chains) {
        SeparateChainingHashST<Key, Value> temp = new SeparateChainingHashST<Key, Value>(chains);
        for (int i = 0; i < m; i++) {
            for (Key key : st[i].keys()) {
                temp.put(key, st[i].get(key));
            }
        }
        this.m  = temp.m;
        this.n  = temp.n;
        this.st = temp.st;
    }

    private int hash(Key key) {
        return (key.hashCode() & 0x7fffffff) % m;
    } 

    public int size() {
        return n;
    } 

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(Key key) {
        return get(key) != null;
    } 

    public Value get(Key key) {
        int i = hash(key);
        return st[i].get(key);
    } 

    public void put(Key key, Value val) {
        if (n >= 10*m) resize(2*m);

        int i = hash(key);
        if (!st[i].contains(key)) n++;
        st[i].put(key, val);
    } 
}

class SequentialSearchST<Key, Value> {
    private int n;
    private Node first;

    private class Node {
        private Key key;
        private Value val;
        private Node next;

        public Node(Key key, Value val, Node next)  {
            this.key  = key;
            this.val  = val;
            this.next = next;
        }
    }

    public SequentialSearchST() {
    }

    public int size() {
        return n;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(Key key) {
        return get(key) != null;
    }

    public Value get(Key key) {
        for (Node x = first; x != null; x = x.next) {
            if (key.equals(x.key))
                return x.val;
        }
        return null;
    }

    public void put(Key key, Value val) {
        if (val == null) {
            delete(key);
            return;
        }

        for (Node x = first; x != null; x = x.next) {
            if (key.equals(x.key)) {
                x.val = val;
                return;
            }
        }
        first = new Node(key, val, first);
        n++;
    }

    public void delete(Key key) {
        first = delete(first, key);
    }

    private Node delete(Node x, Key key) {
        if (x == null) return null;
        if (key.equals(x.key)) {
            n--;
            return x.next;
        }
        x.next = delete(x.next, key);
        return x;
    }
    public Iterable<Key> keys()  {
        Queue<Key> queue = new LinkedList<Key>();
        for (Node x = first; x != null; x = x.next)
            queue.add(x.key);
        return queue;
    }

}