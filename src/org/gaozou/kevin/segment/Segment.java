package org.gaozou.kevin.segment;

import java.io.IOException;
import java.util.List;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public interface Segment {

    List<Word> segment() throws IOException;

    Word next() throws IOException;

    List<Word> segment(char[] chars, int begin, int length);

    void collect(Word word);
}
