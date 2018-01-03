import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.oreilly.servlet.MultipartRequest;


public class GroupController {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String userID;
	private PrintWriter out;
	private UserInfoModel userinfo;
	private SendQuery SQ;
	private JsonObject jsonobj;
	private JsonArray jsonarr;
	private JsonArray buf;
	private Gson gson;
	private ArrayList<String> mem;
	private ChatInfoModel chatinfo;
	private String option;

	public GroupController(HttpServletRequest request , HttpServletResponse response){
		this.request = request;
		this.response = response;
		userID = request.getParameter("userID");
		option = request.getParameter("Option");
		System.out.println(option);
		storeToModel("userinfo");
		switch (option){
			case "renewGroupList" :renewGroupList(); break;
			case "makeGroup" : makeGroup(); break;
			case "writeContents" : writeContents(); break;
			case "renewContents" : renewContents(); break;
			case "savePhoto" : savePhoto(); break;
			case "getMembers" : getMembers(); break;
			case "invite" : invite(); break;
			
		}
	}
	

	
	public void makeGroup(){
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
		memberlist.append(request.getParameter("memberlist"));
		memberlist.append(userinfo.getSno());  //그룹 멤버에 나 사진또한 포
		String groupname = request.getParameter("groupname");
		
		if(!SQ.isExsist("group_name","`band`.`group`", groupname)){
			SQ.InsertQuery("INSERT ignore INTO `band`.`group` (`group_member`,`group_name`,`group_dept`,`group_school`,`chief`) VALUES ('"+memberlist.toString()+"','"+groupname+"','"+userinfo.getDept()+"','"+userinfo.getSchool()+"','"+userinfo.getSno()+ " " + userinfo.getName()+"');");
			String member[] = memberlist.toString().split(",");
			System.out.println(member.length);
			for(int i = 0; i < member.length;i++){
				SQ.InsertQuery("INSERT ignore INTO `band`.`usergroup` (`usergroup_id`,`usergroup_userid`) VALUES (last_insert_id(),'"+member[i]+"');");
			}
		
		JsonArray jsonarr = new JsonArray();
		System.out.println("3");
		jsonarr = SQ.serchQuery("group_id,group_member,group_name,group_dept,group_school,chief", "`band`.`group`","where group_name = '"+groupname+"' and group_dept = '"+userinfo.getDept()+"' and group_school = '"+userinfo.getSchool()+"'");
		out.print(jsonarr.get(0).getAsJsonObject().toString());   ///방을 만들고 roomid를 반환!!!!!!!!!
		}else out.println("group name is already exsist");
		}
	
	
	public void renewContents(){
		try {
			out = response.getWriter();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} 
		String groupid = request.getParameter("groupid");
		jsonobj = new JsonObject();
		jsonarr = SQ.serchQuery("group_id,message,sender_id,sender_name,picture_path,time", "contents","where group_id = '"+groupid+"'");
		
		System.out.println("chattinglist"+jsonarr);
		out.print(jsonarr);
	}
	 
	public void renewGroupList(){
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
		jsonarr2 = new JsonArray();
		
		//채팅룸 하나만 얻고싶을
		if(option.equals("getChattingRoomItem")){
			jsonarr = SQ.serchQuery("room.roomid,count,roomname,lastchat", "userroom,room","where room.roomid = '"+request.getParameter("roomid")+"' and userroom.roomid = room.roomid and userid = '"+userinfo.getSno()+"'");
		}else{ // 채팅룸 올 리
			jsonarr = SQ.serchQuery("group_id,group_name,group_member,group_dept,group_school,chief", "band.`group`","where group_dept='"+userinfo.getDept()+"' and group_school = '"+userinfo.getSchool()+"'");
		}

		try {
			SQ.c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		out.print(jsonarr.toString());
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
		
		public void writeContents(){
			try {
				out = response.getWriter();
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				} 
			
			String contents = request.getParameter("contents");
			String groupinfo = request.getParameter("groupinfo");

			Gson gson = new Gson();
			ContentsInfoModel contentsInfo = gson.fromJson(contents, ContentsInfoModel.class);
			JsonObject jsonContents = gson.fromJson(contents, JsonObject.class);
			jsonContents.addProperty("type", "contents");
			jsonContents.addProperty("groupinfo", groupinfo);
			System.out.println(contentsInfo.getMessage());
			JsonArray jsonarr = SQ.serchQuery("id,token", "usergroup,memberinfo", "where usergroup_id = '"+contentsInfo.getGroup_id()+"' and usergroup.usergroup_userid = memberinfo.sno");
			SQ.InsertQuery("INSERT ignore INTO `band`.`contents` (`group_id`, `message`,`sender_id`,`sender_name`,`picture_path`,`time`) VALUES('"+contentsInfo.getGroup_id()+"','"+contentsInfo.getMessage()+"','"+contentsInfo.getSender_id()+"','"+contentsInfo.getSender_name()+"','"+contentsInfo.getPicture_path()+"','"+contentsInfo.getTime()+"');");
			
			
			//		System.out.println(buf.get(0).getAsJsonObject().get("lastchat").getAsString());
			
			try {
				for(int i = 0 ; i< jsonarr.size(); i++){	
					if(!userinfo.getToken().equals(jsonarr.get(i).getAsJsonObject().get("token").getAsString())){ // 자신에게는 보내지 말
						if(!jsonarr.get(i).getAsJsonObject().get("token").getAsString().equals("0")){
						// targetID 는 받는 쪽에서 자신의 아이디를 알게 하려고!!
						System.out.println(jsonContents.toString());
						pushFcmNotification fcm = new pushFcmNotification(jsonContents.toString(),jsonarr.get(i).getAsJsonObject().get("token").getAsString());
						fcm.start();
						//fcm.pushFCMNotification(jsonMessage.toString(),jsonarr.get(i).getAsJsonObject().get("token").getAsString());
						
						}
					}
				}
			
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		    	out.print("contents send");
		}
		
		public void savePhoto() {
			String photo = request.getParameter("photo");
			System.out.println(photo);
			String dir = request.getServletContext().getRealPath("/img");
			System.out.println(dir);
			String dir1 = "/Users/namgiwon/Documents/contentsImage";
			 // 저는 "/altong/mon" 밑에 이미지를 저장했습니다.

			int max = 10*1024*1024;
			//최대크기, dir 디렉토리에 파일을 업로드하는 multipartRequest
			
			MultipartRequest mr;
			try {
				mr = new MultipartRequest(request, dir1, max, "UTF-8");
				String orgFileName = mr.getOriginalFileName("uploaded_file");
				String saveFileName = mr.getFilesystemName("uploaded_file");

//				out.println(orgFileName+"이 저장되었습니다.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		public void getMembers(){
			try {
				out = response.getWriter();
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				} 
			String groupID = request.getParameter("groupID");
			JsonArray jsonarr = SQ.serchQuery("sno,name","usergroup,memberinfo", "where memberinfo.sno = usergroup_userid and usergroup_id = '"+groupID+"'");
			out.print(jsonarr);
	
		};
		
		
		public void invite(){
			StringBuilder sb= new StringBuilder();
			String groupID = request.getParameter("groupID");
			String inviteMember = request.getParameter("inviteMember");
			String inviteList[] = inviteMember.split(",");

			if(inviteList.length != 0){ // 초대 대상이 없으면 무효화 
				JsonArray jsonarr = SQ.serchQuery("group_member", "`band`.`group`","where group_id = '"+groupID+"'");
				String alreadymember = jsonarr.get(0).getAsJsonObject().get("group_member").getAsString();
			sb.append(alreadymember);
			for(int i = 0; i < inviteList.length;i++){
				if(!sb.toString().contains(inviteList[i])) sb.append(",").append(inviteList[i]);
			}
			System.out.println(sb.toString());
			
			SQ.UpdateQuery("update `band`.`group` set group_member = '"+sb.toString()+"' where group_id = '"+groupID+"';");
			for(int i=0; i<inviteList.length; i++){
				SQ.InsertQuery("INSERT ignore INTO `band`.`usergroup` (`usergroup_userid`, `usergroup_id`) VALUES ('"+inviteList[i]+"', '"+groupID+"');");
			}
			}else{
				System.out.println("초대대상 없");
			}
			
			
		}
		
		


}
