/*
 * Ezra Addas, CSCE 416
 * Assignment 2, GroupChatServer
 */

// Importing packages for input, output, socket, ArrayLists
// and threading
import java.net.*;
import java.io.*;
import java.util.*;

/*
 * The GroupChatServer will handle watching for incoming clients
 * and reading client messages simultaneously, using a list to
 * track active clients and relay messages to all clients. The
 * server will use threads to handle these tasks without having
 * to wait (asynchronous operations). To use threads, the server
 * must implement "Runnable"
 */
public class GroupChatServer implements Runnable {

    // Create BufferedReader object to read from any given socket
    private BufferedReader clientReader;

    // Create PrintWriter object to write to socket
    private PrintWriter clientWriter;

    // Create String variable for client name associated with
    // each server object
    private String clientName;

    // Create Socket object to track client's socket to close
    // when client quits
    private Socket clientSock;

    // Create ArrayList of active clients with GroupChatServer objects
    private static ArrayList<GroupChatServer> activeClients = new ArrayList<GroupChatServer>();

    // Constructor method should set reader and writer objects for
    // a child (being handled individually) and set the associated
    // client name and client socket
    public GroupChatServer(BufferedReader reader, PrintWriter writer, String name, Socket sock) {
        this.clientReader = reader;
        this.clientWriter = writer;
        this.clientName = name;
        this.clientSock = sock;
    }

    // Create a child thread that runs the following tasks
    public void run() {

        try {

            // Announce new client added to group chat
            String hello = this.clientName + " has entered the group chat";
            this.sendToAll(hello, this);

            // Watch for the client at this server instance
            while (true) {

                // Read a line from this client
                String line = this.clientReader.readLine();

                // If null is received, client has quit and must be
                // removed from the client list
                if (line == null) {
                    System.out.println(this.clientName + " disconnected from chat.");
                    break;
                }

                // Broadcast the message to other clients using
                this.sendToAll(line, this);
            }
         } catch (Exception e) {
            System.out.println(this.clientName + " suddenly disconnected.");
        } finally {

            // Send a final goodbye message to all clients
            String goodbye = this.clientName + " has left the group chat";
            this.sendToAll(goodbye, this);

            // Then remove the client from the list
            GroupChatServer.removeClient(this);

            // And finally shut down all open connections
            // Close related resources when client quits
            try {
                this.clientReader.close();
                this.clientWriter.close();
                this.clientSock.close();

            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    /*
     * In main, the server program begins, starting a parent thread
     * that watches for and accepts incoming clients, adding it
     * to the active clients list.
     */
    public static void main(String args[]) {

        // Check if correct number of arguments given to set up a
        // port to listen on
        if (args.length != 1) {
            System.out.println("usage: java GroupChatServer <port>");
            System.exit(1);
        }

        // Use command-line argument to set up port connection
        int serverPort = Integer.parseInt(args[0]);

        // Use try and catch blocks to catch socket related exceptions
        try {

            // Create server on given port
            ServerSocket server = new ServerSocket(serverPort);
            System.out.println("Group chat created!");

            // Continuously wait for clients to join the group chat
            while (true) {

                // Use try and catch block to ensure that the connection
                // will not reset every time a client disconnects
                try {

                    Socket clientSock = server.accept();

                    // Set up reader and writer objects for associated client
                    BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
                    PrintWriter toClient = new PrintWriter(clientSock.getOutputStream(), true);

                    // Immediately take the client name sent to server socket
                    String name = fromClient.readLine();

                    System.out.println(name + " connected to chat.");

                    // Spawn a child thread to watch for activity from client instance
                    // (using new GroupChatServer object) and add to list
                    GroupChatServer newClient = new GroupChatServer(fromClient, toClient, name, clientSock);
                    GroupChatServer.addClient(newClient);
                    Thread child = new Thread(newClient);
                    child.start();

                } catch(Exception e) {
                    System.out.println(e);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    // Getter method to return PrintWriter object of an active client
    public PrintWriter getClientWriter() {
        return this.clientWriter;
    }

    // Getter method to return client name
    public String getClientName() {
        return this.clientName;
    }

    // Synchronized method for sending client messages, since ArrayLists
    // are not thread-safe by default (parent thread may be adding or
    // removing clients as a message is being sent)
    public synchronized void sendToAll(String message, GroupChatServer sender) {
        for (GroupChatServer client : activeClients) {

            // Only send the message to other clients, not sender
            if (client != sender) {

                // Use try and catch block to handle errors during sending
                try {

                    // Make sure client writer is not null (still open)
                    if (client.getClientWriter() != null) {
                        client.getClientWriter().println(sender.getClientName() + ": " + message);
                    }

                } catch (Exception e) {
                    System.out.println(e);
                    // Forcefully remove client so it is not accessed again
                    GroupChatServer.removeClient(client);
                }

            }
        }
    }

    // Synchronized method for adding clients, since ArrayLists are
    // not thread-safe by default (parent thread may be adding a
    // client as a message is being sent to others)
    public synchronized static void addClient(GroupChatServer client) {
        activeClients.add(client);
    }

    // Synchronized method for removing clients, since ArrayLists are
    // not thread-safe by default (child thread may be removing a
    // client as one enters)
    public synchronized static void removeClient(GroupChatServer client) {
        activeClients.remove(client);
    }
}
