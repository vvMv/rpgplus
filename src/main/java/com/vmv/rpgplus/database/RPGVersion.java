package com.vmv.rpgplus.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmv.rpgplus.main.RPGPlus;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RPGVersion {

    private static HttpURLConnection con;
    public String user = "%%__USER__%%";
    public static RPGVersion instance;

    public RPGVersion(RPGPlus plugin) {
        instance = this;
    }

    public boolean checkVersion(String version) {

        String url = "http://rpgplus.survilla.xyz/index.php";
        String urlParameters = "id=" + user;
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> map;
        
        try {
            URL myurl = new URL(url);
            con = (HttpURLConnection) myurl.openConnection();

            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {

                wr.write(postData);
            }

            StringBuilder content;

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }

            System.out.println(content.toString());

            map = mapper.readValue(content.toString(), Map.class);
            return Boolean.valueOf((Boolean) map.get("a"));

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {

            con.disconnect();
        }
        return true; //TODO change
    }

    public static RPGVersion getInstance() {
        return instance;
    }
}
