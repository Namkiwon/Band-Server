import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class LogInController {
	private	HttpServletRequest request;
	private HttpServletResponse response;
	private boolean IDalreadyhas = false;
	private boolean PWalreadyhas = false;
	private String table = "memberinfo";
	private String pk = "id";
	private PrintWriter out = null;
	private String token;
	private SendQuery SQ;
	
	LogInController(HttpServletRequest request,HttpServletResponse response){
		this.request = request;
		this.response = response;
	
		IDPWcheck();
	}
	
	public void IDPWcheck(){
		try {
			out = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String ID = request.getParameter("ID");
		String PW = request.getParameter("PW");
		String token = request.getParameter("token");
		SQ = new SendQuery();
		SQ.UpdateQuery(makeUpdateTokenQuery(ID,token));
		IDalreadyhas = SQ.isExsist("id","memberinfo",ID);
		PWalreadyhas = SQ.isExsist("pw","memberinfo",PW);
		
		if(IDalreadyhas && PWalreadyhas){
			JsonArray jsonarr = SQ.serchQuery("id,name,sno","memberinfo","where id = '"+ID+"'");
			JsonObject jsonobj = new JsonObject();
			if(jsonarr.size() != 0){
				jsonobj = jsonarr.get(0).getAsJsonObject();
				out.print(jsonobj);
				}
		}else out.print("login fail");
	}
	
	public String makeUpdateTokenQuery(String ID, String token){
		StringBuilder querySB = new StringBuilder();
		querySB.append("UPDATE `band`.`memberinfo` SET `token`='"+token+"' WHERE `id`='"+ID+"';");
		return querySB.toString();
	}
}
