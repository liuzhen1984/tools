package util

import (
	"strings"
)

func ContainsStrSlice(find string, strSlice []string) (index int) {
	for p, v := range strSlice {
		if strings.EqualFold(find, v) {
			index = p
			return
		}
	}
	index = -1
	return
}

func ContainsIntSlice(find int, strSlice []int) (index int) {
	for p, v := range strSlice {
		if find == v {
			index = p
			return
		}
	}
	index = -1
	return
}
