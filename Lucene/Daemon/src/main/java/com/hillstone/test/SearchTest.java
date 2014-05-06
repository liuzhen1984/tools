package com.hillstone.test;

import com.hillstone.hsa.index.utils.Configuration;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: zliu
 * Date: 14-3-22
 * Time: 上午7:18
 * To change this template use File | Settings | File Templates.
 */
public class SearchTest {

    @Test
    public void itest() throws Exception {
        Date time = new Date(1395742630011l);
        System.out.println(time.toLocaleString());
        test("20140401","152", "module=\"FLOW\"", 0, 2395802630011l);
    }

    public void test(String date,String time, String queryString, long startTime, long endTime) throws Exception {

        String usage =
                "Usage:\tjava org.apache.lucene.demo.SearchFiles [-index dir] [-field f] [-repeat n] [-queries file] [-query string] [-raw] [-paging hitsPerPage]\n\nSee http://lucene.apache.org/core/4_1_0/demo/ for details.";
        System.out.println(usage);
        String index = "";
        String field = "";
        String queries = null;
        int repeat = 0;
        boolean raw = false;
        int hitsPerPage = 10;

        index = Configuration.PATH + "/" + date+"/"+time;
        field = "log";

        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index)));
        IndexSearcher searcher = new IndexSearcher(reader);

        if (queries == null && queryString == null) {                        // prompt the user
            System.out.println("Enter query: ");
        }
        BooleanQuery booleanQuery = new BooleanQuery();

        if (startTime != 0 && endTime != 0) {
            System.out.println("Starttime : " + startTime + " endTime : " + endTime);
            Query queryN = NumericRangeQuery.newLongRange("receive_time", startTime, endTime, true, true);
            booleanQuery.add(queryN, BooleanClause.Occur.MUST);
        }

        if (queryString == null || queryString.length() == -1 || "".equals(queryString.trim())) {
            return;
        }

        queryString = queryString.trim();
        Query query = null;
        if (queryString.contains(" ")) {
            CharArraySet cas = new CharArraySet(Version.LUCENE_47,1,true);
        cas.add(" ");

            Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_47);
            QueryParser parser = new QueryParser(Version.LUCENE_47, field, analyzer);
            parser.setDefaultOperator(QueryParser.Operator.AND);
            query = parser.parse(queryString);
        }   else{
            query = new WildcardQuery(new Term("log",queryString));
        }


        System.out.println("Searching for: " + query.toString(field));

        booleanQuery.add(query, BooleanClause.Occur.MUST);
        doPagingSearch(searcher, booleanQuery, hitsPerPage);
        reader.close();
    }

    public static void doPagingSearch(IndexSearcher searcher, Query query,
                                      int hitsPerPage) throws IOException {

        TopDocs results = searcher.search(query, hitsPerPage);
        ScoreDoc[] hits = results.scoreDocs;
        int numTotalHits = results.totalHits;
        System.out.println(numTotalHits + " total matching documents");

        int start = 0;
        for (int i = start; i < hitsPerPage; i++) {
            Document doc = searcher.doc(hits[i].doc);
            for (IndexableField ifs : doc.getFields()) {
                System.out.println(ifs.name() + " " + ifs.stringValue());
            }
        }
    }
}

