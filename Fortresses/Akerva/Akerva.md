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

## Now You See Me
