package Distributed_Systems.hw1.InetServer.src; // Add project package

import java.io.*; // Import I/O libraries
import java.net.*; // Import Java networking libraries

class Worker extends Thread {
    Socket sock;

    Worker(Socket s) {
        sock = s;
    }

    public void run() {
        PrintStream out = null;
        BufferedReader in = null;
        try {
            in = new BufferedReader (new InputStreamReader(sock.getOutputStream()));
            out = new PrintStream(sock.getOutputStream());
            try {
                String name;
                name = in.readLine();
                System.out.println("Looking up " + name);
                printRemoteAddress(name, out);
            } catch (IOException x) {
                System.out.println("Server read error");
                x.printStackTrace();
            }
            sock.close();
            catch(IOException ioe) {System.out.println(ioe);}
            }
        }

    static void printRemoteAddress(String name, PrintStream out) {
        try {
            out.println("Looking up " + name + "...");
            InetAddress machine = InetAddress.getByName(name);
            out.println("Host name: " + machine.getHostName());
            out.println("Host IP: " + toText(machine.getAddress()));
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
        int port = 1565;
        Socket sock;

        ServerSocket servsock = new ServerSocket(port, q_len);

        System.out.println("Patrick Marre's Inet Server v1.1 starting up, listening at port 1565.\n");
        while (true) {
            sock = servsock.accept();
            new Worker(sock).start();
        }
    }
}