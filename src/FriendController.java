import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.oreilly.servlet.MultipartRequest;

public class FriendController {
	private	HttpServletRequest request;
	private HttpServletResponse response;
	private String jsonstr = null;
	private FriendInfoModel friendinfo = null;
	private UserInfoModel userinfo = null;
	private SendQuery SQ = null;
	private PrintWriter out = null;
	private String userID = null; 
	private String friendnumber=null;
	private JsonObject jsonobj ;
	private JsonArray jsonarr;
	private Gson gson;
	public FriendController(HttpServletRequest request , HttpServletResponse response){
		this.request = request;
		this.response = response;
		
		String option = request.getParameter("Option");
		System.out.println(option);
		switch (option){
			case "addFriend" : addFriend(); break;
			case "renewFriendList" : renewFriend(); break;
			case "updateNickname" : updateNickname(); break; 
			case "updateProfilePath" : updateProfilePath(); break; 
		}
	}
	 
	public void updateNickname(){
		try {
			out = response.getWriter();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} 
		userID = request.getParameter("userID");
		SQ = new SendQuery();
		friendnumber = request.getParameter("friendnumber");
		String nickname = request.getParameter("nickname");
		System.out.println(nickname+"<<<<<<<<<<<<<");
		storeToModel("userinfo");
		storeToModel("friendinfo");
		SQ.InsertQuery("update friendlist set nickname = '"+nickname+"' where mynumber = '" + userinfo.getPhonenumber()+"' and friendnumber = '"+friendinfo.getPhonenumber()+"';");
		out.print("update friend nickname success");
		}
	
	 
	public void renewFriend(){
		try {
			out = response.getWriter();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} 
		
		//userinfo 저
		this.userID = request.getParameter("userID");
	   SQ = new SendQuery();
		storeToModel("userinfo");
		
		jsonobj = null;
		jsonobj = new JsonObject();
		JsonArray buf;
		JsonArray jsonarr2 = new JsonArray();
		JsonArray jsonarr3;
		jsonarr = SQ.serchQuery("id,name,phonenumber,sno,token,profile_path", "memberinfo","where school = '"+userinfo.getSchool()+"' and dept = '"+userinfo.getDept()+"'");		
		try {
			SQ.c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.print(jsonarr.toString());
	}
	
	
	public void addFriend(){
		try {
			out = response.getWriter();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} 
		userID = request.getParameter("userID");
		friendnumber = request.getParameter("friendnumber");
		String friendname = request.getParameter("friendname");
		storeToModel("userinfo");
		storeToModel("friendinfo");
		SQ = new SendQuery();
		if(!friendnumber.trim().equals("")){  //아이디를 적지 않것을 거른다
			if(SQ.isExsist("name,phonenumber", "memberinfo",friendname+" "+friendnumber )){ 
				if(!SQ.isExsist("mynumber,friendnumber", "friendlist", userinfo.getPhonenumber() + " "+friendinfo.getPhonenumber())){
					System.out.println(friendinfo.getName());
					String query = makeaddFriendQuery();
					SQ.InsertQuery(query);
					out.print("success add friend");}
				else{
					out.print("This number is already exsist in friend list");
				}
			}else{
				out.print("That number is not member of band");
			}
		}else{
			out.print("Insert Friend Number");
		}
		
	}
	 
	public void storeToModel(String type){
		Gson gson = new Gson();
		JsonObject jsonobj = new JsonObject();
		JsonArray jsonarr = new JsonArray();
		SQ = new SendQuery();
		switch(type){
			case "userinfo" :
				jsonarr = SQ.serchQuery("id,name,phonenumber,school,dept,sno,token","memberinfo","where id = '"+userID+"'");
				if(jsonarr.size() != 0){
					jsonobj = jsonarr.get(0).getAsJsonObject();
					userinfo = gson.fromJson(jsonobj.toString(),UserInfoModel.class);	
				}
			 break;
			case "friendinfo" : 
				jsonarr = SQ.serchQuery("id,name,phonenumber","memberinfo","where phonenumber = '"+friendnumber+"'");
				System.out.println(jsonarr.size());
				if(jsonarr.size() != 0){
					jsonobj =jsonarr.get(0).getAsJsonObject();
					friendinfo = gson.fromJson(jsonobj.toString(),FriendInfoModel.class);
					friendinfo.setNickname(friendinfo.getName());
				}
				break;
			}
	}
	
	public String makeaddFriendQuery(){
		
		StringBuilder querySB = new StringBuilder();
		querySB.append("INSERT ignore INTO `band`.`friendlist` (`mynumber`, `friendnumber`,`nickname`) VALUES ");
		querySB.append("(").
		append("'").append(userinfo.getPhonenumber()).append("'").
		append(",").
		append("'").append(friendinfo.getPhonenumber()).append("'").
		append(",").
		append("'").append(friendinfo.getNickname()).append("'").
		append(");");
		return querySB.toString();
	}
	
	public void updateProfilePath() {
		String userid = request.getParameter("id");
		String photopath = request.getParameter("photo");
		System.out.println(photopath);
		SQ = new SendQuery();
		SQ.UpdateQuery("update memberinfo set profile_path = '"+photopath+"' where id = '"+userid+"';");
		
		String dir1 = "/Users/namgiwon/Documents/contentsImage";
		 // 저는 "/altong/mon" 밑에 이미지를 저장했습니다.

		int max = 10*1024*1024;
		//최대크기, dir 디렉토리에 파일을 업로드하는 multipartRequest
		
		MultipartRequest mr;
		try {
			mr = new MultipartRequest(request, dir1, max, "UTF-8");
			String orgFileName = mr.getOriginalFileName("uploaded_file");
			String saveFileName = mr.getFilesystemName("uploaded_file");

//			out.println(orgFileName+"이 저장되었습니다.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	

}
