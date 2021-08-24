#!/bin/bash
ARCH=`uname -m`
echo "Detected architecture is: $ARCH"

if [ "$ARCH" == "x86_64" ] || [ "$ARCH" == "i686" ]
then
	ABSOLUTE_PATH="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/$(basename "${BASH_SOURCE[0]}")"
	# star and its manpages
	cp "${ABSOLUTE_PATH%/*}/"star-1.6-"$ARCH"-cygwin32_nt-gcc/star.exe /usr/local/bin/star.exe
	gzip -9 -k -c "${ABSOLUTE_PATH%/*}/"star-1.6-"$ARCH"-cygwin32_nt-gcc/man/star.1 > /usr/share/man/man1/star.1.gz
	gzip -9 -k -c "${ABSOLUTE_PATH%/*}/"star-1.6-"$ARCH"-cygwin32_nt-gcc/man/star.5 > /usr/share/man/man5/star.5.gz
	# star_sym and its manpage
	cp "${ABSOLUTE_PATH%/*}/"star_sym-1.6-"$ARCH"-cygwin32_nt-gcc/star_sym.exe /usr/local/bin/star_sym.exe
	gzip -9 -k -c "${ABSOLUTE_PATH%/*}/"star_sym-1.6-"$ARCH"-cygwin32_nt-gcc/man/star_sym.1 > /usr/share/man/man1/star_sym.1.gz
	# permissions
	chmod 755 /usr/local/bin/star.exe
	chmod 755 /usr/local/bin/star_sym.exe
	chmod 644 /usr/share/man/man1/star.1.gz
	chmod 644 /usr/share/man/man5/star.5.gz
	chmod 644 /usr/share/man/man1/star_sym.1.gz
	echo "Copying done."
else
    echo "Only Intel based 32 and 64 bits architectures are supported."
fi
