package com.hillstone.hsa.index.utils;

import com.hillstone.hsa.index.analyzer.LogAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NoLockFactory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: hillstone
 * Date: 13-5-17
 * Time: 下午3:48
 * To change this template use File | Settings | File Templates.
 */
public class LuceneUtils {

    private static Analyzer analyzer;

    private static Map<String,RAMDirectory> RAM_DIR_MAP = new HashMap<String, RAMDirectory>();
    private static Map<String,IndexWriter> RAM_IW_MAP = new HashMap<String,IndexWriter>();

    private static Map<String,Directory> DIR_MAP = new HashMap<String, Directory>();
    private static Map<String,IndexWriter> IW_MAP = new HashMap<String, IndexWriter>();

    static {
        // 初始化配置，应是读取配置文件得到的信息。
        try {
            //analyzer = new StandardAnalyzer(Version.LUCENE_30);
//            Reader fileread = new FileReader(new File("E:\\Upan\\Lucene\\Daemon\\src\\main\\resources\\stopwords.txt"));


            analyzer = new LogAnalyzer();
            initDirectory();
            initIndexWriter();
            //analyzer = new IKAnalyzer(); // 使用词库分词的方式
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        initIndexWriter();
        // 加载时，只初始化一次
        System.out.println("IndexWriter创建成功.");

        // 在JVM退出前执行的代码
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("IndexWriter正在关闭。。。");
                    for(String date:RAM_IW_MAP.keySet()){
                        RAM_IW_MAP.get(date).close();
                        RAM_DIR_MAP.get(date).close();
                    }
                    for(String date : IW_MAP.keySet()){
                        IW_MAP.get(date).close();
                    }
                    for(String date : DIR_MAP.keySet()){
                        DIR_MAP.get(date).close();
                    }

                    System.out.println("IndexWriter已关闭.");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /*
    为了把内存的索引合并到指定的文件索引中去
    1. JVM退出调用
    2. 内存索引所占的内存大于2G，调用
     */
    public static long optimizeIndex(String logDate){
        try {
            //System.out.println("Merge RamDirectory to FSDirectory");
//            addDirMap(logDate);
//            addLocalIW(logDate);
            getRAMIW(logDate).commit();
            getLocalIW(logDate).addIndexes(getRAM(logDate));
            getLocalIW(logDate).commit();
            updateRAM(logDate);
            addRAMIW(logDate);
            for(String key:IW_MAP.keySet()){
                if(key.compareTo(logDate)<0){
                    IW_MAP.remove(key);
                    DIR_MAP.remove(key);
                    RAM_IW_MAP.remove(key);
                    RAM_DIR_MAP.remove(key);
                }
            }
//            getLocalIW(logDate).forceMerge(10);
//            getLocalIW(logDate).close();
//            deleteLocalIW(logDate);
//            updateRAM();
//            addRAMIW();
            return new Date().getTime();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static long optimizeIndex(){
        try {
            //System.out.println("Merge RamDirectory to FSDirectory");
//            addDirMap(logDate);
//            addLocalIW(logDate);
            for(String logDate: RAM_IW_MAP.keySet()){
                System.out.println(getRAMIW(logDate).numRamDocs());
                System.out.println(getRAM(logDate).listAll().length);
                getRAMIW(logDate).commit();
                System.out.println(getRAM(logDate).listAll().length);
                System.out.println(getRAMIW(logDate).numRamDocs());
                getLocalIW(logDate).addIndexes(getRAM(logDate));
                getLocalIW(logDate).commit();
                updateRAM(logDate);
                addRAMIW(logDate);
            }
//            getLocalIW(logDate).forceMerge(10);
//            getLocalIW(logDate).close();
//            deleteLocalIW(logDate);
//            updateRAM();
//            addRAMIW();
            return new Date().getTime();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

     private static void initIndexWriter(){
         addRAMIW(ToolsUtils.getCurrentDate(Configuration.DATEFORMAT));
         addLocalIW(ToolsUtils.getCurrentDate(Configuration.DATEFORMAT));
     }

    /*
   设置内存索引
   1. 初始化第一次创建内存索引
   2. 当内存索引合并到文件索引后，再把内存索引重新初始化一次；
    */
    public static void addRAMIW(String logDate){
        try {
            if(RAM_IW_MAP.get(logDate) != null){
                try{
                    RAM_IW_MAP.get(logDate).close();
                }catch(Exception ex){
                    //  ex.printStackTrace();
                }
                RAM_IW_MAP.put(logDate,null);
            }
            IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_47,getAnalyzer());
            iwConfig.setRAMBufferSizeMB(2000);
            iwConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            RAM_IW_MAP.put(logDate, new IndexWriter(getRAM(logDate), iwConfig));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void addLocalIW(String logDate){
        IndexWriter indexWriter= null;
        if(IW_MAP.containsKey(logDate)){
            indexWriter = IW_MAP.get(logDate);
        }
        try {
            if(indexWriter != null){
                try{
                    indexWriter.close();
                }catch(Exception ex){
                    //  ex.printStackTrace();
                    ex.printStackTrace();
                    return;
                }
                indexWriter = null;
            }
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_47, getAnalyzer());
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            indexWriter = new IndexWriter(getLOCAL(logDate),iwc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        IW_MAP.put(logDate, indexWriter);
    }

    private static void deleteLocalIW(String logDate){
        if(IW_MAP.containsKey(logDate)){
            IW_MAP.put(logDate,null);
            IW_MAP.remove(logDate);
        }
    }
    public static IndexWriter getRAMIW(String logDate){
        if(RAM_IW_MAP.get(logDate) == null){
            addRAMIW(logDate);
        }
        return RAM_IW_MAP.get(logDate);

    }

    public static IndexWriter getLocalIW(String logDate) throws IOException {
        if(IW_MAP.get(logDate)==null){
            addLocalIW(logDate);
        }

//        IW_MAP.get(logDate).commit();
        return IW_MAP.get(logDate);
    }
    /*
    设置读取目录中的文件索引
    1. 当内存索引没有合并到文件索引中的时候如果调用则是以前
    2. 如果内存索引合并到文件索引中，那么该目录需要重新设置一次，一边合并后的内存索引能够及时的被搜索到
    params date 20140701
     */
    private static void initDirectory() throws IOException {
        addDirMap(ToolsUtils.getCurrentDate(Configuration.DATEFORMAT));
    }
    public static void addDirMap(String date) throws IOException {
        if(!DIR_MAP.containsKey(date) || DIR_MAP.get(date)==null){
            Directory directory = FSDirectory.open(new File(Configuration.PATH + "/" + date));

            DIR_MAP.put(date,directory);
        }
        if(RAM_DIR_MAP.get(date) ==null){
            RAM_DIR_MAP.put(date, new RAMDirectory());
            RAM_DIR_MAP.get(date).setLockFactory(NoLockFactory.getNoLockFactory());
        }

    }
    /*
    根据时间，只是合并和更新的Directory。
     */
    public static void updateDirMap(String date) throws IOException {
        if(!DIR_MAP.containsKey(date)){
            addDirMap(date);
            return;
        }
        updateLOCAL(date);
        updateRAM(date);
        return;
    }

    public static void updateLOCAL(String date) throws IOException {
        Directory directory = null;
        if(!DIR_MAP.containsKey(date)){
            addDirMap(date);
            return;
        }
        directory = DIR_MAP.get(date);
        if(directory!=null){
            directory.close();
            directory = null;
        }
        directory = FSDirectory.open(new File(Configuration.PATH+"/"+date));
        DIR_MAP.put(date, directory);
    }

    public static void updateRAM(String logDate) throws IOException {

        if(RAM_DIR_MAP.get(logDate)!=null){
            RAM_DIR_MAP.get(logDate).close();
            RAM_DIR_MAP.put(logDate, null);
        }
        RAM_DIR_MAP.put(logDate,new RAMDirectory());
        RAM_DIR_MAP.get(logDate).setLockFactory(NoLockFactory.getNoLockFactory());
    }

    public static Directory getLOCAL(String date) throws IOException {
        updateLOCAL(date);
        return DIR_MAP.get(date);
    }
    public static RAMDirectory getRAM(String logDate) throws IOException {
        if(RAM_DIR_MAP.get(logDate) ==null){
            updateRAM(logDate);
        }
        return RAM_DIR_MAP.get(logDate);
    }

    public static Analyzer getAnalyzer() {
        return analyzer;
    }


}
