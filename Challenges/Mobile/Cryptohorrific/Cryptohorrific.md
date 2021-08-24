# Cryptohorrific


### Challenge Author(s): [bsecure](https://app.hackthebox.eu/users/25695)

### Description: 
   ```
   Secure coding is the keystone of the application security!
   ```
### Difficulty: `Easy`
---
# Challenge

Using `file` to check the contents of the downloadable files, we can see that it is kind of an iOS app.

There are `.plist` files but they are not properly formatted to read so we convert them to `.xml` to make it more readable.

```bash
plistutil -i challenge.plist -o challenge.xml
```

Looking at the resulted XML file we have an encoded flag string and potential keys.

As the description had coding and we also had an executable file, I opened the hackthebox exec in ida.

Opening it We find different variables loading their addresses like the flag, challenge and 2 random strings possible keys like the ones from the xml file.

There was also a function called CCCrypt googling it we find it's uses AES encryption.
So I thought of using an online decryptor and maybe one of the 4 potential keys would work.

potential keys
```
123
HackTheBoxIsCool
!A%D*G-KaPdSgVkY
QfTjWnZq4t7w!z%C
```

and it worked
![[Pasted image 20210821115056.png]]