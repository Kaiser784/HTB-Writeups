# You know 0xDiablos


### Challenge Author(s): [RET2pwn](https://app.hackthebox.eu/users/47422)

### Description: 
   ```
   I missed my flag
   ```
### Difficulty: `Easy`
---
# Challenge

using radare2 to analyze the executable we find a `sym.vuln` function.

Checking the `vuln` function we find it uses `gets` to input a 184 length buffer.
![[Pasted image 20210829134205.png]]

But we don't find any flag or flag function to print.

So we check all the functions using `afl`
![[Pasted image 20210829134308.png]]

We find the flag function along with it's address to jump to.

analyzing the flag function we see that it's comparing 2 strings and only then prints the function. 
The strings are
```
0xdeadbeef
0xc0ded00d
```

So what we have to overflow

```
184			string
4			return address
0x080491e2	flag func address
4			return address
0xdeadbeef	arg1
0xc0ded00d	arg2
```

So we use pwn from python and write an exploit will try to change to golang.