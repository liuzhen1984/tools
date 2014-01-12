package main

import (
	"./datamgmt"
	"./tools/config"
	"./tools/convert"
	"fmt"
	"net"
)

func syslogd() {
	listenIp := convert.IpToBytes("")
	socket, err := net.ListenUDP("udp4", &net.UDPAddr{
		IP:   net.IPv4(listenIp[0], listenIp[1], listenIp[2], listenIp[3]),
		Port: 514,
	})
	if err != nil {
		fmt.Println(fmt.Sprintf("监听端口514失败: %v",err))
		return
	}
	defer socket.Close()
	socket.SetReadBuffer(40 * 1024)

	for {
		//获取数据
		request := make([]byte, 1024)
		read, _, err := socket.ReadFromUDP(request)
		if err != nil {
			fmt.Println("读取数据失败！", err)
			continue

		}
		//验证数据长度是否小于最小长度，如果小于直接丢弃
		if read < 10 {
			fmt.Println("lenght < 10")
			continue
		}
		//多线程处理接收数据
		go datamgmt.DataAnalyse(request[0:read])
		
	}

}

func main() {
	for _, v := range config.HSACONFIG.Rtypes {
		config.LogTypeBuffMap[v] = make(chan interface{}, 8192)
	}

	for _, v := range config.HSACONFIG.Rtypes {
		go datamgmt.BuffToFile(v)
	}
	//启动syslogd主进程
	syslogd()

}
