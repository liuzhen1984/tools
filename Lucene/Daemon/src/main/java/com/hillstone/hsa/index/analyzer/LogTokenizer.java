package com.hillstone.hsa.index.analyzer;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.IOException;
import java.io.Reader;

/**
 * Created with IntelliJ IDEA.
 * User: zliu
 * Date: 14-4-1
 * Time: 下午3:42
 * To change this template use File | Settings | File Templates.
 */
public final class LogTokenizer extends Tokenizer {
    public LogTokenizer(Reader in) {
        super(in);
    }

    public LogTokenizer(AttributeFactory factory, Reader in) {
        super(factory, in);
    }

    private boolean isFinish = false;   //第一遍的时候是false。 当执行第二遍的时候则为true，表示该循环结束了
    private int offset = 0, bufferIndex = 0, dataLen = 0;
    private final static int MAX_WORD_LEN = 255;
    private final static int IO_BUFFER_SIZE = 1024;
    private final char[] buffer = new char[MAX_WORD_LEN];
    private final char[] ioBuffer = new char[IO_BUFFER_SIZE];

    private int realLen = 0;


    private int length;
    private int start;
    //处理后的词元写进这两个属性，一个记录词元，一个是记录位置信息
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);//记录词元
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);//记录位置信息<span style="white-space:pre">  </span>//本地的一个写缓冲区，要是英文就先写到这里，写完一个英文单词再写到termAtt

    private final void push(char c) {

        if (length == 0) start = offset - 1;            // start of token
        if (isFinish) {
            buffer[length++] = Character.toLowerCase(c);  // buffer it
        } else {
            buffer[length++] = c;  // buffer it
        }

    }

    //把词元和词元的位置信息写到字典，返回true是表示还有词需要继续处理，返回false表示此次输入的文档处理完毕
    private final boolean flush() {
//length是指写入词典的词元的长度
        if (length > 0) {
            //System.out.println(new String(buffer, 0,
            //length));
            termAtt.copyBuffer(buffer, 0, length);
            offsetAtt.setOffset(correctOffset(start), correctOffset(start + length));
            return true;
        }
        return !isFinish; //如果没有结束则返回true，否则返回false结束
    }

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();
        if (offset != 0) {
            if (offset >= realLen && realLen < dataLen) {
                length = 0;//重置length
                start = 0;
                bufferIndex = 0;
                offset = 0;
                dataLen = realLen;
                isFinish = true; //表示最后一遍，如果异常既推出
            } else if (offset >= realLen) {
                return false;
            }
        }
//写完一个词元后，长度清零，新词元的起始位置从上一个词元的最后位置开始
        length = 0;//重置length
        start = offset;//把上一次的偏移量赋值成这一次的起始值

        int dhao = 0;

        while (true) {

            final char c;
            offset++;
//将输入流ioBuffer读出来，当bufferIndex>=dataLen的时候，也就是一个输入流被处理完的时候
//再读ioBuffer，dateLen就会等于-1，也就是input.read(ioBuffer)=-1
            if (bufferIndex >= dataLen && realLen <= dataLen) {
                realLen = input.read(ioBuffer);
                dataLen = realLen * 2;  //为了能够执行两边，首先把dataLen设置增加两倍
                bufferIndex = 0;
            }
//如果dataLen等于-1，length是等于0的，进入flush，会直接返回false，就是该次输入的文档分析结束
            if (dataLen == -1) {
                offset--;
                return flush();
            } else c = ioBuffer[bufferIndex++];//取出输入流的字符

            switch (Character.getType(c)) {//如果是数字和字母，就写入本地缓存，然后处理下一个字符，如果等于最大长度了，直接写入
                case Character.DECIMAL_DIGIT_NUMBER:
                case Character.LOWERCASE_LETTER:
                case Character.UPPERCASE_LETTER:
                    push(c);
                    if (length == MAX_WORD_LEN) return flush();
                    break;
//如果是其他符号，要是有本地缓存，就先写本地缓存，再后退一次(避免数字，字母和其他字符变成一个词元写入)，要是没有本地缓存就直接写入，
//保证了数字和字母结束后遇到其他符号，可以吧数字和字符完整写入，和其他字符也能正常写入
                case Character.OTHER_LETTER:
                    if (length > 0 && realLen >= dataLen) {  //第一遍索引时不需要做任何的删除，第二遍的时候再判断
                        bufferIndex--;
                        offset--;
                        return flush();
                    }
                    push(c);
                    return flush();

                default:
                    //第一次索引所有的组合，time=‘dd dd dd dd dd '
                    if (c != ' ' && c != ',' && c != '\u0000' && realLen < dataLen) {     //当realLen 小于dataLen说明是第一遍，则如果遇到, 空格才会分词，同时需要记录’
                        if (c == '\"') {
                            dhao++;
                        }
                        push(c);
                        if (length == MAX_WORD_LEN) return flush();
                        break;
                    }
                    //第二次把=，空格去掉
                    else if (dataLen == realLen && (c == '.' || c == '_' || c == '-')) {
                        push(c);
                        if (length == MAX_WORD_LEN) return flush();
                        break;
                    }
                    if (dhao == 1) {
                        push(c);
                        if (length == MAX_WORD_LEN) return flush();
                        break;
                    }
                    dhao = 0;
                    if (length > 0 || offset >= realLen) return flush();
                    break;
            }
        }
    }

    @Override
    public final void end() {
        // set final offset
        final int finalOffset = correctOffset(offset);
        this.offsetAtt.setOffset(finalOffset, finalOffset);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        isFinish = false;
        offset = bufferIndex = dataLen = realLen = 0;
    }
}
