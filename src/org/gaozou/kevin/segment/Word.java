package org.gaozou.kevin.segment;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public class Word implements Comparable {
    public static final int noise = -1;
    public static final int chinese = 1;
    public static final int english = 2 ;
    public static final int number = 3;
    public static final int date = 4;
    public static final int isolate = 5;

    private String text;
    private int start, end;
    private int type;

    public Word(String text) {
        this(text, 0, 0);
    }

    public Word(String text, int start, int end){
        setText(text);
        setStart(start);
        setEnd(end);
    }

    public int length() {
        return text.length();
    }

    public char charAt(int index) {
        return text.charAt(index);
    }

    @Override
    public int compareTo(Object o) {
        Word other = (Word) o;

        if (end > other.getEnd()) {
            return 1;
        } else if (end == other.getEnd()) {
            return other.getStart() - start;
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (null == o) return false;
        if (this == o) return true;
        if (! (o instanceof Word)) return false;
        Word other = (Word) o;

        if ("lucene".equalsIgnoreCase(SegmentHelper.strategy)) {
            return end == other.getEnd();
        } else {
            return start == other.getStart() && end == other.getEnd();
        }
    }

    @Override
    public int hashCode() {
        return (null == text ? 0 : text.hashCode()) + (start + end) * 31;
    }

    @Override
    public String toString() {
        return (null == text ? 0 : text) + "(" + start + ", " + end + ")";
    }


    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public int getStart() {
        return start;
    }
    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }
    public void setEnd(int end) {
        this.end = end;
    }
    
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
}
