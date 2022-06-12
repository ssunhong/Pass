package serial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import User.user;


public class serialDAO {
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
	public List<serial> getAll() throws SQLException {
		Connection conn = open();
		
		String sql = "select * from serial";
		ArrayList<serial> list = new ArrayList<serial>();
		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				serial s = new serial();
				s.setNumber(rs.getString("number"));
				
				list.add(s);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			pstmt.close();
			conn.close();
		}
		
		return list;
	}
	public void insert(String number)throws Exception {
		Connection conn = open();
		
		String sql = "insert into serial(number) values(?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		pstmt.setString(1, number);
		
		pstmt.executeUpdate();
	}
	public void deleteSerial(String number)throws Exception{
		Connection conn = open();
		
		String sql = "delete from serial where number = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		try(conn;pstmt){
			pstmt.setString(1, number);
			if(pstmt.executeUpdate() == 0) {
				throw new SQLException("DB에러");
			}
		}
	}
	public boolean check(String number) throws SQLException {
		Connection conn = open();
		
		String sql = "select number from serial where number = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		pstmt.setString(1, number);
		ResultSet rs = pstmt.executeQuery();
		
		try(conn;pstmt;rs){
				if(rs.next())
					return true;
				else
					return false;
			}
		}
}

