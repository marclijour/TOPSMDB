/*
   Copyright 2012 Marc Lijour
    This file is part of TOPSMDB.

    TOPSMDB is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
  
    TOPSMDB is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package tops.servlet.admin.users;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import tops.servlet.CheckLoginCredentialsServlet;

/**
 * Servlet implementation class AddUserServlet
 */
@WebServlet("/admin/AddUserServlet")
public class AddUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(AddUserServlet.class);
	private DataSource ds = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddUserServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	
    	// setting up data source 
        Context ctx;
		try {
			ctx = new InitialContext();
	        ds  = (DataSource) ctx.lookup("java:comp/env/jdbc/topsDB");
	        //logger.debug("hooked up embedded database though JNDI");
	        
		} catch (NamingException e) {
			logger.fatal(e.getMessage());
		}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		String user = (String)session.getAttribute("user");
		
		if(user == null) {
			//logger.debug("user = null");
			logger.warn("bounced unlogged user to login page");
			response.sendRedirect("/TOPS/login.jsp"); 
			return;
		}	
		
		String login = request.getParameter("login");			
		String email = request.getParameter("email");		
		String password = request.getParameter("password");	
		String role = request.getParameter("role");
		
		
		Connection conn = null;
		PreparedStatement pstmt = null; // to insert data
		
		try{
			conn = ds.getConnection();
	        //String sql = "UPDATE dbuser SET email = ?, password = ?, role = ? WHERE login = ?";
			String sql = "INSERT INTO dbuser VALUES(?, ?, ?, ?)";
	        pstmt = conn.prepareStatement(sql);

        	pstmt.setString(1, login);
        	pstmt.setString(2, email);
        	pstmt.setString(3, password);
        	pstmt.setString(4, role);

        	int i = pstmt.executeUpdate();
        	
	        String selectedUser = new StringBuffer(login).append(" (").append(email).append(") ").toString();
	        switch(i) {
	        case 1:
	        	logger.info("User " + user + " updated: " + selectedUser + "'s email...");
	        	break;
	        default:
	        	logger.error("Unable to update " + selectedUser);
	        	break;
	        }
        	
	        pstmt.close(); 
    		pstmt = null;
    		conn.close();
    		conn = null;
	        
		}catch(SQLException se) {
			logger.error(se.getMessage());	
			
		} finally {
		    // Always make sure result sets and statements are closed,
		    // and the connection is returned to the pool
		    if (pstmt != null) {
			      try { pstmt.close(); } catch (SQLException e) { ; }
			      pstmt = null;
			    }
		    if (conn != null) {
		      try { conn.close(); } catch (SQLException e) { ; }
		      conn = null;
		    }
		}

		// Back to display
		response.sendRedirect("/TOPS/");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
