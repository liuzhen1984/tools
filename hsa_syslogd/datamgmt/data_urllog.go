package datamgmt

import (
	"../tools/config"
	"../tools/convert"
	"fmt"
)

const (
	URL_LOGID [uint32]int =
	{ 
		0x4438360d:1,
		0x4438360f:1,
	}
)

//url日志内容
/*
unsigned int sip;
	unsigned int snat_ip;
	unsigned int dip;
	unsigned int dnat_ip;
	
	unsigned short sport;
	unsigned short snat_port;
	unsigned short dport;
	unsigned short dnat_port;

	short length;
	unsigned short res;

	* data format as:
	* user\tvrname\turl\tcategory\tmethod\taction\treson\ta3server\tmac
	char data[0];
 */
type URLLogObj struct {
	LogHeader LogHeader

	SrcIp    []byte //uint32
	SrcNatIp []byte //uint32
	DstIp    []byte //uint32
	DstNatIp []byte //uint32

	SrcPort    int
	SrcNatPort int
	DstPort    int
	DstNatPort int
	Length     int
	Res        uint16
	/**
	* data format as:
	* user\tvrname\turl\tcategory\tmethod\taction\treson\ta3server\tmac
	 */
	Data []byte //length = Length
}

//LogObj.LogData
func (urlLog *URLLogObj) NewLog(logstream []byte) {
	urlLog.LogHeader.NewLog(logstream)
	urlLog.SrcIp = logstream[16:20]
	urlLog.SrcNatIp = logstream[20:24]
	urlLog.DstIp = logstream[24:28]
	urlLog.DstNatIp = logstream[28:32]
	urlLog.SrcPort = convert.BinToInt(logstream[32:34])
	urlLog.SrcNatPort = convert.BinToInt(logstream[34:36])
	urlLog.DstPort = convert.BinToInt(logstream[36:38])
	urlLog.DstNatPort = convert.BinToInt(logstream[38:40])
	urlLog.Length = convert.BinToInt(logstream[40:42])
	urlLog.Res = convert.BinToUint16(logstream[42:44])
	urlLog.Data = logstream[44:(44 + urlLog.Length)]
}

func (urlLog *URLLogObj) LogWrite() {
	config.LogTypeBuffMap[config.URL_NAME] <- urlLog
	//写入到处理url日志的缓存
}

func (urlLog *URLLogObj) FileFormat(year, month, day, hour, min, sec int, pkgHeader PkgHeader, logHeader LogHeader) string {
	return fmt.Sprintf("Host:%s,\tReceiveTime:%d-%d-%d %d:%d:%d,\tCategory:NBC,\tLevel:info,\tRealTime:%d-%d-%d %d:%d:%d,\tSrcIp:%v,\tSrcPort:%d,\tSrcNatIp:%v,\tSrcNatPort:%d,\tDstIp:%v,\tDstPort%d,\tDstNatIp:%v,\tDstNatPort:%d,\tDesc:%s\n",
		string(pkgHeader.Host),year, month, day, hour, min, sec,
		logHeader.Year,logHeader.Month,logHeader.Day,logHeader.TmHour,logHeader.TmMin,logHeader.TmSec,
		urlLog.SrcIp, urlLog.SrcPort, urlLog.SrcNatIp, urlLog.SrcNatPort,
		urlLog.DstIp, urlLog.DstPort, urlLog.DstNatIp, urlLog.DstNatPort,
		string(urlLog.Data))
}
