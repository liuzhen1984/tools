package com.hillstone.hsa.log;

import com.hillstone.hsa.Interface.LogFileToIndexNoneImp;
import com.hillstone.hsa.domain.LogObj;
import com.hillstone.hsa.utils.Configuration;
import com.hillstone.hsa.utils.FilesIndex;
import com.hillstone.hsa.utils.LogDocumentUtils;
import com.hillstone.hsa.utils.LuceneUtils;
import org.apache.lucene.document.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: hillstone
 * Date: 13-5-17
 * Time: 下午4:40
 * To change this template use File | Settings | File Templates.
 */
public class LogIndex {
    public LogIndex(String logType, String logSource){
        setLogSource(logSource);
        setLogType(logType);
    }
    private String logType;
    private String logSource;
    public void readLogDir(String path){
        File dirs = new File(path);
        File[] files = dirs.listFiles();
        for(int max=files.length,i=0;i<max;i++){
            if (files[i].isFile()){
                FilesIndex filesIndex = new FilesIndex(files[i].getPath(),getLogType(),getLogSource());
                filesIndex.start();
                filesIndex = null;
            }
            if(i>30){
                break;
            }
        }
    }
    public void readLogFile(String path){
        LogFileToIndexNoneImp logFileToIndexNoneImp = new LogFileToIndexNoneImp();
        FilesIndex filesIndex = new FilesIndex(path,getLogType(),getLogSource(),logFileToIndexNoneImp);
        filesIndex.readLog();
    }
    public String getLogType() {
        return logType;
    }

    private void setLogType(String logType) {
        this.logType = logType;
    }

    public String getLogSource() {
        return logSource;
    }

    private void setLogSource(String logSource) {
        this.logSource = logSource;
    }
}
