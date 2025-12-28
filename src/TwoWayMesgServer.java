/*
 * Ezra Addas, CSCE 416
 * Assignment 1, TwoWayMesgServer
 */

// Importing packages for input, output, and socket
import java.io.*;
import java.net.*;

/*
 * The TwoWayMesgServer will receive requests from clients
 * and connect to the client to print received messages
 * and send new messages.
 */
public class TwoWayMesgServer {

    public static void main(String args[]) {

        // At command line, port number must be given (one argument)
        if (args.length != 1) {
            // Tell user what is needed to run
            System.out.println("usage: java TwoWayMesgServer <port>");
            System.exit(1);
        }

        // Take port number given to direct the server to
        int portNum = Integer.parseInt(args[0]);

        // Use try and catch block to handle any exceptions
        try {

            // Create a new server socket using provided port,
            // effectively binding the port number to this socket
            ServerSocket server = new ServerSocket(portNum);

            // Wait for connection request and accept
            Socket client = server.accept();

            // Print connection details
            System.out.println("Connected to client at ('" +
                    ((InetSocketAddress) client.getRemoteSocketAddress()).getAddress().getHostAddress()
                    + "', '" + ((InetSocketAddress) client.getRemoteSocketAddress()).getPort()
                    + "')");

            // Don't accept other clients, close socket
            server.close();

            // Create BufferedReader object to read from client
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(
                    client.getInputStream()));

            // Create another BufferedReader object to read from keyboard
            // (server side)
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(System.in));

            // Write to client (auto flush on)
            PrintWriter clientWriter = new PrintWriter(client.getOutputStream(), true);

            // Serve client until client quits
            while (true) {
                // Read any messages from client
                String message = clientReader.readLine();

                // If null received, then client quit (break and close)
                if (message == null) {
                    System.out.println("Client closed connection");
                    client.close();
                    break;
                }

                // Display client message
                System.out.println("Client: " + message);

                // Read keyboard input
                String servMesg = serverReader.readLine();

                // Send server message to client socket input stream
                clientWriter.println(servMesg);
            }

            // Close and exit
            clientWriter.close();
        }
        catch (Exception e) {
            // Print exception
            System.out.println(e);
        }
    }
}
