package com.hillstone.hsa.index.analyzer;

import org.apache.lucene.analysis.Tokenizer;

import java.io.IOException;
import java.io.Reader;

/**
 * Created with IntelliJ IDEA.
 * User: zliu
 * Date: 14-4-1
 * Time: 下午6:30
 * To change this template use File | Settings | File Templates.
 */
public class LogSimpleTokenizer extends Tokenizer {

    public LogSimpleTokenizer(Reader input) {
        super(input);
//        this.termAtt = ((TermAttribute)
//                addAttribute(TermAttribute.class));
//        this.done = false;
    }

    @Override
    public boolean incrementToken() throws IOException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

//    private static TernarySearchTrie dic = new
//            TernarySearchTrie("SDIC.txt");
//    //词典
//    private TermAttribute termAtt;// 词属性
//    private static final int IO_BUFFER_SIZE = 4096;
//    private char[] ioBuffer = new char[IO_BUFFER_SIZE];
//
//    private boolean done;
//    private int i = 0;// i是用来控制匹配的起始位置的变量
//    private int upto = 0;
//
//
//    public void resizeIOBuffer(int newSize) {
//        if (ioBuffer.length < newSize) {
//            // Not big enough; create a new array with slight
//            // over allocation and preserve content
//            final char[] newnewCharBuffer = new char[newSize];
//            System.arraycopy(ioBuffer, 0,
//                    newCharBuffer, 0, ioBuffer.
//                    length);
//            ioBuffer = newCharBuffer;
//        }
//    }
//
//    @Override
//    public boolean incrementToken() throws IOException {
//        if (!done) {
//            clearAttributes();
//            done = true;
//            upto = 0;
//            i = 0;
//            while (true) {
//                final int length = input.
//                        read(ioBuffer, upto, ioBuffer.
//                                length
//                                - upto);
//                if (length == -1)
//                    break;
//                upto += length;
//                if (upto == ioBuffer.length)
//                    resizeIOBuffer(upto * 2);
//            }
//        }
//
//        if (i < upto) {
//            char[] word = dic.matchLong(ioBuffer, i, upto);
//            // 正向最大长度匹配
//            if (word != null)// 已经匹配上
//            {
//                termAtt.setTermBuffer(word, 0, word.length);
//                i += word.length;
//            } else {
//                termAtt.setTermBuffer(ioBuffer, i, 1);
//                ++i;// 下次匹配点在这个字符之后
//            }
//            return true;
//        }
//        return false;
//    }
}
