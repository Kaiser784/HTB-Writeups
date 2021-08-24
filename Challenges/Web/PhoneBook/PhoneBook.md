# PhoneBook


### Challenge Author(s): [vajkdry](https://app.hackthebox.eu/users/94857)

### Description: 
   ```
   Who is lucky enough to be included in the phonebook?
   ```
### Difficulty: `Easy`
---
# Challenge

Opening the given link we find a login page and we try some default creds and it shows authentication failed but if you look care fully at the URL there's a message parameter which is being displayed.

![[Pasted image 20210817215008.png]]

This is Reflected XSS. But there's not much you can exploit from here

I tried using normal SQLi but not much came out then I thought maybe if I just input `*`  it'll return all data and get authenticated surprisingly it worked.
This is not an SQLi but an LDAP.

After logging in there's not much there that you can find, all empty searches just output names and mails but no passwords or flags. So the Flag could actually be the admin password which is Reese bcoz she was the one mentioned in the login page.

The star `*` works as everything 

`H*` means the string starts with H and everything that starts with H from table will be inputted and if it is correct we will be logged in.

So we now have to write a script to test that, which I didn't and couldn't bcoz of my lack of incompetence and Python is useless Have to look more into Golang which is as fast as C and more readable.
For now made do with a script found online.

So we have to write a script which tests each Letter and outputs the full password.

We can pull the Headers and content length from the success requests from BurpSuite


