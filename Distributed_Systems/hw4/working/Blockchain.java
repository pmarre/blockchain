

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.*;
/* import java.util.Date;
   import java.util.Random;
   import java.util.regex.*;
   import java.util.StringTokenizer;
*/

import java.text.*;
import java.util.concurrent.BlockingQueue;


class BuildBlock {
    String BlockID;
    String CurrentHash;
    String PreviousHash;
    String Fname;
    String BlockNum;
    Boolean isVerified = false;

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

    public String getCurrentHash(){return CurrentHash;}
    public void setCurrentHash(String curr) {this.CurrentHash = curr;}

    public String getPreviousHash(){return PreviousHash;}
    public void setPreviousHash(String prev) {this.PreviousHash = prev;}

    public String getFname() {return Fname;}
    public void setFname(String name){this.Fname = name;}

    public String getBlockNum() {return BlockNum;}
    public void setBlockNum(String blockNum){this.BlockNum = blockNum;}

    public Boolean isVerified() {return isVerified;}
}




public class Blockchain {
    /* Token indexes for input: */
    private static final int iFNAME = 0;
    private static final int iLNAME = 1;
    private static final int iDOB = 2;
    private static final int iSSNUM = 3;
    private static final int iDIAG = 4;
    private static final int iTREAT = 5;
    private static final int iRX = 6;
    private static final PriorityQueue<BuildBlock> pq = new PriorityQueue<BuildBlock>();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LinkedList<BuildBlock> ListBuilder(String argv[]) throws Exception {
        LinkedList<BuildBlock> block_chain = new LinkedList<BuildBlock>();
        int pnum;
        if (argv.length < 1) pnum = 0;
        else if (argv[0].equals("0")) pnum = 0;
        else if (argv[0].equals("1")) pnum = 1;
        else if (argv[0].equals("2")) pnum = 2;
        else pnum = 0;

        String FILENAME;

        switch(pnum) {
            case 1: FILENAME = "BlockInput1.txt"; break;
            case 2: FILENAME = "BlockInput2.txt"; break;
            default: FILENAME = "BlockInput0.txt"; break;
        }

        try{
            BufferedReader buf_reader = new BufferedReader(new FileReader(FILENAME));
            String ssuid;
            UUID uuid;
            BuildBlock tempBuild = null;
            String InputLine;
            String[] tokens = new String[10];



            int n = 0;  // account for the dummyblock at start
            //String names[] = {"DummyBlock", "John", "Jane", "Rick"};
            while ((InputLine = buf_reader.readLine()) != null) {
                BuildBlock BB = new BuildBlock();
                try {Thread.sleep(1001);}catch(InterruptedException e){}
                ssuid = new String(UUID.randomUUID().toString());
                tokens = InputLine.split(" +");
                BB.setBlockID(ssuid);
                BB.setFname(tokens[iFNAME]);
                BB.setBlockNum(Integer.toString(n));
                if (n == 0 ){
                    BB.setPreviousHash("0000000000000000000000");
                } else {
                    BB.setPreviousHash(tempBuild.getCurrentHash());
                }
//                if (n == 0) {
//                    BB.setFname("StarterBlock");
//                    BB.setBlockNum(Integer.toString(n));
//                    BB.setPreviousHash("000000000000000000000");
//                } else {
//                BB.setFname(tokens[iFNAME]);
//                BB.setBlockNum(Integer.toString(n));
//                BB.setPreviousHash(tempBuild.getCurrentHash());
        //        }
                BlockUtilities(BB);
                tempBuild = BB;
                System.out.println("\n<============ NEW BLOCK: " + BB.getBlockNum() + " ============>\n");
                System.out.println("Name: " + BB.getFname() +"\nBID: "
                        + BB.getBlockID() +
                        "\nPreviousHash: " +
                        BB.getPreviousHash() +
                        "\nCurrentHash: " +
                        BB.getCurrentHash());
                block_chain.add(BB);
                n++;
            }

        } catch (Exception e){ e.printStackTrace();}
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Convert the Java object to a JSON String:
        String json = gson.toJson(block_chain);

        System.out.println("\nJSON (suffled) String list is: " + json);

        // Write the JSON object to a file:
        try (FileWriter writer = new FileWriter("bcList.json")) {
            gson.toJson(block_chain, writer);
        } catch (IOException e) {e.printStackTrace();}

        return block_chain;
    }

    public void BlockUtilities(BuildBlock block) throws Exception {
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

           str_cat = t_stamp_str +
                   block.getBlockNum() +
                   block.getBlockID() +
                   block.getFname() +
                   block.getPreviousHash();
           System.out.println("Concat string: " + str_cat + "\n");
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
           block.setCurrentHash(SHA256String);
           System.out.println("Hexadecimal byte[] Representation of Original SHA256 Hash: " + SHA256String + "\n");
           WorkB work = new WorkB();
          // pq.add(block);
           while(!(block.isVerified)) {
               work.runWork(block, str_cat);
           }
           pq.remove(block);

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

    class WorkB {
        public String ByteArrayToString(byte[] ba){
            StringBuilder hex = new StringBuilder(ba.length * 2);
            for(int i=0; i < ba.length; i++){
                hex.append(String.format("%02X", ba[i]));
            }
            return hex.toString();
        }

        public  String randomAlphaNumeric(int count) {
            StringBuilder builder = new StringBuilder();
            while (count-- != 0) {
                int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
                builder.append(ALPHA_NUMERIC_STRING.charAt(character));
            }
            return builder.toString();
        }

        private  final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
         String someText = "one two three";
         String randString;

        public void runWork(BuildBlock block, String c_str) throws IOException {
            String concatString = "";  // Random seed string concatenated with the existing data
            String stringOut = ""; // Will contain the new SHA256 string converted to HEX and printable.

            Scanner ourInput = new Scanner(System.in);
            System.out.print("Enter some blockdata: ");
            String stringIn = c_str;

            randString = randomAlphaNumeric(8);
            System.out.println("Our example random seed string is: " + randString + "\n");
            // System.out.println("Concatenated with the \"data\": " + stringIn + randString + "\n");

            System.out.println("Number will be between 0000 (0) and FFFF (65535)\n");
            int workNumber = 0;     // Number will be between 0000 (0) and FFFF (65535), here's proof:
            workNumber = Integer.parseInt("0000",16); // Lowest hex value
            System.out.println("0x0000 = " + workNumber);

            workNumber = Integer.parseInt("FFFF",16); // Highest hex value
            System.out.println("0xFFFF = " + workNumber + "\n");

            try {

                for(int i=1; i<20; i++){ // Limit how long we try for this example.
                    randString = randomAlphaNumeric(8); // Get a new random AlphaNumeric seed string
                    concatString = stringIn + randString; // Concatenate with our input string (which represents Blockdata)
                    MessageDigest MD = MessageDigest.getInstance("SHA-256");
                    byte[] bytesHash = MD.digest(concatString.getBytes("UTF-8")); // Get the hash value

                    //stringOut = DatatypeConverter.printHexBinary(bytesHash); // Turn into a string of hex values Java 1.8
                    stringOut = ByteArrayToString(bytesHash); // Turn into a string of hex values, java 1.9
                    System.out.println("Hash is: " + stringOut);

                    workNumber = Integer.parseInt(stringOut.substring(0,4),16); // Between 0000 (0) and FFFF (65535)
                    System.out.println("First 16 bits in Hex and Decimal: " + stringOut.substring(0,4) +" and " + workNumber);
                    if (!(workNumber < 20000)){  // lower number = more work.
                        System.out.format("%d is not less than 20,000 so we did not solve the puzzle\n\n", workNumber);
                    }
                    if (workNumber < 20000){
                        System.out.format("%d IS less than 20,000 so puzzle solved!\n", workNumber);
                        System.out.println("The seed (puzzle answer) was: " + randString);
                        block.isVerified = true;
                        break;
                    }
                    if (block.isVerified){
                        break;
                    }
                    // Here is where you would periodically check to see if the blockchain has been updated
                    // ...if so, then abandon this verification effort and start over.
                    // Here is where you will sleep if you want to extend the time up to a second or two.
                }
            } catch(Exception ex) {ex.printStackTrace();}


        }
    }

}