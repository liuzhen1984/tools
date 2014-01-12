package datamgmt

import (
	"fmt"
	"net"
)

func SendData() {
	socket, err := net.DialUDP("udp4", nil, &net.UDPAddr{
		IP:   net.IPv4(0, 0, 0, 0),
		Port: 514,
	})
	if err != nil {
		panic("连接失败: " + fmt.Sprint(err))
		return
	}
	senddata := []byte("hello123456")
	_, err = socket.Write(senddata)
	if err != nil {
		panic("发送数据失败:" + fmt.Sprint(err))
		return
	}
}
