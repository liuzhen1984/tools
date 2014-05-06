package com.hillstone.hsa.index.analyzer;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: zliu
 * Date: 14-4-1
 * Time: 下午9:20
 * To change this template use File | Settings | File Templates.
 */
public class LogFilter extends TokenFilter {
    // Only English now, Chinese to be added later.停用词，可以添加在这里
    public static final String[] STOP_WORDS = {

    };


    private CharArraySet stopTable;

    private CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private TokenStream in ;
    private Reader reader;

    public LogFilter(Reader reader , TokenStream in){
        this(in);
        this.reader = reader;
    }

    public LogFilter(TokenStream in) {
        super(in);
        this.in = in;
        stopTable = new CharArraySet(Version.LUCENE_47, Arrays.asList(STOP_WORDS), false);
    }

    @Override
    public boolean incrementToken() throws IOException {

        while (input.incrementToken()) {
//            char text[] = termAtt.buffer();//以空格为截断符截取出来的的字符数组
            int termLength = termAtt.length();
//过滤器的主要功能，字符是先按照空格截取后的字符数组，先判断是不是在停用词里面，然后判断是不是英文字母，在判断是不是其他字符
            // why not key off token type here assuming ChineseTokenizer comes first?
            if(termLength>1){
                return true;
            }
        }
        return false;
    }

    private boolean incrementToken2() throws IOException {


        this.in = new WhitespaceTokenizer(Version.LUCENE_47,this.reader);
        this.in.reset();
        while (this.in.incrementToken()) {
            char text[] = termAtt.buffer();//以空格为截断符截取出来的的字符数组
            int termLength = termAtt.length();
//过滤器的主要功能，字符是先按照空格截取后的字符数组，先判断是不是在停用词里面，然后判断是不是英文字母，在判断是不是其他字符
            // why not key off token type here assuming ChineseTokenizer comes first?
            if (!stopTable.contains(text, 0, termLength)) {//是不是在停用词里面
                switch (Character.getType(text[0])) {

                    case Character.LOWERCASE_LETTER://是不是引文字母
                    case Character.UPPERCASE_LETTER:

                        // English word/token should larger than 1 character.
                        if (termLength>1) {//要是英文字母，且长度大于1才回返回给语汇处理器
                            return true;
                        }
                        break;
                    case Character.OTHER_LETTER://要是其他字符，直接返回

                        // One Chinese character as one Chinese word.
                        // Chinese word extraction to be added later here.

                        return true;
                }

            }

        }
        return false;
    }

}
