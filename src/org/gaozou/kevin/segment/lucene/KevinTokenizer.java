package org.gaozou.kevin.segment.lucene;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.gaozou.kevin.segment.Segment;
import org.gaozou.kevin.segment.Segmenter;
import org.gaozou.kevin.segment.Word;

import java.io.IOException;
import java.io.Reader;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public class KevinTokenizer extends Tokenizer {
    private Segment segmenter;
    public KevinTokenizer(Reader input) {
        super(input);
        segmenter = new Segmenter(input);
    }
    public Token next() throws IOException {
        Word word = segmenter.next();
        if (null == word) return null;
        return new Token(word.getText(), word.getStart(), word.getEnd());
    }
}
