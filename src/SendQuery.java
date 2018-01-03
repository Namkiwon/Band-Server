
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SendQuery {
	public Connection c;
	private boolean alreadyhas = false;
	
	
	private String sql;

	
	public SendQuery(){
	c = DBconnect.connect();
	}
	
	public void  InsertQuery(String sql){  // 삽입쿼리
		Statement st = null;
			try {
				//스테이트먼트 생성
				st = c.createStatement();  
				int result = st.executeUpdate(sql);  
				st.close();
			}catch (SQLException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
	
	public void  UpdateQuery(String sql){  // 삽입쿼리
		Statement st = null;
			try {
				//스테이트먼트 생성
				st = c.createStatement();  
				int result = st.executeUpdate(sql);
				st.close();
			}catch (SQLException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
	 
	public boolean isExsist(String colum,String table,String findValue){
		Statement st = null;
		String colums[] = colum.split(","); // 한 행을 찾을 수 있게  , 를 사용하여사용할 수 있다. 
		StringBuilder SB = new StringBuilder();
		
			try {
			st = c.createStatement();
			StringBuilder serchQuerySB = new StringBuilder();
			serchQuerySB.append("select * from ").append(table).append(" ;");
			ResultSet rs = st.executeQuery(serchQuerySB.toString());
			 
			while(rs.next()){
				SB.setLength(0);

				for(int i= 0; i < colums.length; i++){
					SB.append(rs.getString(colums[i]));
					if(i != colums.length-1) SB.append(" ");
						}
				 System.out.println(SB.toString());	
				if(SB.toString().equals(findValue)){
					 alreadyhas = true;
					 break;
				 }
			 }			
			rs.close();
			st.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(alreadyhas) {alreadyhas = false;
			return true;}
		else return false;				
	}
	
	
	public JsonArray serchQuery(String select,String table,String where){
		Statement st = null;
		JsonObject jsonobj = new JsonObject();
		JsonArray jsonarr = new JsonArray();
		String colum[] = select.split(",");	
		try {
			st = c.createStatement();
			StringBuilder serchQuerySB = new StringBuilder();
			serchQuerySB.append("select ").append(select).append(" from ").append(table+" ").append(where).append(" ;");
			ResultSet rs = st.executeQuery(serchQuerySB.toString());
			Gson gson = new Gson();
			while(rs.next()){ 
				jsonobj = null;
				jsonobj = new JsonObject(); 
				for(int i=0 ; i< colum.length ; i++){
					if(colum[i].equals("room.roomid")){colum[i] = "roomid";}
					 jsonobj.addProperty(colum[i], rs.getString(colum[i]));
				 }
				 jsonarr.add(jsonobj);
			 }			
			rs.close();
			st.close(); 
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonarr;
	}
		
		
		
}