package com.hillstone.hsa.index.utils;

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
     1.每天创建一个文件夹
     */
    public static String PATH = "/data/hsa/db/solr/12345";
//    public static String PATH = "F:\\solr\\";
    public static String CACHE_PATH = "/data/hsa/cache/txtlog";
//    public static String CACHE_PATH = "F:\\cache\\";
    final public static String DATEFORMAT="yyyyMMdd";



}
