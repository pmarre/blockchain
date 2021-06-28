/*--------------------------------------------------------

1. Patrick Marre / 6.27.21:

2. Java version used (java -version), if not the official version for the class:

openjdk version "11.0.11" 2021-04-20
OpenJDK Runtime Environment AdoptOpenJDK-11.0.11+9 (build 11.0.11+9)
OpenJDK 64-Bit Server VM AdoptOpenJDK-11.0.11+9 (build 11.0.11+9, mixed mode)

3. Precise command-line compilation examples / instructions:

> javac JokeServer.java


4. Precise examples / instructions to run this program:

In separate shell windows:

> java JokeServer
> java JokeClient
> java JokeClientAdmin

All acceptable commands are displayed on the various consoles.

This may run across multiple machines but has had some issues. To test it, try the following with the IP Address as the second argument.

e.g. if you IP Address is 140.192.1.22 then you would type:

> java JokeClient 140.192.1.22
> java JokeClientAdmin 140.192.1.22

5. Files needed for running the program.

 1. checklist.html
 2. JokeServer.java
 3. JokeClient.java
 4. JokeClientAdmin.java

5. Notes:

The shuffling of the list hasn't been working. Also, when not using 'localhost', it has been buggy and timing out. 

----------------------------------------------------------*/

import java.io.*; // Import I/O libraries
import java.net.*; // Import Java networking libraries
import java.util.*; // Import UUID 

class ClientWorker extends Thread { // Worker constructor for client
    Socket sock; // init socket

    ClientWorker(Socket s) {
        sock = s;
    }

    public void run() {

        PrintStream out = null; // init output stream to null
        BufferedReader in = null; // init input to null

        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream())); // read input from inputStream of
                                                                                   // socket
            out = new PrintStream(sock.getOutputStream()); // printable value of sock's output stream
            try {
                String uuid; // init unique ID
                String name; // init name

                uuid = in.readLine(); // get UUID from input
                name = in.readLine(); // get username

                String newMode = JokeServer.CURR_MODE; // set newMode to the new mode type
                if (newMode.equals("joke")) { // if mode == joke, print the joke out
                    printJoke(name, newMode, out);
                } else if (newMode.equals("proverb")) {
                    System.out.println("<" + name + "> Looking for a " + newMode + ".");
                    printProverb(name, newMode, out);

                }
                System.out.println("Looking up " + newMode);
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

    static void printJoke(String name, String mode, PrintStream out) {
        /*
         * This function takes the name of the user, the current mode, and the output
         * stream to then print a joke from the list that is stored here on the server
         */

        // jokes were pulled from https://www.rd.com/list/short-jokes/
        String jokes[] = { "(PA) I went to buy some camo pants but couldn’t find any.",
                "(PB) I failed math so many times at school, I can’t even count.",
                "(PC) What’s the best thing about Switzerland? I don’t know, but the flag is a big plus.",
                "(PD) Helvetica and Times New Roman walk into a bar. 'Get out of here!' shouts the bartender. 'We don’t serve your type.'" };
        if (JokeServer.J_COUNT < 4) {
            // confirm that the J_Counter is less than the length of the Joke array
            try {
                int n;
                n = JokeServer.J_Order_List.get(JokeServer.J_COUNT); // set index number for jokes
                out.println("Looking up " + mode + "...");
                out.println("<" + name + ">: " + jokes[JokeServer.J_COUNT]); // output name and joke
                if (JokeServer.J_COUNT == 3) { // if end of list, print end and reset counter
                    out.println("JOKE CYCLE COMPLETED");
                    JokeServer.J_COUNT = 0; // reset counter to 0 to start from beginning
                    Collections.shuffle(JokeServer.J_Order_List); // shuffle list order to randomize joke order
                } else
                    JokeServer.J_COUNT++; // increment order
            } finally { // THIS SHOULD BE CHANGED
            }
        }
    }

    static void printProverb(String name, String mode, PrintStream out) {
        /*
         * This function takes the name of the user, the current mode, and the output
         * stream to then print a proverb from the list that is stored here on the
         * server
         */

        // list of proverbs are quotes from Stars Wars movie and not my original
        // thoughts
        String proverbs[] = { "(PA) The ability to speak does not make you intelligent. -Qui-Gon Jinn",
                "(PB) Try not. Do or do not. There is no try. -Yoda",
                "(PC) Who’s the more foolish: the fool or the fool who follows him? -Obi-Wan Kenobi",
                "(PD) Train yourself to let go of everything you fear to lose. -Yoda" };
        if (JokeServer.P_COUNT < 4) {
            // confirm that the J_Counter is less than the length of the Joke array
            try {
                int n;
                n = JokeServer.P_Order_List.get(JokeServer.P_COUNT); // set index
                out.println("Looking up " + mode + "..."); // let the user now that a joke or proverb is being looked up
                out.println("<" + name + ">: " + proverbs[n]); // output name and proverb
                if (JokeServer.P_COUNT == 3) { // if at end reset counter then print out end statement
                    out.println("JOKE CYCLE COMPLETED");
                    JokeServer.P_COUNT = 0; // reset the counter over to 0
                    Collections.shuffle(JokeServer.P_Order_List); // shuffle order of the numerical arraylist so that
                                                                  // the order becomes randomized
                } else
                    JokeServer.P_COUNT++; // increment p_count to move to next proverb
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

class AdminWorker extends Thread {
    // Worker thread specifically for the use with the JokeServerAdmin, this allows
    // the admin to control the mode of the program and switch between joke and
    // proverb
    Socket sock;

    AdminWorker(Socket s) {
        sock = s;
    }

    public void run() {
        PrintStream out_stream = null;
        BufferedReader in_stream = null;

        try {
            in_stream = new BufferedReader(new InputStreamReader(sock.getInputStream())); // get the input stream to get
                                                                                          // information from the user
            out_stream = new PrintStream(sock.getOutputStream()); // get the output stream to print information back
            try {
                String mode;

                mode = in_stream.readLine(); // get mode from ClientAdmin
                if ((mode.equals("joke")) || (mode.equals("proverb"))) {
                    JokeServer.CURR_MODE = mode; // set mode to the new mode
                    System.out.println("Changed mode to " + mode + " mode.");
                }

            } catch (IOException x) {
                System.out.println("Server error");
                x.printStackTrace();
            }
            sock.close();

        } catch (IOException x) {
            System.out.println("Server error");
            x.printStackTrace();
        }
    }
}

class AdministatorLoop implements Runnable {
    public boolean admin_switch = true;

    public void run() { // Running the Admin listen loop
        System.out.println("In the admin looper thread");

        int q_len = 6; /* Number of requests for OpSys to queue */
        int port = 5050; // We are listening at a different port for Admin clients
        Socket sock;

        try {
            ServerSocket servsock = new ServerSocket(port, q_len);
            while (admin_switch) {
                // wait for the next ADMIN client connection:
                sock = servsock.accept();
                new AdminWorker(sock).start();
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

}

public class JokeServer {
    public static String CURR_MODE = "joke"; // default to joke mode
    public static ArrayList<Integer> J_Order_List = new ArrayList<Integer>(); // init list order
    public static ArrayList<Integer> P_Order_List = new ArrayList<Integer>(); // init list order
    public static int J_COUNT;
    public static int P_COUNT;

    public static void main(String a[]) throws IOException {
        int q_len = 6; /* Number of requests for OpSys to queue */
        int port = 4545;
        Socket sock;
        J_COUNT = 0;
        P_COUNT = 0;
        for (int i = 0; i < 4; i++) { // add numbers to order
            J_Order_List.add(i);
            P_Order_List.add(i);
        }

        AdministatorLoop AdminLoop = new AdministatorLoop(); // create a new thread for the admin
        Thread t = new Thread(AdminLoop);
        t.start(); // start thread and wait for admin response

        ServerSocket ss = new ServerSocket(port, q_len);

        System.out.println("Patrick Marre's Joke server starting up at port " + port + ".\n");
        while (true) {
            // wait for the next client connection:
            sock = ss.accept();
            new ClientWorker(sock).start(); // Start up client thread
        }
    }
}
