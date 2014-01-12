package datamgmt

import (
	"../tools/config"
	"../tools/convert"
	"fmt"
)

var IM_LOGID  = []uint32 { 0x464c7619,0x464C761A }

//im日志内容
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
	* user\tvrname\ttype\tid\taction\ta3server\tmac

	char data[0];
 */
type IMLogObj struct {
	LogHeader LogHeader

	SrcIp    string //uint32
	SrcNatIp string //uint32
	DstIp    string //uint32
	DstNatIp string //uint32

	SrcPort    int
	SrcNatPort int
	DstPort    int
	DstNatPort int
	Length     int
	Res        uint16
	/**
	* data format as:
	* user\tvrname\ttype\tid\taction\ta3server\tmac
	 */
	Data string //length = Length
}

//LogObj.LogData
func (imLog *IMLogObj) NewLog(logstream []byte) {
	imLog.LogHeader.NewLog(logstream)
	imLog.SrcIp = convert.BytesToIp(logstream[16:20])
	imLog.SrcNatIp = convert.BytesToIp(logstream[20:24])
	imLog.DstIp = convert.BytesToIp(logstream[24:28])
	imLog.DstNatIp = convert.BytesToIp(logstream[28:32])


	imLog.SrcPort = convert.BinToInt(logstream[32:34])
	imLog.SrcNatPort = convert.BinToInt(logstream[34:36])
	imLog.DstPort = convert.BinToInt(logstream[36:38])
	imLog.DstNatPort = convert.BinToInt(logstream[38:40])
	imLog.Length = convert.BinToInt(logstream[40:42])
	imLog.Res = convert.BinToUint16(logstream[42:44])
	imLog.Data = string(logstream[44:imLog.LogHeader.Length])
}

func (imLog *IMLogObj) LogWrite() {
	config.LogTypeBuffMap[config.IM_NAME] <- imLog
	//写入到处理im日志的缓存
}

func (imLog *IMLogObj) FileFormat(year, month, day, hour, min, sec int, pkgHeader PkgHeader, logHeader LogHeader) string {
	return fmt.Sprintf("[Binary Log IM]-> Host:%s,\tReceiveTime:%d-%d-%d %d:%d:%d,\tTYPE:NBC,\tLevel:info,\tRealTime:%d-%d-%d %d:%d:%d,\tSrcIp:%s,\tSrcPort:%d,\tSrcNatIp:%s,\tSrcNatPort:%d,\tDstIp:%s,\tDstPort%d,\tDstNatIp:%s,\tDstNatPort:%d,\tDesc:%s\n",
		pkgHeader.Host,year, month, day, hour, min, sec,
		logHeader.Year,logHeader.Month,logHeader.Day,logHeader.TmHour,logHeader.TmMin,logHeader.TmSec,
		imLog.SrcIp, imLog.SrcPort, imLog.SrcNatIp, imLog.SrcNatPort,
		imLog.DstIp, imLog.DstPort, imLog.DstNatIp, imLog.DstNatPort,
		imLog.Data)
}
