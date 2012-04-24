package com.binaydevkota.javabus;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import mjson.*;

 /**
 * Contains only one main method which initializes the JFrame <br />
 * Read more about in the description of that method <br />
 * <br />
 * @author Binay Devkota and Ishan Regmi
 */
public class dRegisterWindow extends JFrame{
    /**
     * Main method of the class, initializes JFrame and other panels
     * Show all the fields that user needs to input and allow user to register themselves into the system.
     * Rest information can be seen in the code itself in comments.
     */
    public dRegisterWindow(){ //main method for the this class
        
        //init the fields
        final JTextField username = new JTextField("",15);
        final JPasswordField password = new JPasswordField("",15);
        final JPasswordField repassword = new JPasswordField("",15);
        final JTextField email = new JTextField("",15);
        final JTextField pin = new JTextField("",4);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() { //register button action
            @Override
            public void actionPerformed(ActionEvent evt) {
                /* Validations*/
                boolean noErrors = true;
                
                // get value from fields
                final String name = username.getText();
                final String pass = new String(password.getPassword());
                String repass = new String(repassword.getPassword());
                final String p = pin.getText();
                final String em = email.getText();
                String error = "";
                
                //validate and check for errors
                if("".equals(name) || !(name.matches("^[A-Za-z0-9._]{5,14}$"))){ //regex match
                    noErrors = false;
                    error += "Username is not valid, must be 5 characters long (A-Z, a-z, 0-9, _)\n";
                }
                if(!(pass.equals(repass)) || pass.length() == 0){
                    noErrors = false;
                    error += "Both the password must match with each other \n";
                }
                if(p.length() != 4){
                    noErrors = false;
                    error += "Pin must be 4 chars \n";
                }
                if("".equals(em) || !(em.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"))){ //regex match for email
                    noErrors = false;
                    error += "Email address is invalid \n";
                }
                if(noErrors){ // send to the server to register the user
                    Map<String,String> rdata = new HashMap<String, String>(){{put("username",name);put("password",pass);put("email",em);put("pin",p);}};
                    bFetchURL url = new bFetchURL("http://binaydevkota.com/javabus/register.php",rdata,"GET");
                    String returnedText = null;
                    try {
                        returnedText = url.content();
                    } catch (NullPointerException ex) {
                        JOptionPane.showMessageDialog(null, "Cannot find register server", "Fatal error", JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Cannot find register server", "Fatal error", JOptionPane.ERROR_MESSAGE);
                    }
                    Json returnedJson = Json.read(returnedText);
                    if(!returnedJson.at("error").isNull()){
                        JOptionPane.showMessageDialog(null, returnedJson.at("error").asString(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    if(!returnedJson.at("info").isNull()){
                        JOptionPane.showMessageDialog(null, returnedJson.at("info").asString(), "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                        
                    if(!returnedJson.at("success").isNull()){
                        // if sucess close this window and load cLoginWindow
                        dispose();
                        new cLoginWindow();
                    }
                }
                else{
                    JOptionPane.showMessageDialog(null, error, "Error",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        
        //add fields to panel
        JPanel register = new JPanel();

        register.setLayout(new GridLayout(0,2));
        
        register.add(new JLabel("Username: "));
        register.add(username);
        
        register.add(new JLabel(""));
        register.add(new JLabel(""));
        
        register.add(new JLabel("Password: "));
        register.add(password);
        
        register.add(new JLabel(""));
        register.add(new JLabel(""));
        
        register.add(new JLabel("Password again: "));
        register.add(repassword);
        
        register.add(new JLabel(""));
        register.add(new JLabel(""));
        
        register.add(new JLabel("Email: "));
        register.add(email);
        
        register.add(new JLabel(""));
        register.add(new JLabel(""));
        
        register.add(new JLabel("Pin (4 chars numeric pin for mobile access): "));
        register.add(pin);
        
        register.add(new JLabel(""));
        register.add(new JLabel(""));
        
        register.add(registerButton);
        register.add(new JLabel(""));
        
        
        
        
        setTitle("Reittiopas: Register");
        setIconImage(Toolkit.getDefaultToolkit().getImage( getClass().getResource("icon.png")));
        setSize(550, 300);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //stop the default action of close operation and let the WindowListener handle it, which is at the end
        add(register);
        setVisible(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width-getSize().width)/2, (dim.height-getSize().height)/2);
        setResizable(false);
        
        final JFrame rW = this;
        addWindowListener(new WindowAdapter() { // when dRegisterWindow is closed, open cLoginWindow, overriding the windowClosing event
            @Override
            public void windowClosing(WindowEvent evt) {
                rW.dispose();
                new cLoginWindow();
            }
        });
    }
}

       