
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnect {
	
	private static Connection connection = null;		
	
	public  static Connection connect(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/band?useUnicode=yes&characterEncoding=UTF-8", "root", "1234");
			System.out.println(connection);
			return connection;
				
		}catch(SQLException sqle){
		sqle.getMessage();
		}catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
}