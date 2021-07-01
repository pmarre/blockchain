/*
 * 
 * Header Goes Here
 * 
 */

import java.io.*;
import java.net.*;

class ListenWorker extends Thread {
    Socket sock;

    ListenWorker(Socket s) {
        sock = s;
    }

    public void run() {
        PrintStream out = null;
        BufferedReader in = null;
        try {
            out = new PrintStream(sock.getOutputStream());
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String sockdata;
            while (true) {
                sockdata = in.readLine();
                if (sockdata != null)
                    System.out.println(sockdata);
                System.out.flush();
            }
            // sock.close();
        } catch (IOException x) {
            System.out.println("Connection reset. Listening again...");
        }

    }
}

public class MyListener {
    public static boolean controlSwitch = true;

    public static void main(String a[]) throws IOException {
        int q_len = 6;
        int port = 2540;
        Socket sock;

        ServerSocket ss = new ServerSocket(port, q_len);

        System.out.println("Patrick Marre's Port listener running at port " + port + ".\n");
        while (controlSwitch) {
            sock = ss.accept();
            new ListenWorker(sock).start();
            // try {Thread.sleep(1000);} catch(InterruptedException ex) {}
        }
    }
}
