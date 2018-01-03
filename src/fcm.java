import java.io.IOException;
import java.util.ArrayList;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
import com.google.android.gcm.server.MulticastResult;

public class fcm {

	
	public void ff() throws IOException{
	ArrayList<String> token = new ArrayList<String>(); // token값을 ArrayList에 저장
    String MESSAGE_ID = String.valueOf(Math.random() % 100 + 1); // 메시지 고유 ID
    boolean SHOW_ON_IDLE = true; // 옙 활성화 상태일때 보여줄것인지
    int LIVE_TIME = 1; // 옙 비활성화 상태일때 FCM가 메시지를 유효화하는 시간
    int RETRY = 2; // 메시지 전송실패시 재시도 횟수
//push 보내기
    
    String simpleApiKey ="AAAA5TF_qWE:APA91bEr_aC-qAlqBER-JMGHWhSiUxo1_nOCrhmozB4LzqV5E-wVP5kIeb4f6zDOUpNcKMM9uyt241CL2q6nPU_xcA9fh_Y2XQFf3yrDBfrwpll9FavHngxYVbc_O5pb54_7vkOnbwMu";
    String gcmURL = "https://android.googleapis.com/fcm/send";
    String msg = "asdfasfd";
    String token1 = "c9C68MMOuuc:APA91bF7AKY2DPnG2eeGoNgCsF8QeCYHR1wMt5GYNDjz_2MN3TxYhGmqv8v5ZDpxWHw2mbSZ46DDmsFLtKoQumz3THG8weP9xTJudq-6ifX1P4ENPpUsMgzOGW-5OcdGHEc3lK-OjdLk";
    token.add(token1);
    Sender sender = new Sender(simpleApiKey);
    Message message = new Message.Builder().collapseKey(MESSAGE_ID).delayWhileIdle(SHOW_ON_IDLE)
          .timeToLive(LIVE_TIME).addData("msg", msg).build();
    
    
	MulticastResult result = sender.send(message, token, RETRY);
	
	
	
	}
}
