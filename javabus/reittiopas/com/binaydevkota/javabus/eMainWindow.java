package com.binaydevkota.javabus;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mjson.Json;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.w3c.dom.*;


/**
 * Main Search Window aka Main Window<br />
 * Layout Tree(Frame):<br />
 * &nbsp;&nbsp;&nbsp;&nbsp;Tabbed Panel (Search)<br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Panel Left<br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Panel Right (with Scroll panel) aka JPanel pR<br />
 *              <br />
 * &nbsp;&nbsp;&nbsp;&nbsp;Tabbed Panel (Settings)<br />
 *          <br />
 * &nbsp;&nbsp;&nbsp;&nbsp;Tabbed Panel (Logout)<br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Empty panel , when clicked it will logout the current user, more description in the main method<br />
 * <br />
 * @author Binay Devkota and Ishan Regmi
 */

public class eMainWindow extends JFrame{
    
    /**
     * List of places for autocomplete fields
     */
    public final List AutoComplete;
    /**
     * List(Map) of places got from LocationData file using mjson library
     */
    public Map<String, Json> Datas;
    /**
     * ComboBox for selecting month, declared outside the main method because it has been used in other methods too.
     */
    public JComboBox m; //Month
    /**
     * ComboBox for selecting date, declared outside the main method because it has been used in other methods too.
     */
    public JComboBox d; //Date
    /**
     * ComboBox for selecting hours, declared outside the main method because it has been used in other methods too.
     */
    public JComboBox hh; //Hours
    /**
     * ComboBox for selecting minute, declared outside the main method because it has been used in other methods too.
     */
    public JComboBox mm; //Minute
    /**
     * Radio button to select Departing at, declared outside the main method because it has been used in other methods too.
     */
    public JRadioButton departing;
    /**
     * Radio button to select Reaching at, declared outside the main method because it has been used in other methods too.
     */
    public JRadioButton reaching;
    /**
     * Label for the month ,since Calender object returns month in integer form, from 0 to 11, declared outside the main method because it has been used in other methods too.
     */
    public final String[] monthLabel = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    /**
     * Token for searching via reittiopas site, is fetched from main method, it is declared outside the main method because it has been used in other methods too.
     */
    public String token;
    /**
     * Right panel for showing the search results, it is declared outside the main method because it has been used in other methods too.
     */
    public JPanel pR = new JPanel();
    /**
     * Right panel for showing the search results, it is declared outside the main method because it is enclosing the Right Panel (pR) which is also defined outside
     */
    public JScrollPane scroll = new JScrollPane(pR,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // Scrollable Right Panel aka pR
    
    /**
     * Initialized the JFrame and Tabbed Panel on it<br />
     * Fetches the search token from reittiopas site<br />
     */
    public eMainWindow() {
        Json AllData  = null;
        String strLine = "";
        BufferedReader br = null;
        try { // read all name of the places from file LocationData saved in JSON format
            DataInputStream in = new DataInputStream(getClass().getResource("LocationData").openStream());
            br = new BufferedReader(new InputStreamReader(in));
            while ((strLine = br.readLine())!= null)   {
                break;
            }
            AllData = Json.read(strLine); //convert text to JSON object
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(cLoginWindow.class.getName()).log(Level.SEVERE,"Cannot read the file with data", ex);
        }
        
        // Reittiopas uses token to verify the requests which comes in the following link(http://reittiopas.turku.fi/fi/config.js_b8d9ad995abbbe6251c6c1c2b1017cd0e71e286b.php), which is normal javascript file
        String tokenText=null;
        
        //request for the token
        bFetchURL requestForToken = new bFetchURL("http://reittiopas.turku.fi/fi/config.js_b8d9ad995abbbe6251c6c1c2b1017cd0e71e286b.php", new HashMap<String, String>(){{put("","");}}, "GET");
        try {
            tokenText = requestForToken.content();
        } 
        catch (NullPointerException ex){
            JOptionPane.showMessageDialog(null, "Sorry couldn't read the URL for search token, exiting the application", "Fatal error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Sorry couldn't read the URL for token, exiting the application", "Fatal error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        try{ //parse the token, if we got the response.
            token = Json.read(tokenText.substring(11, tokenText.length())).at("token").asString();
        }
        catch(NullPointerException ex){ //if token couldn't be parsed application cannot initialize i.e. there is no use of application
            JOptionPane.showMessageDialog(null, "Sorry couldn't parse the search token, exiting the application");
            System.exit(-1);
        }
        Datas = AllData.asJsonMap(); //convert all the location's object into Map<String, Json>
        List a = new ArrayList();
        a.add("");
        for(Entry<String, Json> entry : Datas.entrySet()) { //break each item from Map and make it to a List
            String key = entry.getKey();
            a.add(key);
        }
        Collections.sort(a); //sort the new list
        AutoComplete = a; //save the sorted list to AutoComplete
        
        JTabbedPane mainPanel = new JTabbedPane();
        mainPanel.addTab("Search",  searchPanel()); //tabbed panel for search loaded from searchPanel() function
        //mainPanel.addTab("My Location", mylocationPanel());
        mainPanel.addTab("Settings", settingsPanel());//tabbed panel for settings loaded from settingsPanel() function
        mainPanel.addTab("Log out", new JPanel());//tabbed panel for logout , but is empty
        mainPanel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                JTabbedPane pane = (JTabbedPane)evt.getSource();
                if(pane.getSelectedIndex() == 2){ //if the item clicked it Logout tab which is indexed 2
                    JOptionPane.showMessageDialog(null,"Logged out");
                    File file = new File("login"); 
                    file.delete(); //delete the file with login info
                    dispose(); //close the window
                }
            }
        });
        
        //init frame and add the fields
        setTitle("Reittiopas: Search Location");
        setIconImage(Toolkit.getDefaultToolkit().getImage( getClass().getResource("icon.png")));
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(mainPanel);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width-getSize().width)/2, (dim.height-getSize().height)/2);
        setResizable(true);
        setVisible(true);
    }
    
    /**
     * Tabbed panel which contains all fields like To, From , Departing or Reaching, Month, Time<br />
     * @return JPanel with all fields for searching a route
     */
    public final JPanel searchPanel(){ // Tabbed Search Panel
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(0,2));
        JPanel pL = new JPanel();
        pL.setLayout(new GridLayout(0,1));
        pR.setLayout(new GridLayout(0,1));
        
        final Java2sAutoComboBox from = new Java2sAutoComboBox(AutoComplete); //attach the location list to AutoComboBox
        from.setFont(new Font("Dialog", 1,20));
        pL.add(new JLabel("From:"));
        pL.add(from);
        
        final Java2sAutoComboBox to = new Java2sAutoComboBox(AutoComplete);  //attach the location list to AutoComboBox
        to.setFont(new Font("Dialog", 1,20));
        pL.add(new JLabel("To:"));
        pL.add(to);

        final JRadioButton departing = new JRadioButton("Departing");
        departing.setActionCommand("Departing");
        departing.setSelected(true);
        
        JRadioButton arriving = new JRadioButton("Reaching");
        arriving.setActionCommand("Reaching");
        
        ButtonGroup rgroup = new ButtonGroup(); //group the arriving and departing radio button
        rgroup.add(departing);
        rgroup.add(arriving);
        
        JPanel rpanel = new JPanel();
        rpanel.setLayout(new GridLayout(0,2));
        rpanel.add(departing);
        rpanel.add(arriving);
        
        pL.add(rpanel);
        
        pL.add(new JLabel("Date:"));
        pL.add(datePicker()); //load datepicker
        
        pL.add(new JLabel("Time:"));
        pL.add(timePicker()); //load timepicker
        
        pL.add(new JLabel());
        
        JButton search = new JButton("Search");
        search.addActionListener(new ActionListener() {
            @Override
              public void actionPerformed(ActionEvent evt) {
                //get values
                String getFrom = (String)from.getSelectedItem();
                String getTo = (String)to.getSelectedItem();
                String getMonth = (String)m.getSelectedItem();
                String getDate = (String)d.getSelectedItem();
                String getHH = (String)hh.getSelectedItem();
                String getMM = (String)mm.getSelectedItem();
                String routeType;
                
                if(departing.isSelected()){
                    routeType = "forward";
                }
                else{
                    routeType = "backward";
                }
                
                int month = 0;
                
                String sendFromX=null,sendFromY=null,sendToX=null,sendToY=null;
                for(int i=0;i<monthLabel.length;i++){
                    if(getMonth==monthLabel[i]){
                        month = i+1;
                        break;
                    }
                }
                for(Entry<String, Json> entry : Datas.entrySet()) { //get which place is selected in combobox and then get x,y point from locationdata
                    String key = entry.getKey();
                    Json value = entry.getValue();
                    if(key==getFrom){
                        sendFromX  = value.at("x").asString();
                        sendFromY = value.at("y").asString();
                        break;
                    }
                }
                for(Entry<String, Json> entry : Datas.entrySet()) {
                    String key = entry.getKey();
                    Json value = entry.getValue();
                    if(key==getTo){
                        sendToX = value.at("x").asString();
                        sendToY = value.at("y").asString();
                        break;
                    }
                }
                DateFormat formatter = new SimpleDateFormat("yyyy:M:d:H:m"); // set a date format, which we suppose to get from user input
                Date date = null;
                try {
                    date = (Date)formatter.parse("2012:"+month+":"+getDate+":"+getHH+":"+getMM); //make a string similar to date format from user input and parse it as date object
                } catch (ParseException ex) {
                    Logger.getLogger(eMainWindow.class.getName()).log(Level.SEVERE, "Date couldn't be parsed", ex);
                }
                Long timestamp = new Long(date.getTime()/1000); //convert parsed date to UNIX EPOCH supported timestamp
                SendRequest(sendFromX,sendFromY,sendToX,sendToY,routeType,timestamp); // send the request to reittiopas
              }
        });
        
        pL.add(search);
        p.add(pL);
        p.add(scroll);
        return p;
    }
    
    /*
    // placeholder for My Location for further development
    public final JPanel mylocationPanel(){
        JPanel p = new JPanel();
        
        return p;
    }*/
    
    
    /**
     * Makes one ComboBox for days in current month and next month , since reittiopas only supports two months of searching.<br />
     * Makes another ComboBox for this month and next month with the help of monthLabel<br />
     * Also detects today's date and make it selected by default<br />
     * @return JPanel with two ComboBox to select Date and Month
     */
    public final JPanel datePicker(){
        JPanel tdate = new JPanel();
        tdate.setLayout(new GridLayout(0,2));
        final Calendar rightNow = Calendar.getInstance();
        final int mo = rightNow.get(Calendar.MONTH);
        int nextmo;
        if(mo==11){ nextmo = 0;}
        else{ nextmo = mo+1;}
        final int da = rightNow.get(Calendar.DAY_OF_MONTH);
        int max = rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);
        final String[] day1 = new String[(max-da)+1]; //number of days for first month
        int j = 0;
        for (int i = da; i <= max; i++) {
            day1[j] = ""+(da+j)+"";
            j++;
        }
        String[] month = {monthLabel[mo],monthLabel[nextmo]};
        m = new JComboBox(month);
        d = new JComboBox(day1);
        rightNow.set(rightNow.get(Calendar.YEAR), nextmo,1);
        max = rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);
        final String[] day2 = new String[max]; //number of days for second month
        m.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                String selectedMonth = (String)cb.getSelectedItem();
                d.removeAllItems();
                String j[];
                int k = 0;
                if(monthLabel[mo].equals(selectedMonth)){
                    for (int i = da; k < day1.length; i++) {
                        d.addItem(""+i+"");
                        k++;
                    }
                }
                else{
                    for (int i = 0; i < day2.length; i++) {
                        d.addItem(""+(i+1)+"");
                    }
                }

            }     
        });
        tdate.add(m);
        tdate.add(d);
        return tdate;
    }
    
    /**
     * Finds out current time make it selected by default<br />
     * @return JPanel with two ComboBox to select Hours and Minutes
     */
    public final JPanel timePicker(){
        JPanel ptime = new JPanel();
        ptime.setLayout(new GridLayout(0,2));
        String[] m =new String[60],h = new String[24]; 
        for (int i = 0; i < 60; i++) {
            m[i] = ""+i+"";
        }
        for (int i = 0; i < 24; i++) {
            h[i] = ""+i+"";
        }
        hh = new JComboBox(h);
        mm = new JComboBox(m);
        hh.setSelectedIndex(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        mm.setSelectedIndex(Calendar.getInstance().get(Calendar.MINUTE));
        ptime.add(hh);
        ptime.add(mm);
        return ptime;
    }
    
    /**
     * This function sends the request to reittiopas and deals with the returned response<br />
     * 
     * @param sendFromX : Location From X value, got from LocationData (defined inside searchPanel search button's action)
     * @param sendFromY : Location From Y value, got from LocationData (defined inside searchPanel search button's action)
     * @param sendToX : Location To X value, got from LocationData (defined inside searchPanel search button's action)
     * @param sendToY : Location To Y value, got from LocationData (defined inside searchPanel search button's action)
     * @param routeType : Route type either backward or forward (defined inside searchPanel search button's action, converted from Departing or Arriving)
     * @param timestamp : UNIX EPOCH timestamp for the route search (defined inside searchPanel search button's action)
     */
    public void SendRequest(final String sendFromX,final String sendFromY,final String sendToX,final String sendToY,final String routeType,final Long timestamp){
        try{
            if(!sendFromX.isEmpty() && !sendFromY.isEmpty() && !sendToX.isEmpty() && !sendToY.isEmpty()){
                Map<String,String> request = new HashMap<String,String>() {{
                    // make list of data according to the requirement of reittiopas
                    put("request[changeMargin]", "3"); 
                    put("request[end][x]", sendToX);
                    put("request[end][y]",sendToY);
                    put("request[excludedLines]","null");
                    put("request[includedLines]","null");
                    put("request[maxTotWalkDist]","no_restriction");
                    put("request[numberRoutes]","1");
                    put("request[routingMethod]","default");
                    put("request[start][x]",sendFromX);
                    put("request[start][y]",sendFromY);
                    put("request[timeDirection]",routeType);
                    put("request[timestamp]",timestamp.toString());
                    put("request[via]","null");
                    put("request[walkSpeed]","70");
                    put("token",token);
                }};
                // Map of data to send with URL , created using "Double Brace Initialization"
                
                bFetchURL urlContent = new bFetchURL("http://reittiopas.turku.fi/getroute.php",request,"POST");
                //fetch the url
                String html = null;
                try {
                    html = urlContent.content();
                    // get the reposonse content
                }         
                catch (NullPointerException ex){
                    JOptionPane.showMessageDialog(null, "Sorry cannot connect to server, searching route failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
                catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Sorry cannot connect to server, searching route failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
                Document DOMxml=null;
                try {
                    //since the reposonse data is XML we need to parse XML from normal html text
                    DOMxml = loadXMLFromString(html);
                } catch (Exception ex) {
                    Logger.getLogger(eMainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                //update the search panel with the XML response
                updateRightPanel(DOMxml);
            }
        }
        catch(NullPointerException ex){
            JOptionPane.showMessageDialog(null, "Some fields are missing","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * This function accepts normal xml string and converts it into org.w3c.dom.Document<br />
     * dom = Document Object Model<br />
     * @param xml : Plain XML text fetch from server, http://binaydevkota.com/javabus/display.xml is one example, which can be converted into org.w3c.dom.Document
     * @return org.w3c.dom.Document which can be easily navigated/parsed using XML DOM Methods
     * @throws Exception if raw xml sting is now convertable/parseable into dom
     */
    public Document loadXMLFromString(String xml) throws Exception{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document doc = db.parse(is);
        return doc;
    }
    
    /**
     * This function accepts the XML Document and after that it parses the data<br />
     * and show it to the user<br />
     * @param DOMxml : XML DOM Document (org.w3c.dom.Document) can be acquired from loadXMLFromString function
     */
    public void updateRightPanel(Document DOMxml){
        
        //clear everything in the panel
        pR.removeAll();
        
        //get the the first recommended route by the site(i.e. Route element)
        Element mainElem = (Element) DOMxml.getElementsByTagName("ROUTE").item(0);
        NodeList lines = mainElem.getElementsByTagName("*");
        if(lines.getLength()==0){
            pR.add(new JLabel("NO ROUTE FOUND AT THIS TIME"));
        }
        else{
            /*
             * Route has lot of tags and among which WALK and LINE has information about 
             * WALK(Walking) distance time and 
             *      WALK has two attribute dist(Walking distance) and time(Time for that distance)
             * LINE(Bus) route and information about it
             *      LINE has STOP element containing information about the stops that bus will pass by at what time
             */
            for (int i = 0; i < lines.getLength(); i++) {
                Element line_elem = (Element) lines.item(i);
                String tagName = line_elem.getTagName();
                if("WALK".equals(tagName) || "LINE".equals(tagName))
                {
                    Node route = line_elem.getElementsByTagName("LENGTH").item(0);
                    //Attr route_time = (Attr)route.getAttributes().item(1);
                    Attr route_length = (Attr)route.getAttributes().item(0);
                    pR.add(new JLabel(""));
                    if("WALK".equals(tagName)) {
                        pR.add(new JLabel("[WALK] "+route_length.getValue()+" mtrs"));
                    }
                    if("LINE".equals(tagName)) {
                        pR.add(new JLabel("[BUS] "+line_elem.getAttribute("code")+" ("+route_length.getValue()+" mtrs)"));
                    }
                    NodeList stops = line_elem.getElementsByTagName("STOP");
                    for (int j = 0; j < stops.getLength(); j++) {
                        Element stop_elem = (Element) stops.item(j);
                        Attr nameTag = (Attr)stop_elem.getElementsByTagName("NAME").item(0).getAttributes().item(1);
                        Attr arriveTimeTag = (Attr)stop_elem.getElementsByTagName("ARRIVAL").item(0).getAttributes().item(1);
                        String arriveTime = arriveTimeTag.getValue();
                        arriveTime = arriveTime.charAt(0)+""+arriveTime.charAt(1)+":"+arriveTime.charAt(2)+""+arriveTime.charAt(3);
                        pR.add(new JLabel(arriveTime+" "+nameTag.getValue()));
                    }
                }
            }
            pR.add(new JLabel(""));
        }
        
        // revalidate and repaint the panels, so that the values are visible without out changing the position of frame
        pR.repaint();
        pR.validate();
        scroll.repaint();
        scroll.validate();
    }
    
    /**
     * Settings Tabbed panel<br />
     * For now it only has option to change password<br />
     * @return JPanel with all the password fields for user to change their password
     */
    public final JPanel settingsPanel(){
        JPanel p = new JPanel();
        final JPasswordField oldpassword = new JPasswordField("",15);
        final JPasswordField password = new JPasswordField("",15);
        final JPasswordField repassword = new JPasswordField("",15);
        JButton registerButton = new JButton("Change");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                boolean noErrors = true;
                String error = "";
                final String oldpass = new String(oldpassword.getPassword());
                final String pass = new String(password.getPassword());
                final String repass = new String(repassword.getPassword());
                File file = new File("login");
                if(file.exists()){
                        Json localJson= null;
                        String md5pass = "";
                        try {
                            md5pass = md5(oldpass);
                        } catch (NoSuchAlgorithmException ex) {
                            Logger.getLogger(cLoginWindow.class.getName()).log(Level.SEVERE, "Couldn't get md5 hash of the password", ex);
                        }
                        try {
                            String strLine = "";
                            DataInputStream in = new DataInputStream(new FileInputStream(file));
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));
                            try {
                                while ((strLine = br.readLine())!= null)   {
                                    break;
                                }
                                in.close();
                                localJson = Json.read(strLine);
                            } catch (IOException ex) {
                                Logger.getLogger(cLoginWindow.class.getName()).log(Level.SEVERE,"Cannot read line in the file", ex);
                            }
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(cLoginWindow.class.getName()).log(Level.SEVERE,"File not found", ex);
                        }
                        final String id = localJson.at("id").asString();
                        if(!md5pass.equals(localJson.at("password").asString())){
                            noErrors = false;
                            error += "Old password dont match \n";
                        }
                        
                       if(!(pass.equals(repass)) || pass.length() == 0){
                            noErrors = false;
                            error += "Both the password must match with each other \n";
                        }
                        if(noErrors){
                            Map<String,String> rdata = new HashMap<String, String>(){{put("id",id);put("oldpass",oldpass);put("newpass",pass);}};
                            bFetchURL url = new bFetchURL("http://binaydevkota.com/javabus/changepassword.php",rdata,"GET");
                        String returnedText = null;
                            try {
                                returnedText = url.content();
                            } catch (NullPointerException ex){
                                JOptionPane.showMessageDialog(null, "Sorry cannot connect to server, change password failed", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            catch (IOException ex) {
                                JOptionPane.showMessageDialog(null, "Sorry cannot connect to server, change password failed", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            Json returnedJson = Json.read(returnedText);
                            if(!returnedJson.at("error").isNull()){
                                JOptionPane.showMessageDialog(null, returnedJson.at("error").asString(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            if(!returnedJson.at("info").isNull()){
                                JOptionPane.showMessageDialog(null, returnedJson.at("info").asString(), "Info", JOptionPane.INFORMATION_MESSAGE);
                            }

                            if(!returnedJson.at("success").isNull()){
                                File files = new File("login");
                                files.delete();
                                new cLoginWindow();
                                dispose();
                            }
                        }
                        else{
                            JOptionPane.showMessageDialog(null, error, "Error",JOptionPane.ERROR_MESSAGE);
                        }     
 
                }
                else{
                    JOptionPane.showMessageDialog(null, "You need to login to change password");
                }

            }
        });
        
        p.setLayout(new GridLayout(0,2));
        
        p.add(new JLabel("Old Password: "));
        oldpassword.setFont(new Font("Dialog", 1,20));
        p.add(oldpassword);
        
        p.add(new JLabel(""));
        p.add(new JLabel(""));
        
        p.add(new JLabel("New Password: "));
        password.setFont(new Font("Dialog", 1,20));
        p.add(password);
        
        p.add(new JLabel(""));
        p.add(new JLabel(""));
        
        p.add(new JLabel("Password again: "));
        repassword.setFont(new Font("Dialog", 1,20));
        p.add(repassword);
        
        p.add(new JLabel(""));
        p.add(new JLabel(""));
        
        p.add(registerButton);
        p.add(new JLabel(""));
        return p;
    }
    
    /**
     * Accepts string as input and returns PHP supported MD5 algorithm of it.<br />
     * @param input normal string
     * @return String containing MD5 hash of the input String
     * @throws NoSuchAlgorithmException if MD5 hashing is not supported by MessageDigest
     */
    public static String md5(String input) throws NoSuchAlgorithmException {
        String result = input;
        if(input != null) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            BigInteger hash = new BigInteger(1, md.digest());
            result = hash.toString(16);
            while(result.length() < 32) {
                result = "0" + result;
            }
        }
        return result;
    }
}
