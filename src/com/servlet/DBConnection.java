package com.servlet;

import java.sql.*;
import java.util.ArrayList;

	public class DBConnection {
	    public final static String url = "jdbc:mysql://localhost:3306/test";
	    public final static String user = "root";
	    public final static String password = "123";

	    private static Connection con;
	    private static Statement stmt;
	    private static ResultSet result;

	    public static void main(String args[]) {
	        String query = "select count(*) from books";
	 
	        try {
	            // opening database connection to MySQL server
	            con = DriverManager.getConnection(url, user, password);
	 
	            // getting Statement object to execute query
	            stmt = con.createStatement();
	 
	            // executing SELECT query
	            result = stmt.executeQuery(query);
	 
	            while (result.next()) {
	                int count = result.getInt(1);
	                System.out.println("Total number of books in the table : " + count);
	            }
	 
	        } catch (SQLException sqlEx) {
	            sqlEx.printStackTrace();
	        } finally {
	            //close connection ,stmt and resultset here
	            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
	            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
	            try { result.close(); } catch(SQLException se) { /*can't do anything */ }
	        }
	    }
	 
	    public boolean insertRow(Connection connection, String name, int followed, int follows, int comments30,
				int likes30, ArrayList<String> commentsTexts30) throws SQLException {
	        String insert = "insert into \"instaQueries\" (name, followed, follows) values (?, ?, ?)";

	        PreparedStatement statement = connection.prepareStatement(insert);
	        statement.setString(1, name);
	        statement.setInt(2, followed);
	        statement.setInt(3, follows);
	        return statement.execute();
	    }
	    
	}

