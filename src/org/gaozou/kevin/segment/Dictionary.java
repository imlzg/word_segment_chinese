package org.gaozou.kevin.segment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gaozou.kevin.utility.ResourceUtil;
import org.gaozou.kevin.utility.SimpleException;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public class Dictionary {
    private static final Logger log = LoggerFactory.getLogger(Dictionary.class);
    public static final String MOSTWORDS = "mostWords";
    public static final String MAXLENGTH = "maxLength";
    public static final String MINLENGTH = "minLength";
    public static final String LUCENE = "lucene";
    public static final String APPLICATION = "application";
    public static final String ODDMENTS = "yes";

    private final String strategy = SegmentHelper.strategy;
    private final String oddments = SegmentHelper.oddments;

    private StringBuffer inDct = new StringBuffer();
    private StringBuffer build = new StringBuffer();
    private Word noiseWord;
    private Segment segmenter;

    private static int size;
    private static Node stock;
    private static Dictionary instance;

    private Dictionary() {
        init();
    }

    public static Dictionary getInstance() {
        if (null == instance) instance = new Dictionary();
        return instance;
    }

    public Dictionary setSegmenter(Segment segmenter) {
        this.segmenter = segmenter;
        return this;
    }

    private static void init() {
        log.info("Initializing dictionary...");
        stock = new Node();
        BufferedReader reader = null;
        String line;

        List<File> dcts = ResourceUtil.getFiles(new File(SegmentHelper.dctPath));
        for (File dct : dcts) {
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(dct), "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.length() > 0 && line.charAt(0) != '#') {
                        addWord(line);
                    }
                }
            } catch (IOException e) {
                log.warn("problem reading word list.");
                throw new SimpleException("problem reading word list.", e);
            } finally {
                if (null != reader) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        log.info("Could not close word list reader.");
                    }
                }
            }
        }
    }

    public static void addWord(String word) {
        addWord(word.toCharArray());
    }
    public static void addWord(char[] word) {
        size++;
        Node tmp = stock;
        for (char c : word) {
            c = SegmentHelper.simpleChar(c);
            Node node = new Node(c);
            if (!tmp.contains(node)) {
                tmp.addChild(node);
                tmp = node;
            } else {
                tmp = tmp.getChild((int) c);
            }
        }
        tmp.setAtEnd(true);
    }

    public static boolean contains(char[] word) {
        Node tmp = stock;
        for (char c : word) {
            Node node = new Node(c);
            if (! tmp.contains(node)) {
                return false;
            } else {
                tmp = tmp.getChild((int) c);
            }
        }
        return tmp.isAtEnd();
    }

    public static int getSize() {
        return size;
    }

    public void print(Writer out) throws IOException {
        stock.print(out);
    }

    public int search(char[] chars, int begin, int length) {
        clearBuffer();
        int end = Math.min(begin + length, chars.length);
        int seged = end - 1;


        char c, d;
        for (int i = begin; i < end; i++) {
            c = SegmentHelper.simpleChar(chars[i]);
//            System.out.println(c);

            
            /////
            boolean hit = false;
            Node tmp = stock.getChild((int) c);
            if (null != tmp) {
                inDct.append(c);
            }

            Word maxLenWord = null;
            for (int j = i + 1; tmp != null && j < end; j++) {
                d = SegmentHelper.simpleChar(chars[j]);
                tmp = tmp.getChild((int) d);
                if (null == tmp) {
                    if ((MAXLENGTH.equalsIgnoreCase(strategy) || APPLICATION.equalsIgnoreCase(strategy)) && null != maxLenWord) {
                        i = maxLenWord.getEnd() - 1;
                        collect(maxLenWord);
                        maxLenWord = null;
                    }
                    break;
                }

                inDct.append(d);
//                System.out.println(inDct);
                if (tmp.isAtEnd()) {
                    hit = true;
                    if (MOSTWORDS.equalsIgnoreCase(strategy) || LUCENE.equalsIgnoreCase(strategy)) {
                        collect(new Word(inDct.toString(), i, j + 1));
                    }
                    if (MAXLENGTH.equalsIgnoreCase(strategy) || APPLICATION.equalsIgnoreCase(strategy)) {
                        maxLenWord = new Word(inDct.toString(), i, j + 1);
                        if (j == end - 1) {
                            collect(maxLenWord);
                            i = j;
                            maxLenWord = null;
                        }
                    }
                    if (MINLENGTH.equalsIgnoreCase(strategy)) {
                        collect(new Word(inDct.toString(), i, j + 1));
                        i = j;
                        break;
                    }
                }

                // If the last one is in a word, it maybe can make up a word with the first one in next buffer.
                // If the last one is not in a word, it must not make up a word with other.
                if (j == end - 1) seged = Math.min(seged, i);
            }
            if ((MAXLENGTH.equalsIgnoreCase(strategy) || APPLICATION.equalsIgnoreCase(strategy)) && null != maxLenWord) {
                i = maxLenWord.getEnd() - 1;
                collect(maxLenWord);
                clearBuffer();
                continue;
            }

            if (hit) {
                clearBuffer();
                continue;
            }

            
            ////
            if (! SegmentHelper.isLetter(c) && ! SegmentHelper.isNumber(c)) {
                if (ODDMENTS.equalsIgnoreCase(oddments) && SegmentHelper.isOddments(c)) {
                    collect(new Word(Character.toString(c), i, i + 1));
                }
                clearBuffer();
                continue;
            }

//            System.out.println(".."+ c);
            build.append(c);
            for (int j = i + 1; build.length() > 0 && j < end; j++) {
                d = SegmentHelper.simpleChar(chars[j]);

                if (! SegmentHelper.isForBuild(d)) {
                    if (build.length() > 1) {
                        collect(new Word(build.toString(), j - build.length(), j));
                    } else {
                        if (ODDMENTS.equalsIgnoreCase(oddments) && SegmentHelper.isOddments(c)) {
                            collect(new Word(build.toString(), i, i + 1));
                        }
                    }
                    i = j - 1;
                    break;
                }
                build.append(d);
                if (j == end - 1) {
                    if (build.length() > 1) {
                        collect(new Word(build.toString(), j - build.length() + 1, j));
                    } else {
                        if (ODDMENTS.equalsIgnoreCase(oddments) && SegmentHelper.isOddments(c)) {
                            collect(new Word(build.toString(), i, i + 1));
                        }
                    }
                    i = j;
                }
                // If the last one is in a word, it maybe can make up a word with the first one in next buffer.
                // If the last one is not in a word, it must not make up a word with other.
                if (j == end - 1) seged = Math.min(seged, i);
            }
            clearBuffer();
        }

        return seged;
    }

    private void collect(Word word) {
        if (LUCENE.equalsIgnoreCase(strategy)) {
            if (SegmentHelper.stopWords.contains(word.getText()) || SegmentHelper.noiseWords.contains(word.getText())) {
                noiseWord = word;
                return;
            }
        }
        if (noiseWord != null && word.getEnd() <= noiseWord.getEnd()) {
            return;
        }
        segmenter.collect(word);
    }

    private void clearBuffer() {
        inDct.setLength(0);
        build.setLength(0);
    }
}

class Node {
    private char c;
    private Node parent;
    private boolean atEnd = false;
    private Map<Integer, Node> children = new HashMap<Integer, Node>();


    public Node() {}
    public Node(char c) {
        this.c = c;
    }

    public Node(char c, Node parent, boolean atEnd) {
        this.c = c;
        this.parent = parent;
        this.atEnd = atEnd;
    }

    public char getC() {
        return c;
    }
    public void setC(char c) {
        this.c = c;
    }

    public Node getParent() {
        return parent;
    }
    public void setParent(Node parent) {
        this.parent = parent;
    }

    public boolean isAtEnd() {
        return atEnd;
    }
    public void setAtEnd(boolean atEnd) {
        this.atEnd = atEnd;
    }

	public boolean hasChildren() {
		return this.children.size() != 0;
	}
    public Map<Integer, Node> getChildren() {
        return children;
    }
    public void setChildren(Map<Integer, Node> children) {
        this.children = children;
    }
    public void removeChildren() {
        children.clear();
    }

    public void addChild(Node node) {
        node.setParent(this);
        children.put(node.hashCode(), node);
    }
    public Node getChild(Integer key) {
        return children.get(key);
    }
    public void removeChild(Node node) {
        children.remove(node.hashCode());
    }

    public boolean contains(Node node) {
        return children.containsKey(node.hashCode());
    }

    public void print(Writer writer) throws IOException {
        writer.write(System.getProperty("line.separator"));
        writer.flush();
    }


    @Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Node) {
			Node another = (Node) obj;
			if (another.c == this.c) return true;
		}
		return false;
	}
    @Override
    public int hashCode() {
        return c;
    }
    @Override
    public String toString() {
        return Character.toString(c);
    }
}