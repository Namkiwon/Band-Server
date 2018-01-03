import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainController  {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String type = null;
	public MainController(HttpServletRequest request, HttpServletResponse response){
		this.request = request;
		this.response = response;
		type = request.getParameter("Type");
	}

	public void Excute(){
		System.out.println(type);
		
		switch (type){ 	
			case "group" : GroupController GC = new GroupController(request,response); break;
			case "token" : TokenController TC = new TokenController(request,response); break;
			case "signUp" : SignUpController SC = new SignUpController(request,response); break;
			case "logIn" : LogInController LC = new LogInController(request,response); break;
			case "friendList" : FriendController FC = new FriendController(request,response); break;
			case "chatting" : ChattingController CC = new ChattingController(request,response); break;
		}
		
	}
}