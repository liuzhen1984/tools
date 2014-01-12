package datamgmt

import (
	"../tools/config"
	"../tools/convert"
	"fmt"
)

var NAT_LOGID = []uint32{ 0x46083606,0x46083607,0x46083615,0x46083616}

//日志内容
type NATLogObj struct {
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
	* user\tvrname\ttype\tid\taction
	 */
	Data string//length = Length
}

//LogObj.LogData
func (logObj *NATLogObj) NewLog(logstream []byte) {
	logObj.LogHeader.NewLog(logstream)
	logObj.SrcIp = convert.BytesToIp(logstream[16:20])
	logObj.SrcNatIp = convert.BytesToIp(logstream[20:24])
	logObj.DstIp = convert.BytesToIp(logstream[24:28])
	logObj.DstNatIp = convert.BytesToIp(logstream[28:32])
	logObj.SrcPort = convert.BinToInt(logstream[32:34])
	logObj.SrcNatPort = convert.BinToInt(logstream[34:36])
	logObj.DstPort = convert.BinToInt(logstream[36:38])
	logObj.DstNatPort = convert.BinToInt(logstream[38:40])
	logObj.Length = convert.BinToInt(logstream[40:42])
	logObj.Res = convert.BinToUint16(logstream[42:44])
	logObj.Data = string(logstream[44:(logObj.LogHeader.Length)])
}

func (logObj *NATLogObj) LogWrite() {
	config.LogTypeBuffMap[config.NAT_NAME] <- logObj
	//写入到处理日志的缓存
}

func (logObj *NATLogObj) FileFormat(year, month, day, hour, min, sec int, pkgHeader PkgHeader, logHeader LogHeader) string {
	return fmt.Sprintf("[Binary Log SNAT] -> Host:%s,\tReceiveTime:%d-%d-%d %d:%d:%d,\tCategory:NBC,\tLevel:info,\tRealTime:%d-%d-%d %d:%d:%d,\tSrcIp:%s,\tSrcPort:%d,\tSrcNatIp:%s,\tSrcNatPort:%d,\tDstIp:%s,\tDstPort%d,\tDstNatIp:%s,\tDstNatPort:%d,\tDesc:%s\n",
		pkgHeader.Host,year, month, day, hour, min, sec,
		logHeader.Year,logHeader.Month,logHeader.Day,logHeader.TmHour,logHeader.TmMin,logHeader.TmSec,
		logObj.SrcIp, logObj.SrcPort, logObj.SrcNatIp, logObj.SrcNatPort,
		logObj.DstIp, logObj.DstPort, logObj.DstNatIp, logObj.DstNatPort,
		logObj.Data)
}
