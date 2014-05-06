package com.hillstone.test;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: zliu
 * Date: 14-4-2
 * Time: 上午10:49
 * To change this template use File | Settings | File Templates.
 */
public class OtherTest {
    @Test
    public void test() {
        String line = "log=\"d df\"";
        System.out.println(containtSpace(line));
    }
    public boolean containtSpace(String str){
        char[] lines = str.trim().toCharArray();
        int dquota = 0;
        for (char l : lines) {
            if(l==' ' && dquota==0){
                return true;
            }
            if(l == '"'){
                if(dquota == 1 ){
                    dquota = 0;
                }else{
                    dquota = 1;
                }
            }
        }
        return false;
    }
}
