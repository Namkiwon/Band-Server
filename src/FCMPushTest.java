import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class FCMPushTest {

    // Method to send Notifications from server to client end.

    public final static String AUTH_KEY_FCM = "AAAA5TF_qWE:APA91bEr_aC-qAlqBER-JMGHWhSiUxo1_nOCrhmozB4LzqV5E-wVP5kIeb4f6zDOUpNcKMM9uyt241CL2q6nPU_xcA9fh_Y2XQFf3yrDBfrwpll9FavHngxYVbc_O5pb54_7vkOnbwMu";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    
    
    
    // userDeviceIdKey is the device id you will query from your database
    public  void pushFCMNotification(String message ,String token)
            throws Exception {
        String authKey = AUTH_KEY_FCM; // You FCM AUTH key
        String FMCurl = API_URL_FCM;

        URL url = new URL(FMCurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + authKey);
        conn.setRequestProperty("Content-Type", "application/json");
        	
        JsonObject json = new JsonObject();
        JsonObject info = new JsonObject();
        JsonObject jsonobj = new JsonObject();
        JsonArray jsonarr = new JsonArray();
        Gson gson = new Gson();
      
        info.addProperty("body", message); // Notification body

        json.add("notification", info);
        json.addProperty("to", token); // deviceID
//        json.addProperty("to", userDeviceIdKey.trim()); // deviceID
         try(OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream())){
//혹시나 한글 깨짐이 발생하면 
//try(OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")){ 인코딩을 변경해준다. 	
       		 wr.write(json.toString());
        	 wr.flush();
            
        }catch(Exception e){
        }

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        System.out.println("Output from Server .... \n");
        
        while ((output = br.readLine()) != null) {
            System.out.println(output);
        }
        
        conn.disconnect();
//        System.out.println(output);
   	}
 }
