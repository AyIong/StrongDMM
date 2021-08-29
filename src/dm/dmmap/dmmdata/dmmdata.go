package dmmdata

import (
	"fmt"
	"log"
	"os"
	"time"

	"sdmm/dm/dmmap/dmminstance"
	"sdmm/util"
)

type Key string

type DmmData struct {
	Filepath string

	IsTgm          bool
	IsWinLineBreak bool

	KeyLength        int
	MaxX, MaxY, MaxZ int

	Dictionary map[Key][]dmminstance.Instance
	Grid       map[util.Point]Key
}

func (d DmmData) String() string {
	return fmt.Sprintf(
		"Filepath: %s, IsTgm: %t, IsWinLineBreak: %t, KeyLength: %d, MaxX: %d, MaxY: %d, MaxZ: %d",
		d.Filepath, d.IsTgm, d.IsWinLineBreak, d.KeyLength, d.MaxX, d.MaxY, d.MaxZ)
}

func New(path string) (*DmmData, error) {
	file, err := os.Open(path)
	if err != nil {
		return nil, err
	}
	defer file.Close()

	start := time.Now()
	data, err := parse(file)
	elapsed := time.Since(start).Milliseconds()
	log.Printf("[dmmdata] data [%s] parsed in [%d] ms", path, elapsed)

	return data, err
}
