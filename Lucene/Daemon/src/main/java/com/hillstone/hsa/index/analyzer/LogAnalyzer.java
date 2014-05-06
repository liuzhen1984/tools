package com.hillstone.hsa.index.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

import java.io.Reader;

/**
 * Created with IntelliJ IDEA.
 * User: zliu
 * Date: 14-4-1
 * Time: 下午2:32
 * To change this template use File | Settings | File Templates.
 */
public class LogAnalyzer extends Analyzer {
    /** Default maximum allowed token length */
    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
    private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;
    public static final CharArraySet STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    private Version matchVersion = Version.LUCENE_47;

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        final Tokenizer source = new LogTokenizer(reader);//new一个tokenizer
        TokenStreamComponents tsc = new TokenStreamComponents(source, new LogFilter(reader,source));
        return tsc;//把tokonizer和过滤器放入语汇流处理器组建中
    }
}
