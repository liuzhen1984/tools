package com.hillstone.hsa.index.utils;

import com.hillstone.hsa.index.domain.LogObj;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;

/**
 * Created with IntelliJ IDEA.
 * User: hillstone
 * Date: 13-5-17
 * Time: 下午3:45
 * To change this template use File | Settings | File Templates.
 */
public class LogDocumentUtils {
    public static Document log2Document(LogObj logObj){
        Document doc = new Document();
        //String idStr = NumericUtils.intToPrefixCoded(article.getId()); // 对于数字，一定要使用NumericUtils工具类
        //doc.add(new Field("id", idStr, Store.YES, Field.Index.NOT_ANALYZED)); // 唯一标识符应使用不分词但建索引
        doc.add(new LongField("time",logObj.getDate(), Field.Store.YES));
//        doc.add(new StringField("type", logObj.getType(), Field.Store.YES));
//        doc.add(new StringField("severity", logObj.getSeverity(), Field.Store.YES));
        doc.add(new TextField("log", logObj.getLog(), Field.Store.YES));
        return doc;
    }
    public static LogObj document2Log(Document doc){
        LogObj logObj = new LogObj();
        if (doc.get("receive_time")!=null){
            logObj.setDate(Long.parseLong(doc.get("receive_time")));
        }
        logObj.setLog(doc.get("log"));
        logObj.setSn(doc.get("sn"));
        logObj.setType(doc.get("type"));
        logObj.setSeverity(doc.get("severity"));
        return logObj;
    }
}
