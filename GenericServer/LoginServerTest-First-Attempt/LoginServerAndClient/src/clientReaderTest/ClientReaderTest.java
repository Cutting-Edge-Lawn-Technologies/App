/**
 * 
 */
package clientReaderTest;


import javax.swing.JFrame;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * @author Aaron
 *
 */
@SuppressWarnings("serial")
public class ClientReaderTest implements Runnable {
	private ClientReaderGUI gui;
	public static void main(String[] args) {
		ClientReaderTest client = new ClientReaderTest();
	}
	public ClientReaderTest(){
		JFrame loginWindow = new JFrame();
		loginWindow.setBounds(100, 100, 300, 150);
		gui = new ClientReaderGUI();
		loginWindow.add(gui);
		Thread loginThread = new Thread(this);
		loginThread.start();
		loginWindow.setVisible(true);
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean running = true;
		try{
			while(running){
				int theServerPort = 4444;

				Socket sock = new Socket("localhost",theServerPort);
				PrintStream ps = new PrintStream(sock.getOutputStream());
				InputStreamReader isr = new InputStreamReader(sock.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				boolean loggedIn = false;
				boolean tooManyLoginAttempts = false;
				while(!loggedIn && !tooManyLoginAttempts){
					if(gui.getCredentials()){
						gui.buttonReset();
						String s = gui.getUserName()+" "+gui.getPassword();
						ps.println(s);
						ps.flush();

						String message;
						message = br.readLine();
						loggedIn=message.equals("Login Success");
						if( message.equals("Please Contact System Administrator: too many login attempts")){
							System.out.println(message);
							tooManyLoginAttempts = true;
						}
					}

				}
				running = false;
				System.exit(0);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
