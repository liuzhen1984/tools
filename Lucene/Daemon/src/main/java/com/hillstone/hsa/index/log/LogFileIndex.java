package com.hillstone.hsa.index.log;


import com.hillstone.hsa.index.domain.LogObj;
import com.hillstone.hsa.index.utils.Configuration;
import com.hillstone.hsa.index.utils.LogDocumentUtils;
import com.hillstone.hsa.index.utils.LuceneUtils;
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
public class LogFileIndex {
    private long timer = new Date().getTime();

    public void readLogDir(){
        File[] files = null;
        File dirs = null;
        while (true){
            dirs = new File(Configuration.CACHE_PATH);
            files = dirs.listFiles();
            for(File file: files){
                System.out.println(file.getName());
                if (file.isFile()){
                    long ctime = new Date().getTime();
                    System.out.println(file.getPath());
                    StringBuffer logDate = new StringBuffer();
                    try{
                        logDate.append(file.getName().split("_")[2].split("#")[0]);
                    }catch(Exception ex){
                        file.delete();
                        continue;
                    }
                     readLog(file, logDate.toString());
                    System.out.println(file.getPath() + "  Index end");
                    file.delete();
                    System.out.println(new Date().getTime()-ctime);
                }

            }
            if(new Date().getTime()-timer>3000){
                timer = LuceneUtils.optimizeIndex();
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
    public void readLog(File file,String logDate){
        BufferedReader reader = null;
        FileReader fr = null;
        try {
             fr = new FileReader(file);
            reader = new BufferedReader(fr);
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            int i=0;
            while ((tempString = reader.readLine()) != null) {
//                System.out.println(tempString);
                if( save(indexLog(tempString),logDate)){
                   i++;
               }
            }
            if(i>0)    {
//                LuceneUtils.getLocalIW(logDate).commit();
//                 LuceneUtils.optimizeIndex(logDate);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    file = null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if(fr !=null){
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }
    private LogObj indexLog(String logline){
        LogObj logObj = new LogObj();
        logObj.setDate(new Date().getTime());
        logObj.setLog(logline);
//        String[] logdesc = logline.split("\t");
//        if(logdesc.length>=2){
//            logObj.setLog(logdesc[1]);
//            logObj.setDate(Long.valueOf(logdesc[0]));
////            logObj.setType(logdesc[1]);
////            logObj.setSeverity(logdesc[2]);
//            return logObj;
//        }
//        return null;
        return logObj;
    }
    private boolean save(LogObj logObj,String logDate) {
        //测试字符串创建的索引
//        try {
//            AnalyTest.testAnalyzer(Configuration.getAnalyzer(),logObj.getLog());
//        } catch (Exception e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

        if(logObj== null){
            return false;
        }
        // 1，把Log转为Document
        Document doc = LogDocumentUtils.log2Document(logObj);
        // 2，把Document存到索引库中
        try {
//            LuceneUtils.getLocalIW(logDate).addDocument(doc);
//            LuceneUtils.getLocalIW(logDate).commit();
//            LuceneUtils.addRAMIW();
            LuceneUtils.getRAMIW(logDate).addDocument(doc); // Add
//            LuceneUtils.getRAMIW().commit(); // 提交更改
            //如果处理日志超过5万条则合并一次到文件中
            if (LuceneUtils.getRAMIW(logDate).numRamDocs()>100000){
                timer =  LuceneUtils.optimizeIndex(logDate);

            }


            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
