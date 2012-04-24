package com.binaydevkota.javabus;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import mjson.Json;

/**
* Contains only one main method which initializes the JFrame <br />
* Read more about in the description of that method <br />
 * <br />
* @author Binay Devkota and Ishan Regmi
*/
public class cLoginWindow extends JFrame{
    /**
    * Checks if user is already logged in or not <br />
    * Allows user to navigate to Register window <br />
    * Allows user to login with their credentials <br />
    * Other information can be check directly in the code as comments <br />
     */
    public cLoginWindow(){
        final File file = new File("login");
        if(file.exists()){ //if login information already exists, dipose this frame and load search window aka eMainWindow
            dispose();
            new eMainWindow();
        }
        else{
       
        JLabel usernameLabel = new JLabel("Username:",JLabel.CENTER);
        
        final JTextField username = new JTextField("",20);
        username.setHorizontalAlignment(JTextField.CENTER);
        username.setFont(new Font("Dialog", 1,30));
        
        JLabel passwordLabel = new JLabel("Password:",JLabel.CENTER);
        
        final JPasswordField password = new JPasswordField("",20);
        password.setHorizontalAlignment(JPasswordField.CENTER);
        password.setFont(new Font("Dialog", 1,30));
        
        JButton loginButton = new JButton("Login");
        // login button action start
        loginButton.addActionListener(new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent evt) {
                /* Validations*/
                boolean noErrors = true; // Start with No Errors as true value
                final String name = username.getText(); // get the username entered
                final String pass = new String(password.getPassword()); // get password entered as string , since they are in bytes
                String error = "";
                if("".equals(name) || !(name.matches("^[A-Za-z0-9._]{5,14}$"))){ // username must be 5 to 14 characters and can only include A-Z a-z 0-9 and _
                    noErrors = false; // No Errors is flase
                    error += "Sorry username or password is invalid\n"; //appends to error string
                }
                if(noErrors){ // if no error is true
                        Map<String,String> udata =  new HashMap<String, String>() {{
                            put("username", name); // this sends username=<USERINPUT> in HttpURLConnection 
                            put("password", pass); // this sends password=<USERINPUT> in HttpURLConnection 
                        }};// Map of data to send with URL , created using "Double Brace Initialization"
                        
                        bFetchURL url = new bFetchURL("http://binaydevkota.com/javabus/login.php",udata,"GET");
                        // Initialize new instance of bFetchURL, for more see bFetchURL.java
                        
                        String returnedText=null;
                        try {
                            returnedText = url.content(); // get the content returned by bFetchURL.content() method
                        } catch (NullPointerException ex) { // read more about exception thrown by bFetchURL.content() on bFetchURL.java file
                             JOptionPane.showMessageDialog(null, "Cannot find login server", "Fatal error", JOptionPane.ERROR_MESSAGE);
                             System.exit(-1);
                             
                        } catch (IOException ex) {
                             JOptionPane.showMessageDialog(null, "Cannot find login server", "Fatal error", JOptionPane.ERROR_MESSAGE);
                             System.exit(-1);
                        }
                        
                        // parse returned content as JSON using msjon library(msjon.jar), more info about msjon.jar is on main.java page
                        Json returnedJson = Json.read(returnedText);
                        System.out.print(returnedText);
                        if(!returnedJson.at("error").isNull()){ //check if server has sent any error or not, if there is display the error
                            JOptionPane.showMessageDialog(null, returnedJson.at("error").asString(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        if(!returnedJson.at("info").isNull()){//check if server has sent any info or not, if there is display the info
                            JOptionPane.showMessageDialog(null, returnedJson.at("info").asString(), "Info", JOptionPane.INFORMATION_MESSAGE);
                        }
                        if(!returnedJson.at("success").isNull()){ //if response has success message then
                            Writer output = null;
                            try {
                                 if(!file.exists()){ // create a new login file
                                     file.createNewFile();
                                 }
                                output = new BufferedWriter(new FileWriter(file,false));
                                output.write(returnedJson.at("user").toString()); //put the JSON response of user information into that file
                                output.close();
                            } catch (IOException ex) {
                                Logger.getLogger(cLoginWindow.class.getName()).log(Level.SEVERE,"Cannot create/write to the file", ex);
                            }
                            dispose(); //close this window since login is success
                            new eMainWindow(); // open search window
                        }
                }
                else{ //if noError is false then show all the errors in error string
                    JOptionPane.showMessageDialog(null, error, "Error",JOptionPane.ERROR_MESSAGE); 
                }
            }
        });
        // login button action ends
        JButton register = new JButton("Register");
        register.setMargin(new Insets(0, 0, 0, 0));
        register.addActionListener(new ActionListener() { //register button closes this window and calls dRegisterWindow
            @Override
              public void actionPerformed(ActionEvent evt) {
                dispose();
                new dRegisterWindow();
              }
        });
        
        //Frames and Panels
        
        setTitle("Reittiopas: Please login"); //frame title
        setIconImage(Toolkit.getDefaultToolkit().getImage( getClass().getResource("icon.png"))); // frame icon
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(0,1));
        JPanel buttonC = new JPanel();
        buttonC.setLayout(new GridLayout(2,3));

        loginPanel.add(usernameLabel);
        loginPanel.add(username);
        loginPanel.add(passwordLabel);
        loginPanel.add(password);
        
        buttonC.add(new JLabel(""));
        buttonC.add(loginButton);
        buttonC.add(new JLabel(""));
                                
        buttonC.add(new JLabel(""));
        buttonC.add(register);
        buttonC.add(new JLabel(""));
        loginPanel.add(buttonC);
        add(loginPanel);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width-getSize().width)/2, (dim.height-getSize().height)/2);
        setResizable(false);
        setVisible(true);
        }
    }
}

