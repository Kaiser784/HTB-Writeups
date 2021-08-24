# Cat


### Challenge Author(s): [gh0stm5n](https://app.hackthebox.eu/users/91108)

### Description: 
   ```
   Easy leaks
   ```
### Difficulty: `Easy`
---
# Challenge

We're given an `.ab` file which is an android backup file.
To extract this we need a we need to add a `tar` header and can extract it on linux.

```bash
( printf "\x1f\x8b\x08\x00\x00\x00\x00\x00" ; tail -c +25 backup.ab ) |  tar xfvz -
```

and we get an `apps` and `shared` directories. The apps looked like it had normal OS files.

You can list files recursively to check which has what

```bash
ls -alR
```

The `Pictures` one had 6 images and in one of the images the flag was written on the paper a man is holding.