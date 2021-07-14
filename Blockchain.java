/* 2021-7-10


Patrick Marre
CSC 435 - Blockchain Assignment
Blockchain.java for BlockChain

Built on Java Version:
openjdk version "11.0.11" 2021-04-20
OpenJDK Runtime Environment AdoptOpenJDK-11.0.11+9 (build 11.0.11+9)
OpenJDK 64-Bit Server VM AdoptOpenJDK-11.0.11+9 (build 11.0.11+9, mixed mode)

Files Required:
- Blockchain.java
- BlockInput0.txt
- BlockInput1.txt
- BlockInput2.txt
- BlockchainChecklist.html
- gson-2.8.2.jar


To compile code run the following in the terminal:
    javac -cp "gson-2.8.2.jar" Blockchain.java


INSTRUCTIONS:
- On Windows:
    ** This method hasn't been tested **
    AllStart.bat:

    REM for three procesess:
    start java blockchain 0
    start java blockchain 1
    java blockchain  2

- On Mac:
    1. Create a .scpt file with the following commands (replace "/path/to/file/" with absolute
     path to the file on your local machine
        tell application "Terminal"
            activate
            set targetWindow to 0
            do script "cd /path/to/file/ && java -cp \".:gson-2.8.2.jar\" Blockchain 0"
            delay 0.1
            tell application "System Events" to keystroke "t" using command down
            do script "cd /path/to/file/ && java -cp \".:gson-2.8.2.jar\" Blockchain 1" in window 0
            delay 0.1
            tell application "System Events" to keystroke "t" using command down
            do script "cd /path/to/file/ && java -cp \".:gson-2.8.2.jar\" Blockchain 2" in window 0
        end tell
    2. Run using Apple Script Editor
Script source: https://condor.depaul.edu/elliott/435/hw/programs/Blockchain/UnixScriptA.html

Sources used by Professor Clark Elliott:
Thanks: http://www.javacodex.com/Concurrency/PriorityBlockingQueue-Example
Reading lines and tokens from a file:
http://www.fredosaurus.com/notes-java/data/strings/96string_examples/example_stringToArray.html
Good explanation of linked lists:
https://beginnersbook.com/2013/12/linkedlist-in-java-with-example/
Priority queue:
https://www.javacodegeeks.com/2013/07/java-priority-queue-priorityqueue-example.html
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


Notes:
    - A few bugs exist:
        - Ledger doesn't print out all blocks
        - Duplicates can end up in the blockchain
        - Currently doesn't verify the whole blockchain repeatedly
        - Process Public Keys don't always show up accurately
        - Process 2 does not control the start, but I tried and left the code in there

*/

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;


// Construct various ports for each server
// Ports will increase by 1000 for each additional process
class BuildPorts {
    public static int StartProcessServerBase = 6050;
    public static int KeyServerPortBase = 6051;
    public static int UnverifiedBlockServerPortBase = 6052;
    public static int BlockchainServerPortBase = 6053;

    public static int KeyServerPort;
    public static int UnverifiedBlockServerPort;
    public static int BlockchainServerPort;

    public void setPorts(){
        StartProcessServerBase = StartProcessServerBase + (Blockchain.PID * 1000);
        KeyServerPort = KeyServerPortBase + (Blockchain.PID * 1000);
        UnverifiedBlockServerPort = UnverifiedBlockServerPortBase + (Blockchain.PID * 1000);
        BlockchainServerPort = BlockchainServerPortBase + (Blockchain.PID * 1000);
    }
}

class BlockBuilder implements Serializable {
    // Create a new Block record that will get added the the blockchain
    String TimeStamp;
    String Data;
    String BlockID;
    String CurrentHash;
    String PreviousHash;
    String Fname;
    String Lname;
    String DOB;
    String SSN;
    String Condition;
    String Treatment;
    String Rx;
    String BlockNum;
    Boolean isVerified = false;

    /*
     * Setters & getters for all attributes that will be stored in each block
     *
     * Attributes:
     *  1. BlockID => a unique ID for current block
     *  2. PreviousHash => the previous blocks unique hashcode
     *  3. Fname => first name of the user that is being stored in the current block
     *  4. Lname => last name of the user that is being stored in the current block
     *  5. DOB => user date of birth
     *  6. SSN => social security number
     *  7. Condition => user's ailment
     *  8. Treatment => treatment for ailment
     *  9. Rx => prescription for ailment
     *  10. BlockNum => running total of blocks created, starting at 0
     *  11. isVerified => has the block been verified by consortium yet?
     * */

    public String getTimeStamp() {return TimeStamp;}
    public void setTimeStamp(String TS){this.TimeStamp = TS;}

    public String getData() {return Data;} // getData() is used to return the string value of all data in a single block
    public void setData(String DATA){this.Data = DATA;}

    public String getBlockID(){ return BlockID;}
    public void setBlockID(String BID) {this.BlockID = BID;}

    public String getCurrentHash(){return CurrentHash;}
    public void setCurrentHash(String curr) {this.CurrentHash = curr;}

    public String getPreviousHash(){return PreviousHash;}
    public void setPreviousHash(String prev) {this.PreviousHash = prev;}

    public String getFname() {return Fname;}
    public void setFname(String name){this.Fname = name;}

    public String getLname() {return Lname;}
    public void setLname(String name){this.Lname = name;}

    public String getDOB() {return DOB;}
    public void setDOB(String dob){this.DOB = dob;}

    public String getCondition() {return Condition;}
    public void setCondition(String condition){this.Condition = condition;}

    public String getTreatment() {return Treatment;}
    public void setTreatment(String treat){this.Treatment = treat;}

    public String getSSN() {return SSN;}
    public void setSSN(String sn){this.SSN = sn;}

    public String getRx() {return Rx;}
    public void setRx(String rx){this.Rx = rx;}

    public String getBlockNum() {return BlockNum;}
    public void setBlockNum(String blockNum){this.BlockNum = blockNum;}

    public Boolean isVerified() {return isVerified;}
}


class StarterWorker extends Thread {
    // this is meant to be the worker for the starting process but I never got it running
    // correctly
    Socket starterSock;
    StarterWorker (Socket s) {starterSock = s;}
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(starterSock.getInputStream()));
            String start = in.readLine();
            System.out.println("Starter value = " + start);
            System.out.println(" ------- Starting Blockchain Verification ------- ");
            Blockchain.startProcesses = (start.equals("true"));
        }catch (IOException e) {e.printStackTrace();}
    }
}

class StartServer implements Runnable {
    // this is meant to be the server for starting all process, but never got it to run correctly
    public void run(){
        int q = 6; // set max client queue length
        Socket startSock;  // Key Socket
        System.out.println("Start Server input thread using " + Integer.toString(BuildPorts.StartProcessServerBase));
        try{
            ServerSocket ss = new ServerSocket(BuildPorts.StartProcessServerBase, q); // assign serversocket
            while (true) {
                startSock = ss.accept(); // wait for server to connect
                new StarterWorker (startSock).start(); // start up server
            }
        }catch (IOException ioe) {System.out.println(ioe);}
    }
}




// Used to process public keys
class PublicKeyWorker extends Thread {
    Socket keySock;
    PublicKeyWorker (Socket s) {keySock = s;}
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(keySock.getInputStream())); // get inputstream (public keys)
            String data = in.readLine(); // read each public key
            System.out.println(data); // print public key to terminal
            keySock.close(); // close socket
        } catch (IOException x){x.printStackTrace();}
    }
}

class PKServer implements Runnable {
    public void run(){
        int q = 6; // set max client queue length
        Socket ks;  // Key Socket
        System.out.println("Starting Key Server input thread using " + Integer.toString(BuildPorts.KeyServerPort));
        try{
            ServerSocket ss = new ServerSocket(BuildPorts.KeyServerPort, q); // assign serversocket
            while (true) {
                ks = ss.accept(); // wait for server to connect
                new PublicKeyWorker (ks).start(); // start up server
            }
        }catch (IOException ioe) {System.out.println(ioe);}
    }
}

class UVBServer implements Runnable {
    // Class to build out a queue of unverified blocks, these will then be multicast to all processes
    BlockingQueue<BlockBuilder> q;
    UVBServer(BlockingQueue<BlockBuilder> q){
        this.q = q; // Constructor -> sets local variable to priority queue
    }

    // Compare blocks by timestamp to avoid duplicates being added into the priority queue
    public static Comparator<BlockBuilder> BlockTSComparator = new Comparator<BlockBuilder>()
    {
        @Override
        public int compare(BlockBuilder b1, BlockBuilder b2)
        {
            String s1 = b1.getTimeStamp();
            String s2 = b2.getTimeStamp();
            if (s1 == s2) {return 0;}
            if (s1 == null) {return -1;}
            if (s2 == null) {return 1;}
            return s1.compareTo(s2);
        }
    };



// UVBWorker is used to share the priority queue. Unverified blocks will get added and sorted (by timestamp)
    // this will then get shared with the consumer who will then pop a block off an verify it
    class UVBWorker extends Thread { // Receive a UVB and put it into the shared priority queue.
        Socket sock;
    UVBWorker (Socket s) {this.sock = s;}
        BlockBuilder block = new BlockBuilder(); // create new instance of BlockBuilder

        public void run(){
            try{
                ObjectInputStream unverifiedIn = new ObjectInputStream(sock.getInputStream()); // get Java object from Input
                block = (BlockBuilder) unverifiedIn.readObject(); // read the input as a Java Object
                System.out.println("Received Unverified Block: " + block.getTimeStamp() + " " + block.getData()); // Print out the UVB & Timestamp
                if (!q.contains(block)) { // add block to queue if not already in there (mostly redundancy here)
                    q.put(block);
                }
                sock.close();
            } catch (Exception x){x.printStackTrace();}
        }
    }

    public void run(){ // Start UVBWorker
        int q = 6; // number of client request allowed in queue
        Socket sock;
        System.out.println("Start UVBWorker input thread using port: " +
                Integer.toString(BuildPorts.UnverifiedBlockServerPort));
        try{
            ServerSocket UVBServer = new ServerSocket(BuildPorts.UnverifiedBlockServerPort, q);
            while (true) {
                sock = UVBServer.accept(); // connect to server, get a new block from UVBlock queue
                new UVBWorker(sock).start(); // Create a new thread and process the new block
            }
        }catch (IOException ioe) {System.out.println(ioe);}
    }
}


class UVBConsumer implements Runnable {
    PriorityBlockingQueue<BlockBuilder> queue; // get the queue that was passed from the Blockchain class
    UVBConsumer(PriorityBlockingQueue<BlockBuilder> queue){
        this.queue = queue; // set local variable to the queue that was passed to class
    }

    public void run(){ // start UVBConsumer
        String data;
        BlockBuilder tempRec;
        PrintStream toBCServer;
        Socket BCSock;
        String verified_block;

        System.out.println("Starting UVBConsumer thread to verify blocks from priority queue.\n");
        BCWorkAlgo work = new BCWorkAlgo(); // create an instance of the work class
        try{
            while(true) {

                    tempRec = queue.take(); // pop the first block from the queue, take() locks queue and pops it
                    data = tempRec.getData(); // get string of data from block

                    /* This is were the work gets done - if queue is not null & tempRec has not yet been verified
                     * run the work on the block; (work class will periodically check if block has been verified and
                     * stop to move to next block
                     */
                    if (!(queue == null || tempRec.isVerified)) work.runWork(tempRec, tempRec.getData());

                    /* Filter out all duplicates, not perfect but for the most part effective for
                     * this assignment. May come back an rework this to block all duplicates
                     */
                    // If the first 15 characters in data are not in blockchain already add to block
                    if (Blockchain.blockchain.indexOf(data.substring(1, 15)) < 0) {
                        verified_block = "[" + data + " verified by P" + Blockchain.PID + " at time "
                                + Integer.toString(ThreadLocalRandom.current().nextInt(100, 1000)) + "]\n";
                        System.out.print("Verified block: " + verified_block);

                        //  used Google's gson library to create a new gson Instance
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String json = gson.toJson(tempRec);
                        String temp_bc = verified_block + Blockchain.blockchain; // add block to the blockchain
                        ObjectOutputStream toBC_OOS = null;
                        Iterator<BlockBuilder> iter = Blockchain.br_list.iterator();
                        for (int i = 0; i < Blockchain.numProcesses; i++) {
                            iter = Blockchain.br_list.iterator();
                            // Multicast the updated blockchain to all processes, including the current process
                            // assign the servername && port to BCSock
                            BCSock = new Socket(Blockchain.serverName, BuildPorts.BlockchainServerPortBase + (i * 1000));
                            toBCServer = new PrintStream(BCSock.getOutputStream());
                            toBCServer.println(temp_bc); // send the blockchain to the server
                            toBCServer.flush();
                            BCSock.close(); // close the socket
                        }
                        // Print Ledger to file
                        try (FileWriter writer = new FileWriter("BlockchainLedger.json")) {
                                    gson.toJson(Blockchain.br_list, writer);
                        } catch (IOException e) {e.printStackTrace();}

                    }

                    Thread.sleep(1500); // Wait for blockchain to update with the newly verified block before moving to the next block

            }

        }catch (Exception e) {System.out.println(e);}
    }
}


class BCWorker extends Thread { // Get the input blockchain and add winner
    Socket sock;
    BCWorker (Socket s) {sock = s;}

    public void run(){ // BCWorker
        // Added in gson to write to file here as well (currently for troubleshooting since ledger is only partial
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try{
            // retrieve the verified block chain
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String blockData = "";
            String blockDataIn;
            while((blockDataIn = in.readLine()) != null){
                // concatenate each data with each line from the input
                blockData = blockData + "\n" + blockDataIn + "\r";
            }

            Blockchain.prev_blockchain += Blockchain.blockchain;
            // NEED TO CHECK FOR WINNER HERE FIRST BEFORE adding new data
            Blockchain.blockchain = blockData;
            System.out.println("\n         --NEW BLOCKCHAIN--\n" + Blockchain.blockchain + "\n\n");
            sock.close(); // close socket
        } catch (IOException x){x.printStackTrace();}
    }
}

class BCServer implements Runnable {
    public void run(){
        int q = 6; // # of client requests allowed in queue
        Socket bc_sock;
        System.out.println("Starting the BCServer thread using port  " +
                Integer.toString(BuildPorts.BlockchainServerPort));
        try{
            ServerSocket ss = new ServerSocket(BuildPorts.BlockchainServerPort, q);
            while (true) {
                bc_sock = ss.accept(); // start up socket
                new BCWorker (bc_sock).start(); // create new thread
            }
        }catch (IOException ioe) {System.out.println(ioe);}
    }
}


public class Blockchain {
    static public boolean startProcesses = false;
    static LinkedList<BlockBuilder> br_list = new LinkedList<BlockBuilder>();
    static LinkedList<String> test = new LinkedList<String>();
    LinkedList<BlockBuilder> block_chain = new LinkedList<BlockBuilder>();
    static String serverName = "localhost";
    static String blockchain = ""; // used to build out current verified blockchain
    static String prev_blockchain = "";
    static PrivateKey private_key;
    static int numProcesses = 3; // This can be changed but it must match starter (script) file
    static String[] processPKs = new String[numProcesses];
    static int PID = 0; // Default process ID
    LinkedList<BlockBuilder> blockList = new LinkedList<BlockBuilder>();

   // This queue is shared with all processes and must maintain concurrency in order
   // to not have the same block verified multiple times
    final PriorityBlockingQueue<BlockBuilder> pq = new PriorityBlockingQueue<>(100, BlockTSComparator);

    // Compare blocks to confirm no duplicates
    public static Comparator<BlockBuilder> BlockTSComparator = new Comparator<BlockBuilder>()
    {
        @Override
        public int compare(BlockBuilder b1, BlockBuilder b2)
        {
            //System.out.println("In comparator");
            String s1 = b1.getTimeStamp();
            String s2 = b2.getTimeStamp();
            if (s1 == s2) {return 0;}
            if (s1 == null) {return -1;}
            if (s2 == null) {return 1;}
            return s1.compareTo(s2);
        }
    };

    public static void PKBuilder(int PID) throws Exception {
        Random r = new Random(); // get a random numer
        KeyPair kp = generateKeyPair(r.nextInt(999)); // create public/private keys
        private_key = kp.getPrivate(); // get the private key for the Process
        byte[] public_key = kp.getPublic().getEncoded(); // get byte array of the public key
        String str_public = Base64.getEncoder().encodeToString(public_key); // get the public key in string format
        processPKs[PID] = str_public; // assign the public key to the PID location
    }

    public void KeySend (){
        // method is used to multicast the public key out to all processes
        Socket sock;
        PrintStream toServer;
        try{
            for(int i=0; i< numProcesses; i++){ // Iterate through, sending key to each process
                sock = new Socket(serverName, BuildPorts.KeyServerPortBase + (i * 1000));
                toServer = new PrintStream(sock.getOutputStream());
                toServer.println("P" + Blockchain.PID + " Public Key: " + Blockchain.processPKs[i]); toServer.flush();
                sock.close();
            }
        }catch (Exception x) {x.printStackTrace ();}
    }

    public void StartSend() throws Exception {
        // This is meant to control the start so that only P2 can start the bc, but I was never
        //able to get it up and fully functioning. I left the calls to it in as comments
        Socket sock;
        PrintStream toServer;
        try{
            for(int i=0; i< numProcesses; i++){ // Iterate through, sending key to each process
                sock = new Socket(serverName, BuildPorts.StartProcessServerBase + (i * 1000));
                toServer = new PrintStream(sock.getOutputStream());
                toServer.println("true");
                toServer.flush();
                sock.close();
            }
        }catch (Exception x) {x.printStackTrace ();}
    }

    public void UVSend () throws InterruptedException {
        // Build out and send unverified blocks to processes, reads from file input
        Socket UVBsock; // Will be client connection to the Unverified Block Server for each other process.
        BlockBuilder tempRec;
        String blockData;
        String T1;
        String TimeStampString;
        Date date;
        Random r = new Random();
        String ssuid;
        UUID uuid;
        BlockBuilder tempBuild = null;
        String InputLine;
        String[] tokens = new String[10];
        BlockBuilder prevBlock = null;
        String FILENAME;

        switch(PID) { // based on what argument was used in start up command read data from a specific file
            case 1: FILENAME = "BlockInput1.txt"; break;
            case 2: FILENAME = "BlockInput2.txt"; break;
            default: FILENAME = "BlockInput0.txt"; break;
        }

        // wait for the public keys to get processed before moving forward,
        // this fakes waiting for ACK
          Thread.sleep(1000);
        try{
            BufferedReader buf_reader = new BufferedReader(new FileReader(FILENAME)); // read the file
            int i = 0;
            while((InputLine = buf_reader.readLine()) != null){
                BlockBuilder BB = new BlockBuilder();
                ssuid = new String(UUID.randomUUID().toString()); // create a unique ID for each block
                tokens = InputLine.split(" +"); // split input up by spaces
                date = new Date(); // get the current date & time
                T1 = String.format("%1$s %2$tF.%2$tT", "", date);  // format the date and time
                TimeStampString = T1 + "." + i; // add process number to avoid any duplicates
                BB.setTimeStamp(TimeStampString); //
                BB.setBlockID(ssuid);//
                BB.setFname(tokens[0]);
                BB.setLname(tokens[1]);
                BB.setDOB(tokens[2]);
                BB.setSSN(tokens[2]);
                BB.setCondition(tokens[4]);
                BB.setTreatment(tokens[5]);
                BB.setRx(tokens[6]);
                BB.setBlockNum(Integer.toString(i));
                if (i == 0 ){
                    // Create a initialize first block
                    BB.setPreviousHash("0000000000000000000000000000000000000000000");
                } else {
                    // Set all other hashes to previous hash
                    BB.setPreviousHash(prevBlock.getCurrentHash());
                }

                // concatenate all the data together to get block data
                blockData = BB.getFname() +
                        BB.getLname() +
                        BB.getDOB() +
                        BB.getSSN() +
                        BB.getCondition() +
                        BB.getTreatment() +
                        BB.getRx();

                // set the data
                BB.setData(blockData);

                BlockUtilities(BB); // Run Block Utilities
                // Block utilities creates hash, creates signature, verifies signature
                blockList.add(BB);
                prevBlock = BB;
                i++;
            }

            Iterator<BlockBuilder> iterator = blockList.iterator(); // create iterator() to allows start at the beginning
            ObjectOutputStream toServerOOS = null; // Stream for sending blocks
            for(int j = 0; j < numProcesses; j++){// Send blocks to each process
                System.out.println("Sending UVBs to process " + j + "...");
                iterator = blockList.iterator(); // Start from beginning of the list everytime
                while(iterator.hasNext()){
                    // Start up UVBWorker to retrieve block
                    UVBsock = new Socket(serverName, BuildPorts.UnverifiedBlockServerPortBase + (j * 1000));
                    toServerOOS = new ObjectOutputStream(UVBsock.getOutputStream());
                    Thread.sleep((r.nextInt(9) * 100)); // Helps keep sending random
                    tempRec = iterator.next();
                    toServerOOS.writeObject(tempRec); // Actually send the UVB
                    toServerOOS.flush();
                    UVBsock.close(); // close socket
                }
            }
            Thread.sleep((r.nextInt(9) * 100)); // more randomization to keep things a bit more unpredictable
        }catch (Exception x) {x.printStackTrace ();}
    }

    public void BlockUtilities(BlockBuilder block) throws Exception {
        if (Blockchain.PID == 0) {
            System.out.println("<========= In BlockUtilities =======>");
            String str_cat;
            str_cat = block.getData(); // get the block data

            /*
             * Create a hash for each block, this combines all attributes in to a string
             * and converts them into a hash
             */
            str_cat = str_cat + block.getPreviousHash(); // Add previous blocks hash code to current block data
            System.out.println("Concat string: " + str_cat + "\n");
            MessageDigest msg_digest = MessageDigest.getInstance("SHA-256"); // Create instanace of SHA-256 hashing algorithm
            msg_digest.update(str_cat.getBytes()); // update the digest with the string of data converted into bytes
            byte byteData[] = msg_digest.digest(); // return an array of bytes after completing computation

            StringBuffer str_buf = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                str_buf.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1)); // convert buffer to hexadecimal
            }

            String SHA256String = str_buf.toString(); // convert hex into a string representation
            Random r = new Random();
            int x = r.nextInt(1000); // create a pseudo random number
            KeyPair keyPair = generateKeyPair(x); // set seed to random int
            byte[] digitalSignature = signData(SHA256String.getBytes(), keyPair.getPrivate());

            boolean verified = verifySig(SHA256String.getBytes(), keyPair.getPublic(), digitalSignature);
            System.out.println("Signature has been verified: " + verified + "\n");
            block.setCurrentHash(SHA256String);
            System.out.println("Hexadecimal byte[] Representation of Original SHA256 Hash: " + SHA256String + "\n");
        }
    }
    // This is pulled from BlockJ.java class file
    public static boolean verifySig(byte[] data, PublicKey key, byte[] sig) throws Exception {
        Signature signer = Signature.getInstance("SHA1withRSA"); // create an instance of SHA1 algorithm
        signer.initVerify(key); // get teh signers public key and verify
        signer.update(data);

        return (signer.verify(sig)); // return t/f if the signature is valid
    }

    // This is pulled from BlockJ.java class file
    public static KeyPair generateKeyPair(long seed) throws Exception {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA"); // create private / public key pair
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN"); // random secure number with SHA1PRNG algorithm
        rng.setSeed(seed); // set the sed of the random number
        keyGenerator.initialize(1024, rng); // generate the key

        return (keyGenerator.generateKeyPair());
    }
    // This is pulled from BlockJ.java class file
    public static byte[] signData(byte[] data, PrivateKey key) throws Exception {
        Signature signer = Signature.getInstance("SHA1withRSA"); // create an intance of SHA1 algo
        signer.initSign(key); // initialize with private key
        signer.update(data);
        return (signer.sign()); // return t/f if correct key was used (i.e. Public & Private Keys are a matching pair)
    }

    public static void main(String args[]) {
        Blockchain s = new Blockchain();
        s.run(args); // Run run() to get out of main()
    }

    public void run(String args[]) {
        // Run starts up the program and starts sending keys and blocks to other processes
        System.out.println("Running now\n");
        PID = (args.length < 1) ? 0 : Integer.parseInt(args[0]); // Process ID is passed to the JVM
        try{PKBuilder(PID);}catch (Exception e) {};
        System.out.println("Patrick Marre's Blockchain. Use Control-C to stop the process.\n");
        System.out.println("Using processID " + PID + "\n");
        new BuildPorts().setPorts(); // Connect to port
        new Thread(new StartServer()).start(); // start up all processes
        new Thread(new PKServer()).start(); // create a new thread for public keys
        new Thread(new UVBServer(pq)).start(); // Create a new thread to UVBs
        new Thread(new BCServer()).start(); // Create a new thread for updated blockchains
        try{Thread.sleep(3000);}catch(Exception e){} // Wait for all servers
       // if (PID == 2) try{new Blockchain().StartSend();}catch (Exception e) {}  // set starter value
        try{Thread.sleep(5000);}catch(Exception e){} // Wait for all servers
      //  if (startProcesses) {
            new Blockchain().KeySend(); // send the keys over
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            } // more waiting...
            try {
                new Blockchain().UVSend();
            } catch (InterruptedException e) {
            } // Send out all the UVBs to all processes
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
            } // wait some more... this time for the multicast
            new Thread(new UVBConsumer(pq)).start(); // Start up all consumers to start verifying blocks
        }
   // }
}

class BCWorkAlgo {
    // This is where a process goes to verify a new UVB, work difficulty can be adjusted


    public String ByteArrayToString(byte[] ba){
        StringBuilder hex = new StringBuilder(ba.length * 2);
        for(int i=0; i < ba.length; i++){
            hex.append(String.format("%02X", ba[i])); // generate a hexadecimal number
        }
        return hex.toString(); // convert hex number to string
    }

    public  String randomAlphaNumeric(int count) {
        // Build out a random string
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length()); // get random chars from ALPHA_NUMERIC_STRING
            builder.append(ALPHA_NUMERIC_STRING.charAt(character)); // concatenate chars together
        }
        return builder.toString(); // return a random string
    }

    private  final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    String randString;

    public void runWork(BlockBuilder block, String data) throws IOException {
        String concatString = "";  // Random string, this will get added onto the data
        String stringOut = ""; // Created hex value
        String stringIn = data; // get the block data

        randString = randomAlphaNumeric(8); // create a random string of 8 letters
        //System.out.println("Our example random seed string is: " + randString + "\n");
        // System.out.println("Concatenated with the \"data\": " + stringIn + randString + "\n");

        //System.out.println("Number will be between 0000 (0) and FFFF (65535)\n");

        int target = 0;     // Initialize the work number, smaller ranges = more difficulty

        Random r = new Random();
        try {
            while (!block.isVerified) {
                if (Blockchain.blockchain.contains(block.getData())) {
                    break; // check if block has been verified already and break out of loop if it has
                }
                randString = randomAlphaNumeric(8); // Generate a new random string
                concatString = stringIn + randString; // Add random string to block data
                MessageDigest MD = MessageDigest.getInstance("SHA-256"); // create a hash code with that data
                byte[] bytesHash = MD.digest(concatString.getBytes("UTF-8")); // return the hash value in a byte array

                stringOut = ByteArrayToString(bytesHash); // convert from byte array to string hex value
                System.out.println("Hash is: " + stringOut); // print out the hash

                target = Integer.parseInt(stringOut.substring(0, 4), 16); // get the target of first 4 chars in hex number
                // larger substrings == more difficulty and more work (which is good for added security!)

                if (!(target < 20000)) {  // lower number = more work.
                    // puzzle not solved if the target is greater than 20,000
                    if (Blockchain.blockchain.contains(block.getData())) {
                        break; // check if block has been verified already and break out of loop if it has
                    }
                }

                if (target < 20000) {
                    // if target lands under 20,000 a winning hash has been created
                    System.out.format("%d IS less than 20,000 so puzzle solved!\n", target);
                    block.isVerified = true; // verify the block
                    Blockchain.br_list.add(block); // add the block the list
                    break; // break from loop
                }

                if (Blockchain.blockchain.contains(block.getData())) {
                    break; // check if block has been verified already and break out of loop if it has
                }
                Thread.sleep((r.nextInt(9) * 100)); // This is to mimic more difficult work and help generate
                                                            // more randomness in which process wins
            }

        } catch(Exception ex) {ex.printStackTrace();}


    }
}