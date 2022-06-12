package Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import User.user;
import serial.serial;

public class RoomDAO {
	
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
	public void insert(Room r) throws SQLException{
		Connection conn = open();
		String sql = "insert into Room(DeviceID, date) values (?, ?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		java.sql.Timestamp date = new java.sql.Timestamp(System.currentTimeMillis());
		System.out.println("Time:" + date.toString());
		
		try(conn; pstmt){
			pstmt.setString(1, r.getDeviceID());
			pstmt.setTimestamp(2, date);
			pstmt.executeUpdate();
		}
	}
	
	public List<Room> getAll() throws Exception{
		Connection conn = open();
		List<Room> list = new ArrayList<>();
		String sql = "select * from Room";
		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Room r = new Room();
				r.setDate(rs.getString("date"));
				r.setDeviceID(rs.getString("DeviceID"));
				
				list.add(r);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			pstmt.close();
			conn.close();
		}
		
		return list;
	}
}
