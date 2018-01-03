import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FCMconnect {
	private static HttpURLConnection conn;

    public final static String AUTH_KEY_FCM = "AAAA5TF_qWE:APA91bEr_aC-qAlqBER-JMGHWhSiUxo1_nOCrhmozB4LzqV5E-wVP5kIeb4f6zDOUpNcKMM9uyt241CL2q6nPU_xcA9fh_Y2XQFf3yrDBfrwpll9FavHngxYVbc_O5pb54_7vkOnbwMu";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    
	public static HttpURLConnection connect() throws Exception{
		 String authKey = AUTH_KEY_FCM; // You FCM AUTH key
	        String FMCurl = API_URL_FCM;

	        URL url;
			try {
				url = new URL(FMCurl);
			    conn = (HttpURLConnection) url.openConnection();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    
		return conn;
	}

}
