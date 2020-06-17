import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.nio.charset.Charset;

import javax.swing.JOptionPane;

import java.io.*;

//based on code snippets from original Java course
public class ChatClient {

  private TextArea output;
  private TextField input;
  private Button sendButton;
  private Button quitButton;
  private Frame frame;
  private TextField username;
  private Socket connection = null;
  private BufferedReader serverIn = null;
  private PrintStream serverOut = null;
  private Label userLabel;

  public ChatClient() {
    output = new TextArea(20, 40);
    input = new TextField(40);
    sendButton = new Button("Send");
    quitButton = new Button("Quit");
    username = new TextField(30);
    username.setText("Name");
    userLabel = new Label("Username:");
  }

  private void doConnect() {
    String serverIP = System.getProperty("serverIP", "127.0.0.1");
    String serverPort = System.getProperty("serverPort", "2000");
    try {
      connection = new Socket(serverIP, Integer.parseInt(serverPort));
      InputStream is = connection.getInputStream();
      InputStreamReader isr = new InputStreamReader(is, Charset.forName("UTF-8"));
      serverIn = new BufferedReader(isr);
      serverOut = new PrintStream(connection.getOutputStream());
      Thread t = new Thread(new RemoteReader());
      t.start();
    } catch (Exception e) {
      System.err.println("Unable to connect to server!");
      e.printStackTrace();
    }
  }

  public void launchFrame() {
    frame = new Frame("PPC Chat");

    // Use the Border Layout for the frame
    frame.setLayout(new BorderLayout());

    Panel sendPanel = new Panel();
    sendPanel.setLayout(new GridLayout(2, 1));
    sendPanel.add(input);
    sendPanel.add(sendButton);

    frame.add(output, BorderLayout.WEST);
    frame.add(sendPanel, BorderLayout.SOUTH);

    // Create the button panel
    Panel p1 = new Panel();
    p1.setLayout(new GridLayout(10, 1));
    p1.add(userLabel);
    p1.add(username);
    p1.add(quitButton);

    // Add the button panel to the center
    frame.add(p1, BorderLayout.CENTER);

    // Create menu bar and File menu
    MenuBar mb = new MenuBar();
    Menu file = new Menu("File");
    MenuItem quitMenuItem = new MenuItem("Quit");
    quitMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
    file.add(quitMenuItem);
    mb.add(file);
    frame.setMenuBar(mb);

    // Add Help menu to menu bar
    Menu help = new Menu("Help");
    MenuItem aboutMenuItem = new MenuItem("About");
    aboutMenuItem.addActionListener(new AboutHandler());
    help.add(aboutMenuItem);
    mb.setHelpMenu(help);

    // Attach listener to the appropriate components
    sendButton.addActionListener(new SendHandler());
    input.addActionListener(new SendHandler());
    frame.addWindowListener(new CloseHandler());
    quitButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });

    frame.pack();
    frame.setVisible(true);
    frame.setLocationRelativeTo(null);
    doConnect();
  }

  private class SendHandler implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      String text = input.getText();
      text = username.getText() + ": " + text + "\n";
      serverOut.print(text);
      input.setText("");
    }
  }

  private class RemoteReader implements Runnable {
    public void run() {
      try {
        while ( true ) {
          String nextLine = serverIn.readLine();
          output.append(nextLine + "\n");
        }
      } catch (Exception e) {
          System.err.println("Error while reading from server.");
          e.printStackTrace();
        }
    }
  }

  private class CloseHandler extends WindowAdapter {
    public void windowClosing(WindowEvent e) {
      System.exit(0);
    }
  }

  private class AboutHandler implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      // Create the aboutDialog when it is requested
      JOptionPane.showMessageDialog(frame, "The ChatClient is a neat tool that allows you to talk " + "to other ChatClients via a ChatServer");
    }
  }

  public static void main(String[] args) {
    ChatClient c = new ChatClient();
    c.launchFrame();
  }
}