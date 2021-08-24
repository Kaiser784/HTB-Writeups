/** 
	Copyright 2021 dragomerlin
	
 	This file is part of tar-binary-splitter.
  
    tar-binary-splitter is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    tar-binary-splitter is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Tar Binary Splitter. If not, see <http://www.gnu.org/licenses/>.
 */

/* POSIX header from src/tar.h*/

//struct posix_header
//{				/* byte offset */
//  char name[100];		/*   0 */
//  char mode[8];			/* 100 */
//  char uid[8];			/* 108 */
//  char gid[8];			/* 116 */
//  char size[12];		/* 124 */
//  char mtime[12];		/* 136 */
//  char chksum[8];		/* 148 */
//  char typeflag;		/* 156 */
//  char linkname[100];		/* 157 */
//  char magic[6];		/* 257 */
//  char version[2];		/* 263 */
//  char uname[32];		/* 265 */
//  char gname[32];		/* 297 */
//  char devmajor[8];		/* 329 */
//  char devminor[8];		/* 337 */
//  char prefix[155];		/* 345 */
//				/* 500 */
//};

package dragomerlin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class TarSplitMain {
	public static final int HELIUM_CARBON_HEADER = 31;
	public static final int TAR_HEADER_SIZE = 512;
	public static final int TAR_NAME_SIZE = 100;
	public static final int TAR_SIZE_START = 124;
	public static final int TAR_SIZE_SIZE = 12;
	public static final int TAR_CHKSUM_START = 148;
	public static final int TAR_CHKSUM_SIZE = 8;
	public static final int TAR_TYPEFLAG_START = 156;
	
	public static final String TAR_BIN_SPLIT_NAME = "tar-binary-splitter";
	public static final String TAR_BIN_SPLIT_VERSION = "v20210814";
	public static final int CHUNK_SIZE = 4*1024*1024; // Always use power of 2!
	public static final String APP_PREFIX = "apps/";
	public static final String SHARED_PREFIX = "shared/";
	public static final String AB_PATH_SEPARATOR = "/";
	
	public static void main(String args[]) throws Exception {
		
		if (args.length < 2) {
			usage();
        	System.exit(0);
        }
		
		File maintarfile = new File(args[1]);
		if (!maintarfile.isFile() || !maintarfile.exists()){
			System.err.println("File " + args[1] + " doesn't exist.");
			System.exit(2);
		}
//		System.err.println("Integer MAX value is " + Integer.MAX_VALUE);
		
		if (args[0].equals("-extract-archives")){
			// Create a sequentially numbered archive for each archive
			unTar(maintarfile);
			System.exit(0);
		}	else if (args[0].equals("-split-subtar")){
			// Create a sequentially numbered archive for each subtar
			split_subtar(maintarfile);
			System.err.println("Single subtar splitting done.");
			System.exit(0);
		}	else if (args[0].equals("-extract-name")){
			// Extract all the files from the tar (regular files ONLY!)
			extractfile(maintarfile);
			System.exit(0);
		}	else if (args[0].equals("-split-android")){
			// Group subtars by android's applicationId or combined sdcard
			split_android(maintarfile);
			System.err.println("Android splitting with combined shared done.");
			System.exit(0);
		}	else if (args[0].equals("-split-android-shared")){
			// Group subtars by android's applicationId or individual sdcard
			split_android_shared(maintarfile);
			System.err.println("Android splitting with individual shared done.");
			System.exit(0);
		}	else {
			System.err.println("Invalid option " + args[0]);
			System.exit(1);
			}
	}
	
	
	public static void usage(){
		System.err.println(TAR_BIN_SPLIT_NAME + " " + TAR_BIN_SPLIT_VERSION);
		System.err.println("Split to individual subtar each item:");
		System.err.println("\tjava -jar tar-bin-split.jar -split-subtar <archive.tar>");
		System.err.println("Resulting archives go into split-subtar directory");
		System.err.println("Split by individual android app and common shared:");
		System.err.println("\tjava -jar tar-bin-split.jar -split-android <archive.tar>");
		System.err.println("Resulting archives go into split-android directory");
		System.err.println("Split by individual android app and individual shared:");
		System.err.println("\tjava -jar tar-bin-split.jar -split-android-shared <archive.tar>");
		System.err.println("Resulting archives go into split-android directory");
	}
	
	
	
	/**
	 * @param maintarfile 
	 * @throws Exception
	 */
	public static void split_android(File maintarfile) throws Exception{
		/* Read TAR File into TarArchiveInputStream */
		TarArchiveInputStream myTarFile=new TarArchiveInputStream(new FileInputStream(maintarfile));
		/* To read individual TAR file */
		TarArchiveEntry entry = null;
		/* Create a loop to read every single entry in TAR file */
		boolean doloop = true;
		long start = 0;
		long end = 0;
		boolean firstread = false;
		int filecounter = 0;
		ArrayList<String> individualnameArrayList = new ArrayList<String>(); //Use out of loop
		String workingdir = new File(System.getProperty("user.dir")).toString();
		String out_dir = "split-android";
		File newdir = new File(workingdir + File.separator + out_dir);
		// Create output directory if doesn't exist
		if (!newdir.exists())
			newdir.mkdirs();                
		while (doloop) {
			if (firstread && end >= start){
				filecounter++;
			}

			doloop = myTarFile.getNextTarEntry_1_of_2_docontinue();
			if (doloop == false){
				System.err.println("doloop is false");
				break;
			}
			end = (long) myTarFile.getBytesRead();
			//        	System.err.println("Read bytes I: " + end);
			// Check if there's some actual data
			if (end > 0){
				// Write the current subtar
				// Make it work if end-start is bigger than integer. Prevent java.lang.NegativeArraySizeException. MAX Int = 2147483647
				// Prevent "java.lang.OutOfMemoryError: Requested array size exceeds VM limit" when creating byte[] content.
				boolean cont = true;
				String individualname = entry.getName();
				//        		System.err.println("Individual name is " + individualname);
				if (individualname.startsWith(APP_PREFIX)){
					// Get android applicationId or shared
					individualname = individualname.substring(APP_PREFIX.length(), individualname.indexOf(AB_PATH_SEPARATOR, APP_PREFIX.length()));
				} else if (individualname.startsWith(SHARED_PREFIX)){
					// Each sdcard is represented by a directory index starting at 0.
					// If any sdcard is present, it will start with "shared/0/", then "shared/1/", etc. 
					// "shared/" only with files directly inside should not occur.
					// "shared/0" directory would mean empty shared, which should not occur,
					// 		each shared only appears if there's at least something inside.
					// All sdcards go into a single file
					individualname = "shared";
				} else {
					System.err.println("Prefix not recognized. Probably not an android adb backup. Please report bug!");
					System.exit(3);
				}
				String fileout = workingdir + File.separator + out_dir + File.separator + individualname + ".tar";
				// Remove fileout if already exists in current dir, because we always append
				if (!individualnameArrayList.contains(individualname)){
					File filetodelete = new File(fileout);
					if (filetodelete.exists()){
						if (filetodelete.isFile()){
							if (!filetodelete.delete()){
								System.err.println("Couldn't delete " + fileout + ": please check write permissions!");
								System.exit(4);
							}
						} else {
							System.err.println("A directory with the name \"" + individualname + ".tar\"" + " already exists.");
							System.err.println("Please delete or rename it before continuing!");
							System.exit(5);
						}

					}
					individualnameArrayList.add(individualname);
				}
				RandomAccessFile in = new RandomAccessFile(maintarfile, "r");
				RandomAccessFile out = new RandomAccessFile(fileout, "rw");
				// Position at the end in case the file already exists
				out.seek(out.length());
				while (cont){
					long distance = end - start;
					if (distance >= (long)CHUNK_SIZE){
						byte content[] = new byte[CHUNK_SIZE];
						in.seek(start);
						in.readFully(content, 0, CHUNK_SIZE);
						out.write(content);
						start += CHUNK_SIZE;
					} else {
						byte content[] = new byte[(int) distance];
						in.seek(start);
						in.readFully(content, 0, (int) distance);
						out.write(content);
						cont = false;
					}
				}

				in.close();
				out.close();
			}
			// Get ready for the next iteration
			start = end;
			entry = myTarFile.getNextTarEntry_2_of_2();
			//        	System.err.println("Read bytes T: " + myTarFile.getBytesRead());
			if (entry == null){
				//        		System.err.println("entry is null. Done");
				break;
			}

			firstread = true;
		}               
		/* Close TarAchiveInputStream */
		myTarFile.close();
	}
	
	
	
	/**
	 * @param maintarfile 
	 * @throws Exception
	 */
	public static void split_android_shared(File maintarfile) throws Exception{
		/* Read TAR File into TarArchiveInputStream */
		TarArchiveInputStream myTarFile=new TarArchiveInputStream(new FileInputStream(maintarfile));
		/* To read individual TAR file */
		TarArchiveEntry entry = null;
		/* Create a loop to read every single entry in TAR file */
		boolean doloop = true;
		long start = 0;
		long end = 0;
		boolean firstread = false;
		int filecounter = 0;
		ArrayList<String> individualnameArrayList = new ArrayList<String>(); //Use out of loop
		String workingdir = new File(System.getProperty("user.dir")).toString();
		String out_dir = "split-android";
		File newdir = new File(workingdir + File.separator + out_dir);
		// Create output directory if doesn't exist
		if (!newdir.exists())
			newdir.mkdirs();                
		while (doloop) {
			if (firstread && end >= start){
				filecounter++;
			}

			doloop = myTarFile.getNextTarEntry_1_of_2_docontinue();
			if (doloop == false){
				System.err.println("doloop is false");
				break;
			}
			end = (long) myTarFile.getBytesRead();
			//        	System.err.println("Read bytes I: " + end);
			// Check if there's some actual data
			if (end > 0){
				// Write the current subtar
				// Make it work if end-start is bigger than integer. Prevent java.lang.NegativeArraySizeException. MAX Int = 2147483647
				// Prevent "java.lang.OutOfMemoryError: Requested array size exceeds VM limit" when creating byte[] content.
				boolean cont = true;
				String individualname = entry.getName();
				if (individualname.startsWith(APP_PREFIX)){
					// Get android applicationId or shared
					individualname = individualname.substring(APP_PREFIX.length(), individualname.indexOf(AB_PATH_SEPARATOR, APP_PREFIX.length()));
				} else if (individualname.startsWith(SHARED_PREFIX)){
					// Each sdcard is represented by a directory index starting at 0.
					// If any sdcard is present, it will start with "shared/0/", then "shared/1/", etc. 
					// "shared/" only with files directly inside should not occur.
					// "shared/0" directory would mean empty shared, which should not occur,
					// 		each shared only appears if there's at least something inside.
					String shared_index = null;
					shared_index = individualname.substring(SHARED_PREFIX.length(), individualname.indexOf(AB_PATH_SEPARATOR, SHARED_PREFIX.length()));
					// Split shared
					individualname = "shared_" + shared_index;
				} else {
					System.err.println("Prefix not recognized. Probably not an android adb backup. Please report bug!");
					System.exit(3);
				}
				String fileout = workingdir + File.separator + out_dir + File.separator + individualname + ".tar";
				// Remove fileout if already exists in current dir, because we always append
				if (!individualnameArrayList.contains(individualname)){
					File filetodelete = new File(fileout);
					if (filetodelete.exists()){
						if (filetodelete.isFile()){
							if (!filetodelete.delete()){
								System.err.println("Couldn't delete " + fileout + ": please check write permissions!");
								System.exit(4);
							}
						} else {
							System.err.println("A directory with the name \"" + individualname + ".tar\"" + " already exists.");
							System.err.println("Please delete or rename it before continuing!");
							System.exit(5);
						}

					}
					individualnameArrayList.add(individualname);
				}
				RandomAccessFile in = new RandomAccessFile(maintarfile, "r");
				RandomAccessFile out = new RandomAccessFile(fileout, "rw");
				// Position at the end in case the file already exists
				out.seek(out.length());
				while (cont){
					long distance = end - start;
					if (distance >= (long)CHUNK_SIZE){
						byte content[] = new byte[CHUNK_SIZE];
						in.seek(start);
						in.readFully(content, 0, CHUNK_SIZE);
						out.write(content);
						start += CHUNK_SIZE;
					} else {
						byte content[] = new byte[(int) distance];
						in.seek(start);
						in.readFully(content, 0, (int) distance);
						out.write(content);
						cont = false;
					}
				}

				in.close();
				out.close();
			}
			// Get ready for the next iteration
			start = end;
			entry = myTarFile.getNextTarEntry_2_of_2();
			//        	System.err.println("Read bytes T: " + myTarFile.getBytesRead());
			if (entry == null){
				//        		System.err.println("entry is null. Done");
				break;
			}

			firstread = true;
		}               
		/* Close TarAchiveInputStream */
		myTarFile.close();
	}
	
	
	
	/**
	 * @param maintarfile 
	 * @throws Exception
	 */
	public static void split_subtar(File maintarfile) throws Exception{
		/* Read TAR File into TarArchiveInputStream */
		//TarArchiveInputStream myTarFile=new TarArchiveInputStream(new FileInputStream(new File("tar_ball.tar")));
		TarArchiveInputStream myTarFile=new TarArchiveInputStream(new FileInputStream(maintarfile));
		/* To read individual TAR file */
		TarArchiveEntry entry = null;
		//      FileOutputStream outputFile=null;
		/* Create a loop to read every single entry in TAR file */
		boolean doloop = true;
		long start = 0;
		long end = 0;
		boolean firstread = false;
		int filecounter = 0;
		int offset=0;
		int offset_header=0;
		String workingdir = new File(System.getProperty("user.dir")).toString();
		String out_dir = "split-subtar";
		File newdir = new File(workingdir + File.separator + out_dir);
		// Create output directory if doesn't exist
		if (!newdir.exists())
			newdir.mkdirs();                
		while (doloop) {
			if (firstread && end >= start){
				// Create the name of the file
				String filename = workingdir + File.separator + out_dir + File.separator + Integer.toString(filecounter) + ".archive";
				filecounter++;
				//        		/* Get Size of the file and create a byte array for the size */
				//        		//byte[] content = new byte[(int) (end - start)];
				//        		byte[] content = new byte[(int) entry.getSize()];
				//        		firstread = true;
				//        		// Read file from the archive into byte array
				//        		myTarFile.read(content, offset, content.length - offset);

				// Define OutputStream for writing the file
				////outputFile=new FileOutputStream(new File(filename));
				// Use IOUtiles to write content of byte array to physical file
				////org.apache.commons.io.IOUtils.write(content,outputFile);              
				// Close Output Stream
				////outputFile.close();

				//        	/* Get the name of the file */
				//                individualFiles = entry.getName();
				//                /* Get Size of the file and create a byte array for the size */
				//                byte[] content = new byte[(int) entry.getSize()];
				//                offset=0;
				//                /* Some SOP statements to check progress */
				//                System.err.println("File Name in TAR File is: " + individualFiles);
				//                System.err.println("Size of the File is: " + entry.getSize());                  
				//                System.err.println("Byte Array length: " + content.length);
				//                /* Read file from the archive into byte array */
				//                myTarFile.read(content, offset, content.length - offset);
				//                /* Define OutputStream for writing the file */
				//                outputFile=new FileOutputStream(new File(individualFiles));
				//                /* Use IOUtiles to write content of byte array to physical file */
				//                org.apache.commons.io.IOUtils.write(content,outputFile);              
				//                /* Close Output Stream */
				//                outputFile.close();
				//               // System.err.println(myTarFile.getNextTarEntry().);
				//                //
			}


			doloop = myTarFile.getNextTarEntry_1_of_2_docontinue();
			if (doloop == false){
				System.err.println("doloop is false");
				break;
			}
			end = (long) myTarFile.getBytesRead();
			//        	System.err.println("Read bytes I: " + end);
			// Check if there's some actual data
			if (end > 0){
				// Write the current subtar
				String fileout = workingdir + File.separator + out_dir + File.separator + Integer.toString(filecounter) + ".subtar";
				RandomAccessFile in = new RandomAccessFile(maintarfile, "r");
				RandomAccessFile out = new RandomAccessFile(fileout, "rw");

				// Make it work if end-start is bigger than integer. Prevent java.lang.NegativeArraySizeException. MAX Int = 2147483647
				// Prevent "java.lang.OutOfMemoryError: Requested array size exceeds VM limit" when creating byte[] content.
				boolean cont = true;
				while (cont){
					long distance = end - start;
					if (distance >= (long)CHUNK_SIZE){
						byte content[] = new byte[CHUNK_SIZE];
						in.seek(start);
						in.readFully(content, 0, CHUNK_SIZE);
						out.write(content);
						start += CHUNK_SIZE;
					} else {
						byte content[] = new byte[(int) distance];
						in.seek(start);
						in.readFully(content, 0, (int) distance);
						out.write(content);
						cont = false;
					}
				}

				in.close();
				out.close();
			}
			// Get ready for the next iteration
			start = end;

			entry = myTarFile.getNextTarEntry_2_of_2();
			//        	System.err.println("Read bytes T: " + myTarFile.getBytesRead());
			offset_header = (int) myTarFile.getBytesRead();
			if (entry == null){
				//        		System.err.println("entry is null. Done");
				break;
			}

			firstread = true;
		}               
		/* Close TarAchiveInputStream */
		myTarFile.close();
	}




	
    // TODO unfinished
    public static  void unTar(File maintarfile) throws Exception{
    	/* Read TAR File into TarArchiveInputStream */
    	TarArchiveInputStream myTarFile=new TarArchiveInputStream(new FileInputStream(maintarfile));
    	/* To read individual TAR file */
    	TarArchiveEntry entry = null;
    	FileOutputStream outputFile=null;
    	/* Create a loop to read every single entry in TAR file */
    	boolean doloop = true;
    	long start = 0;
    	long end = 0;
    	boolean firstread = false;
    	int filecounter = 1;
    	int offset=0;
    	int offset_header=0;
    	String workingdir = new File(System.getProperty("user.dir")).toString();
    	String out_dir = "split-out";
    	File newdir = new File(workingdir + File.separator + out_dir);
    	// Create output directory if doesn't exist
    	if (!newdir.exists())
    		newdir.mkdirs();                
    	while (doloop) {
    		if (firstread && end >= start){
    			// Create the name of the file
    			String filename = workingdir + File.separator + out_dir + File.separator + Integer.toString(filecounter) + ".archive";
    			filecounter++;
    			/* Get Size of the file and create a byte array for the size */
    			//byte[] content = new byte[(int) (end - start)];
    			byte[] content = new byte[(int) entry.getSize()];
    			firstread = true;
    			// Read file from the archive into byte array
    			myTarFile.read(content, offset, content.length - offset);

    			// Define OutputStream for writing the file
    			outputFile=new FileOutputStream(new File(filename));
    			// Use IOUtiles to write content of byte array to physical file
    			org.apache.commons.io.IOUtils.write(content,outputFile);              
    			// Close Output Stream
    			outputFile.close();

    			//                	/* Get the name of the file */
    			//                        individualFiles = entry.getName();
    			//                        /* Get Size of the file and create a byte array for the size */
    			//                        byte[] content = new byte[(int) entry.getSize()];
    			//                        offset=0;
    			//                        /* Some SOP statements to check progress */
    			//                        System.err.println("File Name in TAR File is: " + individualFiles);
    			//                        System.err.println("Size of the File is: " + entry.getSize());                  
    			//                        System.err.println("Byte Array length: " + content.length);
    			//                        /* Read file from the archive into byte array */
    			//                        myTarFile.read(content, offset, content.length - offset);
    			//                        /* Define OutputStream for writing the file */
    			//                        outputFile=new FileOutputStream(new File(individualFiles));
    			//                        /* Use IOUtiles to write content of byte array to physical file */
    			//                        org.apache.commons.io.IOUtils.write(content,outputFile);              
    			//                        /* Close Output Stream */
    			//                        outputFile.close();
    			//                       // System.err.println(myTarFile.getNextTarEntry().);
    			//                        //
    		}


    		doloop = myTarFile.getNextTarEntry_1_of_2_docontinue();
    		if (doloop == false){
    			System.err.println("doloop is false");
    			break;
    		}
    		System.err.println("Read bytes I: " + myTarFile.getBytesRead());
    		start = end;
    		end = (long) myTarFile.getBytesRead();
    		entry = myTarFile.getNextTarEntry_2_of_2();
    		System.err.println("Read bytes T: " + myTarFile.getBytesRead());
    		offset_header = (int) myTarFile.getBytesRead();
    		if (entry == null){
    			System.err.println("entry is null");
    			break;
    		}

    		firstread = true;
    	}               
    	/* Close TarAchiveInputStream */
    	myTarFile.close();
    }

    // TODO unfinished
    public static void extractfile(File maintarfile) throws Exception{
    	// Extracts only regular files to the current directory
    	/* Read TAR File into TarArchiveInputStream */
    	TarArchiveInputStream myTarFile=new TarArchiveInputStream(new FileInputStream(maintarfile));
    	/* To read individual TAR file */
    	TarArchiveEntry entry = null;
    	String individualFiles;
    	int offset = 0;
    	FileOutputStream outputFile=null;
    	/* Create a loop to read every single entry in TAR file */
    	while ((entry = myTarFile.getNextTarEntry()) != null) {
    		/* Get the name of the file */
    		individualFiles = entry.getName();
    		/* Get Size of the file and create a byte array for the size */
    		long sizeofentry =  entry.getSize();
    		System.err.println("Size of entry is " + sizeofentry);
    		if (entry.isFile()){
    			byte[] content = new byte[(int) entry.getSize()];
    			offset=0;
    			/* Some SOP statements to check progress */
    			System.err.println("File Name in TAR File is: " + individualFiles);
    			System.err.println("Size of the File is: " + entry.getSize());                  
    			System.err.println("Byte Array length: " + content.length);
    			/* Read file from the archive into byte array */
    			myTarFile.read(content, offset, content.length - offset);
    			/* Define OutputStream for writing the file */
    			RandomAccessFile out = new RandomAccessFile(individualFiles, "rw");
    			//outputFile=new FileOutputStream(new File(individualFiles));
    			/* Use IOUtiles to write content of byte array to physical file */
    			///org.apache.commons.io.IOUtils.write(content,outputFile);
    			boolean doloop = true;
    			long maxcounter = sizeofentry;
    			long mincounter = 0;
    			while (doloop){
    				if (maxcounter > CHUNK_SIZE){

    				} else {

    				}
    			}
    			out.write(content);
    			/* Close Output Stream */
    			out.close();
    		} else {
    			System.err.println("Skipping current item, not a regular file");
    		}

    	}               
    	/* Close TarAchiveInputStream */
    	myTarFile.close();
    }

}
