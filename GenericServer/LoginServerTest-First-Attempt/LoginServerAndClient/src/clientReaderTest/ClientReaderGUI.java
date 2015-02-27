/**
 * 
 */
package clientReaderTest;

import java.awt.LayoutManager;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * @author Aaron
 *
 */
@SuppressWarnings("serial")
public class ClientReaderGUI extends JPanel implements ActionListener{
	private JPasswordField passwordField;
	private JTextField userNameField;
	private JButton login;
	private String password = "";
	private String username = "";
	private	boolean buttonPressed = false;
	public ClientReaderGUI(){
		this.setLayout((LayoutManager) new BoxLayout(this, BoxLayout.PAGE_AXIS));

		userNameField = new JTextField();
		userNameField.setSize(50, 20);
		userNameField.setFocusable(true);
		this.add(userNameField);
		
		passwordField = new JPasswordField();
		passwordField.setFocusable(true);
		passwordField.setSize(50, 20);
		passwordField.setLocation(25,75);
		this.add(passwordField);
		
		login = new JButton("Login");

		login.setLocation(230, 75);
		login.setSize(10, 100);
		this.add(login);
		login.setEnabled(false);
		login.addActionListener(this);
	}
	public boolean getCredentials(){
		while(userNameField.getText()=="" || !buttonPressed){
				login.setEnabled(true);
		}
		username = userNameField.getText();
		userNameField.setText("");
		password = "";
		for(char c : passwordField.getPassword()){
			password+= c;
		}
		passwordField.setText("");

		login.isSelected();
		return buttonPressed;
		
	}
	public String getPassword(){
		return password;
	}
	public String getUserName(){
		return username;
	}
	public void buttonReset(){
		buttonPressed=false;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		buttonPressed = true;
	}
}
