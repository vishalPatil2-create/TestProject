

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PsuNcbDataBaseConnection 
{
	public static Connection getConnection() throws SQLException, Exception 
	{		
		Connection connection;
		FileReader fileReader_Properties = null;
		Properties properts_Creadantial = null;
		fileReader_Properties = new FileReader("E:\\LGI\\PSU NCB WorkSpace\\PsuNcbConfirmationToInsurer\\PsuNcb Properties File.properties");
			
		properts_Creadantial = new Properties();
		properts_Creadantial.load(fileReader_Properties);
		
		
		
		String str_forNameDriver = properts_Creadantial.getProperty("forNameDriver");
		String str_URL =  properts_Creadantial.getProperty("URL");
		String str_UserName =  properts_Creadantial.getProperty("UserName");
		String str_Password =  properts_Creadantial.getProperty("Password");
		
		Class.forName(str_forNameDriver);
		connection = DriverManager.getConnection(str_URL, str_UserName, str_Password);
		System.out.println("Data Base Connection Successful");
		
		return connection;
	}
}
