package com.hillstone.hsa.utils;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
//import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * 根据配置文件生成的配置信息（代表配置文件）
 *
 * @author tyg
 *
 */
public class Configuration {
    //暂时放在一个目录下
    /*
    真正的日志会根据
    1. 日志的类型
    2. 日志的来源
    3. 每天的日志
     */
    final public static String PATH = "./indexDir";

    private static Directory directory=null;
    private static Analyzer analyzer;

    static {
        // 初始化配置，应是读取配置文件得到的信息。
        try {
            //\analyzer = new StandardAnalyzer(Version.LUCENE_30);
            analyzer = new WhitespaceAnalyzer(Version.LUCENE_43);
            //analyzer = new IKAnalyzer(); // 使用词库分词的方式
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
    设置读取目录中的文件索引
    1. 当内存索引没有合并到文件索引中的时候如果调用则是以前
    2. 如果内存索引合并到文件索引中，那么该目录需要重新设置一次，一边合并后的内存索引能够及时的被搜索到
     */
    public static void setDirectory(String logType) throws IOException {
        if (directory==null){
           directory = FSDirectory.open(new File(PATH+"/"+logType));
        }
    }
    public static Directory getDirectory() {
        return directory;
    }
    public static Analyzer getAnalyzer() {
        return analyzer;
    }

}
