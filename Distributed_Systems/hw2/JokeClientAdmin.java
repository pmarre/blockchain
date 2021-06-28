/*--------------------------------------------------------
1. Patrick Marre / 6.27.21:

2. Java version used (java -version), if not the official version for the class:

openjdk version "11.0.11" 2021-04-20
OpenJDK Runtime Environment AdoptOpenJDK-11.0.11+9 (build 11.0.11+9)
OpenJDK 64-Bit Server VM AdoptOpenJDK-11.0.11+9 (build 11.0.11+9, mixed mode)

3. Precise command-line compilation examples / instructions:

> javac JokeClientAdmin.java


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

When not using 'localhost', it has been buggy and timing out. 

----------------------------------------------------------*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class JokeClientAdmin {

    public static void main(String args[]) {
        String serverName;
        if (args.length < 1) // if command line args are less than 1, servername = localhost
            serverName = "localhost";
        else
            serverName = args[0]; // else servername is the first command line arg
        System.out.println("Patrick Marre's Joke Client, v1.1.\n");
        System.out.println("Using server: " + serverName + ", Port: 5050");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); // create input buffer

        try {
            String mode;
            do { // while user does not type quit do the following
                System.out.print("Type 'joke' for joke mode or 'proverb' for proverb mode: ");
                System.out.flush();
                mode = in.readLine(); // get new mode from user
                if (mode.indexOf("quit") < 0) {
                    if (mode.equals("joke") || mode.equals("proverb")) {
                        setMode(mode, serverName); // set the mode
                        System.out.println("Changed mode to " + mode);
                    }
                }
            } while (((mode.equals("joke")) || (mode.equals("proverb"))));
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

    static void setMode(String mode, String serverName) {
        Socket sock;
        BufferedReader from_server;
        PrintStream to_server;
        String txt_from_server;

        try {
            sock = new Socket(serverName, 5050); // new socket at port 5050
            from_server = new BufferedReader(new InputStreamReader(sock.getInputStream())); // read from the buffer
                                                                                            // input
            to_server = new PrintStream(sock.getOutputStream()); // print to the out stream from the socket
            to_server.println(mode); // send mode to the server
            to_server.flush();

            for (int i = 1; i <= 3; i++) { // print 3 lines from the information that is returned from the server
                txt_from_server = from_server.readLine();
                if (txt_from_server != null)
                    System.out.println(txt_from_server);
            }
            sock.close(); // close socket to avoid leaks
        } catch (IOException x) { // if error print error
            System.out.println("Socket error.");
            x.printStackTrace();
        }
    }

}