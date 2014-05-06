package com.hillstone.hsa.index.analyzer;

import org.apache.lucene.analysis.standard.StandardTokenizerInterface;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.Reader;

/**
 * Created with IntelliJ IDEA.
 * User: zliu
 * Date: 14-4-1
 * Time: 下午8:07
 * To change this template use File | Settings | File Templates.
 */
public class LogTokenizerImpl implements StandardTokenizerInterface {
    private Reader input;
    private int yylength = 0;
    private int yychar = 0;

    private int zzStartRead = 0;
    private int zzMarkedPos = 0;

    private static final int ZZ_BUFFERSIZE = 4096;
    private char zzBuffer[] = new char[ZZ_BUFFERSIZE];


    public LogTokenizerImpl(Reader input) {
        this.input = input;
        try {
            this.input.read(zzBuffer);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void getText(CharTermAttribute t) {
        t.copyBuffer(zzBuffer, zzStartRead, zzMarkedPos - zzStartRead);
    }

    @Override
    public int yychar() {
        return yychar++;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void yyreset(Reader reader) {
        System.out.println(reader);
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int yylength() {
        yylength++;
        return this.yylength;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getNextToken() throws IOException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
