package convert

import (
	"fmt"
	"strconv"
	"strings"
)

func BytesToIp(bip []byte) string {
	if len(bip) != 4 {
		panic(fmt.Sprintf("%v length error!\n", bip))
	}
	return fmt.Sprintf("%d.%d.%d.%d", bip[0], bip[1], bip[2], bip[3])
}

func IpToBytes(ip string) (bip [4]byte) {
	ip = strings.TrimSpace(ip)
	if strings.EqualFold("", ip) {
		bip[0] = byte(0)
		bip[1] = byte(0)
		bip[2] = byte(0)
		bip[3] = byte(0)
		return
	}
	sip := strings.Split(ip, ".")
	if len(sip) != 4 {
		panic(fmt.Sprintf("%s format error!\n", ip))
	}
	ips, _ := strconv.Atoi(sip[0])
	bip[0] = byte(ips)
	ips, _ = strconv.Atoi(sip[1])
	bip[1] = byte(ips)
	ips, _ = strconv.Atoi(sip[2])
	bip[2] = byte(ips)
	ips, _ = strconv.Atoi(sip[3])
	bip[3] = byte(ips)
	return

}
