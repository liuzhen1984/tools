package com.hillstone.hsa.utils;

import com.hillstone.hsa.domain.LogObj;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.util.Date;

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
        doc.add(new StringField("time",String.valueOf(logObj.getDate().getTime()), Field.Store.YES));
        doc.add(new StringField("type", logObj.getType(), Field.Store.YES));
        doc.add(new StringField("source", logObj.getSource(), Field.Store.YES));
        doc.add(new TextField("log", logObj.getLog(), Field.Store.YES));
        return doc;
    }
    public static LogObj document2Log(Document doc){
        LogObj logObj = new LogObj();
        if (doc.get("date")!=null){
            logObj.setDate(new Date(Long.parseLong(doc.get("date"))));
        }
        logObj.setLog(doc.get("log"));
        logObj.setSource(doc.get("source"));
        logObj.setType(doc.get("type"));
        return logObj;
    }
}
