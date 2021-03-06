package config

var LogTypeBuffMap = make(map[string]chan interface{})

/*
读取配置文件，把xml转换为HSA_Config对象，供给其他程序进行使用。
启动时初始化一次，其他的则直接读取
*/

var LOGTYPES = []string {"IM","URL","NAT","STONE_TXT"}
var SAVE_PATH string

const (
	IM_NAME      string = "IM"
	URL_NAME     string = "URL"
	NAT_NAME     string = "NAT"
	//NAT444_NAME  string = "NAT444"
	//WEBPOST_NAME string = "WEBPOST"
	TXTLOG_NAME  string = "STONE_TXT"
)