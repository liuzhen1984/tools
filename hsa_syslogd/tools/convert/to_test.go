package convert

import (
	"fmt"
	"testing"
)

func TestToIp(t *testing.T) {
	bip := []byte{172, 10, 3, 0}
	fmt.Println("bip=[172.10.3.0] , BytesToIp=", BytesToIp(bip))
	sip := " 172.10.3.0 "
	fmt.Println("sip=172.10.3.0 , IpToBytes=", IpToBytes(sip))
}
