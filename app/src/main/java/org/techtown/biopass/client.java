package org.techtown.biopass;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class client extends AsyncTask<String, Void, String> {
    String msg;
    @Override
    protected String doInBackground(String... strings) {
        try{
            String str = null;

            URL url = new URL("http://183.105.72.7:8080/Pass/pass");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");

            if(strings[0].equals("newbi")){
                str = "action="+strings[0]+"&id="+strings[1]+"&name="+strings[2];
            }else if(strings[0].equals("responseDeviceID")){
                str = "action="+strings[0]+"&DeviceID="+strings[1]+"&name="+strings[2];
            }else if(strings[0].equals("scan")){
                str = "action="+strings[0]+"&encryted="+strings[1];
            }
            osw.write(str);
            osw.flush();

            if(conn.getResponseCode() == conn.HTTP_OK){
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream());
                BufferedReader  reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();
                 while((str = reader.readLine()) != null){
                     buffer.append(str);
                 }
                 msg = buffer.toString();
            }
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return msg;
    }

}
