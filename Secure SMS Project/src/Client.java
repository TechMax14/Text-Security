import java.io.*;
import java.net.*;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Client extends JFrame{
	
	private static final long serialVersionUID = 1L;
	public  char temp;
	public  char rc;
	private JTextField userText; // writing text field (up top)
	private JTextArea chatWindow;	// conversation/text display
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	Random r = new Random();
	//char rc = (char) (48 + r.nextInt(47));
	//char rc = 'a';
	Server skey = new Server();
	
	
	//constructor
	public Client(String host){
		super("Secured Client");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendMessage(event.getActionCommand()); // encrypt?
					userText.setText("");
				}
			}
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(400, 300); //Sets the window size
		setVisible(true);
		rc = skey.rc;
	}
	
	//connect to server
	public void startRunning() throws InterruptedException{
		try{
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException eofException){
			showMessage("\nClient terminated the connection");
		}catch(IOException ioException){
			ioException.printStackTrace();
		}finally{
			closeConnection();
		}
	}
	
	//connect to server
	private void connectToServer() throws IOException, InterruptedException{
		showMessage("Attempting connection... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		Thread.sleep(1200);
		showMessage("Connection Established! Connected to: " + connection.getInetAddress().getHostName());
	}
	
	//set up streams
	private void setupStreams() throws IOException, InterruptedException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		Thread.sleep(800);
		showMessage("\nThe streams are now set up! \n");
	}
	
	//while chatting with server
	private void whileChatting() throws IOException{ // outputs 
		ableToType(true);
		System.out.println("Key = [" + rc + "] \nEncrypted [Client] Messages:" );
		do{
			try{ 				
				message = (String) input.readObject();
				char temp = message.charAt(message.length()-1);
				if(temp == ']')
					showMessage("\n" + message);
				else {
					showMessage("\n" + decrypt(message));
				}
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("Unknown data received!");
			}
		}while(!message.equals("SERVER - END"));	
	}

	//Close connection
	private void closeConnection(){
		showMessage("\nConnection closed.");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	//send message to server
	private void sendMessage(String message){
		try{
			output.writeObject(encrypt("Client - " + message));
			output.flush();
			showMessage("\nClient - " + message); 
//			}
		}catch(IOException ioException){
			chatWindow.append("\n Oops! Something went wrong!");
		}
	}
	
	//update chat window
	private void showMessage(final String message){

			SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						chatWindow.append(message);
					}
				}
			);
		
	}
	
	//allows user to type
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