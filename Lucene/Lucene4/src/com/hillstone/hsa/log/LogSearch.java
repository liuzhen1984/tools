package com.hillstone.hsa.log;

import com.hillstone.hsa.domain.LogObj;
import com.hillstone.hsa.utils.Configuration;
import com.hillstone.hsa.utils.LogDocumentUtils;
import com.hillstone.hsa.utils.QueryResult;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

import org.apache.lucene.search.*;
import org.apache.lucene.search.payloads.PayloadNearQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hillstone
 * Date: 13-5-20
 * Time: 下午11:03
 * To change this template use File | Settings | File Templates.
 */
public class LogSearch {
    private String logType="";
    private String source="";
    private Integer startTime=0;
    private Integer endTime=0;
    public LogSearch(String logType){
        setLogType(logType);
    }
    public LogSearch(String logType,String source){
        setLogType(logType);
        setSource(source);
    }

    public LogSearch(String logType,String source,Integer startTime,Integer endTime){
        setLogType(logType);
        setSource(source);
        setStartTime(startTime);
        setEndTime(endTime);
    }
    /**
     * * 搜索
     *
     * @param queryString
     *            查询字符串（条件）
     * @param firstResult
     *            从结果集合中的哪一条索引开始取数据
     * @param maxResults
     *            最多取多少条数据
     * @return
     */
    public QueryResult searchTermQuery(String queryString, Integer firstResult, Integer maxResults) {
        Query query = new TermQuery(new Term("log", queryString));
        System.out.println(query.toString());
        return search(query,firstResult,maxResults);
    }
    public QueryResult searchRangeQuery(String queryString, Integer firstResult, Integer maxResults,Integer min,Integer max) {
        // 1，把查询字符串转为Query对象
        //RangeQuery
        Query query = NumericRangeQuery.newIntRange("log",min,max,true,true);
        System.out.println(query.toString());
        return search(query,firstResult,maxResults);
    }
    //支持通配符查询
    public QueryResult searchWildcardQuery(String queryString, Integer firstResult, Integer maxResults) {
        // 1，把查询字符串转为Query对象
        //RangeQuery
        Query query = new WildcardQuery(new Term("log",queryString));
        System.out.println(query.toString());
        return search(query,firstResult,maxResults);
    }
    public QueryResult searchPhraseQuery(String[] queryString, Integer firstResult, Integer maxResults) {
        // 1，把查询字符串转为Query对象
        //RangeQuery
        PhraseQuery query = new PhraseQuery();
        for (int max = queryString.length, i=0;i<max;i++){
            query.add(new Term("log",queryString[i]));
        }
        query.setSlop(10);
        System.out.println(query.toString());
        return search(query,firstResult,maxResults);
    }
    public QueryResult searchBooleanQuery(String[] queryString, Integer firstResult, Integer maxResults) {
        // 1，把查询字符串转为Query对象
        BooleanQuery booleanQuery = new BooleanQuery();
        // booleanQuery.add(query, Occur.MUST); // 必须满足
        // booleanQuery.add(query, Occur.MUST_NOT); // 非
        // booleanQuery.add(query, Occur.SHOULD); // 多个SHOULD一起用，是OR的关系

        // 对应的查询字符串为：+title:lucene +id:{5 TO 15]
        // 对应的查询字符串为：title:lucene AND id:{5 TO 15]
        // booleanQuery.add(query1, Occur.MUST);
        // booleanQuery.add(query2, Occur.MUST);

        // 对应的查询字符串为：+title:lucene -id:{5 TO 15]
        // 对应的查询字符串为：title:lucene NOT id:{5 TO 15]
        // booleanQuery.add(query1, Occur.MUST);
        // booleanQuery.add(query2, Occur.MUST_NOT);

        // 对应的查询字符串为：title:lucene id:{5 TO 15]
        // 对应的查询字符串为：title:lucene OR id:{5 TO 15]
        // MUST + SHOULD，与只有一个MUST效果相同
        // SHOULD + MUST_NOT，这时SHOULD就相当MUST
        // MUST_NOT + MUST_NOT，没有匹配结果，也不报错。
        if(queryString.length==0){
            Query query = new WildcardQuery(new Term("log","*"));
            booleanQuery.add(query,BooleanClause.Occur.MUST);
        } else{
            for(int max=queryString.length,i=0;i<max;i++){
                Query query = new WildcardQuery(new Term("log",queryString[i]));
                booleanQuery.add(query, BooleanClause.Occur.MUST);
            }
        }

        //根据日志类型查询由于日志分类存储的，所以暂时不需要考虑该字段的过滤
//        Query query = new TermQuery(new Term("type",getLogType()));
//        booleanQuery.add(query,BooleanClause.Occur.MUST);
        //根据来源查询
        if(!"".equals(getSource())){
            Query query = new TermQuery(new Term("type",getSource()));
            booleanQuery.add(query,BooleanClause.Occur.MUST);
        }
        //根据时间
        if(0!=getStartTime() && 0!=getEndTime()){
            Query query = NumericRangeQuery.newIntRange("date",Integer.valueOf(getStartTime()),Integer.valueOf(getEndTime()),true,true);
            booleanQuery.add(query,BooleanClause.Occur.MUST);
        }

        return search(booleanQuery,firstResult,maxResults);
    }
//    public QueryResult search_queryParser(String queryString, int firstResult, int maxResults) {
//            // 1，把查询字符串转为Query对象
//            // QueryParser，只能在一个Field中查询
//            // QueryParser queryParser = new QueryParser(Version.LUCENE_30, "title", Configuration.getAnalyzer());
//            // MultiFieldQueryParser，可以多个Field中查询
//            QueryParser queryParser = new QueryParser(Version.LUCENE_30,"log", Configuration.getAnalyzer());
//        Query query = null;
//        try {
//            query = queryParser.parse(queryString);
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//        return search(query,firstResult,maxResults);
//    }

    private QueryResult search(Query query,Integer firstResult,Integer maxResults){
        IndexSearcher indexSearcher = null;
        String path = Configuration.PATH+"/"+this.getLogType();
        try {
            Directory directory = FSDirectory.open(new File(path));
            indexSearcher = new IndexSearcher(DirectoryReader.open(directory));
            TopDocs topDocs = indexSearcher.search(query, firstResult + maxResults); // 返回前n条结果，返回足够的结果就可以了
    //            Sort sort = new Sort(new SortField("date",SortField.STRING));
    //            TopDocs topDocs = indexSearcher.search(query,null, firstResult + maxResults,sort); // 返回前n条结果，返回足够的结果就可以了
            int count = topDocs.totalHits; // 符合条件的总结果数
            ScoreDoc[] scoreDocs =  topDocs.scoreDocs;  //根据得分排序
            // 3，处理结果
            List<LogObj> list = new ArrayList<LogObj>();
            int endIndex = Math.min(firstResult + maxResults, topDocs.scoreDocs.length); // 用比较小的那个值做为结束的索引，以防数组下标越界异常

            for (int i = firstResult; i < endIndex; i++) { // 只取一段数据
                // 根据内部编号取出相应的Document数据
                ScoreDoc scoreDoc = scoreDocs[i];
                Document doc = indexSearcher.doc(scoreDoc.doc);

                // 把Document转为Article，并添加集合中
                LogObj logObj = LogDocumentUtils.document2Log(doc);
                list.add(logObj);
            }

            // 返回结果
            return new QueryResult(count, list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }
}
