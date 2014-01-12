package datamgmt

import "../tools/convert"

/*
该方法主要处理日志接收过来后的一些处理，比如日志流的处理，日志的分析，日志的存储
*/
var const_magic = []byte{0xab, 0xcd, 0xab, 0xcd}

const (
	LOG_HEADER_LENGTH int = 16
	PKG_HEADER_LENGTH int = 40
	MAGIC_HEADER      int = 0
)

type Syslog interface {
	NewLog([]byte)
}

type LogInterface interface {
	NewLog([]byte)
	LogWrite()
	FileFormat(int, int, int, int, int, int, PkgHeader, LogHeader) string
}

/*

 转换，把字符留的日志，转换为结构化的log对象，后续处理方便
*/

//syslog 日志头一些信息
type LogObj struct {
	Mac     []byte //source mac and dst mac and type(ip) uint32
	Ip      []byte //source ip ,dst ip and version     uint32
	Port    []byte //source port ,dst port and logData length  uint32 
	Length  int    //source port ,dst port and logData length
	LogData []byte //具体日志的内容 
}

//dut 发送二进制头 一些基本信息，
type PkgHeader struct {
	Magic   uint32  //packet header uint32
	Version int     //log 日志版本（第一个版本没有长度，第二个版本有日志长度）
	Count   int     //log 日志的数量
    Length  int     //log 日志的长度，通过len(logstream)获取的
	Host    string //Host 32 byte  length=32
}

//二进制日志的头，包括日志id
type LogHeader struct {
	Facility  uint8
	Servirity uint8
	Year      int
	Month     int
	Day       int
	TmHour    int
	TmMin     int
	TmSec     int
	Padding   byte //3 byte
	Length    int
	Logid     uint32
}

func (logObj *LogObj) NewLog(logstream []byte) {
	logObj.Mac = logstream[0:14]                       //source mac and dst mac and type(ip)
	logObj.Ip = logstream[14:34]                       //source ip ,dst ip and version
	logObj.Port = logstream[34:42]                     //source port ,dst port and logData length
	logObj.Length = convert.BinToInt(logstream[38:40]) //发送日志，会根据该长度发送
	logObj.LogData = logstream[42:(34 + logObj.Length)]
}

//LogObj.LogData
func (pkgHeader *PkgHeader) NewLog(logstream []byte) {
	pkgHeader.Magic = convert.BinToUint32(logstream[0:4])
	pkgHeader.Version = convert.BinToInt(logstream[4:6])
	pkgHeader.Count = convert.BinToInt(logstream[6:8])
    pkgHeader.Length = len(logstream)
	pkgHeader.Host = convert.BinToString(logstream[8:40])
}

//LogObj.LogData
func (logHeader *LogHeader) NewLog(logstream []byte) {
	logHeader.Facility = logstream[0]
	logHeader.Servirity = logstream[1]
	logHeader.Year = convert.BinToInt(logstream[2:4])
	logHeader.Month = int(logstream[4])
	logHeader.Day = int(logstream[5])
	logHeader.TmHour = int(logstream[6])
	logHeader.TmMin = int(logstream[7])
	logHeader.TmSec = int(logstream[8])
	logHeader.Padding = logstream[9]
	logHeader.Length = convert.BinToInt(logstream[10:12])
	logHeader.Logid = convert.BinToUint32(logstream[12:16])
}

//添加新的日志类型，
/*
1. 创建data_typelog.go的文件，实现LogObj接口  （其中还包括，该日志对应的id是多少)
2. data_mgmt.go 中添加相应日志的id判断并调用新类型的对象
3. 配置文件写入相应的启动配置，和日志的名称
*/
