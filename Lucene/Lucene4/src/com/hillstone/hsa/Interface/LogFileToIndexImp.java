package com.hillstone.hsa.Interface;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: hillstone
 * Date: 13-5-27
 * Time: 下午11:11
 * To change this template use File | Settings | File Templates.
 */
public class LogFileToIndexImp implements LogFileToIndex {
    public void afterIndex(File file){
        file.deleteOnExit();
    }
}
