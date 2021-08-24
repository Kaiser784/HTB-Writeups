#!/bin/bash

# Check requirements
command -v zip >/dev/null 2>&1 || { echo >&2 "I require zip but it's not installed.  Aborting."; exit -2; }
command -v unzip >/dev/null 2>&1 || { echo >&2 "I require unzip but it's not installed.  Aborting."; exit -2; }

if [ $# -lt 1 ]
then
	echo "Usage: bash convert-iso88591-to-utf8.sh <archive.tar.bz2>"
	echo "	Will create <archive_UTF-8.zip>"
	exit 0
fi

[ ! -f "$1" ] && echo "\"$1\" does not exist." && exit 1

echo "Found file \"$1\""

# Detect extension
EXTENSION=""
shopt -s nocasematch
if [[ "$1" == *.tar.bz2 ]] # * is used for pattern matching
then
	EXTENSION=".tar.bz2"
else
	echo "Unsupported extension."
	exit -1 #255
fi
shopt -u nocasematch

FOLDERBASE=`basename "$1" "$EXTENSION"`

TOPLINE=`tar -tvf "$1" | head -n 1`

if [[ ! "$TOPLINE" =~ ^d ]]
then
	echo "First entry is not a directory, exiting."
	exit 2
fi

TOPLINE2=`tar -tf "$1" | head -n 1`

NUMBEROFTRAILINGSLASHES=`echo $TOPLINE2 | grep -o '/' | wc -l`

if [ $((NUMBEROFTRAILINGSLASHES)) -gt 1 ]
then
	echo "More than one trailing slash. Exiting."
	exit 3
fi

FOLDERINTAR=`echo "$TOPLINE2" | sed 's:/*$::'`

if [[ "$FOLDERBASE" != "$FOLDERINTAR" ]]
then
	echo "Folder inside tar does not match basename. Exiting."
	exit 4
fi

echo "Working with package \"$FOLDERINTAR\""

echo "Decompressing \"$1\"..."
tar -jxf "$1" || exit 5

echo "Converting ISO-8859-1 text files to UTF-8..."
find "$FOLDERBASE" -type f -iname '*' -print0 | xargs -0 file -i | grep 'charset=iso-8859-1' | awk -F':' '{print $1}' | xargs -i sh -c '(iconv -f ISO-8859-1 -t UTF-8 {} > {}.bak && mv {}.bak {}) || echo "iconv failed to convert \"{}\". Exiting." && exit 6'

echo "Renaming folder..."
mv "$FOLDERBASE" "$FOLDERBASE""_UTF-8"

echo "Compressing \"$FOLDERBASE""_UTF-8"".zip\"..."
zip -9 -r -q "$FOLDERBASE""_UTF-8"".zip" "$FOLDERBASE""_UTF-8"

echo "Cleaning up..."
rm -r "$FOLDERBASE""_UTF-8"

echo "Checking \"$FOLDERBASE""_UTF-8"".zip\"..."
unzip -q -t "$FOLDERBASE""_UTF-8"".zip" > /dev/null || (echo "ZIP file does not pass test. Exiting." && exit 7)

echo "Done."
