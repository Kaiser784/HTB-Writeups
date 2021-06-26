# Easy Phish
#osint #cryptocurrency 

### Challenge Author(s): [Sm4rtK1dz](https://app.hackthebox.eu/users/276784)

### Description: 
    Frank Vitalik is a hustler, can you figure out where the money flows?
### Difficulty: `Easy`

---
# Challenge

The description tells us about a man named **Frank Vitalik**, let's do a plain google search about him in social media sites. We'll use google dorks for this and see if anything relevant to money comes up.

```
"Frank Vitalik" site:twitter.com OR site:facebook.com OR site:instagram.com OR site:reddit.com OR site:tumblr.com
```

Use the above snippet in the google search. We find a reddit post about him talking about cryptocurrency with an Ethereum address. Let's look at other posts made by **Frank Vitalik**. There is one other post with a link, pivoting to that we find another Ethereum address with a comment saying the coins on `ropsten net`.

Searching about it we find that a website `ropsten.etherscan.io` where we can explore different addresses so we search for the address we found on the site.

Anyone can send a transaction to a crypto wallet so most of the 123 transactions are noise, let's check the outgoing ones. We find 2 of them.

![[Pasted image 20210626144907.png]]

Check the details of both of them and in the input data you'll find the flag.

![[Pasted image 20210626145014.png]]