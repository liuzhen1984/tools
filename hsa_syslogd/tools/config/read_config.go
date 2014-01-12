package config

import (
	"fmt"
)
var LogTypeBuffMap = make(map[string]chan interface{})

/*
读取配置文件，把xml转换为HSA_Config对象，供给其他程序进行使用。
启动时初始化一次，其他的则直接读取
*/

const (
	IM_NAME      string = "IM"
	URL_NAME     string = "URL"
	NAT_NAME     string = "NAT"
	NAT444_NAME     string = "NAT444"
	WEBPOST_NAME string = "WEBPOST"
)




type Logs struct {
	
	Rtypes []string
}




var HSACONFIG = new(Logs)

func ReadConfig() {
		
		HSACONFIG.Rtypes = append(HSACONFIG.Rtypes, IM_NAME)
		HSACONFIG.Rtypes = append(HSACONFIG.Rtypes, NAT_NAME)
		HSACONFIG.Rtypes = append(HSACONFIG.Rtypes, URL_NAME)
		HSACONFIG.Rtypes = append(HSACONFIG.Rtypes, NAT444_NAME)
		HSACONFIG.Rtypes = append(HSACONFIG.Rtypes, WEBPOST_NAME)

	fmt.Println(HSACONFIG)

}
