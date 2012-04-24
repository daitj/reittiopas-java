package com.binaydevkota.javabus;

/**
 * This application is dependent on the <a target="_blank" href="reittiopas.turku.fi">reittiopas.turku.fi</a><br />
 * This Location data, bus routes in this application are fetched from <a target="_blank" href="reittiopas.turku.fi">reittiopas.turku.fi</a>.<br />
 * Java2sAutoComboBox and Java2sAutoTextField classes<br />
 * Used for autocomplete combo box in main window for Place names<br />
 * <a target="_blank" href="http://www.java2s.com/Code/Java/Swing-JFC/AutocompleteTextField.htm">http://www.java2s.com/Code/Java/Swing-JFC/AutocompleteTextField.htm</a> <br />
 * <a target="_blank" href="http://www.java2s.com/Code/Java/Swing-Components/AutocompleteComboBox.htm">http://www.java2s.com/Code/Java/Swing-Components/AutocompleteComboBox.htm</a> <br />
 * <br />
 * mjson.jar library to parse JSON data <br />
 * Documentation: <a target="_blank" href="http://www.sharegov.org/mjson/doc">http://www.sharegov.org/mjson/doc</a><br />
 * Download: <a target="_blank" href="http://www.sharegov.org/mjson/mjson.jar">http://www.sharegov.org/mjson/mjson.jar</a><br />
 * Used in most of the place to connect with server and save login information<br />
 * <br /> 
 * Rest are done by authors(Binay Devkota and Ishan Regmi)<br />
 * <br />
 * All the links under http://binaydevkota.com/javabus/ serves as user management server<br />
 * &nbsp;&nbsp;login.php : For user login<br />
 * &nbsp;&nbsp;register.php : For user registration<br />
 * &nbsp;&nbsp;changepassword.php : For changing password<br />
 * &nbsp;&nbsp;display.xml : An example of how a returned(from reittiopas search) XML data about route looks like<br />
 * <br />
 * All of them return JSON encoded data for every request except the XML file<br />
 * In every response returned there is "info" and "error" json objects included even if it is empty<br />
 * And "success" object is only available when the requested action is successfully completed<br />
 * <br />
 * Extra Files:<br />
 * LocationData =  List of all places in Turku with there co-ordinates<br />
 * icon.png = Icon of the application<br />
 * <br />
 * @author Binay Devkota and Ishan Regmi
 */

public class aMain{
    /**
     * 
     * @param agrs : Default arguments
     */
    public static void main(String[] agrs){
        new cLoginWindow();
    }
}
