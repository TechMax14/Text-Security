import java.io.*;
import java.net.*;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame {
	

	private static final long serialVersionUID = 1L;
	private JTextField userText;  // text/writing box up top
	private JTextArea chatWindow; // chat display box under
	private ObjectOutputStream output; // sends to client
	private ObjectInputStream input; // receives from client
	private ServerSocket server; 
	private Socket connection; // socket = connection b/w you and some other computer
	public  char rc;
	public  char temp1;
	static  JTextField key = new JTextField();
	static  JLabel label = new JLabel();
	Random r = new Random();
	//char rc = (char) (48 + r.nextInt(47));
	//char rc = 'a';

	//constructor
	public Server(){					// user interface (GUI)
		super("Secured Server");
		userText = new JTextField();
		userText.setEditable(false);  // b4 connected to client we cannot access text box (userText)
		userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendMessage(event.getActionCommand());	// turns message into an action event
					userText.setText("");	// resets text box after message is sent/read
				}
			}
		);
		add(userText, BorderLayout.NORTH); // adds/assigns text box to top of screen (NORTH)
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(400, 300); //Sets the window size
		setVisible(true);
		Object[] fields = {
				"Choose a unique character for both parties", label,
				"char:", key
		};
		JOptionPane.showConfirmDialog(null, fields, "Special Key", JOptionPane.OK_CANCEL_OPTION);
		String sKey = Server.key.getText();
		temp1 = sKey.charAt(0);
		rc = temp1;
	}

	public void startRunning() throws InterruptedException{
		try{
			server = new ServerSocket(6789, 100); //6789 is a dummy port for testing, where the input arrives to. The 100 is the maximum people waiting to connect.
			while(true){
				try{
					//Trying to connect and have conversation
					waitForConnection();
					setupStreams(); // sets up output and input streams
					whileChatting(); // lets us send messages back and forth
				}catch(EOFException eofException){
					showMessage("\nServer ended the connection! "); // error for when connection is ended
				} finally{
					closeConnection(); //Changed the name to something more appropriate
				}
			}
		} catch (IOException ioException){
			ioException.printStackTrace();
		}
	}
	//wait for connection, then display connection information
	private void waitForConnection() throws IOException{
		showMessage("Waiting for someone to connect... \n");
		connection = server.accept();	// accepts a connection to the server
		showMessage("Now connected to [" + connection.getInetAddress().getHostName() + "]");
	}
	
	//get stream to send and receive data
	private void setupStreams() throws IOException, InterruptedException{
		output = new ObjectOutputStream(connection.getOutputStream()); // sets pathway to send things out, connects output to connection made by socket
		output.flush(); // output house keeping, keeps shit clean		
		input = new ObjectInputStream(connection.getInputStream()); // sets pathway to receive things in
		Thread.sleep(800);
		showMessage("\nStreams are now setup! \n");
	}
	
	//during the chat conversation
	private void whileChatting() throws IOException{
		String message = "[You are now connected]";
		sendMessage2(message);	// encrypt
		ableToType(true);
		System.out.println("Key = [" + rc + "] \nEncrypted [Server] Messages:" );
		do{
			try{
				message = (String) input.readObject(); // reads in message sent
				showMessage("\n" + decrypt(message));
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("The user has sent an unknown object!");
			}
		}while(!message.equals("CLIENT - END"));
	}

	public void closeConnection(){
		showMessage("\nClosing Connections... \n\n");
		ableToType(false);
		try{
			output.close(); //Closes the output path to the client
			input.close(); //Closes the input path to the server, from the client.
			connection.close(); //Closes the connection between you can the client
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	//Send a mesage to the client
	private void sendMessage(String message){
		try{
			output.writeObject(encrypt("SERVER - " + message));
			output.flush();
			showMessage("\nSERVER - " + message); 
		}catch(IOException ioException){
			chatWindow.append("\n ERROR: CANNOT SEND MESSAGE, PLEASE RETRY");
		}
	}
	
	private void sendMessage2(String message){
		try{
			output.writeObject("Client - " + message);
			output.flush();
			showMessage("\nSERVER - " + message); 
		}catch(IOException ioException){
			chatWindow.append("\n ERROR: CANNOT SEND MESSAGE, PLEASE RETRY");
		}
	}
	
	//update chatWindow
	private void showMessage(final String message){
			SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						chatWindow.append(message);
					}
				}
			);
		
	}
	
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(tof);
				}
			}
		);
	}
	
	private String encrypt(String message) {
		String msg = message;
		char[] temp = new char[msg.length()];
		for(int i = 0; i < msg.length(); i++) {		
			char c = msg.charAt(i);
			temp[i] = (char) (c + rc);
		}
		for(int j = 8; j<temp.length; j++) {
			System.out.print(temp[j]);
		}
		System.out.println("");
		String t = String.valueOf(temp);
		//System.out.println(t); // in console
		return t;
}

	private String decrypt(String message) {
		String msg = message;
		char[] temp = new char[msg.length()];
		for(int i = 0; i < msg.length(); i++) {	
			char c = msg.charAt(i);
			temp[i] = (char) (c - rc);
		}
		String t = String.valueOf(temp);
		return t;	
	}

}