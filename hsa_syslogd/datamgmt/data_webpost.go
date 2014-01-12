package datamgmt

import (
	"../tools/config"
	"../tools/convert"
	"fmt"
)

const (
	WEBPOST_LOGID uint32 = 0x4438360e
)

//日志内容
type WEBPOSTLogObj struct {
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
	* user\tvrname\ttype\tid\taction
	 */
	Data string //length = Length
}

//LogObj.LogData
func (logObj *WEBPOSTLogObj) NewLog(logstream []byte) {
	logObj.LogHeader.NewLog(logstream)
	logObj.SrcIp = logstream[16:20]
	logObj.SrcNatIp = logstream[20:24]
	logObj.DstIp = logstream[24:28]
	logObj.DstNatIp = logstream[28:32]
	logObj.SrcPort = convert.BinToInt(logstream[32:34])
	logObj.SrcNatPort = convert.BinToInt(logstream[34:36])
	logObj.DstPort = convert.BinToInt(logstream[36:38])
	logObj.DstNatPort = convert.BinToInt(logstream[38:40])
	logObj.Length = convert.BinToInt(logstream[40:42])
	logObj.Res = convert.BinToUint16(logstream[42:44])
	logObj.Data = string(logstream[44:logObj.LogHeader.Length])
}

func (logObj *WEBPOSTLogObj) LogWrite() {
	config.LogTypeBuffMap[config.WEBPOST_NAME] <- logObj
	//写入到处理日志的缓存
}

func (logObj *WEBPOSTLogObj) FileFormat(year, month, day, hour, min, sec int, pkgHeader PkgHeader, logHeader LogHeader) string {
	return fmt.Sprintf("[Binary Log WEBPOST] ->Host:%s,\tReceiveTime:%d-%d-%d %d:%d:%d,\tCategory:NBC,\tLevel:info,\tRealTime:%d-%d-%d %d:%d:%d,\tSrcIp:%v,\tSrcPort:%d,\tSrcNatIp:%v,\tSrcNatPort:%d,\tDstIp:%v,\tDstPort%d,\tDstNatIp:%v,\tDstNatPort:%d,\tDesc:%s\n",
		string(pkgHeader.Host),year, month, day, hour, min, sec,
		logHeader.Year,logHeader.Month,logHeader.Day,logHeader.TmHour,logHeader.TmMin,logHeader.TmSec,
		logObj.SrcIp, logObj.SrcPort, logObj.SrcNatIp, logObj.SrcNatPort,
		logObj.DstIp, logObj.DstPort, logObj.DstNatIp, logObj.DstNatPort,
		logObj.Data)
}
