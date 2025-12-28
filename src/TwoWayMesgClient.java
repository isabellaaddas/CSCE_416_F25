/*
 * Ezra Addas, CSCE 416
 * Assignment 1, TwoWayMesgClient
 */

// Importing packages for input, output, and socket
import java.io.*;
import java.net.*;

/*
 * The TwoWayMesgClient will send a request to a server
 * and connect to the server to send messages and
 * receive server messages
 */
public class TwoWayMesgClient {

    public static void main(String args[]) {

        // At command line, server information must be given (two arguments)
        if (args.length != 2) {
            System.out.println("usage: java TwoWayMesgClient <server name> <server port>");
        }

        // Get server contact information
        String serverName = args[0];
        int serverPort = Integer.parseInt(args[1]);

        // Use try and catch block to handle any exceptions
        try {

            // Connect to server at given host name and port number
            // (creates socket binding to name and port_
            Socket sock = new Socket(serverName, serverPort);

            // Print connection details
            System.out.println("Connected to sever at ('" + serverName + "', '"
                                + serverPort + "')");

            // Write to server (auto flush on)
            PrintWriter serverWriter = new PrintWriter(sock.getOutputStream(), true);

            // Create BufferedReader object to read from keyboard
            BufferedReader userReader = new BufferedReader(new InputStreamReader(System.in));

            // Create another BufferedReader object to read from server
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(sock.getInputStream()));

            // Stay on server until user quits client
            while (true) {
                // Read line from keyboard
                String line = userReader.readLine();

                // If null, user has quit
                if (line == null) {
                    System.out.println("Closing connection");
                    break;
                }

                // Send line to server
                serverWriter.println(line);

                // Read message from server
                String serverMesg = serverReader.readLine();

                // Display server message
                System.out.println("Server: " + serverMesg);
            }

            // Close and exit
            serverWriter.close();
            sock.close();
        }
        catch (Exception e) {
            // Print exception
            System.out.println(e);
        }
    }
}
