package sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author xerx593
 */
public class chat {

    public static void main(String args[]) throws IOException {
        //args: <nickName> <server port> <client port>
        startServer(Short.valueOf(args[1]));
        startClient(args[0], Short.valueOf(args[2]));
    }

    private static void startServer(short port) throws IOException {
        final Thread st = new Thread(() -> {
            try {
                //start the server...
                final ServerSocket ss = new ServerSocket(port);
                System.out.println("Listening for connections..");
                //and (blockingly) listen for connections 
                final Socket sock = ss.accept();
                //got one!
                final BufferedReader input = new BufferedReader(new InputStreamReader(
                    sock.getInputStream()//remote input!
                ));
                while (true) {// as long as connected, bad exception otherwise
                    String message1 = input.readLine();// read remote message
                    System.out.println(message1);// print to (local) console
                }
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
                ioe.printStackTrace(System.err);
            }
        });
        st.start();
    }

    private static void startClient(String id, short port) {
        final Thread ct = new Thread(() -> {
            Socket sock = null;
            while (sock == null) {
                try {//..to connect with a server
                    sock = new Socket("127.0.0.1", port);
                } catch (IOException io) {
                    System.out.println("Socket Failed.. retry in 10 sec");
                    try {//..retry in 10 secs
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace(System.err);
                    }
                }
            }
            //sock != null -> connection established
            try {
                System.out.println("Connection Successful..");
                final BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in //(local!) "keyboard"
                ));
                //remote output
                final PrintStream out = new PrintStream(sock.getOutputStream());
                while (true) {
                    System.out.print(id + ">");
                    final String message1 = in.readLine();
                    out.println(id + ">" + message1);
                }
            } catch (IOException io) {
                System.out.println("Socket Failed");
            }
        });
        ct.start();

    }

}