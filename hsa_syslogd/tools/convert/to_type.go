package convert

import (
	"bytes"
	"encoding/binary"
	"fmt"
)

func BinToUint16(bin []byte) (pi uint16) {
	buf := bytes.NewBuffer(bin)
	err := binary.Read(buf, binary.BigEndian, &pi)
	if err != nil {
		panic("[BinToUint16] binary.Read failed:" + fmt.Sprint(err))
	}
	return
}

func BinToUint32(bin []byte) (pi uint32) {
	buf := bytes.NewBuffer(bin)
	err := binary.Read(buf, binary.BigEndian, &pi)
	if err != nil {
		panic("[BinToUint32] binary.Read failed:" + fmt.Sprint(err))
	}
	return
}

func BinToInt(bin []byte) (pi int) {
	switch len(bin) {
	case 1:
		pi = int(bin[0])
	case 2:
		pi = int(BinToInt16(bin))
	case 4:
		pi = int(BinToUint32(bin))
	default:
		pi = -1
	}
	return
}

func BinToInt16(bin []byte) (pi int16) {
	buf := bytes.NewBuffer(bin)
	err := binary.Read(buf, binary.BigEndian, &pi)
	if err != nil {
		panic("[BinToInt16] binary.Read failed:" + fmt.Sprint(err))
	}
	return
}

func BinToInt32(bin []byte) (pi int32) {
	buf := bytes.NewBuffer(bin)
	err := binary.Read(buf, binary.BigEndian, &pi)
	if err != nil {
		panic("[BinToInt32] binary.Read failed:" + fmt.Sprint(err))
	}
	return
}

func BinToString(bin []byte) (string) {
	var lenb = 0
	for i:=0;i<len(bin);i++{
		if bin[i] == 0 {
			lenb = i
			break
		}
	}
	buf := bytes.NewBuffer(bin[0:lenb])
	return buf.String()
}
