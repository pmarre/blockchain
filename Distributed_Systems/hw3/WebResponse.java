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

            System.out.println("Sending the HTML Response now: " + Integer.toString(WebResponse.i) + "\n");
            String HTMLResponse = "<html> <h1> Hello Browser World! " + Integer.toString(WebResponse.i++)
                    + "</h1> <p><p> <hr><p>";
            out.println("HTTP/1.1 200 OK");
            out.println("Connection: close");
            // int Len = HTMLResponse.length();
            // out.println("Content-Length: " + Integer.toString(Len));
            out.println("Content-Length: 400");
            out.println("Content-Type: text/html \r\n\r\n");
            out.println(HTMLResponse);

            for (int j = 0; j < 6; j++) {
                out.println(in.readLine() + "<br>\n");
            }
            out.println("</html>");

            sock.close();
        } catch (IOException x) {
            System.out.println("Error: Connection reset. Listening again...");
        }
    }
}

public class WebResponse {
    static int i = 0;

    public static void main(String args[]) throws IOException {
        int q_len = 6;
        int port = 2540;
        Socket sock;

        ServerSocket ss = new ServerSocket(port, q_len);

        System.out.println("Patrick Marre's WebResponse running at " + port + ".");
        System.out.println("Point Firefox browser to http://localhost:2540/abc.\n");
        while (true) {
            sock = ss.accept();
            new ListenWorker(sock).start();
        }
    }

}
