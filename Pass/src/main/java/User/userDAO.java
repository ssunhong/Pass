package User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class userDAO {
	
	final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver" ;
	final String JDBC_URL = "jdbc:mysql://localhost/pass";
	
	public Connection open() {
		Connection conn = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(JDBC_URL, "root", "1234");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public void addUser(user u)throws Exception {
		Connection conn = open();
		
		String sql = "insert into user(DeviceID, name) values(?, ?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		pstmt.setString(1, u.getDeviceID());
		pstmt.setString(2, u.getName());
		
		pstmt.executeUpdate();
	}
	public void deleteUser(user u)throws Exception{
		Connection conn = open();
		
		String sql = "delete from user where DeciveID = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		try(conn;pstmt){
			pstmt.setString(1, u.getDeviceID());
			if(pstmt.executeUpdate() == 0) {
				throw new SQLException("DB에러");
			}
		}
	}
	public List<user> getAll()  throws Exception{
		Connection conn = open();
		
		List<user> userList = new ArrayList<>();
		
		String sql = "select * from user";
		PreparedStatement pstmt;
		
		try{
			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				user u = new user();
				u.setDeviceID(rs.getString("DeviceID"));
				u.setName(rs.getString("name"));
				userList.add(u);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return userList;
	}
	public boolean check(String DeviceID) {
		Connection conn = open();
		System.out.println(DeviceID);
		String sql = "select DeviceID from user where deviceid = ?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, DeviceID);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			if(rs.getString("DeviceID").equals(DeviceID))
				return true;
			}catch(Exception e) {
				e.printStackTrace();
			}
		return false;
	}
	public String name(String DeviceID) {
		Connection conn = open();
		System.out.println(DeviceID);
		String sql = "select name from user where deviceid = ?";
		String name = null;
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, DeviceID);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			name = rs.getString("name");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return name;
	}
	
	public void update(String name, String DeviceID) {
		Connection conn = open();
		String sql = "update user set DeviceID = ? where name = ?;";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, DeviceID);
			pstmt.setString(2, name);
			pstmt.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}

