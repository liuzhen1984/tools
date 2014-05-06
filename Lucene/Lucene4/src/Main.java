import com.hillstone.hsa.domain.LogObj;
import com.hillstone.hsa.log.LogIndex;
import com.hillstone.hsa.log.LogSearch;
import com.hillstone.hsa.utils.QueryResult;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        if(args.length==0){
            LogSearch logSearch = new LogSearch("nat");
            QueryResult qr = logSearch.searchBooleanQuery(new String[]{"1*"}, 0, 50);
            System.out.println(qr.getCount());
            for(LogObj logObj : qr.getList()){
                System.out.println(logObj.getLog());
            }
        } else{
            if ("add".equalsIgnoreCase(args[0])){
                if (args.length==3){
                    // "add" "path" "logtype"
                    if(args[1].trim().equals("")){
                        System.out.println("LogType Not Null");
                        return;
                    }
                    LogIndex logFile = new LogIndex(args[1],"");
                    if(args[2].trim().equals("")){
                        System.out.println("Log Dir Not Null");
                        return;
                    }
                    File file = new File(args[2]);
                    if (file.isDirectory()){
                        logFile.readLogDir(args[2]);
                    }
                    if(file.isFile()){
                        logFile.readLogFile(args[2]);
                    }
                }else{
                    System.out.println("Args Error:\"add\" \"logtype\"  \"path\"");
                }
            } else if ("search".equalsIgnoreCase(args[0])) {
                // "search" "logtype"  "first" "max" "query query query" "starttime endtime" "source"
                if(args.length<=7 && args.length>=5){
                    String logtype = args[1];
                    Integer first = Integer.valueOf(args[2]);
                    Integer max = Integer.valueOf(args[3]);
                    String[] query = new String[0];
                    if(!args[4].trim().equals("")){
                        query = args[4].split(" ");
                    }
                    Integer starttime = 0;
                    Integer endtime = 0;
                    String source = "";
                    if (args.length>=6){
                        String[] querytime = args[5].split(" ");
                        starttime = Integer.valueOf(querytime[0]);
                        endtime = Integer.valueOf(querytime[1]);
                    }
                    if (args.length == 7 ){
                        source = args[6];
                    }
                    LogSearch logSearch = new LogSearch(logtype,source,starttime,endtime);
                    QueryResult qr = logSearch.searchBooleanQuery(query,first, max);
                    System.out.println(qr.getCount());
                    for(LogObj logObj : qr.getList()){
                        System.out.println(logObj.getLog());
                    }
                }else{
                    System.out.println("Args Error:\"search\" \"logtype\"  \"first\" \"max\" \"query query query\" \"starttime endtime\" \"source\"");
                }

            }
        }
    }

}
