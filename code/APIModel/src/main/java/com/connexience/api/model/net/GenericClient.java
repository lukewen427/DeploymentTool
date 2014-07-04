/**
 * e-Science Central
 * Copyright (C) 2008-2013 School of Computing Science, Newcastle University
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation at:
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, 5th Floor, Boston, MA 02110-1301, USA.
 */
package com.connexience.api.model.net;

import com.connexience.api.misc.IProgressInfo;
import com.connexience.api.model.json.Base64;
import com.connexience.api.model.json.JSONArray;
import com.connexience.api.model.json.JSONObject;
import com.connexience.api.model.json.JSONTokener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

/**
 * This class implements the basic functionality of a client that talks to the 
 * REST services.
 * @author hugo
 */
public class GenericClient {
    private String hostname;
    private int port;
    private String urlBase;
    private String username;
    private String password;
    private boolean secure = false;

    public GenericClient() {
    }

    public GenericClient(String hostname, int port, boolean secure, String urlBase, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.urlBase = urlBase;
        this.username = username;
        this.password = password;
        this.secure = secure;
    }
    
    public GenericClient(String urlBase, File propertiesFile) throws Exception {
        try {
            Properties props = new Properties();
            props.load(new FileReader(propertiesFile));
            hostname = props.getProperty("hostname", "localhost");
            port = Integer.parseInt(props.getProperty("port", "8080"));
            username = props.getProperty("username", "");
            password = props.getProperty("password", "");
            if("true".equals(props.getProperty("secure", "false"))){
                secure = true;
            } else {
                secure = false;
            }
            this.urlBase = urlBase;
        } catch (Exception e){
            e.printStackTrace();
            throw new Exception("Error loading API properties file: " + e.getMessage(), e);
        }        
    }
    
    public GenericClient(String urlBase) throws Exception {
        try {
            File propertiesFile = new File(System.getProperty("user.home") + File.separator + ".inkspot/api.properties");
            Properties props = new Properties();
            props.load(new FileReader(propertiesFile));
            hostname = props.getProperty("hostname", "localhost");
            port = Integer.parseInt(props.getProperty("port", "8080"));
            username = props.getProperty("username", "");
            password = props.getProperty("password", "");
            if("true".equals(props.getProperty("secure", "false"))){
                secure = true;
            } else {
                secure = false;
            }
            this.urlBase = urlBase;
        } catch (Exception e){
            throw new Exception("Error loading API properties file: " + e.getMessage(), e);
        }
    }

    public void configureClient(GenericClient newClient){
        newClient.secure = secure;
        newClient.hostname = hostname;
        newClient.port = port;
        newClient.username = username;
        newClient.password = password;
        newClient.secure = secure;
    }

    protected JSONObject retrieveJson(String url) throws Exception {
        URLConnection connection = createConnection(url);
        
        return _retrieveJSON(connection);
    }

    protected String retrieveString(String url) throws Exception {
        URLConnection connection = createConnection(url);

        return _retrieveString(connection);
    }
    
    protected JSONArray retrieveJsonArray(String url) throws Exception {
        URLConnection connection = createConnection(url);

        return _retrieveJSONArray(connection);
    }

    protected JSONArray postJsonArrayRetrieveJsonArray(String url, JSONArray postArray) throws Exception {
        URLConnection connection = createConnection(url);
        return _postJSONArrayRetrieveJSONArray(connection, postArray);
    }
    
    protected String postJsonArrayRetrieveText(String url, JSONArray postArray) throws Exception {
        URLConnection connection = createConnection(url);

        _postJSONArray(connection, postArray);
        return _retrieveString(connection);
    }

    protected JSONObject postJsonArrayRetrieveJson(String url, JSONArray postArray) throws Exception {
        URLConnection connection = createConnection(url);

        _postJSONArray(connection, postArray);
        return _retrieveJSON(connection);
    }

    protected JSONObject postJsonRetrieveJson(String url, JSONObject postJson) throws Exception {
        URLConnection connection = createConnection(url);
        
        _postJSON(connection, postJson);
        return _retrieveJSON(connection);
    }

    
    //protected JSONObject postTextRetrieveJson(String url, String data, Map<String, String> requestProperties) throws Exception {
    protected JSONObject postTextRetrieveJson(String url, String data) throws Exception {
        URLConnection connection = createConnection(url);

        //for (Map.Entry<String, String> entry : requestProperties.entrySet()) {
        //    connection.setRequestProperty(entry.getKey(), entry.getValue());
        //}
        _postString(connection, data);
        return _retrieveJSON(connection);
    }

    protected String postTextRetrieveText(String url, String data) throws Exception {
        URLConnection connection = createConnection(url);

        _postString(connection, data);
        return _retrieveString(connection);
    }
    
    protected String postJsonRetrieveText(String url, JSONObject postJson) throws Exception {
        URLConnection connection = createConnection(url);

        _postJSON(connection, postJson);
        return _retrieveString(connection);
    }

    protected int deleteResource(String url) throws Exception {
        URLConnection connection = createConnection(url);
        HttpURLConnection httpCon = (HttpURLConnection)connection;
        httpCon.setDoOutput(false);
        httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
        httpCon.setRequestMethod("DELETE");
        httpCon.connect();
        int response = httpCon.getResponseCode();
        return response;
    }

    
    protected URLConnection createConnection(String url) throws Exception {
        
        URI uri;
        if(secure){
            uri = new URI("https", hostname + ":" + port, urlBase + url, null, null);
        } else {
            uri = new URI("http", hostname + ":" + port, urlBase + url, null, null);
        }
     //   System.out.println(uri);
        URL urlDef = uri.toURL();
        URLConnection connection = urlDef.openConnection();   
        String authData = username + ":" + password;
        connection.addRequestProperty("Authorization", "Basic " + Base64.encodeBytes(authData.getBytes()));
        return connection;
    }


    protected void copyInputStream(InputStream in, OutputStream out) throws IOException {
        copyInputStream(in, out, null);
    }


    /** Copy the data from one stream to another */
    protected void copyInputStream(InputStream in, OutputStream out, IProgressInfo callback) throws IOException {
        byte[] buffer = new byte[16384];
        int len;
        long bytesSent = 0;

        if (callback != null) {
            while ((len = in.read(buffer)) >= 0) {
                out.write(buffer, 0, len);
                
                bytesSent += len;
                callback.reportProgress(bytesSent);
            }
            callback.reportEnd(bytesSent);
        } else {
            while ((len = in.read(buffer)) >= 0) {
                out.write(buffer, 0, len);
            }
        }
    }    
    
    /** Open an InputStream to a URL */
    protected InputStream openInputStream(String url) throws Exception {
        URLConnection connection = createConnection(url);
        InputStream inStream = null;
        try {
            inStream = connection.getInputStream();
            return inStream;
        } catch (Exception e){
            throw e;
        }       
    }
    
    /** Download the contents of a URL to an output stream */
    protected void downloadUrlToOutputStream(String url, OutputStream stream) throws Exception {
        URLConnection connection = createConnection(url);
        InputStream inStream = null;
        try {
            inStream = connection.getInputStream();
            copyInputStream(inStream, stream);
        } catch (Exception e){
            throw e;
        } finally {
            if(stream!=null){
                try {
                    stream.flush();
                } catch (Exception e){}
            }
            
            if(inStream!=null){
                inStream.close();
            }
        }                
    }
    
    /** Download the contents of a URL to a local file */
    protected void downloadUrlToFile(String url, File localFile) throws Exception {
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(localFile);
            downloadUrlToOutputStream(url, outStream);
        } catch (Exception e){
            throw e;
        } finally {
            if(outStream!=null){
                try {
                    outStream.flush();
                    outStream.close();
                } catch (Exception e){}
            }
        }
    }

    private void _postString(URLConnection connection, String data) throws Exception
    {
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "text/plain");
        try (OutputStream outStream = connection.getOutputStream()) {
            outStream.write(data.getBytes());
        }
    }

    private void _postJSON(URLConnection connection, JSONObject jsonObj) throws Exception
    {
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
            jsonObj.write(writer);
        }
    }

    private JSONArray _postJSONArrayRetrieveJSONArray(URLConnection connection, JSONArray array) throws Exception {
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        
        try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
            array.write(writer);
        }
        
        try (InputStream stream = connection.getInputStream()) {
            return new JSONArray(new JSONTokener(stream));
         }        
    }
    
    private void _postJSONArray(URLConnection connection, JSONArray array) throws Exception
    {
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
            array.write(writer);
        }
    }
    
    private String _retrieveString(URLConnection connection) throws Exception
    {
        try (InputStream stream = connection.getInputStream()){
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            copyInputStream(stream, buffer);
            buffer.flush();
            return new String(buffer.toByteArray());
         }
    }

    private JSONObject _retrieveJSON(URLConnection connection) throws Exception
    {
        try (InputStream stream = connection.getInputStream()) {
            return new JSONObject(new JSONTokener(stream));
        }
    }

    private JSONArray _retrieveJSONArray(URLConnection connection) throws Exception
    {
        try (InputStream stream = connection.getInputStream()) {
            return new JSONArray(new JSONTokener(stream));
         }
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUrlBase() {
        return urlBase;
    }

    public void setUrlBase(String urlBase) {
        this.urlBase = urlBase;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }
}
