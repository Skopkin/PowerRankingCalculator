package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
	 private static Connection con = null; 
	    
	    private DbConnection() {
	    	//constructor is private to prevent anyone from trying to create an 
	    	//object from this class.
	    }
	    
	    /*
	     * Below is a static initializer block. It is executed when the class is loaded and can only
	     * initialize static class members. It is used to avoid needing a constructor. The connection
	     * is static so we only have one. 
	     */
	    static  { 
	        String url = "jdbc:mysql://localhost:3306/power ranking"; 
	        String user = "root"; 
	        String pass = ""; 
	        try {  
	            con = DriverManager.getConnection(url, user, pass); 
	        } 
	        catch (SQLException e) {
				  System.out.println("************************");
				  System.out.println("Connection failed using credentials of " + user + "/" + pass);
				  System.out.println("************************");
				  e.printStackTrace(); 
	        } 
	    } 
	    
	    /*
	     * Call this method to get a connection to the database. It is static so you need
	     * to use the class name: DbConnection.getConnection();
	     */
	    public static Connection getConnection() { 
	        return con; 
	    }
}
