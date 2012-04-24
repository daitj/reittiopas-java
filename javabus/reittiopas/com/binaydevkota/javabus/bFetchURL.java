package com.binaydevkota.javabus;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that accepts String(url to the server), Map&lt;String,String&gt; (request data's name, request data's value), String(HTTP request method GET or POST)<br />
 * <br />
 * When new instance is called, it will check if URL is valid or not and also changes the Map into HTTP request vaild string<br />
 * <br />
 * When bFetchURL.content() is called then the actual request to the server is done and will return plain text(response from server) data as string<br />
 * <br />
 * @author Binay Devkota and Ishan Regmi
 */
public class bFetchURL {
    private URL url = null;
    private String allData;
    private String request = "GET";
    
    /**
     * Initializes the instance and checks if URL is vaild, confirms the HTTP request method is valid and also encodes the request data's values
     * @param URLstr : URL in form of string
     * @param requestData : request data's name and value in form of Map &lt;String,String&gt;
     * @param requestMethod : request HTTP method either POST or GET
     */
    public bFetchURL(String URLstr, Map<String,String> requestData, String requestMethod) {
        allData = "";
        for(Entry<String, String> entry : requestData.entrySet()) {
            // parse each key=value from Map for our usage
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                allData += key+"=" +URLEncoder.encode(value, "UTF-8")+"&";
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(bFetchURL.class.getName()).log(Level.SEVERE, "Data UTF-8 encode failed", ex);
            }
        }

        try {
            if(requestMethod=="POST"){
                url = new URL(URLstr);
                request = "POST";
            }
            else{
                url = new URL(URLstr+"?"+allData);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(bFetchURL.class.getName()).log(Level.SEVERE, "Invalid URL", ex);
        }
    }
    
    /**
     * @return opens a new URLConnection , fetches the data from the URL and returns it as String
     * @throws NullPointerException in cases like HTTP server replied 404 Not Found Error
     * @throws IOException in cases like Internet connection not available
     */
    public String content() throws NullPointerException, IOException{
        URLConnection urlConnection = null;
        DataOutputStream outStream = null;
        try {
            urlConnection = url.openConnection();
        } catch (IOException ex) {
            Logger.getLogger(bFetchURL.class.getName()).log(Level.SEVERE, "Cannot establish the connection with URL", ex);
        }
        if(request == "POST"){
            
            // this part is required for POST request , the important part in the POST request is DataOutputSteam
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Content-Length", ""+ allData.length());
            try {
                // Create I/O streams
                outStream = new DataOutputStream(urlConnection.getOutputStream());
                // Send request
                outStream.writeBytes(allData);
                outStream.flush();
                outStream.close();
            } catch (IOException ex) {
                Logger.getLogger(bFetchURL.class.getName()).log(Level.SEVERE, "Couldn't make the request", ex);
            }
        }
        BufferedReader in = null;
        // start reading the response using getInputStream and encode it to UTF-8 to solve the issue with special characters
        try {
            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
        } 
        catch (FileNotFoundException ex){
            Logger.getLogger(bFetchURL.class.getName()).log(Level.SEVERE, "404 file not found error from server", ex);
        }catch (IOException ex) {
            Logger.getLogger(bFetchURL.class.getName()).log(Level.SEVERE, "Cannot read contents from URL", ex);
        }

        // read the inputstream one line at a time and save it as a whole text
        String text = "";
        String line = null;
        try {
            while ((line = in.readLine()) != null)
            {
               text += new String(line);
            }
            in.close(); 
        } catch (IOException ex) {
            Logger.getLogger(bFetchURL.class.getName()).log(Level.SEVERE, "Could not parse the lines from URL content", ex);
        }
        return text;
    }
}
