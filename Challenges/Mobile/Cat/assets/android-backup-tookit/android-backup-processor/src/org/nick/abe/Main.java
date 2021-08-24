package org.nick.abe;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Main {
	
	private static final String NAME = "android-backup-processor";
	private static final String VERSION = "v20210812";

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        int maxKeyLen = -1;
        int argsLength = args.length;
        String envVar = null;
        int argSum = 0;
        String envPassword = null;
        boolean debug = false;
        boolean envpasswordRequested = false;
        
        // Print usage and exit clean
        if (argsLength == 0) {
        	usage();
        	System.exit(0);
        }
        
        // Incomplete command, then exit 1
        if (argsLength == 1) {
        	usage();
        	System.exit(1);
        }
        //-------------FLAG DETECTION START------------------
        // Flags order must be respected
        if (args[0].equals("-debug")) {
        	debug = true;
        	argSum += 1;
        }
        if (args[0].startsWith("-useenv=")){
        	envVar = args[0].substring("-useenv=".length());
        	if (envVar != null && !envVar.equals("")){
        		envPassword = System.getenv(envVar);
        	}
        	argSum += 1;
        }
        if (args[1].startsWith("-useenv=")){
        	envVar = args[1].substring("-useenv=".length());
        	if (envVar != null && !envVar.equals("")){
        		envPassword = System.getenv(envVar);
        	}
        	envpasswordRequested = true;
//        	if (debug){
//        		if (envPassword == null || envPassword.equals("")) {
//        			System.err.println("Using environment variable \"" + envVar + "\", password is null or empty");
//        		} else {
//            		System.err.println("Using environment variable \"" + envVar + "\", password is \"" + envPassword + "\"");
//        		}
//        	}
        	argSum += 1;
        }
        //-------------FLAG DETECTION END------------------
        // Incomplete command, then exit 1
        if (argsLength <= 1 + argSum) {
        	usage();
        	System.exit(1);
        }
        
        // Common debug strings
        if (debug) {
        	displayversion();
        	if (envpasswordRequested) {
        		if (envPassword == null || envPassword.equals("")) {
        			System.err.println("Using environment variable \"" + envVar + "\", password is null or empty");
        		} else {
        			System.err.println("Using environment variable \"" + envVar + "\", password is \"" + envPassword + "\"");
        		}	
        	}
        }

        try {
        	maxKeyLen = Cipher.getMaxAllowedKeyLength("AES");
			if (maxKeyLen < 256){
				if (debug)
					System.err.println("Strong AES encryption not allowed");
			}
			else{
				if (debug)
					System.err.println("Strong AES encryption allowed");
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        
        String mode = args[0 + argSum];
        String password = envPassword;
        
        // If password is given require strong AES encryption
//        if (args.length > 3 && args[3] != null && maxKeyLen < 256){
//        	System.out.println("Password is set but strong AES encryption is not allowed\n" +
//        	"Please install Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 7 or 8\n" +
//			"http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html" + "\n" +
//			"http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html");
//        	System.exit(1);
//        }

        // Check password validity if encrypted and JCE availability
        if ("info".equals(mode)) {
        	String backupFilename = args[1 + argSum];
        	if (argsLength > 2 + argSum) {
//        		 // Prioritize env over provided 
//        		if (password == null || password.equals("")){
            		password = args[2 + argSum];
//        		}
        	}
        	checkFileExists(backupFilename, debug);
        	AndroidBackup.infoBackup(backupFilename, password, debug);
        	System.exit(0);
        }

        // Check that pack, pack-k or unpack is selected. Exit otherwise
        if (!"pack".equals(mode) && !"unpack".equals(mode) && !"pack-kk".equals(mode)) {
            usage();
            System.exit(1);
        }
        // Check at least for 4 arguments: input file, output file, mode and abp.jar. Password is optional.
        if (argsLength < 3 + argSum) {
        	usage();
        	System.exit(1);
        }
        
        boolean unpack = "unpack".equals(mode);
        String backupFilename = unpack ? args[1 + argSum] : args[2 + argSum];
        String tarFilename = unpack ? args[2 + argSum] : args[1 + argSum];
        if (argsLength > 3 + argSum) {
//        	if (password == null || password.equals("")){
                password = args[3 + argSum];
//        	}
        }

        if (unpack) {
        	checkFileExists(backupFilename, debug);
            AndroidBackup.extractAsTar(backupFilename, tarFilename, password, debug);
        } else {
        	checkFileExists(tarFilename, debug);
        	boolean isKitKat = "pack-kk".equals(mode);
            AndroidBackup.packTar(tarFilename, backupFilename, password, isKitKat, debug);
        }
    }

    private static void usage() {
    	displayversion();
        try {
        	int maxKeyLen = Cipher.getMaxAllowedKeyLength("AES");
			System.err.println("Cipher.getMaxAllowedKeyLength(\"AES\") = " + maxKeyLen);
			if (maxKeyLen < 256) System.err.println("Strong AES encryption not allowed, MaxKeyLenght is < 256 \n" +
					"Please install Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 7 or 8\n" +
					"http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html" + "\n" +
					"http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html");
			else System.err.println("Strong AES encryption allowed, MaxKeyLenght >= 256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        System.err.println("Usage:");
        System.err.println("\tinfo:\t\tabp [-debug] [-useenv=yourenv] info <backup.ab> [password]");
        System.err
                .println("\tunpack:\t\tabp [-debug] [-useenv=yourenv] unpack <backup.ab> <backup.tar> [password]");
        System.err
                .println("\tpack:\t\tabp [-debug] [-useenv=yourenv] pack <backup.tar> <backup.ab> [password]");
        System.err
        		.println("\tpack 4.4.3+:\tabp [-debug] [-useenv=yourenv] pack-kk <backup.tar> <backup.ab> [password]");
        System.err
        		.println("\t*If -useenv is used, yourenv is tried when password is not given");
        System.err
        		.println("\t*If -debug is used, information and passwords may be shown");
        System.err
        		.println("\t*If the filename is `-`, then data is read from standard input or written to standard output");
    }
    
    public static void displayversion() {
    	System.err.println(NAME + " " + VERSION);
    }
    
    public static void checkFileExists(String fileSTR, boolean debug) {
    	File file = new File(fileSTR);
    	if (!file.isFile() || !file.exists()){
    		if (debug) {
    			System.err.println("EVENT: \"" + file + "\" doesn't exist.");
    		}
    		System.exit(1);
    	}
    }
}
