import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class SignUpController {
	private static HttpServletRequest request;
	private HttpServletResponse response;
	private String pk = "id";
	private String table = "memberinfo";
	private String jsonstr = null;
	private boolean alreadyhasID = true;
	public MemberInfoModel memberinfo = null;
	private PrintWriter out ;
	private static String checkedID = "checkedID";
	
	public SignUpController(HttpServletRequest request,HttpServletResponse response){
		this.request = request;
		this.response = response;
		String option = request.getParameter("Option");
		System.out.println(option);
		switch (option){
			case "Finish" : Finish(); break;
			case "IDcheck" : IDcheck(); break;
		}
	}
	
	
	public void IDcheck(){  //아이디 중복검사를 하는 메소
		try {
			out = response.getWriter();  
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}	
		jsonstr = request.getParameter("Data");
		storeToModel();    
		
		// 아이디가 공백문자면 재입력하라고 출력 
		if(memberinfo.getID().trim().equals(""))out.println("Input your ID"); 
		else{
		SendQuery SQ = new SendQuery();
		alreadyhasID = SQ.isExsist("id","memberinfo",memberinfo.getID());
			//데이터베이스에 없는 아이디라면 사용 가능!
			if(!alreadyhasID){
				checkedID = memberinfo.getID();
				out.println("USABLE ID");}
			// 이미 존재한다면 사용 불가능! 
			if(alreadyhasID)out.println("UNUSEABLE ID");
		}
		
	}
	//회원가입 완료 메소
	public void Finish(){
	
		try {
			out = response.getWriter();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
			
			jsonstr = request.getParameter("Data");
			System.out.println(jsonstr);
			storeToModel();
			if(memberinfo.getID().equals(checkedID)){
				
				if(!memberinfo.getPW().equals("")){ // 패스워드는 필수사항이므로 체크 
					if(!memberinfo.getName().trim().equals("")){
						if(!memberinfo.getPhonenumber().trim().equals("")){
							SendQuery SQ = new SendQuery();
							SQ.InsertQuery(makeInsertQuery());
							out.println("signup success"); 
						}else{
							out.println("Input your phone number");
						}
					}else{
						out.println("Input your Name");
					}
				}else{
					out.print("Input your password");
				}
			}
			else out.println("Check ID");  
	}
	
	public void storeToModel(){
		Gson gson = new Gson();
		memberinfo = gson.fromJson(jsonstr,MemberInfoModel.class);	
	}
	
	public String makeInsertQuery(){
		StringBuilder querySB = new StringBuilder();
		querySB.append("INSERT INTO `band`.`memberinfo` (`id`, `pw`, `name`,`phonenumber`,`school`,`dept`,`sno`) VALUES ");
		querySB.append("(").
		append("'").append(memberinfo.getID()).append("'").
		append(",").
		append("'").append(memberinfo.getPW()).append("'").
		append(",").
		append("'").append(memberinfo.getName()).append("'").
		append(",").
		append("'").append(memberinfo.getPhonenumber()).append("'").
		append(",").
		append("'").append(memberinfo.getSchool()).append("'").
		append(",").
		append("'").append(memberinfo.getDepartment()).append("'").
		append(",").
		append("'").append(memberinfo.getStudentnumber()).append("'").
		append(");");
		return querySB.toString();
	} 

}

