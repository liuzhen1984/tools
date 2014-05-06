package com.hillstone.hsa.utils;

import com.hillstone.hsa.Interface.LogFileToIndex;
import com.hillstone.hsa.Interface.LogFileToIndexImp;
import com.hillstone.hsa.domain.LogObj;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: hillstone
 * Date: 13-5-27
 * Time: 上午10:52
 * To change this template use File | Settings | File Templates.
 */
public class FilesIndex extends Thread {
    private String logType = null;
    private String logSource = null;
    private String logFile = null;
    private LogFileToIndex logFileToIndex = null;    //当处理完日志文件后该怎么处理
    private RamIndexWriter ramIndexWriter = null;
    private FilesIndex(){

    }
    public FilesIndex(String logFile,String logType,String logSource){
        this.logSource = logSource;
        this.logType = logType;
        this.logFile = logFile;
        this.logFileToIndex = new LogFileToIndexImp();
        try {
            initIndexWriter();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    public FilesIndex(String logFile,String logType,String logSource,LogFileToIndex logFileToIndex){
        this.logSource = logSource;
        this.logType = logType;
        this.logFile = logFile;
        this.logFileToIndex = logFileToIndex;
        try {
            initIndexWriter();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void initIndexWriter() throws IOException {
        this.ramIndexWriter = new RamIndexWriter();
    }
    public void run(){
       readLog();
    }
    public void readLog(){
        System.out.println("Index log: "+this.logFile);
        File file = new File(this.logFile);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                indexLog(tempString);
            }
            reader.close();
            this.logFileToIndex.afterIndex(file);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            LuceneUtils.optimizeIndex(this.logType,this.ramIndexWriter.getRamDirectory());
            this.ramIndexWriter.closeRamIndexWriter();
            this.ramIndexWriter = null;
        }
    }
    private void indexLog(String logline){
        LogObj logObj = new LogObj();
        logObj.setSource(this.logSource);
        logObj.setType(this.logType);
        logObj.setLog(logline);
        logObj.setDate(new Date());
        this.save(logObj);
        logObj = null;
    }

    private void save(LogObj logObj) {
        // 1，把Log转为Document
        Document doc = LogDocumentUtils.log2Document(logObj);
        // 2，把Document存到索引库中
        try {
            this.ramIndexWriter.getRamIndexWriter().addDocument(doc); // Add
            this.ramIndexWriter.getRamIndexWriter().commit(); // commit
            //如果处理日志超过2万条则合并一次到文件中
            if (this.ramIndexWriter.getRamIndexWriter().numDocs()>10000){
                LuceneUtils.optimizeIndex(logObj.getType(),this.ramIndexWriter.getRamDirectory());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
