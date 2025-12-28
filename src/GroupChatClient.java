/*
 * Ezra Addas, CSCE 416
 * Assignment 2, GroupChatClient
 */

// Importing packages for input, output, socket and threading
import java.net.*;
import java.io.*;

/*
 * The GroupChatClient will send a connection request to a group
 * chat server to send messages and receive messages from other
 * clients in the chat. The client will use threads to send
 * and receive messages in the group chat without having to wait
 * (asynchronous messaging). To use threads, the class must
 * implement "Runnable"
 */
public class GroupChatClient implements Runnable {

    // Create BufferedReader object to read from the keyboard
    private BufferedReader userReader;

    // Create PrintWriter object to write to socket
    private PrintWriter serverWriter;

    // Constructor method should set the reader and writer object
    // for a child thread
    public GroupChatClient(BufferedReader reader, PrintWriter writer) {
        this.userReader = reader;
        this.serverWriter = writer;
    }

    // Create a child thread that runs the following tasks
    public void run() {

        // Use a try and catch block to handle possible exceptions
        try {

            // Run this while loop continuously
            while (true) {

                // Read from the user's input
                String line = this.userReader.readLine();

                // If line is null, client has quit
                if (line == null) {
                    System.out.println("*** Client closing connection");
                    break;
                }

                // Otherwise, write to the socket
                this.serverWriter.println(line);
            }
        } catch (Exception e) {
            // Print any caught exceptions
            System.out.println(e);
            System.exit(1);
        }

        // End parent thread by exiting the system
        System.exit(0);
    }

    /*
     * In main, the client program begins, creating a parent thread
     * that reads from the socket and writes to the screen, and
     * spawns a child thread that handles client messaging simultaneously.
     */
    public static void main(String args[]) {

        // Print a usage message if not given host and port number
        // at command-line
        if (args.length != 3) {
            System.out.println("usage: java GroupChatClient <host> <port> <username>");
            System.exit(1);
        }

        // Set up a socket to connect to server, using given host
        // and port number
        Socket sock = null;
        try {

            sock = new Socket(args[0], Integer.parseInt(args[1]));
            System.out.println("Connected to server at " +
                    args[0] + ":" + args[1]);

        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }

        // Now set up a thread to read from user and write to socket
        try {

            // Create PrintWriter object for socket with auto flush
            PrintWriter toServer = new PrintWriter(sock.getOutputStream(), true);

            // Create BufferedReader object for reading from keyboard
            BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));

            // Send the given username to the server socket to be read
            // immediately before any other messages
            toServer.println(args[2]);

            // Spawn the child thread that will watch for client activity
            Thread child = new Thread(new GroupChatClient(fromUser, toServer));
            child.start();

        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }

        // Then read from the server and display to the screen
        try {

            // Create BufferedReader to read other client messages
            // from server socket
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));

            // Run while loop continuously until server quits
            while (true) {

                // Read the line from the server
                String line = fromServer.readLine();

                // If line is null, server has quit
                if (line == null) {
                    System.out.println("Group chat closed.");
                    break;
                }

                // Write lines to the user
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }
}
