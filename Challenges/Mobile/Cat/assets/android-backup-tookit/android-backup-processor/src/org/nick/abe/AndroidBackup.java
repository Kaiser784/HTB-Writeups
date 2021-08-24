
package org.nick.abe;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Scanner;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

// mostly lifted off com.android.server.BackupManagerService.java
public class AndroidBackup {

    private static final int BACKUP_MANIFEST_VERSION = 1;
    private static final String BACKUP_FILE_HEADER_MAGIC = "ANDROID BACKUP\n";
    private static final int BACKUP_FILE_V1 = 1;
    private static final int BACKUP_FILE_V2 = 2;
    private static final int BACKUP_FILE_V3 = 3;
    private static final int BACKUP_FILE_V4 = 4;
    private static final int BACKUP_FILE_V5 = 5;

    private static final String ENCRYPTION_MECHANISM = "AES/CBC/PKCS5Padding";
    private static final int PBKDF2_HASH_ROUNDS = 10000;
    private static final int PBKDF2_KEY_SIZE = 256; // bits
    private static final int MASTER_KEY_SIZE = 256; // bits
    private static final int PBKDF2_SALT_SIZE = 512; // bits
    private static final String ENCRYPTION_ALGORITHM_NAME = "AES-256";

//    private static final boolean DEBUG = true;

    private static final SecureRandom random = new SecureRandom();

    private AndroidBackup() {
    }

    private static void require_jceu(){
    	int maxKeyLen = -1;
        try {
        	maxKeyLen = Cipher.getMaxAllowedKeyLength("AES");
//			if (maxKeyLen < 256) System.out.println("Strong AES encryption not allowed");
//			else System.out.println("Strong AES encryption allowed");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        if (maxKeyLen < 256){
        	System.err.println(
        			"Password is set but strong AES encryption is not allowed\n" +
        			"Please install Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 7 or 8\n" +
        			"http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html" + "\n" +
        			"http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html" + "\n" +
        			"Exiting.");
        	System.exit(1);
        }
    }
    
    public static String requirePassword(String password) throws IOException{
    	// Password is required. If no one is given exit with error
    	if (password == null || "".equals(password)) {
    		Console console = System.console();
//    		System.out.println("Console is " +console);
    		if (console != null) {
    			// This will not work on Cygwin, MinTTY has issues with Console
    			// https://code.google.com/p/mintty/issues/detail?id=56
    			System.err.println("Backup encrypted, enter password (will NOT be displayed):");
    			password = new String(console.readPassword("Password:"));
    		} else {
    			// Cygwin case. Password is echoed
    			System.err.println("Backup encrypted, enter password (WILL be displayed):");
    			System.err.print("Password:");
    			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    		    String input = reader.readLine();
    		    password = input;
    		    reader.close();
    		}
    		if (password == null || "".equals(password)){
    			System.err.println("Password is required but null or empty was read. Exiting.");
    			System.exit(1);
    		}
    	}
    	return password;
    }
    
    public static void notify_if_empty_backup(boolean isCompressed, boolean isEncrypted, long abfileSizelong, long tarfileSizelong, boolean debug) {
    	// Output only if debug is enabled
    	/* Empty ab backups must meet:
    	 * 	TAR size=1024 bytes and all bytes=0x00 (I don't check TAR for now because infoBAckup() does not generate it, must not write anything to disk)
    	 * 	AB file size=549 bytes when compressed and encrypted
    	 * 	AB file size=47 bytes when compressed but not encrypted
    	 */
    	if (debug) {
    		if(isCompressed && isEncrypted && abfileSizelong < 613)
    			System.err.println("EVENT: backup is empty");
    		if(isCompressed && !isEncrypted && abfileSizelong < 104)
    			System.err.println("EVENT: backup is empty");
    	}
    }
    
    public static void exit_if_backup_zero_bytes(long abfileSizelong, boolean debug) {
    	// If input backup is 0 bytes just exit
        if (abfileSizelong == 0) {
        	if (debug) {
        		System.err.println("STOP: backup size is 0 bytes. Nothing do to. Exiting...");
        	}
        	System.exit(0);
        }
        
    }
    
//    public static void extractAsTar(String backupFilename, String filename,
//            String password) {
  public static void infoBackup(String backupFilename, String password, boolean debug) {
      
      //Get input backup file size and output it
      double abfileSize = new File(backupFilename).length();
      long abfileSizelong = new File(backupFilename).length();
      if (debug) {
      	System.err.printf("\"%s\" size is %d bytes\n", backupFilename, abfileSizelong);
      }
      
      // Check if backup file is 0 bytes to exit
      exit_if_backup_zero_bytes(abfileSizelong, debug);
	  
	  try {
            InputStream rawInStream = getInputStream(backupFilename);
            CipherInputStream cipherStream = null;
            
         	String magic = readHeaderLine(rawInStream); // 1
            if (magic.equals("MIUI BACKUP")) {
            	String line2 = readHeaderLine(rawInStream);
            	String line3 = readHeaderLine(rawInStream);
            	String line4 = readHeaderLine(rawInStream);
            	String line5 = readHeaderLine(rawInStream);
            	
            	if (debug) {
            		System.err.println("Magic: " + magic);
            		System.err.println("Header segment 2 (unknown purpose): " + line2);
            		String package_id_name = new String(line3.getBytes("iso8859-1"), "UTF-8");
            		System.err.println("Package ID + display name in the device's language at the time of backup: " + package_id_name);
            		//System.err.println(line3.indexOf(" "));
            		//System.err.println("Package ID: " + package_id_name.substring(0, package_id_name.indexOf(" ")));
            		//System.err.println("Package display name: " + package_id_name.substring(package_id_name.indexOf(" ") + 1));
            		System.err.println("Header segment 4 (unknown purpose): " + line4);
            		System.err.println("Header segment 5 (unknown purpose): " + line5);
            	}
            	magic = readHeaderLine(rawInStream);
            }
            
            
            if (debug) {
                System.err.println("Magic: " + magic);
            }
            
            if (!magic.equals("ANDROID BACKUP")) {
            	System.err.println("Invlaid Magic: " + magic);
            	throw new IllegalArgumentException("Invalid Magic " + magic);
            }
            	
            
            String versionStr = readHeaderLine(rawInStream); // 2
            if (debug) {
                System.err.println("Version: " + versionStr);
            }
            int version = Integer.parseInt(versionStr);
            if (debug && (version < BACKUP_FILE_V1 || version > BACKUP_FILE_V5)) {
            	System.err.println("EVENT: out of range backup version: " + versionStr);
            }
//            if (version < BACKUP_FILE_V1 || version > BACKUP_FILE_V5) {
//                throw new IllegalArgumentException(
//                        "Don't know how to process version " + versionStr);
//            }

            String compressed = readHeaderLine(rawInStream); // 3
            boolean isCompressed = Integer.parseInt(compressed) == 1;
            if (debug) {
                System.err.println("Compressed: " + compressed);
            }
            String encryptionAlg = readHeaderLine(rawInStream); // 4
            if (debug) {
                System.err.println("Algorithm: " + encryptionAlg);
            }
            boolean isEncrypted = false;
            
        	if (debug) {
        		if (encryptionAlg.equals(ENCRYPTION_ALGORITHM_NAME)) {
        			isEncrypted = true;
        			System.err.println("Encrypted: 1");
        		}
        		else
        			System.err.println("Encrypted: 0");
            }
        	
        	notify_if_empty_backup(isCompressed, isEncrypted, abfileSizelong, -1, debug);
            
            if (encryptionAlg.equals(ENCRYPTION_ALGORITHM_NAME)) {
            	isEncrypted = true;
//              if (password == null || "".equals(password)) {
//              throw new IllegalArgumentException(
//                      "Backup encrypted but password not specified");
//          }
            	require_jceu();
            	password = requirePassword(password);

                String userSaltHex = readHeaderLine(rawInStream); // 5
                byte[] userSalt = hexToByteArray(userSaltHex);
                if (userSalt.length != PBKDF2_SALT_SIZE / 8) {
                    throw new IllegalArgumentException("Invalid salt length: "
                            + userSalt.length);
                }

                String ckSaltHex = readHeaderLine(rawInStream); // 6
                byte[] ckSalt = hexToByteArray(ckSaltHex);

                int rounds = Integer.parseInt(readHeaderLine(rawInStream)); // 7
                String userIvHex = readHeaderLine(rawInStream); // 8

                String masterKeyBlobHex = readHeaderLine(rawInStream); // 9

                // decrypt the master key blob
                Cipher c = Cipher.getInstance(ENCRYPTION_MECHANISM);
                // XXX we don't support non-ASCII passwords
                SecretKey userKey = buildPasswordKey(password, userSalt, rounds, false);
                byte[] IV = hexToByteArray(userIvHex);
                IvParameterSpec ivSpec = new IvParameterSpec(IV);
                c.init(Cipher.DECRYPT_MODE,
                        new SecretKeySpec(userKey.getEncoded(), "AES"), ivSpec);
                byte[] mkCipher = hexToByteArray(masterKeyBlobHex);
                byte[] mkBlob = c.doFinal(mkCipher);

                // first, the master key IV
                int offset = 0;
                int len = mkBlob[offset++];
                IV = Arrays.copyOfRange(mkBlob, offset, offset + len);
                if (debug) {
                    System.err.println("IV: " + toHex(IV));
                }
                offset += len;
                // then the master key itself
                len = mkBlob[offset++];
                byte[] mk = Arrays.copyOfRange(mkBlob, offset, offset + len);
                if (debug) {
                    System.err.println("MK: " + toHex(mk));
                }
                offset += len;
                // and finally the master key checksum hash
                len = mkBlob[offset++];
                byte[] mkChecksum = Arrays.copyOfRange(mkBlob, offset, offset
                        + len);
                if (debug) {
                    System.err.println("MK checksum: " + toHex(mkChecksum));
                }

                // now validate the decrypted master key against the checksum
                // first try the algorithm matching the archive version
                boolean useUtf = version >= BACKUP_FILE_V2;
                byte[] calculatedCk = makeKeyChecksum(mk, ckSalt, rounds, useUtf, debug);
                if (debug) {
                	System.err.printf("Calculated MK checksum (use UTF-8: %s): %s\n", useUtf, toHex(calculatedCk));
                }
                if (!Arrays.equals(calculatedCk, mkChecksum)) {
                	if (debug) {
                		System.err.println("Checksum does not match.");
                	}
                	// try the reverse
                	calculatedCk = makeKeyChecksum(mk, ckSalt, rounds, !useUtf, debug);
                	if (debug) {
                		System.err.printf("Calculated MK checksum (use UTF-8: %s): %s\n", useUtf, toHex(calculatedCk));
                	}
                }

                if (Arrays.equals(calculatedCk, mkChecksum)) {
                    ivSpec = new IvParameterSpec(IV);
                    c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(mk, "AES"),
                            ivSpec);
                    // Only if all of the above worked properly will 'result' be
                    // assigned
                    cipherStream = new CipherInputStream(rawInStream, c);
                }
            }

            if (isEncrypted && cipherStream == null) {
                throw new IllegalStateException(
                        "Invalid password or master key checksum.");
            }

//            InputStream baseStream = isEncrypted ? cipherStream : rawInStream;
//            InputStream in = isCompressed ? new InflaterInputStream(baseStream)
//                    : baseStream;
//            FileOutputStream out = null;
//            try {
//                out = new FileOutputStream(filename);
//                byte[] buff = new byte[10 * 1024];
//                int read = -1;
//                long totalRead = 0;
//                while ((read = in.read(buff)) > 0) {
//                    out.write(buff, 0, read);
//                    totalRead += read;
//                    //if (DEBUG && (totalRead % 100 * 1024 == 0)) {
//                    //    System.out.printf("%d bytes read\n", totalRead);
//                    //}
//                }
//                // Print the size of written data, which equals the size of tar file.
//                                System.out.printf("%d bytes written to %s\n", 
//    								totalRead, filename);
//
//            } finally {
//                if (in != null) {
//                    in.close();
//                }
//
//                if (out != null) {
//                    out.flush();
//                    out.close();
//                }
//            }
	  } catch (Exception e) {
		  // Only bother user if he wants so
		  if (debug) {
			  String adb_backup_timeout_error_IO = "java.io.IOException";
			  String adb_backup_timeout_error_PADDING = "javax.crypto.BadPaddingException";
			  // RuntimeException caused by
			  // "Caused by: java.lang.RuntimeException: java.io.IOException: javax.crypto.BadPaddingException: Given final block not properly padded. Such issues can arise if a bad key is used during decryption."
			  if (e.toString().contains(adb_backup_timeout_error_IO) && e.toString().contains(adb_backup_timeout_error_PADDING)) {
				  System.err.println("EVENT: java.io.IOException + javax.crypto.BadPaddingException occurred, likely due to timeout on device during adb backup. "
						  + "Is almost sure that you didn't backup all the data you expected.");
			  }
			  // In case RuntimeException is not caused by adb backup timeout, most likely the user introduced a wrong password. "IOException" must not be present
			  // "javax.crypto.BadPaddingException: Given final block not properly padded. Such issues can arise if a bad key is used during decryption."
			  else if (e.toString().startsWith(adb_backup_timeout_error_PADDING) && !e.toString().contains(adb_backup_timeout_error_IO)) {
				  System.err.println("EVENT: javax.crypto.BadPaddingException occurred, likely due to wrong password.");
			  }
			  else {
				  System.err.println("EVENT: unknown exception occurred:");
				  throw new RuntimeException(e);
			  }
		  }
	  }
  }
    
    

    public static void extractAsTar(String backupFilename, String filename,
            String password, boolean debug) {
    	
        //Get input backup file size and output it
        double abfileSize = new File(backupFilename).length();
        long abfileSizelong = new File(backupFilename).length();
        if (debug) {
        	System.err.printf("\"%s\" size is %d bytes\n", backupFilename, abfileSizelong);
        }
        
        // Check if backup file is 0 bytes to exit
        exit_if_backup_zero_bytes(abfileSizelong, debug);
        
    	try {
    		InputStream rawInStream = getInputStream(backupFilename);
            CipherInputStream cipherStream = null;

            String magic = readHeaderLine(rawInStream); // 1
            
            if (magic.equals("MIUI BACKUP")) {
            	String line2 = readHeaderLine(rawInStream);
            	String line3 = readHeaderLine(rawInStream);
            	String line4 = readHeaderLine(rawInStream);
            	String line5 = readHeaderLine(rawInStream);
            	
            	if (debug) {
            		System.err.println("Magic: " + magic);
            		System.err.println("Header segment 2 (unknown purpose): " + line2);
            		String package_id_name = new String(line3.getBytes("iso8859-1"), "UTF-8");
            		System.err.println("Package ID + display name in the device's language at the time of backup: " + package_id_name);
            		//System.err.println(line3.indexOf(" "));
            		//System.err.println("Package ID: " + package_id_name.substring(0, package_id_name.indexOf(" ")));
            		//System.err.println("Package display name: " + package_id_name.substring(package_id_name.indexOf(" ") + 1));
            		System.err.println("Header segment 4 (unknown purpose): " + line4);
            		System.err.println("Header segment 5 (unknown purpose): " + line5);
            	}
            	magic = readHeaderLine(rawInStream);
            }
            
            
            if (debug) {
                System.err.println("Magic: " + magic);
            }
            
            if (!magic.equals("ANDROID BACKUP")) {
            	System.err.println("Invlaid Magic: " + magic);
            	throw new IllegalArgumentException("Invalid Magic " + magic);
            }
            	
            
            String versionStr = readHeaderLine(rawInStream); // 2
            if (debug) {
                System.err.println("Version: " + versionStr);
            }
            int version = Integer.parseInt(versionStr);
            if (debug && (version < BACKUP_FILE_V1 || version > BACKUP_FILE_V5)) {
            	System.err.println("EVENT: out of range backup version: " + versionStr);
            }
//            if (version < BACKUP_FILE_V1 || version > BACKUP_FILE_V4) {
//                throw new IllegalArgumentException(
//                        "Don't know how to process version " + versionStr);
//            }

            String compressed = readHeaderLine(rawInStream); // 3
            boolean isCompressed = Integer.parseInt(compressed) == 1;
            if (debug) {
                System.err.println("Compressed: " + compressed);
            }
            String encryptionAlg = readHeaderLine(rawInStream); // 4
            if (debug) {
                System.err.println("Algorithm: " + encryptionAlg);
            }
            boolean isEncrypted = false;
            
        	if (debug) {
        		if (encryptionAlg.equals(ENCRYPTION_ALGORITHM_NAME)) {
        			isEncrypted = true;
        			System.err.println("Encrypted: 1");
        		}
        		else
        			System.err.println("Encrypted: 0");
            }

            if (encryptionAlg.equals(ENCRYPTION_ALGORITHM_NAME)) {
                isEncrypted = true;
//                if (password == null || "".equals(password)) {
//                    throw new IllegalArgumentException(
//                            "Backup encrypted but password not specified");
//                }
                require_jceu();
                password = requirePassword(password);

                String userSaltHex = readHeaderLine(rawInStream); // 5
                byte[] userSalt = hexToByteArray(userSaltHex);
                if (userSalt.length != PBKDF2_SALT_SIZE / 8) {
                    throw new IllegalArgumentException("Invalid salt length: "
                            + userSalt.length);
                }

                String ckSaltHex = readHeaderLine(rawInStream); // 6
                byte[] ckSalt = hexToByteArray(ckSaltHex);

                int rounds = Integer.parseInt(readHeaderLine(rawInStream)); // 7
                String userIvHex = readHeaderLine(rawInStream); // 8

                String masterKeyBlobHex = readHeaderLine(rawInStream); // 9

                // decrypt the master key blob
                Cipher c = Cipher.getInstance(ENCRYPTION_MECHANISM);
                // XXX we don't support non-ASCII passwords
                SecretKey userKey = buildPasswordKey(password, userSalt, rounds, false);
                byte[] IV = hexToByteArray(userIvHex);
                IvParameterSpec ivSpec = new IvParameterSpec(IV);
                c.init(Cipher.DECRYPT_MODE,
                        new SecretKeySpec(userKey.getEncoded(), "AES"), ivSpec);
                byte[] mkCipher = hexToByteArray(masterKeyBlobHex);
                byte[] mkBlob = c.doFinal(mkCipher);

                // first, the master key IV
                int offset = 0;
                int len = mkBlob[offset++];
                IV = Arrays.copyOfRange(mkBlob, offset, offset + len);
                if (debug) {
                    System.err.println("IV: " + toHex(IV));
                }
                offset += len;
                // then the master key itself
                len = mkBlob[offset++];
                byte[] mk = Arrays.copyOfRange(mkBlob, offset, offset + len);
                if (debug) {
                    System.err.println("MK: " + toHex(mk));
                }
                offset += len;
                // and finally the master key checksum hash
                len = mkBlob[offset++];
                byte[] mkChecksum = Arrays.copyOfRange(mkBlob, offset, offset
                        + len);
                if (debug) {
                    System.err.println("MK checksum: " + toHex(mkChecksum));
                }

                // now validate the decrypted master key against the checksum
                // first try the algorithm matching the archive version
                boolean useUtf = version >= BACKUP_FILE_V2;
                byte[] calculatedCk = makeKeyChecksum(mk, ckSalt, rounds, useUtf, debug);
                if (debug) {
                	System.err.printf("Calculated MK checksum (use UTF-8: %s): %s\n", useUtf, toHex(calculatedCk));
                }
                if (!Arrays.equals(calculatedCk, mkChecksum)) {
                	if (debug) {
                		System.err.println("Checksum does not match.");
                	}
                	// try the reverse
                	calculatedCk = makeKeyChecksum(mk, ckSalt, rounds, !useUtf, debug);
                	if (debug) {
                		System.err.printf("Calculated MK checksum (use UTF-8: %s): %s\n", useUtf, toHex(calculatedCk));
                	}
                }

                if (Arrays.equals(calculatedCk, mkChecksum)) {
                    ivSpec = new IvParameterSpec(IV);
                    c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(mk, "AES"),
                            ivSpec);
                    // Only if all of the above worked properly will 'result' be
                    // assigned
                    cipherStream = new CipherInputStream(rawInStream, c);
                }
            }

            if (isEncrypted && cipherStream == null) {
                throw new IllegalStateException(
                        "Invalid password or master key checksum.");
            }

            // Set percentage initial value for percentage printing
            double percentDone = -1;
            
            notify_if_empty_backup(isCompressed, isEncrypted, abfileSizelong, -1, debug);
            
            OutputStream out = null;
            InputStream baseStream = isEncrypted ? cipherStream : rawInStream;
            Inflater inf = null;
            InputStream in;
            if (isCompressed) {
            	// The Inflater is needed to get the correct percentage because of compression
            	inf = new Inflater();
            	in = new InflaterInputStream(baseStream, inf);
            } else
            	in = baseStream;
            try {
                out = getOutputStream(filename);
                byte[] buff = new byte[10 * 1024];
                int read = -1;
                long tarfileSizelong = 0; // of the input file decompressed
                double currentPercent; // of the input file
                long bytesRead; // of the input file compressed
                if (debug)
                	System.err.print("Extracting: ");
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                    tarfileSizelong += read;
                    //if (debug && (totalRead % 100 * 1024 == 0)) {
                    //	System.out.printf("%d bytes read\n", totalRead);
                    //}
                    if (debug) {
                    	bytesRead = inf == null ? tarfileSizelong : inf.getBytesRead();
                    	currentPercent = Math.round(bytesRead / abfileSize * 100);
                    	//Log the percentage extracted
                    	if (currentPercent != percentDone) {
                    		System.err.print(String.format("%.0f%% ", currentPercent));
                    		percentDone = currentPercent;
                    		if (percentDone == 100) {
                    			//In case there's Exception at the end of the file because of adb timeout
                    			//javax.crypto.BadPaddingException: Given final block not properly padded. Such issues can arise if a bad key is used during decryption.
                    			System.err.printf("\n");
                    		}
                    	}
                    }
                }
                if (debug) {
                	// Check if maximum displayed percentage is 100%, otherwise display it (depending on files size may or may not display it)
                	if (percentDone < 100) {
                		System.err.print("100%"); //on packTar() this is not needed
                    	// to put shell prompt in a new line not just after the 100%
                		//System.err.println("\n");
                		//System.err.printf("%n");
                    	System.err.printf("\n");
                	}
                 	System.err.printf("%d bytes read from \"%s\"\n", abfileSizelong, backupFilename);
                	System.err.printf("%d bytes written to \"%s\"\n", tarfileSizelong, filename);
                }
                // Print the size of written data, which equals the size of tar file.
                //if (debug) {
                //	System.err.printf("\n%d bytes written to %s.\n", totalRead, filename);
                //}

            } finally {
            	if (in != null) {
                    in.close();
                }

                if (out != null) {
                    out.flush();
                    out.close();
                    }
                }
            }

    	//I'd like to use "catch (javax.crypto.BadPaddingException e)" directly but doesn't work, has to be "IOException"
//    	catch (IOException e) {
//    		throw new UnsupportedOperationException(e);
//        }
    	
    	catch (Exception e) {
    		// Only bother user if he wants so
    		if (debug) {
    			String adb_backup_timeout_error_IO = "java.io.IOException";
    			String adb_backup_timeout_error_PADDING = "javax.crypto.BadPaddingException";
    			// RuntimeException caused by
    			// "Caused by: java.lang.RuntimeException: java.io.IOException: javax.crypto.BadPaddingException: Given final block not properly padded. Such issues can arise if a bad key is used during decryption."
    			if (e.toString().contains(adb_backup_timeout_error_IO) && e.toString().contains(adb_backup_timeout_error_PADDING)) {
    				System.err.println("EVENT: java.io.IOException + javax.crypto.BadPaddingException occurred, likely due to timeout on device during adb backup. "
    						+ "Is almost sure that you didn't backup all the data you expected.");
    			}
    			// In case RuntimeException is not caused by adb backup timeout, most likely the user introduced a wrong password. "IOException" must not be present
    			// "javax.crypto.BadPaddingException: Given final block not properly padded. Such issues can arise if a bad key is used during decryption."
    			else if (e.toString().startsWith(adb_backup_timeout_error_PADDING) && !e.toString().contains(adb_backup_timeout_error_IO)) {
    				System.err.println("EVENT: javax.crypto.BadPaddingException occurred, likely due to wrong password.");
    			}
    			else {
    				System.err.println("EVENT: unknown exception occurred:");
    				throw new RuntimeException(e);
    			}
    		}
        }
    }

    public static void packTar(String tarFilename, String backupFilename,
            String password, boolean isKitKat, boolean debug) {
        boolean encrypting = password != null && !"".equals(password);
        boolean compressing = true; //ABE only supports compressed backups
        
        // If password is set require JCE Unlimited Strength Jurisdiction Policy Files
        if (encrypting){
        	require_jceu();
        }
        
        if (debug) {
        	int compressed = compressing ? 1 : 0;
        	int encrypted = encrypting ? 1 : 0;
        	System.err.println("Compressed: " + compressed);
        	System.err.println("Encrypted: " + encrypted);
        }

        StringBuilder headerbuf = new StringBuilder(1024);

        headerbuf.append(BACKUP_FILE_HEADER_MAGIC);
        // integer, no trailing \n
        headerbuf.append(isKitKat ? BACKUP_FILE_V2 : BACKUP_FILE_V1);
        headerbuf.append(compressing ? "\n1\n" : "\n0\n");

        //Get input file size for percentage printing
        double tarfileSize = new File(tarFilename).length();
        double percentDone = -1;
        
        OutputStream out = null;
        try {
        	InputStream in = getInputStream(tarFilename);
        	OutputStream ofstream = getOutputStream(backupFilename);
        	OutputStream finalOutput = ofstream;
            // Set up the encryption stage if appropriate, and emit the correct
            // header            
            if (encrypting) {
                finalOutput = emitAesBackupHeader(headerbuf, finalOutput,
                        password, isKitKat, debug);
            } else {
                headerbuf.append("none\n");
            }

            byte[] header = headerbuf.toString().getBytes("UTF-8");
            ofstream.write(header);

            // Set up the compression stage feeding into the encryption stage
            // (if any)
            if (compressing) {
                Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
                // requires Java 7
                finalOutput = new DeflaterOutputStream(finalOutput, deflater,
                        true);
            }

            out = finalOutput;

            int read = -1;
            long totalRead = 0; // of the input file decompressed
            double currentPercent; // of the input file
            long bytesRead; // of the input file compressed
            
            
            byte[] buff = new byte[10 * 1024];
            if (debug)
            	System.err.print("Packaging: ");
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
                totalRead += read;
                //if (totalRead % 100 * 1024 == 0) {
                //    System.out.printf("%d bytes written\n", totalRead);
                //}
                if (debug) {
                	bytesRead = totalRead;
                	currentPercent = Math.round(bytesRead / tarfileSize * 100);
                	//Log the percentage extracted
                	if (currentPercent != percentDone) {
                		System.err.print(String.format("%.0f%% ", currentPercent));
                		percentDone = currentPercent;
                	}
                	//System.err.printf("\n%d bytes written to %s.\n", totalRead, filename);
                }
            }
            // Print the size of read data, which equals the size of tar file.
            if (debug) {
            	System.err.printf("\n");
            	System.err.printf("%d bytes read from \"%s\"\n", totalRead, tarFilename);
            	// Don't output here the ab size because will give 12658 bytes less size than real:
            	// long abfileSize = new File(backupFilename).length();
            	// System.err.printf("%d bytes written to \"%s\"\n", abfileSize, backupFilename);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                    long abfileSizelong = new File(backupFilename).length();
                    if (debug) {
                    	System.err.printf("%d bytes written to \"%s\"\n", abfileSizelong, backupFilename);
                    }
                    notify_if_empty_backup(compressing, encrypting, abfileSizelong, -1, debug);
                } catch (IOException e) {
                }
            }
        }
    }

    private static InputStream getInputStream(String filename) throws IOException {
    	if (filename.equals("-")) {
    		return System.in;
    	} else {
    		return new FileInputStream(filename);
    	}
    }

    private static OutputStream getOutputStream(String filename) throws IOException {
    	if (filename.equals("-")) {
    		return System.out;
    	} else {
    		return new FileOutputStream(filename);
    	}
    }
    
    private static byte[] randomBytes(int bits) {
        byte[] array = new byte[bits / 8];
        random.nextBytes(array);

        return array;
    }

    private static OutputStream emitAesBackupHeader(StringBuilder headerbuf,
            OutputStream ofstream, String encryptionPassword, boolean useUtf8, boolean debug) throws Exception {
        // User key will be used to encrypt the master key.
        byte[] newUserSalt = randomBytes(PBKDF2_SALT_SIZE);
        SecretKey userKey = buildPasswordKey(encryptionPassword, newUserSalt,
                PBKDF2_HASH_ROUNDS, useUtf8);

        // the master key is random for each backup
        byte[] masterPw = new byte[MASTER_KEY_SIZE / 8];
        random.nextBytes(masterPw);
        byte[] checksumSalt = randomBytes(PBKDF2_SALT_SIZE);

        // primary encryption of the datastream with the random key
        Cipher c = Cipher.getInstance(ENCRYPTION_MECHANISM);
        SecretKeySpec masterKeySpec = new SecretKeySpec(masterPw, "AES");
        c.init(Cipher.ENCRYPT_MODE, masterKeySpec);
        OutputStream finalOutput = new CipherOutputStream(ofstream, c);

        // line 4: name of encryption algorithm
        headerbuf.append(ENCRYPTION_ALGORITHM_NAME);
        headerbuf.append('\n');
        // line 5: user password salt [hex]
        headerbuf.append(toHex(newUserSalt));
        headerbuf.append('\n');
        // line 6: master key checksum salt [hex]
        headerbuf.append(toHex(checksumSalt));
        headerbuf.append('\n');
        // line 7: number of PBKDF2 rounds used [decimal]
        headerbuf.append(PBKDF2_HASH_ROUNDS);
        headerbuf.append('\n');

        // line 8: IV of the user key [hex]
        Cipher mkC = Cipher.getInstance(ENCRYPTION_MECHANISM);
        mkC.init(Cipher.ENCRYPT_MODE, userKey);

        byte[] IV = mkC.getIV();
        headerbuf.append(toHex(IV));
        headerbuf.append('\n');

        // line 9: master IV + key blob, encrypted by the user key [hex]. Blob
        // format:
        // [byte] IV length = Niv
        // [array of Niv bytes] IV itself
        // [byte] master key length = Nmk
        // [array of Nmk bytes] master key itself
        // [byte] MK checksum hash length = Nck
        // [array of Nck bytes] master key checksum hash
        //
        // The checksum is the (master key + checksum salt), run through the
        // stated number of PBKDF2 rounds
        IV = c.getIV();
        byte[] mk = masterKeySpec.getEncoded();
        byte[] checksum = makeKeyChecksum(masterKeySpec.getEncoded(),
                checksumSalt, PBKDF2_HASH_ROUNDS, useUtf8, debug);

        ByteArrayOutputStream blob = new ByteArrayOutputStream(IV.length
                + mk.length + checksum.length + 3);
        DataOutputStream mkOut = new DataOutputStream(blob);
        mkOut.writeByte(IV.length);
        mkOut.write(IV);
        mkOut.writeByte(mk.length);
        mkOut.write(mk);
        mkOut.writeByte(checksum.length);
        mkOut.write(checksum);
        mkOut.flush();
        byte[] encryptedMk = mkC.doFinal(blob.toByteArray());
        headerbuf.append(toHex(encryptedMk));
        headerbuf.append('\n');

        return finalOutput;
    }


    public static String toHex(byte[] bytes) {
        StringBuffer buff = new StringBuffer();
        for (byte b : bytes) {
            buff.append(String.format("%02X", b));
        }

        return buff.toString();
    }


    private static String readHeaderLine(InputStream in) throws IOException {
        int c;
        StringBuilder buffer = new StringBuilder(80);
        while ((c = in.read()) >= 0) {
            if (c == '\n')
                break; // consume and discard the newlines
            buffer.append((char) c);
        }
        return buffer.toString();
    }

    public static byte[] hexToByteArray(String digits) {
        final int bytes = digits.length() / 2;
        if (2 * bytes != digits.length()) {
            throw new IllegalArgumentException(
                    "Hex string must have an even number of digits");
        }

        byte[] result = new byte[bytes];
        for (int i = 0; i < digits.length(); i += 2) {
            result[i / 2] = (byte) Integer.parseInt(digits.substring(i, i + 2),
                    16);
        }
        return result;
    }

    public static byte[] makeKeyChecksum(byte[] pwBytes, byte[] salt, int rounds, boolean useUtf8, boolean debug) {
        if (debug) {
            System.err.println("key bytes: " + toHex(pwBytes));
            System.err.println("salt bytes: " + toHex(salt));
        }

        char[] mkAsChar = new char[pwBytes.length];
        for (int i = 0; i < pwBytes.length; i++) {
            mkAsChar[i] = (char) pwBytes[i];
        }
        if (debug) {
            System.err.printf("MK as string: [%s]\n", new String(mkAsChar));
        }

        Key checksum = buildCharArrayKey(mkAsChar, salt, rounds, useUtf8);
        if (debug) {
            System.err.println("Key format: " + checksum.getFormat());
        }
        return checksum.getEncoded();
    }

    public static SecretKey buildCharArrayKey(char[] pwArray, byte[] salt,
            int rounds, boolean useUtf8) {
        // Original code from BackupManagerService
        // this produces different results when run with Sun/Oracale Java SE
        // which apparently treats password bytes as UTF-8 (16?)
        // (the encoding is left unspecified in PKCS#5)

        // try {
        // SecretKeyFactory keyFactory = SecretKeyFactory
        // .getInstance("PBKDF2WithHmacSHA1");
        // KeySpec ks = new PBEKeySpec(pwArray, salt, rounds, PBKDF2_KEY_SIZE);
        // return keyFactory.generateSecret(ks);
        // } catch (InvalidKeySpecException e) {
        // throw new RuntimeException(e);
        // } catch (NoSuchAlgorithmException e) {
        // throw new RuntimeException(e);
        // } catch (NoSuchProviderException e) {
        // throw new RuntimeException(e);
        // }
        // return null;

        return androidPBKDF2(pwArray, salt, rounds, useUtf8);
    }
    
    public static SecretKey androidPBKDF2(char[] pwArray, byte[] salt,
            int rounds, boolean useUtf8) {
        PBEParametersGenerator generator = new PKCS5S2ParametersGenerator();
        // Android treats password bytes as ASCII, which is obviously
        // not the case when an AES key is used as a 'password'.
        // Use the same method for compatibility.

        // Android 4.4 however uses all char bytes
        // useUtf8 needs to be true for KitKat
        byte[] pwBytes = useUtf8 ? PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(pwArray)
                : PBEParametersGenerator.PKCS5PasswordToBytes(pwArray);
        generator.init(pwBytes, salt, rounds);
        KeyParameter params = (KeyParameter) generator
                .generateDerivedParameters(PBKDF2_KEY_SIZE);

        return new SecretKeySpec(params.getKey(), "AES");
    }

    private static SecretKey buildPasswordKey(String pw, byte[] salt, int rounds, boolean useUtf8) {
        return buildCharArrayKey(pw.toCharArray(), salt, rounds, useUtf8);
    }

}
