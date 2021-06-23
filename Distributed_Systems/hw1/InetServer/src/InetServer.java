package Distributed_Systems.hw1.InetServer.src; // Add project package

import java.io.*; // Import I/O libraries
import java.net.*; // Import Java networking libraries

class Worker extends Thread { // Worker constructor
    Socket sock; // init socket

    Worker(Socket s) {
        sock = s;
    }

    public void run() {
        PrintStream out = null; // init output stream to null
        BufferedReader in = null; // init input to null
        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream())); // read input from inputStream of
                                                                                   // sock (socket)
            out = new PrintStream(sock.getOutputStream()); // printable value of sock's output stream
            try {
                String name; // init name
                name = in.readLine(); // from in, read one line at a time
                System.out.println("Looking up " + name);
                printRemoteAddress(name, out); // pass name and out to printRemoteAddress, prints the returned
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

    static void printRemoteAddress(String name, PrintStream out) {
        // function is for printing the information received from the server to the the
        // client
        try {
            out.println("Looking up " + name + "...");
            InetAddress machine = InetAddress.getByName(name); // gets IP address by the name passed to function
            out.println("Host name: " + machine.getHostName()); // gets host name by IP address
            out.println("Host IP: " + toText(machine.getAddress())); // get IP address
        } catch (UnknownHostException ex) {
            out.println("Failed in attempt to look up " + name);
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

public class InetServer {
    public static void main(String[] args) throws Exception {
        int q_len = 6;
        int port = 1565; // set the port
        Socket sock; // init sock

        ServerSocket servsock = new ServerSocket(port, q_len); // create server socket

        System.out.println("Patrick Marre's Inet Server v1.1 starting up, listening at port 1565.\n");
        while (true) {
            sock = servsock.accept(); // connect socket
            new Worker(sock).start(); // created multithreads
        }

    }
}