# USB Rippers


### Challenge Author(s): [snovvcrash](https://app.hackthebox.eu/users/51037)

### Description: 
   ```
   There is a sysadmin, who has been dumping all the USB events on his Linux host all the year... Recently, some bad guys managed to steal some data from his machine when they broke into the office. Can you help him to put a tail on the intruders? Note: once you find it, "crack" it.
   ```
### Difficulty: `Easy`
---
# Challenge

So `auth.json` has all the authorized devices and the `syslog` has all the devices logged into the system. We have to find the one which is not authorized.

The `SerialNumber` is unique to each device so it maybe the the numbers present in the auth file. So we'll have to compare the numbers present in logs to auth and print out ones which are not in auth.

So turns out I actually didn't need to code it but there's already an app called `usbrip` which stores logs and auth files and it can detect these "violation events" where unauthorized devices are connected.

```bash
pip3 install usbrip
```

```bash
usbrip events violations auth.json -f syslog
```

This will give us the violation and according to the description we have to "crack it". So using boxentriq it showed hex but nothing came out so I just copy pasted the hash into google and it showed it was an md5 hash along with its decoded string.

