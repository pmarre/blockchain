// package hw4;

/* BlockJ.java

Version 1.3 2020-02-10:

Author: Clark Elliott, with ample help from the below web sources.

You are free to use this code in your assignment, but you MUST add your own comments.

Leave in the web source references.

This is pedagogical code and should not be considered current for secure applications.

The web sources:

https://mkyong.com/java/how-to-parse-json-with-gson/
http://www.java2s.com/Code/Java/Security/SignatureSignAndVerify.htm
https://www.mkyong.com/java/java-digital-signatures-example/ (not so clear)
https://javadigest.wordpress.com/2012/08/26/rsa-encryption-example/
https://www.programcreek.com/java-api-examples/index.php?api=java.security.SecureRandom
https://www.mkyong.com/java/java-sha-hashing-example/
https://stackoverflow.com/questions/19818550/java-retrieve-the-actual-value-of-the-public-key-from-the-keypair-object
https://www.java67.com/2014/10/how-to-pad-numbers-with-leading-zeroes-in-Java-example.html

One version of the JSON jar file here:
https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.2/

You will need to download gson-2.8.2.jar into your classpath / compiling directory.

To compile and run:

javac -cp "gson-2.8.2.jar" BlockJ.java
java -cp ".;gson-2.8.2.jar" BlockJ

-----------------------------------------------------------------------------------------------------*/
import java.io.StringWriter;
import java.io.StringReader;

/* CDE: The encryption needed for signing the hash: */

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.NoSuchAlgorithmException;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.Cipher;
import java.security.spec.*;
// Ah, heck:
import java.security.*;

// Produces a 64-bye string representing 256 bits of the hash output. 4 bits per character
import java.security.MessageDigest; // To produce the SHA-256 hash.

/* CDE Some other uitilities: */

import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.text.*;
import java.util.Base64;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.Reader;

class BlockRecord {
    String BlockID;
    String VerificationProcessID;
    String PreviousHash;
    UUID uuid;
    String Fname;
    String Lname;
    String SSNum;
    String DOB;
    String Diag;
    String Treat;
    String Rx;
    String RandomSeed;
    String WinningHash;

    public String getBlockID() {
        return BlockID;
    }

    public void setBlockID(String BID) {
        this.BlockID = BID;
    }

    public String getVerificationProcessID() {
        return VerificationProcessID;
    }

    public void setVerificationProcessID(String VID) {
        this.VerificationProcessID = VID;
    }

    public String getPreviousHash() {
        return this.PreviousHash;
    }

    public void setPreviousHash(String PH) {
        this.PreviousHash = PH;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID ud) {
        this.uuid = ud;
    }

    public String getLname() {
        return Lname;
    }

    public void setLname(String LN) {
        this.Lname = LN;
    }

    public String getFname() {
        return Fname;
    }

    public void setFname(String FN) {
        this.Fname = FN;
    }

    public String getSSNum() {
        return SSNum;
    }

    public void setSSNum(String SS) {
        this.SSNum = SS;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String RS) {
        this.DOB = RS;
    }

    public String getDiag() {
        return Diag;
    }

    public void setDiag(String D) {
        this.Diag = D;
    }

    public String getTreat() {
        return Treat;
    }

    public void setTreat(String Tr) {
        this.Treat = Tr;
    }

    public String getRx() {
        return Rx;
    }

    public void setRx(String Rx) {
        this.Rx = Rx;
    }

    public String getRandomSeed() {
        return RandomSeed;
    }

    public void setRandomSeed(String RS) {
        this.RandomSeed = RS;
    }

    public String getWinningHash() {
        return WinningHash;
    }

    public void setWinningHash(String WH) {
        this.WinningHash = WH;
    }

}

public class BlockJ {
    public static String CSC435Block = "You will design and build this dynamically. For now, this is just a string.";

    public static final String ALGORITHM = "RSA"; // NAME OF ENCRYPTION METHOD USED

    public static String SignedSHA256;

    public static void main(String argv[]) {
        BlockJ s = new BlockJ(argv);
        s.run(argv);
    }

    public BlockJ(String argv[]) {
        System.out.println("In the constructor...");
    }

    public void run(String argv[]) {
        System.out.println("Running now.\n");

        try {
            DemonstrateUtilities(argv);
        } catch (Exception x) {
            x.printStackTrace();
        }
        ;
        WriteJSON();
        ReadJSON();
    }

    public void DemonstrateUtilities(String args[]) throws Exception {
        System.out.println("\n =========> in DemonstrateUtilities <========= \n");
        int pnum;
        int UnverifiedBlockPort;
        int BlockChainPort;

        if (args.length > 2)
            System.out.println("Special functionality is present \n");

        if (args.length < 1)
            pnum = 0;
        else if (args[0].equals("0"))
            pnum = 0;
        else if (args[0].equals("1"))
            pnum = 1;
        else if (args[0].equals("2"))
            pnum = 2;
        else
            pnum = 0;
        UnverifiedBlockPort = 4710 + pnum;
        BlockChainPort = 4810 + pnum;

        System.out.println("Process number: " + pnum + " Ports: " + UnverifiedBlockPort + " " + BlockChainPort + "\n");

        Date date = new Date();
        // String T1 = String.format("%1Ss %2$tF.%2$tT", "Timestamp:", date);
        String T1 = String.format("%1Ss %2$tF.%2$tT", "", date);
        String TimeStampString = T1 + "." + pnum + "\n"; // Unique process ID to avoid timestamp collisions
        System.out.println("Timestamp: " + TimeStampString);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(CSC435Block.getBytes());
        byte byteData[] = md.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        String SHA256String = sb.toString();
        KeyPair keyPair = generateKeyPair(999);
        byte[] digitalSignature = signData(SHA256String.getBytes(), keyPair.getPrivate());

        boolean verified = verifySig(SHA256String.getBytes(), keyPair.getPublic(), digitalSignature);
        System.out.println("Has the signature been verified: " + verified + "\n");

        System.out.println("Hexadecimal byte[] Representation of Original SHA256 Hash: " + SHA256String + "\n");

        SignedSHA256 = Base64.getEncoder().encodeToString(digitalSignature);
        System.out.println("The signed SHA-256 string: " + SignedSHA256 + "\n");
        byte[] testSignature = Base64.getDecoder().decode(SignedSHA256);
        System.out.println("Testing restore of signature: " + Arrays.equals(testSignature, digitalSignature));

        verified = verifySig(SHA256String.getBytes(), keyPair.getPublic(), testSignature);
        System.out.println("Has the restored signature been verified: " + verified + "\n");

        byte[] bytePubkey = keyPair.getPublic().getEncoded();
        System.out.println("Key in Byte[] form: " + bytePubkey);

        String stringKey = Base64.getEncoder().encodeToString(bytePubkey);
        System.out.println("Key in String form: " + stringKey);

        byte[] bytePubkey2 = Base64.getDecoder().decode(stringKey);
        System.out.println("Key in Byte form again: " + bytePubkey2);

        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(bytePubkey2);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey RestoredKeyBad = keyFactory.generatePublic(pubSpec);

        verified = verifySig(SHA256String.getBytes(), keyPair.getPublic(), testSignature);
        System.out.println("Has the signature been verified: " + verified + "\n");

        verified = verifySig(SHA256String.getBytes(), RestoredKeyBad, testSignature);
        System.out.println("has the CONVERTED-FROM-STRING bad key signature been verified: " + verified + "\n");

        System.out.println("We will now simulate some work: ");
        int randval = 27;
        int tenths = 0;
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            Thread.sleep(100);
            randval = r.nextInt(100);
            System.out.print(".");
            if (randval < 4) {
                tenths = i;
                break;
            }
        }
        System.out.println("<----- We did " + tenths + " tenths of a second of *work*.\n");

        final byte[] cipherText = encrypt(SHA256String, keyPair.getPublic());
        final String plainText = decrypt(cipherText, keyPair.getPrivate());

        System.out.println("\nExtra encryption functionality in case you want it:");
        System.out.println("\nExtra encryption functionality in case you want it:");
        System.out.println("Starting Hash string: " + SHA256String);
        System.out.println("Encrypted Hash string: " + Base64.getEncoder().encodeToString(cipherText));
        System.out.println("Original (now decrypted) Hash string: " + plainText + "\n");
    }

    public static boolean verifySig(byte[] data, PublicKey key, byte[] sig) throws Exception {
        Signature signer = Signature.getInstance("SHA1withRSA");
        signer.initVerify(key);
        signer.update(data);

        return (signer.verify(sig));
    }

    public static KeyPair generateKeyPair(long seed) throws Exception {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN");
        rng.setSeed(seed);
        keyGenerator.initialize(1024, rng);

        return (keyGenerator.generateKeyPair());
    }

    public static byte[] signData(byte[] data, PrivateKey key) throws Exception {
        Signature signer = Signature.getInstance("SHA1withRSA");
        signer.initSign(key);
        signer.update(data);
        return (signer.sign());
    }

    // NOT NEEDED FOR CSC435 BLOCKCHAIN ASSIGNMENT
    public static byte[] encrypt(String text, PublicKey key) {
        byte[] cipherText = null;
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    // NOT NEEDED FOR CSC435 BLOCKCHAIN ASSIGNMENT
    public static String decrypt(byte[] text, PrivateKey key) {
        byte[] decryptedText = null;
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            decryptedText = cipher.doFinal(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(decryptedText);
    }

    public void WriteJSON() {
        System.out.println("=========> In WriteJSON <=========\n");

        /*
         * CDE: Example of generating a unique blockID. This would also be signed by
         * creating process:
         */
        // String suuid = UUID.randomUUID().toString(); // Can do this all at once...
        UUID BinaryUUID = UUID.randomUUID();
        String suuid = BinaryUUID.toString();
        System.out.println("Unique Block ID: " + suuid + "\n");

        // Create new instance of blockRecord
        BlockRecord blockRecord = new BlockRecord();
        blockRecord.setVerificationProcessID("Process2");
        blockRecord.setBlockID(suuid);
        blockRecord.setUUID(BinaryUUID); // Later will show JSON translation from binary to string form.
        blockRecord.setSSNum("123-45-6789");
        blockRecord.setRx("Hot Chili Peppers");
        blockRecord.setFname("Joseph");
        blockRecord.setLname("Chang");

        Random rr = new Random(); //
        int rval = rr.nextInt(16777215); // This is 0xFFFFFF -- YOU choose what the range is

        // In real life you'll want these much longer. Using 6 chars to make debugging
        // easier.
        String randSeed = String.format("%06X", rval & 0x0FFFFFF); // Mask off all but trailing 6 chars.
        rval = rr.nextInt(16777215);
        String randSeed2 = Integer.toHexString(rval);
        System.out.println("Our string random seed is: " + randSeed + ". Wait, I mean it is: " + randSeed2 + "\n");

        blockRecord.setRandomSeed(randSeed2);

        String catRecord = // "Get a string of the block so we can hash it.
                blockRecord.getBlockID() + blockRecord.getVerificationProcessID() + blockRecord.getPreviousHash()
                        + blockRecord.getFname() + blockRecord.getLname() + blockRecord.getSSNum() + blockRecord.getRx()
                        + blockRecord.getDOB() + blockRecord.getRandomSeed();

        System.out.println("String blockRecord is: " + catRecord);

        /* Now make the SHA-256 Hash Digest of the block: */

        String SHA256String = "";

        try {
            MessageDigest ourMD = MessageDigest.getInstance("SHA-256");
            ourMD.update(catRecord.getBytes());
            byte byteData[] = ourMD.digest();

            // CDE: Convert the byte[] to hex format. THIS IS NOT VERFIED CODE:
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            SHA256String = sb.toString(); // For ease of looking at it, we'll save it as a string.
        } catch (NoSuchAlgorithmException x) {
        }
        ;

        blockRecord.setWinningHash(SHA256String); // Here we just assume the first hash is a winner. No real *work*.

        /* Now let's see what the JSON of the full block looks like: */

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Convert the Java object to a JSON String:
        String json = gson.toJson(blockRecord);

        System.out.println("\nJSON String blockRecord is: " + json);

        // Write the JSON object to a file:
        try (FileWriter writer = new FileWriter("blockRecord.json")) {
            gson.toJson(blockRecord, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ReadJSON() {
        System.out.println("\n=========> In ReadJSON <=========\n");

        Gson gson = new Gson();

        try (Reader reader = new FileReader("blockRecord.json")) {

            // Read and convert JSON File to a Java Object:
            BlockRecord blockRecordIn = gson.fromJson(reader, BlockRecord.class);

            // Print the blockRecord:
            System.out.println(blockRecordIn);
            System.out.println("Name is: " + blockRecordIn.Fname + " " + blockRecordIn.Lname);

            String INuid = blockRecordIn.uuid.toString();
            System.out.println("String UUID: " + blockRecordIn.BlockID + " Stored-binaryUUID: " + INuid);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
