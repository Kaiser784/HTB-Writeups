#!/bin/bash
ARCH=`uname -m`
echo "Detected architecture is: $ARCH"

if [ "$ARCH" == "x86_64" ] || [ "$ARCH" == "i686" ]
then
	# star and its manpages
	rm /usr/local/bin/star.exe
	rm /usr/share/man/man1/star.1.gz
	rm /usr/share/man/man5/star.5.gz
	# star_sym and its manpage
	rm /usr/local/bin/star_sym.exe
	rm /usr/share/man/man1/star_sym.1.gz
	echo "Removing done."
else
    echo "Only Intel based 32 and 64 bits architectures are supported."
fi
