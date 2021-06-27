import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.*; // For UUID 

public class JokeClient {
    public static void main(String args[]) {
        String serverName;
        UUID uuid = UUID.randomUUID();
        if (args.length < 1) // if command line args are less than 1, servername = localhost
            serverName = "localhost";
        else
            serverName = args[0]; // else servername is the first command line arg

        System.out.println("Patrick Marre's Joke Client, v1.1.\n");
        System.out.println("Using server: " + serverName + ", Port: 1565");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); // create input buffer
        try {
            String name;
            String mode;
            System.out.print("Enter your username, (quit) to end: ");
            System.out.flush();
            name = in.readLine();
            int j_count = 0;
            int p_count = 0;
            do { // while user does not type quit do the following

                System.out.print("Would you like a joke or proverb?, (quit) to end: ");
                System.out.flush();
                mode = in.readLine();
                if (name.indexOf("quit") < 0) {
                    if (mode.indexOf("quit") < 0) {
                        if (mode.equals("joke")) {
                            getRemoteAddress(uuid, name, mode, serverName, j_count); // get the remote address of the input
                                                                               // from the
                                                                               // user
                            if (j_count == 3)
                                j_count = 0;
                            else
                                j_count++;
                        } else if (mode.equals("proverb")) {
                            getRemoteAddress(uuid, name, mode, serverName, p_count); // get the remote address of the input
                                                                               // from the
                            // user
                            if (p_count == 3)
                                p_count = 0;
                            else
                                p_count++;
                        }
                    }
                }
            } while (name.indexOf("quit") < 0 || mode.indexOf("quit") < 0);
            System.out.println("Cancelled by user request.");
        } catch (IOException x) { // if error print error
            x.printStackTrace();
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

    static void getRemoteAddress(UUID uuid, String name, String mode, String serverName, int count) {
        Socket sock;
        BufferedReader fromServer;
        PrintStream toServer;
        String textFromServer;
        try {
            sock = new Socket(serverName, 1565); // new socket at port 1565
            fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream())); // read from the buffer input
            toServer = new PrintStream(sock.getOutputStream()); // print to the out stream from the socket
            toServer.println(uuid);
            toServer.println(name);
            toServer.println(count);
            toServer.println(mode);

            toServer.flush();

            for (int i = 1; i <= 3; i++) { // print 3 lines from the information that is returned from the server
                textFromServer = fromServer.readLine();
                if (textFromServer != null)
                    System.out.println(textFromServer);
            }
            sock.close(); // close socket to avoid leaks
        } catch (IOException x) { // if error print error
            System.out.println("Socket error.");
            x.printStackTrace();
        }
    }

}
