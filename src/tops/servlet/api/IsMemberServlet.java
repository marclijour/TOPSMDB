/*
   Copyright 2013 Marc Lijour
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
package tops.servlet.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

import tops.TOPSMember;

/**
 * Servlet implementation class IsMemberServlet
 */
@WebServlet("/API/IsMember")
public class IsMemberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(IsMemberServlet.class);
	private DataSource ds;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public IsMemberServlet() {
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
	        ds = (DataSource) ctx.lookup("java:comp/env/jdbc/topsDB");
	        //logger.debug("hooked up embedded database though JNDI");
	        
		} catch (NamingException e) {
			logger.fatal(e.getMessage());
		}
    }
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* Uncomment to enforce restricted access control
		HttpSession session = request.getSession(true);
		String user = (String)session.getAttribute("user");
		
		if(user == null) {
			logger.debug("user = null");
			response.sendRedirect("/TOPS/login.jsp");
			return;
		}			
		*/
		String email = request.getParameter("email");
		
		// Settting up output as JSON
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		Connection conn = null;
		ResultSet results = null;		
		PreparedStatement pstmt = null; 
		
		try{
	        String sql = "select * from members where lower(email)=?";
	        conn = ds.getConnection();
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, email.toLowerCase());
	        results = pstmt.executeQuery();
	        
	        if(!results.next()) {
	        	logger.info("User not found by email = '" + email + "'");
		        pstmt.close(); 
	    		pstmt = null;
	    		conn.close();
	    		conn = null;
	    		out.println(new IsMemberResponse(false).toJson()); 
	    		return;
	        }
	        
			TOPSMember member = new TOPSMember(results.getString(2), results.getString(3), results.getString(4), 
					results.getString(5), results.getString(6), results.getString(7), results.getString(8), 
					results.getString(9), results.getString(10), results.getString(11), 
					results.getString(12), results.getString(13), results.getString(14), 
					results.getString(15), results.getString(16)); 
        	logger.info("Found " + member);
        
        	//out.println(member.toJson());
        	out.println(new IsMemberResponse(true).toJson());
        	
	        pstmt.close(); 
    		pstmt = null;
    		conn.close();
    		conn = null;
	        
		}catch(SQLException se) {
			logger.error(se.getMessage());	
			
		} finally {
		    // Always make sure result sets and statements are closed,
		    // and the connection is returned to the pool
		    if (results != null) {
		      try { results.close(); } catch (SQLException e) { ; }
		      results = null;
		    }
		    if (pstmt != null) {
			      try { pstmt.close(); } catch (SQLException e) { ; }
			      pstmt = null;
			    }
		    if (conn != null) {
		      try { conn.close(); } catch (SQLException e) { ; }
		      conn = null;
		    }
		}
		
		out.close(); // flushing stream to browser
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
