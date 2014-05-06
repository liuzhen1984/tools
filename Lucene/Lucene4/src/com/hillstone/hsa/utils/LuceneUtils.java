package com.hillstone.hsa.utils;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: hillstone
 * Date: 13-5-17
 * Time: 下午3:48
 * To change this template use File | Settings | File Templates.
 */
public class LuceneUtils {
    public static IndexWriter indexWriter=null;
    public static IndexWriterConfig indexWriterConfig = null;

    static {
        // 加载时，只初始化一次
        indexWriterConfig = new IndexWriterConfig(Version.LUCENE_43,Configuration.getAnalyzer());
        indexWriterConfig.setRAMBufferSizeMB(256.0);
        System.out.println("IndexWriter创建成功.");

        // 在JVM退出前执行的代码
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("IndexWriter正在关闭。。。");
                    if(indexWriter!=null){
                        indexWriter.close();
                    }
                    indexWriter=null;
                    System.out.println("IndexWriter已关闭.");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    private static void setIndexWriter() throws IOException {
        if (indexWriter==null){
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_43,Configuration.getAnalyzer());
            indexWriter = new IndexWriter(Configuration.getDirectory(), indexWriterConfig);
        }
    }
    /*
    为了把内存的索引合并到指定的文件索引中去
    1. JVM退出调用
    2. 内存索引所占的内存大于2G，调用
     */
    synchronized public static void optimizeIndex(String logType,Directory ramDirectory){
        try {
            Configuration.setDirectory(logType);
            setIndexWriter();
            indexWriter.addIndexes(ramDirectory);
            indexWriter.forceMerge(10);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
