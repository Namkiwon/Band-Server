import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ChattingController {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String userID;
	private PrintWriter out;
	private String friendlist;
	private UserInfoModel userinfo;
	private SendQuery SQ;
	private JsonObject jsonobj;
	private JsonArray jsonarr;
	private JsonArray buf;
	private Gson gson;
	private ArrayList<String> mem;
	private ChatInfoModel chatinfo;
	private String option;
	public ChattingController(HttpServletRequest request , HttpServletResponse response){
		this.request = request;
		this.response = response;
		userID = request.getParameter("userID");
		option = request.getParameter("Option");
		System.out.println(option);
		storeToModel("userinfo");
		switch (option){
			case "sendmessage" : sendmessage(); break;
			case "makeroom" : makeroom(); break;
			case "renewChattingRoomList" : renewChattingRoom(); break;
			case "renewChattingList" : renewChattingList(); break;
			case "getChattingRoomItem" : renewChattingRoom(); break;
			case "getoutRoom" : getoutroom(); break;
			case "getOtherInfo" : getOtherInfo(); break;
		}
	}
	
	public void renewChattingList(){
		try {
			out = response.getWriter();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} 
		String roomid = request.getParameter("roomid");
		jsonobj = new JsonObject();
		JsonArray jsonarr2 = new JsonArray();
		JsonArray buf = new JsonArray();
		jsonarr = SQ.serchQuery("message,seq,sender_id,sender_name,time", "chat","where roomid = '"+roomid+"'");
		
		System.out.println("chattinglist"+jsonarr);
		out.print(jsonarr);
	}
	
	 
	public void renewChattingRoom(){
		try {
			out = response.getWriter();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} 
		jsonobj = new JsonObject();
		JsonArray jsonarr2 = new JsonArray();
		JsonArray buf = new JsonArray();
		jsonarr = new JsonArray();
		
		//채팅룸 하나만 얻고싶을
		if(option.equals("getChattingRoomItem")){
			jsonarr = SQ.serchQuery("room.roomid,count,roomname,lastchat", "userroom,room","where room.roomid = '"+request.getParameter("roomid")+"' and userroom.roomid = room.roomid and userid = '"+userinfo.getSno()+"'");
		}else{ // 채팅룸 올 리
			jsonarr = SQ.serchQuery("room.roomid,count,roomname,lastchat", "userroom,room","where userid = '"+userinfo.getSno()+"' and userroom.roomid = room.roomid and lastchat is not NULL");
			}

		try {
			SQ.c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println(jsonarr.toString()+"으아아아아아아");
		out.print(jsonarr.toString());
	}
	
	
	public void makeroom(){
		try {
			out = response.getWriter();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} 
		gson = new Gson();
		JsonObject jsonobj = new JsonObject();
		jsonobj.addProperty("name", userinfo.getName());
		jsonobj.addProperty("sno", userinfo.getSno());
		StringBuilder memberlist = new StringBuilder();
		memberlist.append(request.getParameter("friendlist"));
		memberlist.append(userinfo.getSno());  //채팅방멤버에 나 자신또한 포함
		
		String roomname = request.getParameter("roomname");
		
		JsonArray roomnameJA = gson.fromJson(roomname, JsonArray.class);
		
		roomnameJA.add(jsonobj);
		System.out.println(roomnameJA.toString()+"끼얏호 ");
		
		
		if(!SQ.isExsist("member","room", memberlist.toString())){
			SQ.InsertQuery(makeRoomQuery(memberlist.toString()));
			String member[] = memberlist.toString().split(",");
			System.out.println(member.length);
			StringBuilder sb;
			for(int i = 0; i < member.length;i++){
				sb = new StringBuilder();
				buf= gson.fromJson(roomnameJA, JsonArray.class);
				if(buf.get(i).getAsJsonObject().get("sno").getAsString().equals(member[i]))buf.remove(i);
				
				for(int j =0; j < buf.size(); j++){
					sb.append(buf.get(j).getAsJsonObject().get("name").getAsString());
					if(j != buf.size()-1) sb.append(", ");
				}
					
				SQ.InsertQuery("INSERT INTO `band`.`userroom` (`userid`, `roomid`,`roomname`) VALUES ('"+member[i]+"',last_insert_id(),'"+sb.toString()+"');");
			}
		}
		JsonArray jsonarr = new JsonArray();
		jsonarr = SQ.serchQuery("room.roomid,member,roomname,lastchat","room,userroom","where room.roomid = userroom.roomid and member = '"+memberlist+"' and userroom.userid = '"+userinfo.getSno()+"'");
		System.out.println(jsonarr.toString() + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		out.print(jsonarr.get(0).getAsJsonObject());   ///방을 만들고 roomid를 반환!!!!!!!!!
		}
	
	public void getoutroom(){
		try {
			out = response.getWriter();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} 
		gson = new Gson();
		JsonObject jsonobj = new JsonObject();
//		SQ.InsertQuery("delete from room where roomid = '"+request.getParameter("roomid")+"';");
		SQ.InsertQuery("delete from userroom where roomid = '"+request.getParameter("roomid")+"' and userid = '"+userinfo.getSno()+"';");
		out.print("success");   
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
				}
		}
	
	public String makeRoomQuery(String memberlist){
		String a[] = memberlist.split(",");
		StringBuilder querySB = new StringBuilder();
		querySB.append("INSERT INTO `band`.`room` (`member`, `count`) VALUES ");
		querySB.append("(").
		append("'").append(memberlist).append("'").
		append(",").
		append("'").append(a.length).append("'").
		append(");");
		return querySB.toString();
	}
	
	public void sendmessage(){
		try {
			out = response.getWriter();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} 
		String message = request.getParameter("message");
		Gson gson = new Gson();
		JsonObject jsonMessage =  gson.fromJson(message, JsonObject.class);
		jsonMessage.addProperty("type", "chat");
		chatinfo = gson.fromJson(jsonMessage, ChatInfoModel.class);
		jsonarr = SQ.serchQuery("id,token,roomname", "userroom,memberinfo", "where roomid = '"+jsonMessage.get("roomid").getAsString()+"' and userroom.userid = memberinfo.sno");
		SQ.InsertQuery("INSERT ignore INTO `band`.`chat` (`roomid`, `message`,`sender_id`,`sender_name`,`time`) VALUES('"+chatinfo.getRoomid()+"','"+chatinfo.getMessage()+"','"+chatinfo.getSender_id()+"','"+chatinfo.getSender_name()+"','"+chatinfo.getTime()+"');");
		SQ.InsertQuery("update room set lastchat = '"+chatinfo.getMessage()+"' where roomid = '" +chatinfo.getRoomid()+"';");
		JsonArray chattingroomInfoJA = new JsonArray();
		chattingroomInfoJA = SQ.serchQuery("room.roomid,count,lastchat", "userroom,room","where room.roomid = '"+chatinfo.getRoomid()+"' and userroom.roomid = room.roomid and userid = '"+userinfo.getSno()+"'");
		
		//		System.out.println(buf.get(0).getAsJsonObject().get("lastchat").getAsString());
		
		try {
			for(int i = 0 ; i< jsonarr.size(); i++){	
				if(!userinfo.getToken().equals(jsonarr.get(i).getAsJsonObject().get("token").getAsString())){
					if(!jsonarr.get(i).getAsJsonObject().get("token").getAsString().equals("0")){
					// targetID 는 받는 쪽에서 자신의 아이디를 알게 하려고!!
					JsonObject chattingroomJO = new JsonObject(); 
					chattingroomJO = chattingroomInfoJA.get(0).getAsJsonObject();
					chattingroomJO.addProperty("roomname", jsonarr.get(i).getAsJsonObject().get("roomname").getAsString());
					
					jsonMessage.addProperty("chattingroomInfo", chattingroomJO.toString());
					jsonMessage.addProperty("targetID",jsonarr.get(i).getAsJsonObject().get("id").getAsString());
					System.out.println(jsonMessage.toString());
					pushFcmNotification fcm = new pushFcmNotification(jsonMessage.toString(),jsonarr.get(i).getAsJsonObject().get("token").getAsString());
					fcm.start();
					//fcm.pushFCMNotification(jsonMessage.toString(),jsonarr.get(i).getAsJsonObject().get("token").getAsString());
					
					}
				}
			}
		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	    	out.print("message send");
	}
	
	public void getOtherInfo(){
		try {
			out = response.getWriter();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} 
		String otherID = request.getParameter("otherID");
		JsonArray a = SQ.serchQuery("profile_path", "memberinfo", "where id = '"+otherID+"'");
		System.out.println(a.toString());
		out.print(a.get(0).getAsJsonObject().get("profile_path").getAsString());
		
	}
	
	
 

}
