package Distributed_Systems.hw1.InetServer.src;

import java.io.*;
import java.net.*;

public class InetClient {
    public static void main(String args[]) {
        String serverName;
        if (args.length < 1) serverName = "localhost";
        else serverName = args[0];

        System.out.println("Patrick Marre's Inet Client, v1.1.\n");
        System.out.println("Using server: " + serverName + ", Port: 1565");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            String name;
            do {
                System.out.print("Enter a host name or IP address, (quit) to end: ");
                System.out.flush();
                name = in.readLine();
                if (name.indexOf("quit") < 0) getRemoteAddress(name, serverName);
            }
        }
    }
}
