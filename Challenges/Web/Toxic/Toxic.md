# Toxic


### Challenge Author(s): [NoobLegacyy](https://app.hackthebox.eu/users/156190)

### Description: 
   ```
   Humanity has exploited our allies, the dart frogs, for far too long, take back the freedom of our lovely poisonous friends. Malicious input is out of the question when dart frogs meet industrialisation. 🐸
   ```
### Difficulty: `Easy`
---
# Challenge

Opening the given link we don't find anything much interesting. We look at index.php in the downloadable files given and see that the session cookie is `/www/index.html` in base64.

Let's use cyberchef to see if it is correct.
Decoded
```
O:9:"PageModel":1:{s:4:"file";s:15:"/www/index.html";}
```
Yup it works I tried to read server logs and it worked.

change the file entry to `/var/log/nginx/access.log` encode it, paste it in cookies and refresh and we get the log file

The log file is storing 
```
host
origin 
user-agent
request
```

So maybe we can inject php in one of them and send the request and as it is displaying in the browser it may execute the php code and get what we want.

host, origin and request are out of question to change so we have to inject in user-agent.
I used user-agent switcher in firefox to change it and used the same `access.log` cookie.

user-agent payload
```php
<?php system('ls /');?>
```


![[Pasted image 20210818132158.png]]

Holy shit it actually worked, So we just cat the flag from the php injection in user-agent.