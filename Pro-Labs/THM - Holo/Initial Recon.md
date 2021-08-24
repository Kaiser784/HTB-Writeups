#recon 

ping sweep
```bash
nmap -sS -n 10.200.128.0/24
```

Quick scan
```bash
nmap -sC -sV -A -T4 10.200.128.0/24 --open
```