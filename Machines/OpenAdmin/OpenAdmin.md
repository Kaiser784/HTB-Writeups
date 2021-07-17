#portforwarding
# OpenAdmin
### Machine Maker(s): [](https://app.hackthebox.eu/users/82600)

#### Info :
``` 
OS          : Linux
Difficulty  : Easy
Points      : 0
Release     : 
IP          : 10.10.10.171
```
---
### Enumeration

As always fire-up nmap
```bash
nmap -sC -sV -A -T4 -p- 10.10.10.171 --open
```

The scan shows an ssh port and a http page. 

Let's directory bust it using wfuzz.

```bash
wfuzz -u "http://10.10.10.171/FUZZ" -w /usr/share/wordlists/dirb/common.txt --hc 404
```

![[Pasted image 20210717182659.png]]

/artwork and /music redirect to different pages.
Clicking on the login button in /music we get some dashboard page.
![[Pasted image 20210717182935.png]]

### Foothold
It is OpenNetAdmin and shows us the version too. Googling it we find an [exploit](https://github.com/amriunix/ona-rce).

We got a shell but it's not changing directories or giving full return queries. So let's try to get a reverse nc shell as we checked netcat is already present in the system by `which nc` .

We find this in the `/var/www` directory.
![[Pasted image 20210717184829.png]]

Navigating to that directory we don't find any thing that interesting so we check for config files for potential database creds. We fidn the in `/var/www/ona/local/config`

![[Pasted image 20210717185133.png]]

This password can probably used for one of the existing users on the system.

To switch the user you must first convert it to a interactive user shell.

```bash
python3 -c 'import pty;pty.spawn("/bin/bash")'
```

But we find no user flag so we have to horizontally escalate to joanna.

We couldn't access the internal directory before let's try that.

We find files of a website which we didn't find before in enum guessing by the name of the directory it must be a site run on the internal network.

To get into the internal network we have to do a port forwarding into our machine.
To check which ports are open and listening.

```bash
netstat -anlp | grep LISTEN
```

#### Port Forwarding 

From TryHackMe

If you had SSH access to a server (172.16.0.50) with a webserver running internally on port 80 (i.e. only accessible to the server itself on 127.0.0.1:80), how would you forward it to port 8000 on your attacking machine? Assume the username is "user", and background the shell.

```bash
ssh -L 8000:127.0.0.1:80 user@172.16.0.50 -fN
```

#### Back to HTB Target

```
ssh -L 52846:127.0.0.1:52846 jimmy@10.10.10.171 -fN
```

After doing this you can open up your browser and go to `127.0.0.1:52846` and find that website.

From the directory we found earlier there is a get parameter it is taking in and executing so we can just check if it is feeding its input into terminal.

![[Pasted image 20210717205217.png]]

Now we can just put a netcat reverse shell as we already know nc is running on the system.

For some god fucking awful reason the sudo is not working on the obtained reverse shell even after getting a tty shell. So I decided to take the id_rsa and try to ssh into it.

We try to login using with the key directly but it's still asking us the passphrase for id_rsa so we have to bring out john to crack it.

```bash
python3 /usr/share/john/ssh2john.py id_rsa > pass_hash
john --wordlist=/usr/share/wordlists/rockyou.txt pass_hash
```

We get the passphrase as `bloodninjas` 

Don't forget to change the permissions of id_rsa to read only so that ssh can accept it.

```bash
chmod 400 id_rsa
```

### Privilege Escalation

First we check the sudo permissions of the user.

We can run `/bin/nano /opt/priv` as sudo, time for [GTFO](https://gtfobins.github.io/)

```
sudo /bin/nano /opt/priv
^R^X
reset; sh 1>&0 2>&0
```

The above instructions `^R^X` mean ctrl+r and ctrl+x after entering the nano file and enter the the last line.

We are root!