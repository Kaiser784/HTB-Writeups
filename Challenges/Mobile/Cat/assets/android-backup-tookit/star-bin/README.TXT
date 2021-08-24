This project is a compilation of star (tar archiving tool) binaries.
Project file listing:

$ find star-bin -type d -exec sh -c 'printf "%s/\n" "$0"' {} \; -or -print
star-bin/
star-bin/How-to-install-star-on-Cygwin.txt
star-bin/How-to-install-star-on-macOS.txt
star-bin/install-star-cygwin.sh
star-bin/README.TXT
star-bin/remove-star-cygwin.sh
star-bin/star-1.5.2-i686-pc-cygwin/
star-bin/star-1.5.2-i686-pc-cygwin/star.exe
star-bin/star-1.5.3-i686-pc-cygwin/
star-bin/star-1.5.3-i686-pc-cygwin/star.exe
star-bin/star-1.6-i686-cygwin32_nt-gcc/
star-bin/star-1.6-i686-cygwin32_nt-gcc/man/
star-bin/star-1.6-i686-cygwin32_nt-gcc/man/star.1
star-bin/star-1.6-i686-cygwin32_nt-gcc/man/star.5
star-bin/star-1.6-i686-cygwin32_nt-gcc/star.exe
star-bin/star-1.6-x86_64-cygwin32_nt-gcc/
star-bin/star-1.6-x86_64-cygwin32_nt-gcc/man/
star-bin/star-1.6-x86_64-cygwin32_nt-gcc/man/star.1
star-bin/star-1.6-x86_64-cygwin32_nt-gcc/man/star.5
star-bin/star-1.6-x86_64-cygwin32_nt-gcc/star.exe
star-bin/star-ubuntu/
star-bin/star-ubuntu/Index_of_ubuntu_pool_universe_s_star.htm
star-bin/star-ubuntu/star_1.5final-2ubuntu1_hppa.deb
star-bin/star-ubuntu/star_1.5final-2ubuntu1_lpia.deb
star-bin/star-ubuntu/star_1.5final-2ubuntu2_amd64.deb
star-bin/star-ubuntu/star_1.5final-2ubuntu2_i386.deb
star-bin/star-ubuntu/star_1.5final-2ubuntu2_ia64.deb
star-bin/star-ubuntu/star_1.5final-2ubuntu2_powerpc.deb
star-bin/star-ubuntu/star_1.5final-2ubuntu2_sparc.deb
star-bin/star_sym-1.6-i686-cygwin32_nt-gcc/
star-bin/star_sym-1.6-i686-cygwin32_nt-gcc/man/
star-bin/star_sym-1.6-i686-cygwin32_nt-gcc/man/star_sym.1
star-bin/star_sym-1.6-i686-cygwin32_nt-gcc/star_sym.exe
star-bin/star_sym-1.6-x86_64-cygwin32_nt-gcc/
star-bin/star_sym-1.6-x86_64-cygwin32_nt-gcc/man/
star-bin/star_sym-1.6-x86_64-cygwin32_nt-gcc/man/star_sym.1
star-bin/star_sym-1.6-x86_64-cygwin32_nt-gcc/star_sym.exe

Cygwin binaries version 1.6 are compiled from source code in UTF-8:
https://sourceforge.net/projects/schilytools-utf8/

Changelog:
20210818
- Added latest official ubuntu packages for all architectures
- Added star and star_sym 1.6 for Cygwin Intel-based 32 and 64 bits
- Added README.TXT and improved documentation
- Added scripts to install and uninstall star and star_sym on Cygwin

Previous versions
- Added star 1.5.2 and 1.5.3 for 32 bits Cygwin
- Added Readme for macOS
- Added star ubuntu packages for i386 and amd64
