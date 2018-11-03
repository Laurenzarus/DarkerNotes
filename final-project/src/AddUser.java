import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AddUser")
public class AddUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//From previous page, extract parameters
		String email = request.getParameter("email");
		String pass = request.getParameter("pass");
		String name = request.getParameter("name");
		
		//Set up variables to hold response
		boolean success = true;
		String errorMsg = "";
		String dbName = "";
		String dbEmail = "";
		
		//Check for null input
		if (pass == null) {
			success = false;
			errorMsg += "The password is empty!\n";
			pass = "";
		}
		if (email == null) {
			success = false;
			errorMsg += "The email is empty!\n";
			email = "";
		}
		if (name == null) {
			success = false;
			errorMsg += "The name is empty!\n";
			name = "";
		}
		
		String hashPass = "";
		//Hash using sha256
		try {
	        MessageDigest md = MessageDigest.getInstance("SHA-256");
	        byte[] hashInBytes = md.digest(pass.getBytes(StandardCharsets.UTF_8));
	        StringBuilder sb = new StringBuilder();
	        for (byte b : hashInBytes) {
	        	sb.append(String.format("%02x", b));
	        }
	        hashPass = sb.toString();
		} catch (NoSuchAlgorithmException e) { 
			e.printStackTrace(); 
		}
		
		//Begin database access
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		//If we didn't have null input, go into main database access
		if (success) {
			try {
				success = false;
				Class.forName("com.mysql.jdbc.Driver");
				//conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db?user=root&password=password&useSSL=false");
				conn = DriverManager.getConnection("jdbc:mysql://localhost/db?user=root&password=password&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC");
				
				//Check if email already exists in our database
				ps = conn.prepareStatement("SELECT * FROM Users WHERE email=?");
				ps.setString(1, email);
				rs = ps.executeQuery();
				
				if (rs.next()) {
					//If a user with that email exists
					success = false;
					errorMsg = "A user with that email address already exists!";
				}
				else {
					//Insert the new user 
					
					//(a user with that email doesn't exist and they have valid name, pass, and email)
					success = true;
					ps2 = conn.prepareStatement("INSERT INTO Users (fullName, email, hashPass) VALUES ('" + name + "', '" +  email + "', '" + hashPass + "');");
					ps2.executeUpdate();					
					dbName = name;
					dbEmail = email;
				}
				//Set up a JSON return
				String objectToReturn =
						  "{\n"
							+ "\"success\": \"" + success + "\",\n"
							+ "\"data\": {\n"
								+ "\"errorMsg\": \"" + errorMsg + "\",\n"
								+ "\"name\": \"" + dbName + "\",\n"
								+ "\"email\": \"" + dbEmail + "\"\n"
							+ "}\n" 
						+ "}";
				out.print(objectToReturn);
			} catch(SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			} catch(ClassNotFoundException cnfe) {
				System.out.println("cnfe: " + cnfe.getMessage());
			} finally {
				try {
					if(conn!=null) {
						conn.close();
					}
					if(ps!=null) {
						ps.close();
					}
					if(ps2!=null) {
						ps2.close();
					}
					if(rs!=null) {
						rs.close();
					}
				} catch (SQLException sqle) {
					System.out.println("sqle closing stream:-" + sqle.getMessage());
				}
			}
		}
		else {
			String objectToReturn =
					  "{\n"
						+ "\"success\": \"" + success + "\",\n"
						+ "\"data\": {\n"
							+ "\"errorMsg\": \"" + errorMsg + "\",\n"
							+ "\"name\": \"\",\n"
							+ "\"email\": \"\"\n"
						+ "}\n" 
					+ "}";
			out.print(objectToReturn);
		}
		
	}
}



