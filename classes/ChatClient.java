import java.awt.*;
import java.awt.event.*;

//based on code snippets from original Java course
public class ChatClient {

  private TextArea output;
  private TextField input;
  private Button sendButton;
  private Button quitButton;
  private Frame frame;
  private Choice usernames;
  private Dialog aboutDialog;

  public ChatClient() {
    output = new TextArea(10,50);
    input = new TextField(50);
    sendButton = new Button("Send");
    quitButton = new Button("Quit");
    usernames = new Choice();
    usernames.add("John Doe");
    usernames.add("Fox Mulder");
    usernames.add("Dana Scully");
  }

  public void launchFrame() {
    frame = new Frame("PPC Chat");

    // Use the Border Layout for the frame
    frame.setLayout(new BorderLayout());

    frame.add(output, BorderLayout.WEST);
    frame.add(input, BorderLayout.SOUTH);

    // Create the button panel
    Panel p1 = new Panel(); 
    p1.setLayout(new GridLayout(3,1));
    p1.add(sendButton);
    p1.add(quitButton);
    p1.add(usernames);

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
  }

  private class SendHandler implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      String text = input.getText();
      output.append(usernames.getSelectedItem() + ": " + text + "\n");
      input.setText("");
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
      if ( aboutDialog == null ) {
        aboutDialog = new AboutDialog(frame, "About", true);
      }
      aboutDialog.setVisible(true);
    }
  }

  private class AboutDialog extends Dialog implements ActionListener  {
    public AboutDialog(Frame parent, String title, boolean modal) {
      super(parent,title,modal);
      add(new Label("The ChatClient is a neat tool that allows you to talk " +
                  "to other ChatClients via a ChatServer"),BorderLayout.NORTH);
      Button b = new Button("OK");
      add(b,BorderLayout.SOUTH);
      b.addActionListener(this);
      pack();
    }
    // Hide the dialog box when the OK button is pushed
    public void actionPerformed(ActionEvent e) {
      setVisible(false);
    }
  }

  public static void main(String[] args) {
    ChatClient c = new ChatClient();
    c.launchFrame();
  }
}
