package datamgmt

import (
	"../tools/config"
	"strconv"
	"fmt"
)

	var MODULE  = []string{"UNKNOW","SYS","MGMT","NET","FW","VPN","FLOW","AAA","IDP","RES"}
	var SUB_MODULE = map[string][]string{
		"SYS": {"UNKNOW","SM_SYS_IPC", "SM_SYS_FS","SM_SYS_MONITOR","SM_SYS_CHASSISD","SM_SYS_UPDATED","SM_SYS_EBYPASS","SM_SYS_SMSD","SM_SYS_ISSU","SM_SYS_CPU_BUSY"}, 
		"MGMT": {"UNKNOW","SM_MGMT_ADMIN","SM_MGMT_PARSER","SM_MGMT_WEBUI","SM_MGMT_SNMP","SM_MGMT_CONFIG","SM_MGMT_LOGIN","SM_MGMT_LICENSE","SM_MGMT_TELNET","SM_MGMT_SSH","SM_MGMT_HA","SM_MGMT_SMAGENT","SM_MGMT_LOGD","SM_MGMT_FTP","SM_MGMT_SSM_HA","SM_MGMT_NMAGENT","SM_MGMT_HEALTH"}, 
		"NET": {"UNKNOW","SM_NET_IF","SM_NET_ROUTE","SM_NET_RIP","SM_NET_OSPF","SM_NET_BGP","SM_NET_DHCP","SM_NET_DNS","SM_NET_NTP","SM_NET_ARP","SM_NET_PPPOE","SM_NET_PBR","SM_NET_DDNS","SM_NET_SWITCH","SM_NET_DHCPSN","SM_NET_RSTP","SM_NET_VR","SM_NET_DOT1X","SM_NET_ND","SM_NET_WEBAUTH","SM_NET_LACP"},
		"FW": {"UNKNOW","SM_FW_POLICY","SM_FW_NAT","SM_FW_AD","SM_FW_ALG","SM_FW_P2P","SM_FW_IM","SM_FW_IF","SM_FW_SERVICE","SM_FW_TRAFFIC","SM_FW_HA","SM_FW_IPSEC","SM_FW_QOS","SM_FW_AD_ARPSPOOFING","SM_FW_HTTP","SM_FW_CF","SM_FW_AV","SM_FW_HOST_BLACKLIST","SM_FW_STAT","SM_FW_NBC","SM_FW_AD_NDSPOOFING","SM_FW_MEM","SM_FW_PKTDUMP","SM_FW_RES","SM_FW_DP_AGENT","SM_FW_SSLPROXY","SM_FW_WEB_REDIRECT"}, 
		"VPN": {"UNKNOW","SM_VPN_IPSEC","SM_VPN_IKE","SM_VPN_L2TP","SM_VPN_GRE","SM_VPN_SSL"}, 
		"FLOW": {"UNKNOW","SM_FW_POLICY","SM_FW_NAT","SM_FW_AD","SM_FW_ALG","SM_FW_P2P","SM_FW_IM","SM_FW_IF","SM_FW_SERVICE","SM_FW_TRAFFIC","SM_FW_HA","SM_FW_IPSEC","SM_FW_QOS","SM_FW_AD_ARPSPOOFING","SM_FW_HTTP","SM_FW_CF","SM_FW_AV","SM_FW_HOST_BLACKLIST","SM_FW_STAT","SM_FW_NBC","SM_FW_AD_NDSPOOFING","SM_FW_MEM","SM_FW_PKTDUMP","SM_FW_RES","SM_FW_DP_AGENT","SM_FW_SSLPROXY","SM_FW_WEB_REDIRECT"}, 
		"AAA": {"UNKNOW","SM_AAA_AAA","SM_AAA_PKI"}, 
		"IDP": {"UNKNOW","DECODER","DETECT_ENGINE"}, 
		"RES": {"UNKNOW","SM_RES_PKI","SM_RES_APPSIG"}, 
	}

	var TYPE  = []string{"Event","Alarm","Config","Traffic","Debug","Security","Network","NBC","IPS"}
	var SEVERITY = []string{"Emerg","Alert","Critical","Error","Warning","Notice","Info","Debug"}

//日志内容
type TXTLogObj struct {
	Module string;
	SubModule string;
	Type string;
	Severity string;
	Index uint64;
	Data string; //length = Length
}

//LogObj.LogData
func (logObj *TXTLogObj) NewLog(logstream []byte) {
	id:=string(logstream[25:32])
	logObj.Module,logObj.SubModule,logObj.Type,logObj.Severity,logObj.Index=ParseLogid(id)
    logObj.Data = string(logstream[5:])
}

func (logObj *TXTLogObj) LogWrite() {
	config.LogTypeBuffMap[config.TXTLOG_NAME] <- logObj
	//写入到处理日志的缓存
}

func (logObj *TXTLogObj) FileFormat(year, month, day, hour, min, sec int, pkgHeader PkgHeader, logHeader LogHeader) string {
	return fmt.Sprintf("Module:%s,\tSubModule:%s,\tIndex:%d,\tReceiveTime:%d-%d-%d %d:%d:%d,\tCategory:%s,\tLevel:%s,\tLogDesc:%s\n",
		logObj.Module,logObj.SubModule,logObj.Index,year, month, day, hour, min, sec,
		logObj.Type,logObj.Severity,string(logObj.Data))
}


func ParseLogid(id string) (string,string,string,string,uint64){
	logid,err:=strconv.ParseUint("0x"+id,0,0)
	if err==nil{
	    return MODULE[(logid>>24)&0x03F],SUB_MODULE[MODULE[(logid>>24)&0x03F]][(logid>>18)&0x03F],TYPE[(logid>>12)&0x03F],SEVERITY[(logid>>8)&0x03F],logid&0x0FF
	}else{
		return "","","","",0
	}
}
