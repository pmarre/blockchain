import java.io.*; // Import I/O libraries
import java.net.*; // Import Java networking libraries
import java.util.*; // Import UUID 

class Worker extends Thread { // Worker constructor
    Socket sock; // init socket

    Worker(Socket s) {
        sock = s;
    }

    public void run() {
        PrintStream out = null; // init output stream to null
        BufferedReader in = null; // init input to null
        int count;
        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream())); // read input from inputStream of
                                                                                   // sock (socket)
            out = new PrintStream(sock.getOutputStream()); // printable value of sock's output stream
            try {
                String uuid;
                String name; // init name
                String mode; // init Joke or Proverb mode
                uuid = in.readLine();
                name = in.readLine(); // get username
                System.out.println(uuid);
                count = Integer.parseInt(in.readLine());
                mode = in.readLine(); // get mode
                if (mode.equals("joke")) {
                    System.out.println("<" + name + "> Looking for a joke.");
                    printJoke(name, mode, out, count);
                } else if (mode.equals("proverb")) {
                    System.out.println("<" + name + "> Looking for a proverb");
                    printProverb(name, mode, out, count);
                }
                System.out.println("Looking up " + mode);
                // pass name and out to printRemoteAddress, prints the returned
                // information to the client
            } catch (IOException x) { // if error, print error
                System.out.println("Server read error");
                x.printStackTrace();
            }
            sock.close(); // close socket to avoid memory leak
        } catch (IOException ioe) { // if error print error
            System.out.println(ioe);
        }
    }

    static void printJoke(String name, String mode, PrintStream out, int joke_counter) {
        // function is for printing the information received from the server to the the
        // client

        String jokes[] = { "JA", "JB", "JC", "JD" };
        if (joke_counter < 4) {
            try {
                out.println("Looking up " + mode + "...");
                out.println(name + ", " + jokes[joke_counter]);
                if (joke_counter == 3)
                    out.println("JOKE CYCLE COMPLETED");
            } finally { // THIS SHOULD BE CHANGED
            }
        }
    }

    static void printProverb(String name, String mode, PrintStream out, int proverb_counter) {
        // function is for printing the information received from the server to the the
        // client

        String proverbs[] = { "PA", "PB", "PC", "PD" };
        if (proverb_counter < 4) {
            try {
                out.println("Looking up " + mode + "...");
                out.println(name + ", " + proverbs[proverb_counter]);
                if (proverb_counter == 3)
                    out.println("PROVERB CYCLE COMPLETED");
            } finally { // THIS SHOULD BE CHANGED
            }
        }

    }

    static String toText(byte ip[]) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < ip.length; ++i) {
            if (i > 0)
                result.append(".");
            result.append(0xff & ip[i]);
        }
        return result.toString();
    }
}

class User {
    public User(UUID uuid) {
        this.uuid = uuid;
    }

    public final UUID uuid;
    String username;
    int[] order = new int[4];
    String state;
}

public class JokeServer {
    public static void main(String[] args) throws Exception {
        int q_len = 6;
        int port = 1565; // set the port
        Socket sock; // init sock

        ServerSocket servsock = new ServerSocket(port, q_len); // create server socket

        System.out.println("Patrick Marre's Joe Server v1.1 starting up, listening at port 1565.\n");
        while (true) {
            sock = servsock.accept(); // connect socket
            new Worker(sock).start(); // created multithreads
        }

    }
}