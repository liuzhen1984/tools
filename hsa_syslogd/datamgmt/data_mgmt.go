package datamgmt

import (
	"../tools/config" //读取配置文件
	"bytes"
	"fmt"
	"os"
	"time"
)

const (
	log_name_format string = "%d_%d_%d_%d_%d"
)


var pkgHeader = new(PkgHeader)

var logHeader = new(LogHeader)
var imLogObj = new(IMLogObj)
var natLogObj = new(NATLogObj)
var urlLogObj = new(URLLogObj)
var webpostLogObj = new(WEBPOSTLogObj)

//多线程调用
func DataAnalyse(logdata []byte) {


	fmt.Printf("logdata %x\n",logdata)
		//2.当前默认都为本机处理
		if bytes.Equal(logdata[MAGIC_HEADER:(MAGIC_HEADER + 4)], const_magic) {
			pkgHeader.NewLog(logdata)
			var log_count int = PKG_HEADER_LENGTH //纪录读到哪个byte了
			LOGF:
			for (log_count + LOG_HEADER_LENGTH) < pkgHeader.Length {
				//第一个log
				logHeader.NewLog(logdata[log_count : log_count+LOG_HEADER_LENGTH])
									fmt.Printf("logid = %x\n",logHeader.Logid)

				if logHeader.Logid == IM_LOGID {
		                        writeFormat(imLogObj,logdata[log_count:])
				} else if logHeader.Logid == NAT_LOGID_1 || logHeader.Logid == NAT_LOGID_0 {

		                        writeFormat(natLogObj,logdata[log_count:])
				} else if logHeader.Logid == URL_LOGID {
		                        writeFormat(urlLogObj,logdata[log_count:])
				} else if logHeader.Logid == WEBPOST_LOGID {
		                        writeFormat(webpostLogObj,logdata[log_count:])
				} else {
				}
				if pkgHeader.Version != 1 && logHeader.Length != 0 {
					log_count = log_count + logHeader.Length
				} else {
					break LOGF
				}
			}

		} else {
			fmt.Println("日志头错误")
		}
	        
}

func writeFormat(logobj LogInterface,logdata []byte){
    logobj.NewLog(logdata)
    logobj.LogWrite()
}





//每个类型处理后的obj值写入相应类型的buff，由该方法，单独处理，每个类型启用一个方法来多线程处理，写入文件，然后入到数据库中
//一分钟之内的都写入一个文件中
func BuffToFile(logType string) {
	ptime := time.Now()
	year, month, day := ptime.Date()
	hour, min, sec := ptime.Clock()
	stime := fmt.Sprintf(log_name_format, year, month, day, hour, min)

    curFile, err := os.OpenFile("./"+logType+"_"+stime+".log", os.O_RDWR|os.O_CREATE|os.O_APPEND, 0666)
    defer curFile.Close()

	if err != nil {
		panic("Open Log File Faild :" + fmt.Sprint(err))
	}
BTF:
	for {
		//_, ok := <-config.LogTypeBuffMap[logType]
		logObj, ok := <-config.LogTypeBuffMap[logType]
		println(logObj)
		if !ok {
			break BTF
		}
		ctime := time.Now()
		if !ptime.Equal(ctime) {
			curFile.Sync()
			fInfo,_:=curFile.Stat()

			if fInfo.Size() > 100*1024*1024 {
				curFile.Close()
				year, month, day = ctime.Date()
				hour, min, sec = ctime.Clock()
				stime = fmt.Sprintf(log_name_format, year, month, day, hour, min)
			    curFile, _ = os.OpenFile("./"+logType+"_"+stime+".log", os.O_RDWR|os.O_CREATE|os.O_APPEND, 0666)

			}
			ptime = ctime
		}
		logStr := fileFormat(logObj, year, int(month), day, hour, min, sec)
		println(logStr)
		curFile.WriteString(logStr)
	}
}

func fileFormat(logObj interface{}, year, month, day, hour, min, sec int) string {
	return logObj.(LogInterface).FileFormat(year, month, day, hour, min, sec, *pkgHeader, *logHeader)
}
