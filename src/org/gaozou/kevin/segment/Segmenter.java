package org.gaozou.kevin.segment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gaozou.kevin.utility.SimpleException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public class Segmenter implements Segment {
    private static final Logger log = LoggerFactory.getLogger(Segmenter.class);

    private final int bufferSize = SegmentHelper.bufferSize;
    private final Dictionary dic = Dictionary.getInstance().setSegmenter(this);
    private final char[] buffer = new char[bufferSize];
    private int offset = 0, start = 0, count = bufferSize;

    private List<Word> words = new ArrayList<Word>(16*2);
    private Iterator<Word> iterator;

    protected Reader input;

    public Segmenter() {}
    public Segmenter(Reader reader) {
        input = reader;
    }

    public List<Word> getWords() {
        return words;
    }

    public Iterator<Word> getIterator() {
        return iterator;
    }


    public List<Word> segment() {
        if (null == input) return null;
        
        int read;
        try {
            while ((read = input.read(buffer, start, count)) != -1) {
                log.debug("read: " + read);
                segment(buffer, 0, start + read);
            }
        } catch (IOException e) {
            throw new SimpleException("problem segment", e);
        }
        return words;
    }

    public Word next() {
        if (null == input) return null;

        try {
            while (null == iterator || ! iterator.hasNext()) {
                int read = input.read(buffer, start, count);
                log.debug("start: "+ start+", read: " + read);
                if (read == -1) return null;
                List<Word> tmp = new ArrayList<Word>();
                tmp.addAll(words);

                segment(buffer, 0, start + read);
                words.removeAll(tmp);
                iterator = words.iterator();
            }
        } catch (IOException e) {
            throw new SimpleException("problem get next", e);
        }
        return iterator.next();
    }

    @SuppressWarnings("unchecked")
    public List<Word> segment(char[] chars, int begin, int length) {
        int seged = dic.search(chars, begin, length);
        Collections.sort(words);

        if (length >= bufferSize) {
            offset += seged;
            start = bufferSize - seged;
            count = seged;
            System.arraycopy(buffer, seged, buffer, 0, start);
        }
        return words;
    }

    public void collect(Word word) {
        word.setStart(offset + word.getStart());
        word.setEnd(offset + word.getEnd());

        if (words.contains(word)) {
            if (word.getText().length() > 1) {
                words.remove(word);
                words.add(word);
            }
        } else {
            words.add(word);
        }
    }
}
