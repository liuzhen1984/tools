import com.hillstone.hsa.index.log.LogFileIndex;
import com.hillstone.hsa.index.utils.Configuration;
import com.hillstone.test.SearchTest;

public class Main {
    public static void main(String[] args) {
        if(args.length==0){
            LogFileIndex lfi = new LogFileIndex();
            lfi.readLogDir();
        }

//        LuceneUtils.getAnalyzer();
        if ("search".equalsIgnoreCase(args[0])) {
            SearchTest searchTest =new SearchTest();
            try {
                if(args.length<4){
                    searchTest.test(args[1],args[2],args[3],0,0);
                }else{
                   long startTime = Long.valueOf(args[4]);
                   long endTime = Long.valueOf(args[5]);
                    Configuration.PATH = "/data/hsa/db/solr/"+args[6];
                    searchTest.test(args[1], args[2], args[3], startTime, endTime);
                }

            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
//        LogFileIndex logFile = new LogFileIndex("nat","locahost");
//        for(int  i =0;i<5000;i++){
//             logFile.readLog("./data/test3.log");
//        }
    }
}
