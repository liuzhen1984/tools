package datamgmt

import (
	"../tools/config"
	"../tools/convert"
	"fmt"
)

const (
	IM_LOGID uint32 = 0x464c7619
)

//im日志内容
type IMLogObj struct {
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
	Data []byte //length = Length
}

//LogObj.LogData
func (imLog *IMLogObj) NewLog(logstream []byte) {
	imLog.LogHeader.NewLog(logstream)
	imLog.SrcIp = logstream[16:20]
	imLog.SrcNatIp = logstream[20:24]
	imLog.DstIp = logstream[24:28]
	imLog.DstNatIp = logstream[28:32]
	imLog.SrcPort = convert.BinToInt(logstream[32:34])
	imLog.SrcNatPort = convert.BinToInt(logstream[34:36])
	imLog.DstPort = convert.BinToInt(logstream[36:38])
	imLog.DstNatPort = convert.BinToInt(logstream[38:40])
	imLog.Length = convert.BinToInt(logstream[40:42])
	imLog.Res = convert.BinToUint16(logstream[42:44])
	imLog.Data = logstream[44:(44 + imLog.Length)]
}

func (imLog *IMLogObj) LogWrite() {
	config.LogTypeBuffMap[config.IM_NAME] <- imLog
	//写入到处理im日志的缓存
}

func (imLog *IMLogObj) FileFormat(year, month, day, hour, min, sec int, pkgHeader PkgHeader, logHeader LogHeader) string {
	return fmt.Sprintf("Host:%s,\tReceiveTime:%d-%d-%d %d:%d:%d,\tCategory:NBC,\tLevel:info,\tRealTime:%d-%d-%d %d:%d:%d,\tSrcIp:%v,\tSrcPort:%d,\tSrcNatIp:%v,\tSrcNatPort:%d,\tDstIp:%v,\tDstPort%d,\tDstNatIp:%v,\tDstNatPort:%d,\tDesc:%s\n",
		string(pkgHeader.Host),year, month, day, hour, min, sec,
		logHeader.Year,logHeader.Month,logHeader.Day,logHeader.TmHour,logHeader.TmMin,logHeader.TmSec,
		imLog.SrcIp, imLog.SrcPort, imLog.SrcNatIp, imLog.SrcNatPort,
		imLog.DstIp, imLog.DstPort, imLog.DstNatIp, imLog.DstNatPort,
		string(imLog.Data))
}
