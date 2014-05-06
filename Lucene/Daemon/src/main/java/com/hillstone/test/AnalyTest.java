package com.hillstone.test;

import com.hillstone.hsa.index.analyzer.LogAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created with IntelliJ IDEA.
 * User: hillstone
 * Date: 13-5-19
 * Time: 下午11:27
 * To change this template use File | Settings | File Templates.
 */
public class AnalyTest {
    @Test
    public void test(){
        Analyzer analysis = new LogAnalyzer();
        String txt = "time=\"Apr  1 16:17:42\" hname=\"100\" module=\"FLOW\" subm=\"SM_FW_NBC\" entry=\"unknown\" important=\"NO\" CAT=\"WEBPOST\", IP=\"192.168.200.100\", \"(Tom)\", vrouter=\"trust-vr\", \"222\", conten_type=\"12121212\", action=\"21212121\" reason=\"1234\", rule=\"5678\", charset=\"4445\", \"444 \"";
        try {
            AnalyTest.displayTokenInfo(txt,analysis,true);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void displayTokenInfo(String txt, Analyzer analyzer, boolean displayAll) throws Exception {
        //第一个参数没有任何意义,可以随便传一个值,它只是为了显示分词
        //这里就是使用指定的分词器将'txt'分词,分词后会产生一个TokenStream(可将分词后的每个单词理解为一个Token)
        TokenStream stream = analyzer.tokenStream("此参数无意义", new StringReader(txt));
        //用于查看每一个语汇单元的信息,即分词的每一个元素
        //这里创建的属性会被添加到TokenStream流中,并随着TokenStream而增加(此属性就是用来装载每个Token的,即分词后的每个单词)
        //当调用TokenStream.incrementToken()时,就会指向到这个单词流中的第一个单词,即此属性代表的就是分词后的第一个单词
        //可以形象的理解成一只碗,用来盛放TokenStream中每个单词的碗,每调用一次incrementToken()后,这个碗就会盛放流中的下一个单词
        CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
        //用于查看位置增量(指的是语汇单元之间的距离,可理解为元素与元素之间的空格,即间隔的单元数)
        PositionIncrementAttribute pia = stream.addAttribute(PositionIncrementAttribute.class);
        //用于查看每个语汇单元的偏移量
        OffsetAttribute oa = stream.addAttribute(OffsetAttribute.class);
        //用于查看使用的分词器的类型信息
        TypeAttribute ta = stream.addAttribute(TypeAttribute.class);
        try {
            if (displayAll) {
                //等价于while(stream.incrementToken())
                stream.reset();
                for (; stream.incrementToken(); ) {
                    System.out.println(ta.type() + " " + pia.getPositionIncrement() + " [" + oa.startOffset() + "-" + oa.endOffset() + "] [" + cta + "]");
                }
            } else {
                System.out.println();
                while (stream.incrementToken()) {
                    System.out.print("[" + cta + "]");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            stream.reset();
            stream.close();
        }
    }
}

