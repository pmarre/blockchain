/* 
*
* 1. Explained how MIME-types are used to tell the browser what data is coming.
*       - MIME types are used to tell the browser what type of data is coming through so it knows how to
            handle the data. The browser will handle each data type differently, so if a MIME type of text/html
            comes through, the browser will render that differently then a text/plain type.
* 2. Explained how you would return the contents of requested files (web pages) of type HTML (text/html)	
        - 
* 3. Explained how you would return the contents of requested files (web pages) of type TEXT (text/plain)
*       - 
*
*/

import java.net.*;
import java.io.*;

class AWorker extends Thread {
    // Initialize the worker
    Socket sock;

    AWorker(Socket s) {
        sock = s;
    }

    public void run() {
        PrintStream out = null;
        BufferedReader in = null;

        try {
            out = new PrintStream(sock.getOutputStream());
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

            String name;

            // Get the input url as a string
            String str_url = in.readLine();

            // Split separate GET from url
            String[] s = str_url.split(" ");

            System.out.println(s[0] + "\n" + s[1]);

            // prepend localhost and create URL with user input from Firefox
            URL url = new URL("http://localhost:2540" + s[1]);

            // get the query from the URL
            String params = url.getQuery();

            // Initialize array to separate out parameters
            String a[] = new String[3];

            // Split parameters at &
            String p[] = params.split("&");

            // Split paramaters at = to separate parameter name from input value
            for (int i = 0; i < p.length; i++) {
                String x[] = p[i].split("=");
                a[i] = x[1];
            }

            name = a[0];
            int num1, num2, res;
            num1 = Integer.parseInt(a[1]);
            num2 = Integer.parseInt(a[2]);
            res = num1 + num2;

            // Concatenate the users name, inputs, and the result into one string
            String result = name + ", the result of " + num1 + " + " + num2 + " is " + res;

            System.out.println("Sending the HTML Response now: \n");

            /*
             * The variable form (below) holds the html for the original form, I ended up
             * just outputing the form over again to the user so they could resubmit without
             * having to do a manual reload of the webpage. There is likely a more efficient
             * solution to this, but this is my more "brute force" solution to get the page
             * up and running.
             */
            String form = "<body> <h1>WebAdd</h1><form method=\"GET\" action=\"http://localhost:2540/WebAdd.fake-cgi\">Enter your name and two numbers. My program will return the sum:<p><input type=\"text\" name=\"person\" size=\"20\" value=\"YourName\" /><p><input type=\"text\" name=\"num1\" size=\"5\" value=\"4\" /> <br /><input type=\"text\" name=\"num2\" size=\"5\" value=\"5\" /></p><p><input type=\"submit\" value=\"Submit Numbers\" /></p></p></form>";

            // Concat html code, the result (concat of name, numbers, and sum), and
            // reproduce the form

            String HTMLResponse = "<html> " + "<br><br><h1>  " + result + "</h1> <p><p> <hr><p>" + form;
            out.println("<h1>HTTP/1.1 200 OK</h1>");
            out.println("<h2>Connection: close</h2>");
            int content_len = HTMLResponse.length(); // get the length of the HTMLResponse
            out.println("Content-Length: " + Integer.toString(content_len)); // set Content-Length to the HTMLResponse
                                                                             // length to ensure all data shows up
            // out.println("Content-Length: 1000"); // Changed length here manually to
            // adjust the amount of content that shows up
            out.println("Content-Type: text/html \r\n\r\n"); // specify the content-type so the information is
                                                             // accurately displayed
            out.println(HTMLResponse);

            for (int j = 0; j < 6; j++) {
                out.println(in.readLine() + "<br>\n"); // add breakpoints to create a bottom margin
            }
            out.println("</html>"); // close the HTML tag

            sock.close(); // close socket to avoid a memory leak
        } catch (IOException x) {
            System.out.println("Error: Connection reset. Listening again..."); // if there is an error display this
                                                                               // message
        }
    }
}

public class MiniWebserver {
    /*
     * Miniwebserver connects the user to a server and client (browser at localhost)
     * the user can then access a form and submit their name and two integers the
     * result is then returned to the user on the browser and the form is recreated
     * so another submit is possible without manually reloading the page
     */

    static int i = 0;

    public static void main(String args[]) throws IOException {
        int q_len = 6;
        int port = 2540;
        Socket sock;

        ServerSocket ss = new ServerSocket(port, q_len);

        System.out.println("Patrick Marre's MiniWebserver running at " + port + ".");
        System.out.println("Point Firefox browser to http://localhost:2540/abc.\n");

        while (true) {
            sock = ss.accept();
            new AWorker(sock).start();
        }
    }
}