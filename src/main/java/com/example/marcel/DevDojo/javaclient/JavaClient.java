package com.example.marcel.DevDojo.javaclient;

import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JavaClient {
    public static void main(String[] args) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String username = "meimarcel";
        String password = "123456";
        try {
            URL url = new URL("http://localhost:8080/v1/protected/students/1");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Basic " + basicEncode(username, password));
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder jsonB = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonB.append(line);
            }
            System.out.println(jsonB.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
            if (connection != null)
                connection.disconnect();
        }
    }

    private static String basicEncode(String username, String password) {
        String usernameAndPassword = username + ":" + password;
        return new String(Base64.encodeBase64(usernameAndPassword.getBytes()));
    }
}
