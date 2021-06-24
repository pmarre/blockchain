package Distributed_Systems.hw2.JokeServer.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class JokeClient {
    public static void main(String args[]) {
        String serverName;
        if (args.length < 1) // if command line args are less than 1, servername = localhost
            serverName = "localhost";
        else
            serverName = args[0]; // else servername is the first command line arg

        System.out.println("Patrick Marre's Inet Client, v1.1.\n");
        System.out.println("Using server: " + serverName + ", Port: 1565");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); // create input buffer
        try {
            String name;
            do { // while user does not type quit do the following
                System.out.print("Enter a host name or IP address, (quit) to end: ");
                System.out.flush();
                name = in.readLine();
                if (name.indexOf("quit") < 0)
                    getRemoteAddress(name, serverName); // get the remote address of the input from the user
            } while (name.indexOf("quit") < 0);
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

    static void getRemoteAddress(String name, String serverName) {
        Socket sock;
        BufferedReader fromServer;
        PrintStream toServer;
        String textFromServer;
        try {
            sock = new Socket(serverName, 1565); // new socket at port 1565
            fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream())); // read from the buffer input
            toServer = new PrintStream(sock.getOutputStream()); // print to the out stream from the socket
            toServer.println(name);
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
