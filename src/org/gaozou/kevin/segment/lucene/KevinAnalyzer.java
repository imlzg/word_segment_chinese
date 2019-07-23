package org.gaozou.kevin.segment.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import java.io.Reader;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public class KevinAnalyzer extends Analyzer {
    public TokenStream tokenStream(String s, Reader reader) {
        return new KevinTokenizer(reader);
    }
}
