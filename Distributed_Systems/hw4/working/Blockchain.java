import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.Reader;

import java.security.*;
import java.util.*;
/* import java.util.Date;
   import java.util.Random;
   import java.util.regex.*;
   import java.util.StringTokenizer;
*/

import java.io.StringWriter;
import java.io.StringReader;
import java.io.BufferedReader;
import java.text.*;

class BuildBlock {
    String BlockID;
    String PreviousHash;
    String Fname;
    String BlockNum;
    /*
    *
    * Setters & getters for all attributes that will be stored in each block
    *
    * Attributes:
    *  1. BlockID => a unique ID for current block
    *  2. PreviousHash => the previous blocks unique hashcode
    *  3. Fname => first name of the user that is being stored in the current block
    *  4. BlockNum => running total of blocks created, starting at 0
    *
    * */


    public String getBlockID(){ return BlockID;}
    public void setBlockID(String BID) {this.BlockID = BID;}

    public String getPreviousHash(){return PreviousHash;}
    public void setPreviousHash(String prev) {this.PreviousHash = prev;}

    public String getFname() {return Fname;}
    public void setFname(String name){this.Fname = name;}

    public String getBlockNum() {return BlockNum;}
    public void setBlockNum(String blockNum){this.BlockNum = blockNum;}


}



public class Blockchain {
    public static String CSC435Block = "You will design and build this dynamically. For now, this is just a string.";

    public static final String ALGORITHM = "RSA"; // NAME OF ENCRYPTION METHOD USED

    public static String SignedSHA256;

    public static void main(String argv[]) {
        Blockchain b = new Blockchain(argv);
        b.run(argv);
    }

    public Blockchain(String argv[]) {
        System.out.println("In the constructor\n");
    }

    public void run(String args[]) {
        System.out.println("Running now \n");
        try {
           LinkedList<BuildBlock> ll = ListBuilder(args);
            BlockUtilities(args, ll);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LinkedList ListBuilder(String argv[]) throws Exception {
        LinkedList<BuildBlock> block_chain = new LinkedList<BuildBlock>();

        try{
            String ssuid;
            UUID uuid;
            BuildBlock tempBuild = null;

            int n = 0;
            String names[] = {"DummyBlock", "John", "Jane", "Rick"};
            while (n < 4) {
                BuildBlock BB = new BuildBlock();
                try {Thread.sleep(1001);}catch(InterruptedException e){}
                ssuid = new String(UUID.randomUUID().toString());
                BB.setBlockID(ssuid);
                BB.setFname(names[n]);
                BB.setBlockNum(Integer.toString(n));

                if (n == 0) {
                    BB.setPreviousHash("000000000000");
                } else {
                    BB.setPreviousHash(tempBuild.getBlockID());
                }

                tempBuild = BB;
                System.out.println("\n<============ NEW BLOCK: " + BB.getBlockNum() + " ============>\n");
                System.out.println("Name: " + BB.getFname() +"\nBID: " + BB.getBlockID() );
                block_chain.add(BB);
                n++;
            }

        } catch (Exception e){ e.printStackTrace();}
        return block_chain;
    }

    public void BlockUtilities(String args[], LinkedList<BuildBlock> blocks) throws Exception {
        System.out.println("<========= In BlockUtilities =======>");
        int pnum;
        pnum = 0; // Temporarily fixed to value 0

        Date date = new Date();
        String time1 = String.format("%1Ss %2$tF.%2$tT", "", date);
        String t_stamp_str = time1 + "." + pnum + "\n";
        System.out.println("Timestamp: " + t_stamp_str);

        String str_cat;
        str_cat = "";

        /*
         *
         * Create a hash for each block, this combines all attributes in to a string
         * and converts them into a hash
         *
         */
       for (BuildBlock block : blocks) {
           str_cat = t_stamp_str +
                   block.getBlockNum() +
                   block.getBlockID() +
                   block.getFname() +
                   block.getPreviousHash();
       }
        System.out.println("String to hash: " + str_cat + "\n");
        MessageDigest msg_digest = MessageDigest.getInstance("SHA-256");
        msg_digest.update(str_cat.getBytes());
        byte byteData[] = msg_digest.digest();

        StringBuffer str_buf = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            str_buf.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        String SHA256String = str_buf.toString();
        KeyPair keyPair = generateKeyPair(999);
        byte[] digitalSignature = signData(SHA256String.getBytes(), keyPair.getPrivate());

        boolean verified = verifySig(SHA256String.getBytes(), keyPair.getPublic(), digitalSignature);
        System.out.println("Has the signature been verified: " + verified + "\n");

        System.out.println("Hexadecimal byte[] Representation of Original SHA256 Hash: " + SHA256String + "\n");


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
}