import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TokenController {
	private static HttpServletRequest request;
	private HttpServletResponse response;
	private String token;
	private String userID;
	private String jsonstr = null;
	private SendQuery SQ;
	public MemberInfoModel memberinfo = null;
	private PrintWriter out ;
	private JsonArray jsonarr;
	private JsonObject jsonobj;
	
	
	public TokenController(HttpServletRequest request,HttpServletResponse response){
		this.request = request;
		this.response = response;
		token  = request.getParameter("token");
		System.out.println(token);
		checkToken();
		
	}
	
	public void checkToken(){
		try {
			out = response.getWriter();  
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		SQ = new SendQuery();
		jsonarr = SQ.serchQuery("id,name,sno,phonenumber,profile_path","memberinfo","where token = '"+token+"'");
		if(jsonarr.size() != 0){
			jsonobj = jsonarr.get(0).getAsJsonObject();
			System.out.println(jsonobj.toString()+"<<<<<<<<<<<<<<<<77777777777");
			out.print(jsonobj);
		}else{
			out.print("not found token");
		}
		
	}
	
//	
//	public void IDcheck(){  //아이디 중복검사를 하는 메소
//		try {
//			out = response.getWriter();  
//			} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			}	
//		jsonstr = request.getParameter("Data");
//		storeToModel();    
//		
//		// 아이디가 공백문자면 재입력하라고 출력 
//		if(memberinfo.getID().trim().equals(""))out.println("Input your ID"); 
//		else{
//		SendQuery sq = new SendQuery();
//		alreadyhasID = sq.isExsist("id","memberinfo",memberinfo.getID());
//			//데이터베이스에 없는 아이디라면 사용 가능!
//			if(!alreadyhasID){
//				checkedID = memberinfo.getID();
//				out.println("USABLE ID");}
//			// 이미 존재한다면 사용 불가능! 
//			if(alreadyhasID)out.println("UNUSEABLE ID");
//		}
//	}
	
	
	
//	public void storeToModel(String type){
//		Gson gson = new Gson();
//		JsonObject jsonobj = new JsonObject();
//		JsonArray jsonarr = new JsonArray();
//		SQ = new SendQuery();
//		switch(type){
//			case "userinfo" :
//				jsonarr = SQ.serchQuery("id,name,phonenumber,school,dept,sno","memberinfo","where id = '"+userID+"'");
//				if(jsonarr.size() != 0){
//					jsonobj = jsonarr.get(0).getAsJsonObject();
//					System.out.println(jsonobj.toString()+"777");
//					userinfo = gson.fromJson(jsonobj.toString(),UserInfoModel.class);
//					System.out.println(userinfo.getName() + "<<<<<<<<<<<<<<<");	
//					System.out.println(userinfo.getDept() + "<<234<<<<<<<<<<<<<");	
//				}
//			 break;
//			}
//	}
	
	
	
}
