For better use, it's recommended to download android-backup-toolkit, which is a bundle.
It already includes all the software bundled plus additional tools.

DESCRIPTION:
android-backup-processor is a fork of android-backup-extractor.
It provides the same functionality for Android .ab packing and extraction, however has some extra features:
	- It provided the binary code also, called abp.jar
	- Checks Android Backup version when debugging, but doesn't interfere with operation
	- All output information, debug messages, and operation errors, are sent to stderr, only if "-debug" is enabled. NoSuchAlgorithmException will be displayed.
	- You can choose whether or not display any debug information with "-debug" flag
	- You can just view .ab file information, with "info" subcommand, without having to pack or extract anything
	- Checks if you have strong encryption for Java enabled on your system
	- Checks for most common cases of empty or useless backups
	- Can detect improperly padded backups due to adb timeout (only when unpacking)
	- The releases are version-based, using as version the last time the source code was modified, the time format is YYYYMMDD
	- Has Bouncy Castle (JCE with provider and lightweight API) already built-in, so no jar sideloading is required
	- The README.TXT contains several examples and in-deep descriptions of information that is not publicly available
	- You can choose, on demand, to use or not environment variables to read passwords, with "-useenv" flag
	- Asks interactively for backup password, when is encrypted and not provided otherwise
	- The entire Eclipse project with source code is provided
	- Has the ability to view MIUI backups information and extract them (only for those versions tested personally)
	- The debug information is more detailed and accurate

LINKS:
android-backup-toolkit: https://sourceforge.net/projects/android-backup-toolkit/
Full local backup infrastructure: https://android.googlesource.com/platform/frameworks/base/+/4a627c71ff53a4fca1f961f4b1dcc0461df18a06
tar-binary-splitter: https://sourceforge.net/projects/tar-binary-splitter/
android-backup-splitter: https://sourceforge.net/projects/adb-split/
no-adb-backup-app-lister: https://sourceforge.net/projects/no-adb-backup-app-lister/
Perl scripts to encrypt/decrypt adb backup files: http://forum.xda-developers.com/showthread.php?t=1730309
The Legion of the Bouncy Castle: https://www.bouncycastle.org/java.html
Bouncy Castle source (JCE with provider and lightweight API):
	* Releases URL: https://www.bouncycastle.org/latest_releases.html
	* Specific download: https://www.bouncycastle.org/download/bcprov-jdk15on-169.zip
IDE Eclipse: https://www.eclipse.org
Oracle Java Archive: https://www.oracle.com/java/technologies/oracle-java-archive-downloads.html
OpenJDK: https://openjdk.java.net/
adb official documentation: https://developer.android.com/studio/command-line/adb#issuingcommands
	* Section "Backup and restore commands" has been completely removed and also any mention to "backup" or "restore"
Latest Android backup source code: https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/app/backup/

PROJECT INFORMATION:
Application internal name: android-backup-processor
Compiled binary: "android-backup-processor/executable/abp.jar".
Application filename: abp.jar
Application abbreviation: ABP
Site: https://sourceforge.net/projects/android-backup-processor/
Original author: Nikolay Elenkov
Original source code: https://github.com/nelenkov/android-backup-extractor
Original documentation blog: http://nelenkov.blogspot.com/2012/06/unpacking-android-backups.html
Packaging and patches/improvements: dragomerlin
Format: runnable java jar file + third party perl scripts
	* java code is modified (Nikolay Elenkov based)
	* perl scrips are unmodified (Jeffrey J. Kosowsky)
IDE:
	Eclipse IDE for Java Developers (includes Incubating components)
	Version: 2021-06 (4.20.0)
	Build id: 20210612-2011
	Text file encoding: Cp1252 (Eclipse default) and UTF-8
	End of line format: mixed Windows/UNIX
OS: Windows 10 Pro 64 bits, version 21H1
Compiler: using built-in JDK provided by Eclipse:
	%USERPROFILE%\.p2\pool\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_15.0.1.v20201027-0507\jre\bin\
Compiler compliance level: Java 1.7. "Major.Minor Version"=51.0
Bouncy Castle version: 1.69

BACKUPS WITH STRONG CRYPTOGRAPHY
To work with password (encrypted) adb backups, it's required to download, for older versions of Java,
the extended cryptographic libraries, depending if it's for Oracle or OpenJDK:
The unlimited policy files are required only for JDK 8, 7, and 6 updates earlier than 8u161, 7u171, and 6u181.
On those versions and later, the stronger cryptographic algorithms are available by default.
JDK 9 and later offer the stronger cryptographic algorithms by default.
See more here: https://www.oracle.com/java/technologies/javase-jce-all-downloads.html
For Oracle, these are:
	Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files:
	- local_policy.jar
	- US_export_policy.jar
from:
	https://www.oracle.com/java/technologies/jce-6-download.html
	https://www.oracle.com/java/technologies/javase-jce7-downloads.html
	https://www.oracle.com/java/technologies/javase-jce8-downloads.html
and put them in the corresponding lib/security folders of the Oracle Java SE 6, 7 or 8 installations, for example:
--Windows:
C:\Program Files\Java\jdk1.7.0_60\jre\lib\security\
c:\Program Files\Java\jdk1.8.0_66\jre\lib\security\
c:\Program Files\Java\jdk1.8.0_144\jre\lib\security\
C:\Program Files\Java\jre7\lib\security\
c:\Program Files\Java\jre1.8.0_66\lib\security\
C:\Program Files (x86)\Java\jdk1.7.0_60\jre\lib\security\
C:\Program Files (x86)\Java\jre7\lib\security\
c:\Program Files (x86)\Java\jre1.8.0_66\lib\security\
--Linux:
/usr/local/jdk1.7/jre/lib/security/
/usr/lib/jvm/java-7-oracle/jre/lib/security/
/usr/lib/jvm/java-8-oracle/jre/lib/security/
--BSD:
/usr/lib/jvm/java-7-openjdk-*/jre/lib/security/
--OS X / macOS:
/Library/Java/JavaVirtualMachines/jdk1.7.0_60.jdk/Contents/Home/jre/lib/security/
/Library/Java/JavaVirtualMachines/jdk1.8.0_66.jdk/Contents/Home/jre/lib/security/
Won't work if you supply them with the jar application, you must have them installed onto your system.
Latest versions of Oracle Java, JDK™ 9-15, already come with policy files for "unlimited" cryptographic strengths:
	See Java SE README Files: https://www.oracle.com/java/technologies/java-readme.html
OpenJDK "16-ea" 2021-03-16 also includes them.

Usage:
	info:		java -jar abp.jar [-debug] [-useenv=yourenv] info <backup.ab> [password]
	unpack:		java -jar abp.jar [-debug] [-useenv=yourenv] unpack <backup.ab> <backup.tar> [password]
	pack:		java -jar abp.jar [-debug] [-useenv=yourenv] pack <backup.tar> <backup.ab> [password]
	pack-kk:	java -jar abp.jar [-debug] [-useenv=yourenv] pack-kk <backup.tar> <backup.ab> [password]

"info", "pack", "pack-kk" and "unpack" are called subcommands.
*"-debug" must go before "-useenv" when both used, and applies to all operations (pack, pack-kk, unpack, info)
Without "-debug" flag, there won't be any output to stderr, except in 2 cases:
	Bad argument format, or no arguments at all, which will display usage.
	Prompt for password, even if "-" is used for stdin/stdout.
	* The only way to check if there's any error during operation is to use "-debug", otherwise it's quiet.
There is never output to stdout, except for "-" files.
*pack-kk is to create version 2 backups for android kitkat 4.4.3+.
*info gives information about the adb backup without extracting anything.
*yourenv is the environment variable provided to read the password from. Don't mistake it with a bash variable.
*debug shows cryptographic and file information, and password when useenv is used, and any additional information.
	All messages (except NoSuchAlgorithmException) are displayed only with debug, and are written to stderr, even the usage (no arguments given or incorrect ones),
	so it's fine for pipelining and checking the exit code, the only exception is when specifying to output the processed file to stdout with "-".
*If the filename is `-`, then data is read from standard input or written to standard output
If you don't specify a password when packing the backup archive, won't be encrypted but only compressed (default).
Option to create unencrypted and uncompressed backups is possible, but not available on ABP.
If wrong password is entered for encrypted backups, this is the error, however "IOException" is not thrown like when adb backup timeout happens:
	"javax.crypto.BadPaddingException: Given final block not properly padded. Such issues can arise if a bad key is used during decryption."

Setting environment variables:
Don't mistake environment variables with bash variables, which are not available to ABP and are set the same way without the "export".
--Linux: (double quotes may affect bash expansion, consider using single quotes instead if you use special characters like $, etc)
Quick usage:
	export PASSVAR=mysecretpassword
	OR
	export PASSVAR="my secret password"
	OR
	export PASSVAR='my secret password'
To make it persistent between reboots and sessions, add it to your ~/.profile or ~/.bashrc and reload it:
	echo 'export FOO="my pass"' >> ~/.profile
	source ~/.profile
To make it available for all users, add it as root to /etc/profile (most linux distros)
	sudo echo 'export FOO=bar' >> /etc/profile
-- OS X:
To make it persistent for terminals, add it to ~/.bash_profile
	echo 'export FOO=bar' >> ~/.bash_profile
	OR
	echo 'export FOO="my pass"' >> ~/.bash_profile
Globally: very tricky, depends on OSX version
-- Windows: you can use User or System environmental variables with ABP:
	For user environment variables: Windows key + R > rundll32 sysdm.cpl,EditEnvironmentVariables > User variables for your account
	For user or system: Control panel > View by: Large icons > System > Advanced system settings > Environment variables... > System variables
You can view them also with Windows Powershell commands:
	[Environment]::GetEnvironmentVariables("Machine")
	[Environment]::GetEnvironmentVariables("User")
	[Environment]::GetEnvironmentVariables("Process")
	More info to view/set/remove on Windows:
	* http://www.adp-gmbh.ch/win/misc/environment_variables.html
	* https://stackoverflow.com/questions/30675480/windows-user-environment-variable-vs-system-environment-variable
	* http://xahlee.info/powershell/environment_variables.html

Requirements for compilation:
- Java Development Kit version 7 (1.7) or higher
- Bouncy Castle java release "JCE with provider and lightweight API" bcprov-jdk15on*
- Set the Compiler compliance level to 1.7 to allow more compatible versions (optional but recommended)
- IDE Eclipse (optional but recommended)

Requirements for execution:
- Java Runtime Environment version 7 (1.7) or higher
- Oracle Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files (7 or 8) (optional but required when handling encrypted backups)
  * http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
  * http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html


Description and scope:
- android-backup-processor is a Java program that can extract and create android adb backups from or to TAR format. It doesn't edit directly.
- It can display the information of MIUI backups and also extract them. Does not support to create MUIU backups.
- You can choose to view an adb backup properties, extract it to tar, or create a new adb backup from tar.
 - When creating a backup, password if optional. When extracting, if backup is encrypted, entering a password is mandatory to succeed.
   If a password is required, but not provided by the command line, it will ask interactively. Will exit if a null or empty string is provided.
   Providing or not a password when creating already decides if the backup will be encrypted or not.
- A password can be provided from any environment variable with -useenv=envvar. However, if the password is specified directly on the command line, this will be used instead.
  It does not read bash variables, only user environment variables
- Debug messages can be activated with the "-debug" flag. Otherwise operations are quiet except when displaying usage or NoSuchAlgorithmException.
- When working with encryption, JCE USJPF is required, so will notify and exit if not present.
- android-backup-splitter are two bash scripts that allow you to create individual adb backups for each app and shared storage from the full backup,
  so you can restore apps or shared sdcards individually. Same encryption is preserved to them, if any. It also informs when any apk is missing for each app.
  * adb-split-extraction.sh extracts the contents of the tar archive(s) to the hard drive and re-tars. This method will cause issues if extracted content's fullname has characters not allowed by destination filesystem.
    on certain filesystems. It only generates adbs for apps, not shared storages.
  * adb-split-no-extraction.sh splits the adb tar in binary mode instead. It requires the program tar-binary-splitter.
    This is the recommended method. The script generates adb(s) for both apps and shared.
- adb backups (usually with .ab extension) can be password protected or not. If the device is encrypted usually this is mandatory, and the password can be the same
  as the used to unlock the device (some manufacturers don't required password to encrypt the backup even if the device is encrypted or a different one may suffice).
  Examples:
  	* Samsung Galaxy phone with stock Android 11 is encrypted (both phone+extSD), and requires a password with at least 1 character to allow "Backup my data".
  	  Until that the option is greyed out. You can use special characters like "*" or spaces for password.
	* LG phone with LineageOS 18.1 is not encrypted, and allows to create backups with or without a password.
- adb backups are extracted to tar format, and are created from the tar format as well. In reality that's not checked, you can use any binary file instead of tar,
	if you want to create any compressed/encrypted .ab file for any reason, but won't be able to work with Android phones.
- The files and folders inside the tar archive must be in some specific order, not necessarily alphabetical like tar does by default.
  Such order is listed in "Full local backup infrastructure". The easiest way to test is to repackage a tar archive from an already existing one;
  for example a part of an archive, and repackage it following the same order as files are listed in the original. If we don't have the original archive,
  we have to guess out the order. Some redundancy checksums may be present on the manifest so restoring a modified backup may not work.
- Perl scrips are unmodified from original author, and are not updated to have the latest features.
	NOTE: additional scripts and software not included with android-backup-processor is available on android-backup-toolkit which is the recommended download.
- ABP checks also for empty backups based on its size. Does not cover MIUI backups. This information is displayed only with "-debug" flag. Checked sizes are:
	-- If compressed and encrypted, backup size is less than 613 bytes, with any subcommand.
	-- If compressed and not encrypted, backup size is less than 104 bytes, with any subcommand.
	-- backup size is 0 bytes, only with "info" and "unpack" subcommands. ABP can't generate a 0 bytes backup, even if the tar file is empty.

Considerations:
- Read the READMEs and Docs inside android-backup-toolkit for further knowledge.
- The tar archives can be extracted and created on filesystems that don't preserve tar's content's permissions, although works anyway when restoring to the android device.
- Directories inside tar archives must not contain trailing slashes. Since GNU tar doesn't have this option, the use of star or pax is mandatory. For Ubuntu and Cygwin star is included in android-backup-tookit.
- star is recommended instead of pax. Pax only supports path length only up to 100 characters, so will fail in some cases. This usually happens with web browsers' history.
- Java 7 or higher is required because of SYNC_FLUSH mode for the Deflater.
- If the backup contains apk+data for an app, restore will work, if the apk supports destination architecture. If contains only the data, you must install the app first on the device and then restore. Installing the app later won't work.
	You can install such app from local apk or from any store, provided that the app has not been removed from the market.
- If you want a full backup use a nandroid backup with Clockworkmod or TWRP instead of ADB. Nandroid backup has the disadvantage that is not always 100% compatible between devices.
- Nandroid Manager (https://play.google.com/store/apps/details?id=com.h3r3t1c.bkrestore) is the best android app to work with nandroid backups.
- Some apps include a policy where the apk is never backed up even if specified. Same may apply for some data, depending on how the app is programmed regarding adb backups.
- Some apps can't be backed up at all. For example if android:allowBackup="false" is set in the manifest or the app is DRM protected (try Titanium Backup instead).
- Some applications or its data can't be restored to a different device: require specific device's id, android api level, processor architecture, etc.
- Old versions of Android Debugging Bridge are very slow: about 1 MBps if the backup is password encrypted, 1.6 MBps if not, so consider that backing up and restoring may take a long time.
- When adding or removing files for an app you may need to edit _manifest so adb restore accepts the changes.
- ADB backups are not a replacement for a Nandroid backup. The whole /data partition is not backed up, only a part of it. Also, other partitions like /system, /preload, /cache, modem, RIL, efs, boot, kernel or recovery are never backed up because are not user data.
  This is to prevent issues when restoring on a different device. It has also the biggest advantage that doesn't require root to operate, so is totally compatible with stock roms, locked bootloaders and device encryption.
- If you have the device encrypted with a password, you may have to use that particular one for backup creation and restore. You may create a backup without password or a different one, but the restore may fail.
	It depends on manufacturer and specific device. Not all enforce that.
- star for cygwin for windows is attached with android-backup-tookit (move it to C:\cygwin\bin\star.exe) since there is no package available. It can be compiled from the schily tools (http://sourceforge.net/projects/schilytools).
  star for linux can be installed as package or compiled the same way
  star for OSX/macOS can be installed with Homebrew (http://brew.sh/): brew install star
- The best way to test if an adb backup has errors, is to convert it to tar and then check with "tar -tf file.tar && echo $?". 0 is given if ok
- One or more spaces can be used as password. Example: "   "
- With "Android App Bundle", announced at Google I/O '18, apks may differ from one device to another or not contain all assets, so making an adb backup and restoring it to another device may or may not work if apk(s) are included:
	* https://www.youtube.com/watch?v=bViNOUeFuiQ
	This is similar to what happened with iOS starting with iOS 9, where you can't backup anymore the apps (.ipa files) with iTunes/iCloud, only its data. If you restore the backup, the apps are expected to be re-downloaded from the App Store.
	The problem with this, is that if you buy a new iPhone/iPad, and an app was removed from the App Store or is no longer updated to work with newer versions of iOS, you can't transfer it offline to a new device, even if you have it on the old iPhone.
	Apple may allow you to re-download the removed app if you purchased it before (tied to your Apple ID), but new users won't have it available and it may seem the app never existed.
	For that reason, as time passes, increasingly old apps and games will become unavailable to newer users. To have them you must buy an used phone with apps already preinstalled, or use unofficial methods.
	Before iOS 9 and older versions of iTunes, you could backup and restore entire applications which you had on your device, free or paid. The cause may be app thinning and multi-device support:
	* https://www.macworld.com/article/3125509/iphone-ipad/more-on-backing-up-your-ios-device.html
	* https://apple.stackexchange.com/questions/206544/cant-transfer-purchases-from-iphone-to-itunes-in-ios-9

Special characters as part of relative path for certain app's files:
- Some apps' backups will contain files or folders with characters not allowed by Microsoft Windows, like like \/:|*?"<>
	All the cases I've found include only the colon ":"
- Some apps' backup will contain very large relative paths
	The only cases I've found are web browser's history
These two things don't affect ABP in any way, it's only a consideration if you want to extract the tar later on.


Example of quick backup editing:
Here is an example on how to make a custom adb backup from a complete one, for the game Grand Theft Auto III from Rockstar Games, installed on the Nexus 7.

1) Unpack the original adb backup:
$ java -jar abp.jar unpack nexus7.ab nexus7.tar [password]

2) Extract the contents of the tar archive. This may be done on a filesystem where the permissions of the files inside the tar are preserved, for example using linux, mac or bsd. Up to two folders may appear, apps and shared.
   Note that if there are characters in filenames/folders not supported by the operating system or filesystem, it will not work even if it appears to do so. UNIX-based operating systems are the most flexible.
$ tar -xvf nexus7.tar

3) Make a list of all the contents of the original archive in the order they are archived:
$ tar -tf nexus7.tar > nexus7.list

4) Create a new list only with the files and folders you want, in proper order. For example for the GTA 3 (you can try savegames instead of all data):
$ cat nexus7.list | grep com.rockstar.gta3 > gta3.list

5) Create the new tar archive. The directories stored on tar mustn't contain trailing slashes, so I use star instead of tar. Pax works also if paths are up to 100 of length:
$ star -c -v -f gta3.tar -no-dirslash list=gta3.list
OR
$ cat gta3.list | pax -wd > gta3.tar

6) Create the adb backup from the tar archive. Password is optional except if the device is encrypted:
$ java -jar abp.jar pack gta3.tar gta3.ab [password]

Note:
If the backup is compressed but not encrypted, zlib can be used instead for both unpack and pack the ab archive.
If openssl is not compiled with zlib support due to security issues (RHSA-2013-0587;CVE-2012-4929), you can try python instead.
- Quick unpacking:
	tail -c +25 nexus7.ab | openssl zlib -d > nexus7.tar
	tail -c +25 nexus7.ab | python -c "import zlib,sys;sys.stdout.write(zlib.decompress(sys.stdin.read()))" > nexus7.tar
- Quick packing:
	dd if=nexus7.ab bs=24 count=1 of=gta3.ab ; openssl zlib -in gta3.tar >> gta3.ab
	dd if=nexus7.ab bs=24 count=1 of=gta3.ab ; cat gta3.tar | python -c "import zlib,sys;sys.stdout.write(zlib.compress(sys.stdin.read()))" >> gta3.ab
	dd if=nexus7.ab bs=24 count=1 of=gta3.ab ; python -c "import zlib,sys;open('gta3.ab', 'ab').write(zlib.compress(open('gta3.tar', 'rb').read()))"
- Instead of "tail -c +25 nexus7.ab" you can use "dd if=nexus7.ab bs=1 skip=24" but is less efficient.


Example of adb splitting to create an individual adb backup for shared and each app (non extraction method, RECOMMENDED):
Use ADB Backup Splitter (is a shell script). Works on Linux, Cygwin and OSX:
bash adb-split-no-extraction.sh backup.ab [password]
Resulting files go in split-ab folder.
Missing apk(s) are listed in apk-missing.html

Example of adb splitting to create an individual adb backup for each app (extraction method):
Use ADB Backup Splitter (is a shell script). Works on Linux, Cygwin and OSX:
bash adb-split-extraction.sh backup.ab [password]
Resulting files go in app-ab folder.
Missing apk(s) are listed in apk-missing.html


MIUI backups:
MIUI backups are adb backups, except that they contain an additional header at the beginning.
(By default, MIUI backups are neither compressed nor encrypted, at least the tested sample)
(On the other hand, adb backups by default are compressed but not encrypted.)
To extract them, just read/discard the additional header and then treat the rest like a regular adb backup.
- Example: hex dump of the MIUI backup for an app:
-- App: Notes: https://play.google.com/store/apps/details?id=com.trantrigroup.note
-- App version: 1.0
-- Device: Xiaomi MI5
-- Device operating system: MIUI Global 8.5 | Stable | 8.5.3.0 (NAAMIED) | Based on android 7.0 NRD90M
-- Backup size: 853575 bytes
-- Tar size (.BAK to .TAR): 853504 bytes
-- The app contains a Test note
00000000: 4D 49 55 49 20 42 41 43 4B 55 50 0A 32 0A 63 6F 6D 2E 74 72 61 6E 74 72 69 67 72 6F 75 70 2E 6E  MIUI BACKUP.2.com.trantrigroup.n
00000020: 6F 74 65 20 4E 6F 74 65 73 0A 2D 31 0A 30 0A 41 4E 44 52 4F 49 44 20 42 41 43 4B 55 50 0A 34 0A  ote Notes.-1.0.ANDROID BACKUP.4.
00000040: 30 0A 6E 6F 6E 65 0A 61 70 70 73 2F 63 6F 6D 2E 74 72 61 6E 74 72 69 67 72 6F 75 70 2E 6E 6F 74  0.none.apps/com.trantrigroup.not
00000060: 65 2F 5F 6D 61 6E 69 66 65 73 74 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  e/_manifest.....................
00000080: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................................
000000a0: 00 00 00 00 00 00 00 00 00 00 00 30 30 30 36 30 30 20 00 30 31 37 35 30 00 00 00 30 31 37 35 30  ...........000600 .01750...01750
000000c0: 00 00 00 30 30 30 30 30 30 30 32 37 31 34 00 30 00 00 00 00 00 00 00 00 00 00 00 30 31 33 36 32  ...00000002714.0...........01362
000000e0: 37 00 20 30 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  7. 0............................
00000100: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................................
00000120: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................................
00000140: 00 00 00 00 00 00 00 00 75 73 74 61 72 00 30 30 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ........ustar.00................
00000160: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................................
00000180: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................................
000001a0: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................................
000001c0: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................................
000001e0: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................................
00000200: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................................
00000220: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................................
00000240: 00 00 00 00 00 00 00 31 0A 63 6F 6D 2E 74 72 61 6E 74 72 69 67 72 6F 75 70 2E 6E 6F 74 65 0A 33  .......1.com.trantrigroup.note.3
00000260: 0A 32 34 0A 63 6F 6D 2E 61 6E 64 72 6F 69 64 2E 76 65 6E 64 69 6E 67 0A 31 0A 31 0A 33 30 38 32  .24.com.android.vending.1.1.3082
00000280: 30 32 63 37 33 30 38 32 30 31 61 66 61 30 30 33 30 32 30 31 30 32 30 32 30 34 34 61 39 65 32 33  02c7308201afa00302010202044a9e23
000002a0: 31 36 33 30 30 64 30 36 30 39 32 61 38 36 34 38 38 36 66 37 30 64 30 31 30 31 30 62 30 35 30 30  16300d06092a864886f70d01010b0500
[...]

- Example: hex dump of the adb backup for the same app:
-- Device: Samsung Galaxy S7
-- Device operating system: android 7.0 build NRD90M
-- Backup size: 619614 bytes
-- Tar size (.AB to .TAR): 871936 bytes
-- The app contains the same Test note
00000000: 41 4E 44 52 4F 49 44 20 42 41 43 4B 55 50 0A 34 0A 31 0A 6E 6F 6E 65 0A 78 DA EC BB 77 5C 14 4B  ANDROID BACKUP.4.1.none.x...w\.K
00000020: B7 2E 3C E4 8C 20 48 CE 8A 0A 12 66 98 88 E4 1C 54 04 09 12 14 98 19 66 C8 19 24 47 91 24 8A 82  ..<.. H....f....T......f..$G.$..
00000040: 92 73 0E 02 A2 04 51 B2 04 03 41 40 44 90 24 48 CE 39 28 F0 F5 28 FB 7D F7 F6 EC 7D EE 39 F7 DC  .s....Q...A@D.$H.9(..(.}...}.9..
00000060: DF FD E3 FB BE 9E 7E BA 6B 75 55 57 AD 5A EB A9 55 3D 3D 35 68 27 27 57 71 AC A3 BD 98 9B 0B DA  ......~.kuUW.Z..U==5h''Wq.......
00000080: C1 CD C5 DA D2 C5 D1 DD 49 CC C1 D1 0D 27 6E 66 8F 76 B0 C6 E3 5C DD 40 FF C3 0D 0C 06 23 C0 60  ........I....'nf.v...\.@.....#.`
000000a0: 3E 10 18 82 84 83 09 F2 1F E7 5F 9B 04 12 02 03 81 FF 5C 1E 02 45 48 20 41 7C 60 D0 FF 85 CD DD  >........._.......\..EH A|`.....
000000c0: D5 0D ED 02 A8 02 FA FF E6 06 A1 FE 5B E7 53 43 A9 25 60 3F B3 D0 0E 16 2E 8E D6 16 62 B7 70 0E  ............[.SC.%`?........b.p.
000000e0: 16 D6 0E 96 D4 10 E0 03 05 A3 24 C0 12 58 E4 CF 33 04 8D 47 83 C1 50 30 90 02 20 01 86 C1 D0 92  ..........$..X..3..G..P0.. .....
00000100: 38 09 28 04 01 05 83 2D 00 C7 4B 4A A0 51 08 18 0A 85 C0 23 01 19 02 7C 30 60 38 E1 06 08 14 0A  8.(....-..KJ.Q.....#...|0`8.....
00000120: 81 40 80 52 78 A0 14 14 0E 07 C3 C0 50 40 44 C1 61 48 09 04 04 81 93 00 FF 4C 49 FE AC 1A B8 17  .@.Rx.......P@D.aH.......LI.....
00000140: C8 84 42 C1 50 14 14 A8 1C F8 20 A0 30 20 05 87 A2 E0 68 08 0A 8C FF 99 07 F9 F9 91 00 3E D0 BF  ..B.P..... .0 ....h..........>..
00000160: 96 F8 EF B4 46 E8 93 84 C4 3F 6A 0F 21 68 0F A8 41 E8 2F 9E D0 8F 9F 29 34 58 E2 E7 19 02 06 63  ....F....?j.!h..A./....)4X.....c
00000180: 91 16 58 30 12 89 87 A3 61 08 38 04 89 C5 E2 A0 08 34 02 8B 02 4B 42 60 08 30 02 61 81 44 20 F0  ..X0....a.8......4...KB`.0.a.D .
000001a0: 92 12 78 2C 12 0C C3 80 71 30 B4 04 02 21 89 83 C2 51 70 A8 85 24 0C 0A 83 C1 81 5B 80 0B 92 10  ..x,....q0...!...Qp..$.....[....
000001c0: 09 30 0A 81 91 94 80 48 02 C3 47 12 81 96 84 4B 4A C0 C1 58 30 06 0A 03 E3 10 40 B5 38 3C 18 83  .0.....H..G....KJ..X0.....@.8<..
000001e0: 87 5A 40 25 B0 78 18 16 06 83 60 E0 68 09 BC 04 0A 2E 89 C7 E0 F1 68 AC 24 D6 02 8C 84 02 05 00  .Z@%.x....`.h.........h.$.......
00000200: CD E1 08 2C 5C 12 8C 97 94 B4 80 63 D1 08 1C C4 42 D2 02 01 03 63 B0 08 38 1A 0B 46 A1 00 0F 22  ...,\......c....B....c..8..F..."
[...]


DECEITFUL BACKUPS CREATED BY ANDROID
Some ab files (ADB backups) are empty although they look good.
Example app (game) causing the issue:
- Name: CrashOntheRun! / Crash Bandicoot Mobile
- ID=com.king.crash
- Version: 1.30.19
- Market links:
	Google Play Store: https://play.google.com/store/apps/details?id=com.king.crash
	APKPure: https://apkpure.com/crash-bandicoot-on-the-run/com.king.crash
	Uptodown: https://crash.uptodown.com
- AndroidManifest.xml > android:backupAgent="com.king.usdk.cloudstorage.KingBackupAgentHelper" > android:allowBackup="true" > FLAG_ALLOW_BACKUP
The application allows to perform OS-driven backups for its data, however the obtained data is empty.
How to detect empty backups:
- Use default backup process: adb backup -f backup.ab com.king.crash
- Happens when you backup a single app
- The backup process is almost instant
- If compressed and encrypted, backup size is 549 bytes
- If compressed and not encrypted, backup size is 47 bytes
- Both cases generate same tar file, 1024 bytes in size and all bytes=0x00
How to make a sample empty backup:
- Backup any app that doesn't allow adb backup, example Netflix: adb backup -f netflix.ab com.netflix.mediaclient
	It will generate a backup, which is not supposed to happen.
Reasons why this may happen:
- The app uses a cloud account (King.com in this case), to store app's data (game progress), which in incompatible with making local backups.
- The developer wants to meet the formal criteria of allowing adb backups but doesn't have the intention of really doing it.
- The app hasn't been run yet or hasn't any data to backup.
- Just setting android:allowBackup="true" is not enough for Android to make a proper backup.
- The developer did not configure properly the application or development environment in case it's needed.
- The Android Local Backup Infrastructure contains bugs.
- The backup size is too big or too small, or has too much or too few files, or the filenames contain illegal characters.
- The data is placed in directories or partitions that are beyond the scope of the Android Backup Agent.
- The app does not allow adb backup.
Due to its design, ADB backups are not reliable. It's not possible to know what data will be backed up. Check backups contents.
Restore to another device, to make sure everything is in place. Do it before app uninstall or device factory reset.

CREATING DECEITFUL BACKUPS WITH ABP
ABP will pack any file into adb backup using the subcommand "pack" or "pack-kk". The process will not be cancelled regardless
of input file size, extension or contents.
Example: create a backup a 0 bytes file.
	echo "" > zero.tar
	java -jar abp.jar pack|pack-kk zero.tar empty.ab [password]
These are the results:
- Input file has a size of 0 bytes
- If compressed and encrypted, backup size is 533 bytes
- If compressed and not encrypted, backup size is 32 bytes
- Backup can be unpacked without error to a 0 bytes file with any name or extension
- Couldn't find any example where Android generate this kind files (0 bytes after unpacking)

BACKING UP SHARED STORAGE
It's not recommended to use "adb backup" to backup or restore any internal or external shared storage on android. Other ways of copying are available.
ADB DEbugging Bridge and Android's BackupManagerService are known to have bugs that haven't been fixed in more than 10 years:
- Creation of empty or deceitful backup.
- Applications not following good practices when they intend to support adb backup.
- zlib compression broken.
- Each Android version may require a specific version of adb to accept backup parameters.
- Service crash and backup not properly terminated when there are files above a certain size inside the shared storage.
- Backup service has a timeout for backup, possibly for restore too.

CREATION OF SAMPLE BACKUPS
Sample backups are stored at: "android-backup-processor/ab-examples/".
Figuring out the minimum size of any backup: since adb can create empty or deceitful backup, we must figure out what is the minimum size
that any adb backup can have, and below that value notify that it's empty. That would be creating the file "0" inside internal sdcard and delete everything else.
All sample backups with minimum size are located at: "android-backup-processor/ab-examples/ab-less-size/".
How to create a sample backup with less size possible:
- Open Android Studio > More Actions > Virtual Device Manager > Create device > Nexus S > Next > Other Images > navigate to API Level: 17,
	ABI: armeabi-v7a, Target: Android 4.2, Release Name: Download > Finish > Next > Show Advanced Settings > Boot option: Cold boot > Finish
- Device Manager > select Nexus S, API 17, Android 4.2 | arm > Actions: power on
- On virtual device once has completed booting > Settings > {} Developer options > enable USB debugging: Debug mode when USB is connected > OK
- On host computer: platform-tools_r20-windows/platform-tools/adb.exe devices > until it's connected:
	* daemon not running. starting it now on port 5037 *
	* daemon started successfully *
	List of devices attached
	emulator-5554   device
- On host computer run these commands:
	platform-tools_r20-windows\platform-tools\adb.exe shell 'rm -r /sdcard/*'
	platform-tools_r20-windows\platform-tools\adb.exe shell 'touch /sdcard/0'
	platform-tools_r20-windows\platform-tools\adb.exe backup -f backup.ab -shared > On virtual device: Back up my data (with or without password)
	java -jar abp.jar -debug unpack backup.ab backup.tar
	tar -tvf backup.tar
		----rwxr-x 1000/1015         0 1970-01-01 01:08 shared/0/0
- Resulting files are (after being renamed):
	"AVD_NexusS-arm-4.2_API-17_NoPassword_(platform-tools_r20-windows).ab" size 104 bytes
	"AVD_NexusS-arm-4.2_API-17_WithPassword_(platform-tools_r20-windows).ab" size 613 bytes
	"backup.tar" size is 1536 bytes
	* (password length or pattern type does not influence encrypted backup size, which will be the same)
	* (encrypted or not doesn't influence tar size once unpacked, which will be the same)
- Repeat the process with as many API you want. This project includes as many examples as possible unless Device Manager crashes.
Backups that are deceitful or empty, stored at: "android-backup-processor/ab-examples/deceitful-backups/":
- adb backup -f backup.ab -shared
	"AVD_NexusS-x86-6.0_API-23_NoPassword_(platform-tools_r31.0.3-windows).ab" size is 46 bytes
	"AVD_NexusS-x86-6.0_API-23_WithPassword_(platform-tools_r31.0.3-windows).ab" size is 549 bytes
	"backup.tar" size is 1024 bytes and all bytes are 0x00
	"AVD_NexusOne-arm-4.0.3_API-15_NoPassword_(platform-tools_r31.0.3-windows).ab" size is 41 bytes
	"AVD_NexusOne-arm-4.0.3_API-15_WithPassword_(platform-tools_r31.0.3-windows).ab" size is 549 bytes
	"backup.tar" size is 1024 bytes and all bytes are 0x00
	"AVD_NexusS-x86-4.0.3_API-15_NoPassword_(platform-tools_r31.0.3-windows).ab" size is 41 bytes
	"AVD_NexusS-x86-4.0.3_API-15_WithPassword_(platform-tools_r31.0.3-windows).ab" size is 549 bytes
	"backup.tar" size is 1024 bytes and all bytes are 0x00
- adb backup -f backup.ab com.amazon.avod.thirdpartyclient (Prime Video 3.0.301.11845, with android:allowBackup="false")
	"AVD_Pixel2-x86-11.0_API-30_NoPassword_(platform-tools_r31.0.3-windows).ab" size is 47 bytes
	"AVD_Pixel2-x86-11.0_API-30_WithPassword_(platform-tools_r31.0.3-windows).ab" size is 549 bytes
	"backup.tar" size is 1024 bytes and all bytes are 0x00
According to all files in "android-backup-processor/ab-examples/", to consider valid a backup, has to meet these sizes
(only Android backups, not MIUI or any other, which are ignored):
	- No encryption used: backup.ab >= 104 bytes
	- Encryption used: backup.ab >= 613 bytes
Otherwise will be marked as empty.

INFORMATION ABOUT BACKUP SETTINGS IN APPS
Settings that go in AndroidManifest.xml: https://developer.android.com/guide/topics/manifest/application-element
- "android:allowBackup": allows that the app's data can be backed up over adb. Most apps, specially those managing accounts or using its
own cloud account, opt out of this functionality. Setting this value to "true" doesn't guarantee that any backup at all will happen.
Most apps having it set to "true" have it broken.
Starting on Android 12 (API level 31), "android:debuggable" must be set to "true" also, to properly backup app's data.
Is yet unknown if Google will allow apps with this flag enabled on the Play Store.
Practically speaking, almost no app will have a way to backup it up locally (backup-to-file) as time goes on.
Still, Android 12 allows to restore backups like in previous Android versions.
- "android:backupAgent"
- "android:backupInForeground"
- "android:fullBackupContent"
- "android:fullBackupOnly"
- "android:hasFragileUserData": allows to uninstall the app keeping its data inside the phone. It's not mentioned what versions of the app can be
	reinstalled: higher, lower or equal. Does not mention if the user can see that there's such data in the phone, or how can he remove it without
	reinstalling the app, or if can he backup such data without the app being installed.
- "android:killAfterRestore"
- "android:restoreAnyVersion": says if, when the app is installed, allows to restore data from a newer version.
Some applications can't be backed up because don't have set up properly "backupAgentName" even if they allow backup. This can be seen in logcat.

ADB BACKUP TIMEOUT
Android's backup server has a timeout, when performing adb backups, so is possible that will not allow to backup all data that can
be backed up. The timeout depends on Android version and vendor customization. The timeout starts to count when the backup message appears
on device's screen, not when "Back up my data" is hit, so all time spend entering the PIN (or not) will count against real backup time.
If "Back up my data" is not selected within 60 seconds after the "com.android.backupconfirm" appears, the process will be cancelled:
	"I BackupManagerService: Full backup/restore timed out waiting for user confirmation"
Some devices will display on screen that the timeout happened, and others will be misleading and display always that backup finished successfully
regardless of timeout or not. Sometimes, the partial (incomplete) ab backup on the computer will be properly padded and closed, and other times an
error will happen when unpacking the ab file or the tar. It depends if the timeout just happened exactly between the backup of two files.
So it's not reliable to use on device's messages or check ab backup / tar unpacking to determine that all data was backed up.
The timeout specially happens when backing up shared storage with enough space in use, because the compression and encryption is much slower than expected.
Timeout parameters like getSharedBackupAgentTimeoutMillis() are detailed in Android's BackupAgentTimeoutParameters.java and may be available to
"adb logcat", but depends mainly on Android version and vendor branding. Android 11 and 12 removed such debug information from logcat according to tests.
Unpacking ab backup files which are incomplete due to backup timeout, will trigger the following error, unless by misfortune the cut happens exactly between two files,
in this particular case is impossible to tell the backup was dry cut due to timeout and the size and contents must be check to see if something's missing:
	"Caused by: java.lang.RuntimeException: java.io.IOException: javax.crypto.BadPaddingException: Given final block not properly padded. Such issues can arise if a bad key is used during decryption."
In any case, the partial ab backup file on computer is not deleted when timeout happens, only will be dry cut at the end.
"adb backup" does not allow to set a custom timeout from user's perspective. If you want to try to extract all data from a device with adb these steps may work:
- Perform adb backup for one app at a time (can take days if you have hundreds of them and is labor intensive)
- Or perform adb backup until timeout
- Then check which apps or files are totally complete (the next app or file must appear on tar so it assures all the previous files are complete)
- The last file on the tar may or may not be complete
- Remove from the device the apps or files from shared that were properly backup up in tar file except the last one for safety
- Repeat the adb backup process until timeout, so a new set of files will be there since the previous ones were uninstalled
- Repeat until the process takes so little or the ab backup is so small that you can assure it did not timeout
Here are 4 examples, each $ is a command. Using adb Version 31.0.3-7562133. Tar is GNU variant.
(START OF EXAMPLE 1)
Example 1: Android 12 API 31 x86_64 AVD on Android Studio, internal sdcard 128 GB and external sdcard 128 GB, both almost full with files of 100 MB.

$ time adb backup -f A12.ab -shared -obb -all -system -apk
WARNING: adb backup is deprecated and may be removed in a future release
Now unlock your device and confirm the backup operation...

real    30m26.986s
user    0m0.000s
sys     0m0.046s

	* Toast message on device screen when backup stops is "Backup finished". Doesn't mention any timeout.

$ java -jar abp.jar -debug unpack A12.ab A12.tar 1234
android-backup-processor v20210805
Strong AES encryption allowed
"A12.ab" size is 5155054341 bytes
Magic: ANDROID BACKUP
Version: 5
Compressed: 1
Algorithm: AES-256
Encrypted: 1
IV: 26DA5123EAFC08CAB8139B0C809A8CFF
MK: C0E8F87D2E402EAAE0543CB0B0B3D6652A3F477379021E2298BCAE1D8F0A6196
MK checksum: 9A33CC36463974A5BB0E67AB7C41DC1D16273014D30951AFED1F7F37FD13799C
key bytes: C0E8F87D2E402EAAE0543CB0B0B3D6652A3F477379021E2298BCAE1D8F0A6196
salt bytes: B043A3306CA1251209E3AD9BD8B4A32F73AA79AF1A705785B3F8FD055F1DCFCF408FB55E917D1021E23BB757C78BFA937050E29ACD8912CD6E767CB95FB58C40
MK as string: [???}.@.??T<????e*?Gsy☻▲"???↔?
a?]
Key format: RAW
Calculated MK checksum (use UTF-8: true): 9A33CC36463974A5BB0E67AB7C41DC1D16273014D30951AFED1F7F37FD13799C

$ tar --version | head -n 1
tar (GNU tar) 1.34

$ tar -tf A12.tar > /dev/null
tar: Unexpected EOF in archive
tar: Error is not recoverable: exiting now

$ echo $?
2

-- start of adb logcat during backup --
06:58:44.792 11786 11786 D AndroidRuntime: Calling main entry com.android.commands.bu.Backup
06:58:44.799 11786 11786 D bu      : Beginning: backup
06:58:44.808   572  2196 V BackupManagerService: [UserID:0] Requesting backup: apks=true obb=true shared=true all=true system=true includekeyvalue=false pkgs=[Ljava.lang.String;@728f03c
06:58:44.808   572  2196 I BackupManagerService: [UserID:0] Beginning adb backup...
06:58:44.808   572  2196 D BackupManagerService: [UserID:0] Starting backup confirmation UI, token=593789719
06:58:44.809   572  2196 I ActivityTaskManager: START u0 {act=fullback flg=0x20000000 cmp=com.android.backupconfirm/.BackupRestoreConfirmation (has extras)} from uid 1000
06:58:44.810   572  2196 W ActivityTaskManager: startActivity called from non-Activity context; forcing Intent.FLAG_ACTIVITY_NEW_TASK for: Intent { act=fullback flg=0x20800000 cmp=com.android.backupconfirm/.BackupRestoreConfirmation (has extras) }
06:58:44.846   851   912 D StartingSurfaceDrawer: addSplashScreen com.android.backupconfirm theme=10302e3 task= 25
06:58:44.880   572  2196 D BackupManagerService: [UserID:0] Waiting for backup completion...
06:58:45.601   572   613 I ActivityTaskManager: Displayed com.android.backupconfirm/.BackupRestoreConfirmation: +752ms
06:58:46.067   572   906 W InputManager-JNI: Input channel object '8b2d428 Splash Screen com.android.backupconfirm (client)' was disposed without first being removed with the input manager!
06:58:46.914 11046 11046 I AndroidIME: AbstractIme.onActivate():84 PasswordIme.onActivate() : EditorInfo = Package = com.android.backupconfirm : Type = Password : Learning = Disable : Suggestion = Hide : AutoCorrection = Disable : Microphone = Hide : NoPersonalizedLearning = Disable : AutoStartVoiceInput = Disable, IncognitoMode = false
06:58:50.840   572   914 D BackupManagerService: [UserID:0] acknowledgeAdbBackupOrRestore : token=593789719 allow=true
06:58:50.846   572   914 V BackupManagerService: [UserID:0] Acquired wakelock:*backup*-0-1575
06:58:50.848   572 11821 I BackupManagerService: --- Performing adb backup ---
06:58:50.893   572 11821 I BackupManagerService: Package android is key-value.
06:58:50.913   572   625 I ActivityManager: Start proc 11822:com.android.sharedstoragebackup/u0a62 for service {com.android.sharedstoragebackup/com.android.sharedstoragebackup.ObbBackupService}
06:58:51.100 11822 11822 I TetheringManager: registerTetheringEventCallback:com.android.sharedstoragebackup
06:58:51.121 11822 11822 V GraphicsEnvironment: ANGLE Developer option for 'com.android.sharedstoragebackup' set to: 'default'
06:59:06.626   572 11821 I BackupManagerService: --- Performing full backup for package com.android.sharedstoragebackup ---
06:59:06.697   572 11821 D BackupManagerService: [UserID:0] awaiting agent for ApplicationInfo{575afc6 com.android.sharedstoragebackup}
06:59:06.700   572  3558 D BackupManagerService: [UserID:0] agentConnected pkg=com.android.sharedstoragebackup agent=android.os.BinderProxy@7ce5b87
06:59:06.701   572 11821 I BackupManagerService: [UserID:0] got agent android.app.IBackupAgent$Stub$Proxy@42a4ab4
06:59:06.702   572 12237 D BackupManagerService: Calling doFullBackup() on com.android.sharedstoragebackup
06:59:06.751 11822 11849 I FullBackup_native: measured [/storage/emulated/0] at 0
07:29:06.748   572  1575 D BackupManagerService: Timeout message received for token=5e1e8918
07:29:06.755   572  1575 V BackupManagerService: [UserID:0] Cancel: token=5e1e8918
07:29:06.763   572  1575 W BackupManagerService: adb backup cancel of PackageInfo{feda1e9 com.android.sharedstoragebackup}
07:29:06.818   572 11821 E BackupManagerService: Unexpectedly reached end of file while reading data
07:29:06.837   572 11821 E BackupManagerService: Error backing up com.android.sharedstoragebackup: null
07:29:06.858   572   906 I ActivityManager: Process com.android.sharedstoragebackup (pid 11822) has died: vis IMPF
07:29:06.888   572   906 W ActivityManager: Scheduling restart of crashed service com.android.sharedstoragebackup/.ObbBackupService in 1000ms for connection
07:29:06.955   572 11821 W ActivityManager: Unbinding backup agent with no active backup
07:29:06.973   572 11821 W ActivityManager: Process/uid not found attempting kill of com.android.sharedstoragebackup / 10062
07:29:07.019   572  2196 D BackupManagerService: [UserID:0] Adb backup processing complete.
07:29:07.021   572 11821 D BackupManagerService: Full backup pass complete.
07:29:07.088   572 11821 V BackupManagerService: [UserID:0] Released wakelock:*backup*-0-1575
07:29:08.028   572   616 W ActivityTaskManager: Activity top resumed state loss timeout for ActivityRecord{222b71a u0 com.android.backupconfirm/.BackupRestoreConfirmation t25 f}}
07:29:09.723   572  2199 W InputManager-JNI: Input channel object '11a647d com.android.backupconfirm/com.android.backupconfirm.BackupRestoreConfirmation (client)' was disposed without first being removed with the input manager!
07:29:11.462   572  1697 W NotificationService: Toast already killed. pkg=com.android.backupconfirm token=android.os.BinderProxy@9e98c48
-- end of adb logcat --
(END OF EXAMPLE 1)
(START OF EXAMPLE 2)
Example 2: repeat the same backup as example 1, but entering the PIN 1234 quicker on device dialog, so the backup is a little bit bigger.

$ time adb backup -f A12-another.ab -shared -obb -all -system -apk
WARNING: adb backup is deprecated and may be removed in a future release
Now unlock your device and confirm the backup operation...

real    30m26.538s
user    0m0.000s
sys     0m0.031s

	* Toast message on device screen when backup stops is "Backup finished". Doesn't mention any timeout.

$ java -jar abp.jar -debug unpack A12-another.ab A12-another.tar 1234
android-backup-processor v20210805
Strong AES encryption allowed
"A12-another.ab" size is 5439127317 bytes
Magic: ANDROID BACKUP
Version: 5
Compressed: 1
Algorithm: AES-256
Encrypted: 1
IV: C2194F5A3B97A5F1F95405EC2A24A975
MK: 81808EFDEDE8E96F0D9801D7094BCB5C184338C1D943D59AEDEC00F2099AD4E3
MK checksum: 6C8A32F755C861E94CD61BCA7C1834E2856D6B61D86D111BCB5F5227C5487CBE
key bytes: 81808EFDEDE8E96F0D9801D7094BCB5C184338C1D943D59AEDEC00F2099AD4E3
salt bytes: 194490A93E9FED9A2E1F7709533CA580E24CE87654245975959FB05FDCAD31E1CD3A9F78A513A31A44AB2175923172FF3C3907839599A268CCC53036055E27A7
?☺?as stK?\↑C8??C?????o ???]
Key format: RAW
Calculated MK checksum (use UTF-8: true): 6C8A32F755C861E94CD61BCA7C1834E2856D6B61D86D111BCB5F5227C5487CBE
Extracting: 0% 1% 2% 3% 4% 5% 6% 7% 8% 9% 10% 11% 12% 13% 14% 15% 16% 17% 18% 19% 20% 21% 22% 23% 24% 25% 26% 27% 28% 29% 30% 31% 32% 33% 34% 35% 36% 37% 38% 39% 40% 41% 42% 43% 44% 45% 46% 47% 48% 49% 50% 51% 52% 53% 54% 55% 56% 57% 58% 59% 60% 61% 62% 63% 64% 65% 66% 6
7% 68% 69% 70% 71% 72% 73% 74% 75% 76% 77% 78% 79% 80% 81% 82% 83% 84% 85% 86% 87% 88% 89% 90% 91% 92% 93% 94% 95% 96% 97% 98% 99% 100%
5439127317 bytes read from "A12-another.ab"
5447277568 bytes written to "A12-another.tar"

$ tar -tf A12-another.tar > /dev/null

$ echo $?
0
(END OF EXAMPLE 2)
(START OF EXAMPLE 3)
Example 3: backup of Samsung phone with Android 11.

$ time adb backup -f Samsung.ab -shared -obb -all -system -apk
WARNING: adb backup is deprecated and may be removed in a future release
Now unlock your device and confirm the backup operation...

real    10m3.989s
user    0m0.000s
sys     0m0.031s

	* Toast message on device screen when backup stops is "Operation timed out".

$ java -jar abp.jar -debug unpack Samsung.ab Samsung.tar 1234
android-backup-processor v20210812
Strong AES encryption allowed
"Samsung.ab" size is 2627960597 bytes
Magic: ANDROID BACKUP
Version: 5
Compressed: 1
Algorithm: AES-256
Encrypted: 1
IV: 241DE5898B00E9EFB5CA8B7A2D6239EE
MK: 1DB7334E1C5A020E4788382BF34FC4C81F016E7C1112A1376BAD54FEAA747D1C
MK checksum: 2018054D40CA8654A833EB2F6FBB94600888E95AFEBA1BB9F8ECA9E722B3A706
key bytes: 1DB7334E1C5A020E4788382BF34FC4C81F016E7C1112A1376BAD54FEAA747D1C
salt bytes: DA93EED10AC2FBC01E96CC67FF3B797A89E8CC046896C3575B02EFCE79B4E98937BAF1C13A4C641D24C3B9F7C275985D6AB15196F92BDFE66072FA8F5FE55A8B
MK as string: [↔?3N∟Z☻♫G?8+?O??▼☺n|◄↕?7k?T??t}∟]
Key format: RAW
Calculated MK checksum (use UTF-8: true): 2018054D40CA8654A833EB2F6FBB94600888E95AFEBA1BB9F8ECA9E722B3A706
Extracting: 0% 1% 2% 3% 4% 5% 6% 7% 8% 9% 10% 11% 12% 13% 14% 15% 16% 17% 18% 19% 20% 21% 22% 23% 24% 25% 26% 27% 28% 29% 30% 31% 32% 33% 34% 35% 36% 37% 38% 39% 40% 41% 42% 43% 44% 45% 46
% 47% 48% 49% 50% 51% 52% 53% 54% 55% 56% 57% 58% 59% 60% 61% 62% 63% 64% 65% 66% 67% 68% 69% 70% 71% 72% 73% 74% 75% 76% 77% 78% 79% 80% 81% 82% 83% 84% 85% 86% 87% 88% 89% 90% 91% 92% 93
% 94% 95% 96% 97% 98% 99% 100%
EVENT: java.io.IOException + javax.crypto.BadPaddingException occurred, likely due to timeout on device during adb backup. Is almost sure that you didn't backup all the data you expected.

$ tar -tf Samsung.tar > /dev/null
tar: Unexpected EOF in archive
tar: Error is not recoverable: exiting now

-- start of adb logcat during backup --
06:47:18.984 12898 12898 D AndroidRuntime: Calling main entry com.android.commands.bu.Backup
06:47:18.989 12898 12898 D bu      : Beginning: backup
06:47:18.995  1009  3739 V BackupManagerService: [UserID:0] Requesting backup: apks=true obb=true shared=true all=true system=true includekeyvalue=false pkgs=[Ljava.lang.String;@54b016c
06:47:18.995  1009  3739 I BackupManagerService: [UserID:0] Beginning adb backup...
06:47:19.001  1009  3739 D BackupManagerService: isPrivilegeBackupApp() pkg name is android.uid.shell:2000 value : false
06:47:19.001  1009  3739 D BackupManagerService: [UserID:0] Starting backup confirmation UI, token=272643585
06:47:19.045  1009  3739 D BackupManagerService: [UserID:0] Waiting for backup completion...
06:47:23.494  1009  3729 D BackupManagerService: [UserID:0] acknowledgeAdbBackupOrRestore : token=272643585 allow=true
06:47:23.494  1009  3729 V BackupManagerService: [UserID:0] Acquired wakelock:*backup*-0-3651
06:47:23.505  1009 13090 I BackupManagerService: --- Performing adb backup ---
06:47:23.508  1009 13090 D StorageManagerService: getExternalStorageMountMode: uid=10093 packageName=com.android.sharedstoragebackup
06:47:23.558  1009  3772 I ActivityManager: DSS OFF for com.android.sharedstoragebackup
06:54:29.400  1009 13090 I BackupManagerService: --- Performing full backup for package com.android.sharedstoragebackup ---
06:54:29.401  1009 13090 D BackupManagerService: [UserID:0] awaiting agent for ApplicationInfo{89a1362 com.android.sharedstoragebackup}
06:54:29.411  1009  3817 D BackupManagerService: [UserID:0] agentConnected pkg=com.android.sharedstoragebackup agent=android.os.BinderProxy@2bafdb0
06:54:29.411  1009 13090 I BackupManagerService: [UserID:0] got agent android.app.IBackupAgent$Stub$Proxy@909d429
06:54:29.413  1009 20451 D BackupManagerService: Calling doFullBackup() on com.android.sharedstoragebackup
06:54:29.415 13091 13109 I SharedStorageAgent: Backing up 2 shared volumes
06:57:19.148  1009  3651 I BackupManagerService: sep backup/restore timed out waiting for success
06:57:19.149  1009  3739 D BackupManagerService: [UserID:0] Adb backup processing complete.
06:57:19.152  1009  3651 V BackupManagerService: [UserID:0] Released wakelock:*backup*-0-3651
06:57:19.154  1009 13090 E BackupManagerService: Error backing up com.android.sharedstoragebackup: write interrupted
06:57:19.154  1009 13090 W BackupManagerService: Error bringing down backup stack
06:57:19.155 12898 12898 D bu      : Finished.
06:57:19.156  1009 13090 W BackupManagerService: Error attempting to finalize backup stream
06:57:19.157  1009 13090 E BackupManagerService: IO error closing adb backup file: write failed: EBADF (Bad file descriptor)
06:57:19.160 12898 12898 D AndroidRuntime: Shutting down VM
06:57:19.163  1009 13090 D BackupManagerService: Full backup pass complete.
-- end of adb logcat --
(END OF EXAMPLE 3)
(START OF EXAMPLE 4)
Example 4: LG phone with Android 10.

$ time adb backup -f LG.ab -shared -obb -all -system -apk
WARNING: adb backup is deprecated and may be removed in a future release
Now unlock your device and confirm the backup operation...

real    35m4.285s
user    0m0.000s
sys     0m0.031s

	* Toast message on device screen when backup stops is "Backup finished". Doesn't mention any timeout.

$ java -jar abp.jar -debug unpack LG.ab LG.tar 1234
android-backup-processor v20210805
Strong AES encryption allowed
"LG.ab" size is 24844392069 bytes
Magic: ANDROID BACKUP
Version: 5
Compressed: 1
Algorithm: AES-256
Encrypted: 1
IV: D713979B9985FC2CB51994DC960DB03E
MK: 145A3ECF07C4BB2C30913EF5CC5979BA420E3E7C5A975EEF29620D6FD5278DA0
MK checksum: D1A9EAA2CC75AC90ADBFE4A216015D9B653F541DD282921325A9396169D67250
key bytes: 145A3ECF07C4BB2C30913EF5CC5979BA420E3E7C5A975EEF29620D6FD5278DA0
salt bytes: E256F2FA46722DF3325B9ADCFBAF32B4BACA6D01DE45360A75D69EBC86C47E4397E3128669D050C47950B1138B9D999991A6E060EF19FCA7CC26CD75
CDC0D68E
o?'??]string: [¶Z>???,0?>??Yy?B♫>|Z?^?)b
Key format: RAW
Calculated MK checksum (use UTF-8: true): D1A9EAA2CC75AC90ADBFE4A216015D9B653F541DD282921325A9396169D67250
Extracting: 0% 1% 2% 3% 4% 5% 6% 7% 8% 9% 10% 11% 12% 13% 14% 15% 16% 17% 18% 19% 20% 21% 22% 23% 24% 25% 26% 27% 28% 29% 30% 31% 32
% 33% 34% 35% 36% 37% 38% 39% 40% 41% 42% 43% 44% 45% 46% 47% 48% 49% 50% 51% 52% 53% 54% 55% 56% 57% 58% 59% 60% 61% 62% 63% 64% 65
% 66% 67% 68% 69% 70% 71% 72% 73% 74% 75% 76% 77% 78% 79% 80% 81% 82% 83% 84% 85% 86% 87% 88% 89% 90% 91% 92% 93% 94% 95% 96% 97% 98
% 99% 100%
24844392069 bytes read from "LG.ab"
25193064960 bytes written to "LG.tar"

$ tar -tf LG.tar > /dev/null
tar: Unexpected EOF in archive
tar: Error is not recoverable: exiting now

-- start of adb logcat during backup --
09:31:52.707 22647 22647 D AndroidRuntime: Calling main entry com.android.commands.bu.Backup
09:31:52.710 22647 22647 D bu      : Beginning: backup
09:31:52.718  3901  4450 V BackupManagerService: Requesting backup: apks=true obb=true shared=true all=true system=true includekeyvalue=false pkgs=[Ljava.lang.String;@bf19cbb
09:31:52.718  3901  4450 I BackupManagerService: Beginning adb backup...
09:31:52.718  3901  4450 D BackupManagerService: Starting backup confirmation UI, token=1682063260
09:31:52.732  3901  4450 D BackupManagerService: Waiting for backup completion...
09:31:56.995  3901 12483 D BackupManagerService: acknowledgeAdbBackupOrRestore : token=1682063260 allow=true
09:31:56.998  3901 22678 I BackupManagerService: --- Performing adb backup ---
09:36:55.614  3901 22678 I BackupManagerService: --- Performing full backup for package com.android.sharedstoragebackup ---
09:36:55.614  3901 22678 D BackupManagerService: awaiting agent for ApplicationInfo{1ab2208 com.android.sharedstoragebackup}
09:36:55.620  3901  3919 D BackupManagerService: agentConnected pkg=com.android.sharedstoragebackup agent=android.os.BinderProxy@63bca29
09:36:55.620  3901 22678 I BackupManagerService: got agent android.app.IBackupAgent$Stub$Proxy@3c3c1ae
09:36:55.621  3901 26367 D BackupManagerService: Calling doFullBackup() on com.android.sharedstoragebackup
09:36:55.621  3901 26367 V BackupAgentTimeout: getSharedBackupAgentTimeoutMillis(): 1800000
09:36:55.625 22679 22698 I SharedStorageAgent: Backing up 2 shared volumes
10:06:55.631  3901  5643 D BackupManagerService: Timeout message received for token=4e9ece9d
10:06:55.635  3901  5643 V BackupManagerService: Cancel: token=4e9ece9d
10:06:55.636  3901  5643 W BackupManagerService: adb backup cancel of PackageInfo{e962418 com.android.sharedstoragebackup}
10:06:55.647 22679 22679 I Process : Sending signal. PID: 22679 SIG: 9
10:06:55.673  3901 22678 E BackupManagerService: Unexpectedly reached end of file while reading data
10:06:55.673  3901 22678 E BackupManagerService: Error backing up com.android.sharedstoragebackup: null
10:06:55.674  3901 22678 W ActivityManager: Unbinding backup agent with no active backup
10:06:55.677  3472  3472 I Zygote  : Process 22679 exited due to signal 9 (Killed)
10:06:55.683  3901  6495 I ActivityManager: Process com.android.sharedstoragebackup (pid 22679) has died: vis IMPF
10:06:55.683  3901  4450 D BackupManagerService: Adb backup processing complete.
10:06:55.684 22647 22647 D bu      : Finished.
10:06:55.684  3901  6495 W ActivityManager: Scheduling restart of crashed service com.android.sharedstoragebackup/.ObbBackupService in 1000ms
10:06:55.684 22647 22647 D AndroidRuntime: Shutting down VM
10:06:55.688  3901 22678 D BackupManagerService: Full backup pass complete.
-- end of adb logcat --
(END OF EXAMPLE 4)


CHANGELOG:

(Initial)
(6 June 2014)
- Changed "enabled" to "allowed" and "not enabled" to "not allowed" relating to strong AES encryption because some user complained
- JDK updated to 1.7.0_60, mixed mode
- Added support for android 4.4 encrypted backups
- Removed bcprov-jdk15on documentation
- Updated Bouncy Castle source (JCE with provider and lightweight API) from 1.48 to 1.50
- Updated adb-split.sh to add "${SCRIPT_DIR}/" before abe.jar
- Eclipse updated from 20130225-0426 to 20140224-0627

(15 June 2014)
- Updated program to create version 2 backups for android 4.4.3+.

(30 June 2014)
- Added backup version 3 for android L. No real code changes.

(19 October 2015)
- Updated Bouncy Castle source (JCE with provider and lightweight API) from 1.50 to 1.53
- Replace "%s." with "%s" in AndroidBackup.java for better reading
- Added VERSION to usage()
- Added star-1.5.3-i686-pc-cygwin/star.exe compiled from schily-dist-pre2.tar.xz (2015-10-16)
- JDK updated to 1.8.0_60-b27
- Updated README.TXT
- Updated Main.java to add JCE 8 to usage()
- Created VERSION.TXT
- Created folder Doc
	Move there "Full local backup infrastructure.txt"
	Copy there README.TXT from Tar Binary Splitter
	Copy there README.TXT from Android Backup Splitter
- Added Tar Binary Splitter v4.0 (tar-bin-split.jar) to split adb backups without issues.
- Updated Android Backup Splitter
	Extraction version
	Non Extraction version

(20 October 2015)
- Exit 0 if no arguments are given
- Detect when password is provided but strong AES encryption is not allowed, so warn user and exit 1
- Compiler compliance level set to Java 1.7. Changes "Major.Minor Version" from 52.0 to 51.0. Previous version was 1.8
- Updated Tar Binary Splitter to v4.1 with its Readme

(22 October 2015)
- Added method requirePassword() to demand password from user when no one is present but backup is encrypted

(23 October 2015)
- Improved routines for detecting when a password is required
- Improbed routines for detecting when JCE USJPF is required

(24 October 2015)
- Added ability to read password from any specified environment variable

(25 October 2015)
- Send non usage messages to stderr instead of stdout. This allows pipelining to stdout only real data
- Now debug is user selectable with the -debug flag
- Flag "USEENV=" replaced with "-useenv="

(26 October 2015)
- Support "-" for stdin/stdout (pipes)

(2 November 2015)
- Improved README.TXT
- JRE and JDK updated to 1.8.0_66
- star.exe 1.5.3 compiled from schily-dist-pre4.tar.xz (27-10-2015)

(8 July 2016)
- JRE and JDK updated to Oracle Java 1.8.0_91
- Add support for android Nougat (android 7.x)
- Eclipse updated to Version: Mars.2 Release (4.5.2) Build id: 20160218-0600

(9 July 2016)
- Updated Bouncy Castle source (JCE with provider and lightweight API) from 1.53 to 1.54

(10 July 2016)
- Fix issue to support android Nougat on extractAsTar routine also (only present on infoBackup on the two previous versions)

(27 August 2017)
- Display percentage of progress when unpacking ab to tar, when the -debug flag is used.

(28 August 2017)
- Android backup version is no longer checked. This helps support future version without having to recompile provided that the format is the same.

(22 September 2017)
- Android backup version is no longer checked (fixed):
Now the function extractAsTar also ignores the backup version. Previous build only ignored it for infoBackup so you could check it but not extract it.

(05 October 2017)
- Added support to view information from MIUI backups and extract them.
- Now the program will exit if "ANDROID BACKUP" is not detected by extractAsTar(). Inside infoBackup() was already present.
- Improved README.TXT with a MIUI and adb backup example.

(03 February 2018)
- Improved README.TXT
- Android Backup Extractor is now android-backup-extractor
- Updated Bouncy Castle source (JCE with provider and lightweight API) from 1.54 to 1.59
- The following items were removed from android-backup-extractor, now are on android-backup-toolkit and its own sourceforge site:
	adb-split-extraction.sh
	adb-split-no-extraction.sh
	tar-bin-split.jar
	star-ubuntu-lucid
	star-1.5.2-i686-pc-cygwin
	star-1.5.3-i686-pc-cygwin

(20 May 2018)
- Print newline after 100% completion when debug is enabled
- JRE and JDK updated to Oracle Java 1.8.0_172
- Eclipse updated to version: Oxygen.3a Release (4.7.3a); build id: 20180405-1200

(21 May 2018)
- Fixed a bug introduced in v20180520 where a newline was printed to stdout instead of stderr
- abe version is printed when debug is enabled
- All information, included debug and usage(), is output to stderr instead of stdout, for better pipelining
- Current progress is displayed in percentage, for unpack, pack and pack-kk, when debug is enabled
- Display size of both input and output files, when debug is enabled
- Display size of backup with info option, when debug is enabled
- Improved README.TXT

(30 May 2021)
- Improved README.TXT
- Updated Bouncy Castle source (JCE with provider and lightweight API) from 1.59 to 1.68, so it fixes these messages from Java 12 GA (build 12+33) onwards:
	WARNING: An illegal reflective access operation has occurred
	WARNING: Illegal reflective access by org.bouncycastle.jcajce.provider.drbg.DRBG (rsrc:./) to constructor sun.security.provider.Sun()
	WARNING: Please consider reporting this to the maintainers of org.bouncycastle.jcajce.provider.drbg.DRBG
	WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
	WARNING: All illegal access operations will be denied in a future release
- Eclipse IDE for Java Developers updated to: Version: 2021-03 (4.19.0); Build id: 20210312-0638
- JDK udpated to Oracle Java JDK 15.0.2
- Patched packTar so doesn't display "bytes written" without debug flag
- Output Encrypted=1/0 when debug is enabled (for now equals to having Algorithm=AES-256/none)
- Output Compressed=1/0 when debug is enabled. For now only compressed is supported
- Replaced variable abfileSize with abfileSizelong in packTar()
- Replaced variable totalRead with tarfileSizelong
- Added "Extracting: " before percentage to extractAsTar()
- Added "Packaging: " before percentage to packTar()
- Fixed extractAsTar() to output also 100% when percentDone didn't reach the value
- Now a warning is displayed when ADB backup file is presumed to be empty (empty backup).

(28 July 2021)
- Fork renamed from android-backup-extractor to android-backup-processor
- abe.jar is now abp.jar
- Improved README.TXT
- Eclipse IDE for Java Developers updated to: Version: 2021-06 (4.20.0); Build id: 20210612-2011
- Updated Bouncy Castle source (JCE with provider and lightweight API) from 1.68 to 1.69
- Added NAME variable and call displayversion() inside usage()
- Prevent crash when running the subcommand "info" or "unpack" on 0 bytes backup
- Visual improvements in usage()
- star binaries moved to android-backup-tookit

(05 August 2021)
- Improved README.TXT
- Mark backups as empty for sizes less than 613 bytes when encrypted and 104 bytes when not, according to experimental values which set the bare minimum
- Source code and binary go in a single release
- Added several backup examples in "ab-examples" folder

(12 August 2021)
- Improved README.TXT
- Minor visual tweaks
- Added detection of backup version but don't interfere with operation
- Added detection during unpack of IOException + BadPaddingException, which means corrupt backup ending because of adb backup timeout
- Added detection during unpack or info of BadPaddingException only, which means wrong password
- Added Android backup source code in folder: Doc/android.googlesource.com_backup_20210813/
- Added function checkFileExists() to check if input file exists
