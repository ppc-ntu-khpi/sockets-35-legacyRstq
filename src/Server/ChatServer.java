import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

  public static void main(String[] args) {
    ChatServer cs = new ChatServer();
    cs.go();
  }

  public final static int DEFAULT_PORT = 2000;
  public final static int DEFAULT_MAX_BACKLOG = 5;
  public final static int DEFAULT_MAX_CONNECTIONS = 20;
  public final static String MAGIC = "Yippy Skippy";

  private String magic = MAGIC;

  private int port = DEFAULT_PORT;
  private int backlog = DEFAULT_MAX_BACKLOG;
  private int numConnections = 0;
  private int maxConnections = DEFAULT_MAX_CONNECTIONS;
  private List<Connection> connections = null;
  private Map<String,String> hostToStudentMap = null;

  //
  // Methods for the Connection class
  //

  String getMagicPassphrase() {
    return magic;
  }

  String getStudentName(String host) {
    return hostToStudentMap.get(host);
  }

  void sendToAllClients(String message) {
    for ( Connection c : connections ) {
      c.sendMessage(message);
    }
  }


  synchronized void closeConnection(Connection connection) {
    connections.remove(connection);
    numConnections--;
    notify();
  }

  //
  // Private methods
  //

  private void go() {
    String portString = System.getProperty("port");
    if (portString != null) port = Integer.parseInt(portString);
    this.port = port;

    String backlogString = System.getProperty("backlog");
    if (backlogString != null) backlog = Integer.parseInt(backlogString);

    String magicString = System.getProperty("magic");
    if (magicString != null) magic = magicString;

    String connections = System.getProperty("connections");
    if (connections != null) maxConnections = Integer.parseInt(connections);

    this.connections = new ArrayList<Connection>(maxConnections);
    this.hostToStudentMap = new HashMap<String,String>(15);

    System.out.println("Server settings:\n\tPort="+port+"\n\tMax Backlog="+
                       backlog+"\n\tMax Connections="+maxConnections + "");
    // Begin the processing cycle
    processRequests();
  }       

  private void processRequests() {
    ServerSocket serverSocket = null;
    Socket communicationSocket;    

    try {
      System.out.println("Attempting to start server...");
      serverSocket = new ServerSocket(port,backlog);
    } catch (IOException e) { 
      System.out.println("Error starting server: Could not open port "+port);
      e.printStackTrace();
      System.exit(-1);
    }
    System.out.println ("Started server on port "+port);

    // Run the listen/accept loop forever
    while (true) {
      try {
        // Wait here and listen for a connection
        communicationSocket = serverSocket.accept();
        handleConnection(communicationSocket);
      } catch (Exception e) { 
        System.out.println("Unable to spawn child socket.");
        e.printStackTrace();
      }
    }
  }

  private synchronized void handleConnection(Socket connection) {
    while (numConnections == maxConnections) {
      try {
	wait();
      } catch(Exception e) { 
	e.printStackTrace();
      }
    }
    numConnections++;
    Connection con = new Connection(this, connection);
    Thread t = new Thread(con);
    t.start();
    connections.add(con);
  }
}