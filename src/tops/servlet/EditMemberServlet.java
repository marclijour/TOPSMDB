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
package tops.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class EditMemberServlet
 */
@WebServlet("/EditMemberServlet")
public class EditMemberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(EditMemberServlet.class);
	private DataSource ds = null;
    private String deployDir = "ERROR";
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditMemberServlet() {
        super();
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	
    	ServletContext context = getServletContext();
    	deployDir = context.getInitParameter("deploy-dir");	// defined in WEB-INF/web.xml
    	if(!deployDir.contains("TOPS"))
    		logger.warn("The deploy directory (" + deployDir + " does not contain \"TOPS\"");
    	
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
		if(session.getAttribute("user")==null) {
			//logger.debug("user = " + session.getAttribute("user"));
			logger.warn("bounced unlogged user to login page");
			response.sendRedirect("/" + deployDir + "/login.jsp");
			return;
		}		
		String user = (String)session.getAttribute("user");
		

		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
		String jobtitle = request.getParameter("jobtitle");
		String branch = request.getParameter("branch");
		String ministry = request.getParameter("ministry");
		String city = request.getParameter("city");
		String phone = request.getParameter("phone");
		String email = request.getParameter("email");
		String heardfrom = request.getParameter("heardfrom");
		String creatdate = request.getParameter("creatdate");
		String chapter = request.getParameter("chapter");
		String leftdate = request.getParameter("leftdate");
		leftdate = (leftdate == null)?"":leftdate;
		String leftwhy = request.getParameter("leftwhy");
		
		
		String emailUpdate = request.getParameter("emailUpdate");
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet results = null;
		
		try{
			conn = ds.getConnection();
	        
	        /*
	         * 0. If email update -- need first name and last name (coming from blank email)
	         */
	        
	        if(emailUpdate.equals("yes")) {
	        	String sql = "UPDATE members SET jobtitle = ?, branch = ?, ministry = ?, city = ?, "
	        					+ " phone = ?, heardfrom = ?, creatdate = ?, chapter = ?, email = ?, "
	        					+ " leftdate = ?, leftwhy = ? "
	        					+ "WHERE firstname = ? and lastname = ?";
	        	pstmt = conn.prepareStatement(sql);
	        	
	        	pstmt.setString(1, jobtitle);
	        	pstmt.setString(2, branch);
	        	pstmt.setString(3, ministry);
	        	pstmt.setString(4, city);
	        	pstmt.setString(5, phone);
	        	pstmt.setString(6, heardfrom);
	        	pstmt.setString(7, creatdate);
	        	pstmt.setString(8, chapter);
	        	pstmt.setString(9, email);
	        	if(leftdate.equals(""))
	        		pstmt.setNull(10, java.sql.Types.DATE);
	        	else
	        		pstmt.setString(10, leftdate);
	        	pstmt.setString(11, leftwhy);
	        	pstmt.setString(12, firstname);
	        	pstmt.setString(13, lastname);
	        	
	        	int i = pstmt.executeUpdate();
	        	
		        String topsmember = new StringBuffer(firstname).append(" ").append(lastname).append(" (").append(email).append(")").toString();
		        switch(i) {
		        case 1:
		        	logger.info("User " + user + " updated: " + topsmember + "'s email...");
		        	break;
		        default:
		        	logger.error("Unable to update " + topsmember);
		        	break;
		        }
		        
		        pstmt.close(); 
	    		pstmt = null;
	    		conn.close();
	    		conn = null;
	        	
	        	response.sendRedirect("/" + deployDir + "/");
				return;
	        }
	        
			/*
			 * 1. Check that no other member has the same email (ie more than one member)
			 */
	        stmt = conn.createStatement();
	        results = stmt.executeQuery("select * from members where email='" + email + "'");
			if(results.next()) {
				if(!results.getString("email").equals(email) || results.next()) { // more than one match!
					logger.warn("Aborting: more than one member with same email: " + email);
					results.close();
		            results = null;
		    		stmt.close(); 
		    		stmt = null;
		    		conn.close();
		    		conn = null;
					response.sendRedirect("/" + deployDir + "/do/error.jsp?dupemail");
					return;
				}
			}

			results.close();
            results = null;
    		stmt.close(); 
    		stmt = null;
			
			/*
			 *  from there, there are two cases:
			 *  	1. updating user info (email stays the same) - most often
			 *  	2. updating user email (rare cases where the email was wrong or user mailbox is moved) --> TODO
			 */
			

			/*
			 * 2. Insert
			 */
        	String sql = "UPDATE members SET firstname = ?, lastname = ?, jobtitle = ?, branch = ?, "
					+ " ministry = ?, city = ?,phone = ?, heardfrom = ?, creatdate = ?, chapter = ?, "
					+ " leftdate = ?, leftwhy = ? "
					+ "WHERE email = ?";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, firstname);
			pstmt.setString(2, lastname);
			pstmt.setString(3, jobtitle);
			pstmt.setString(4, branch);
			pstmt.setString(5, ministry);
			pstmt.setString(6, city);
			pstmt.setString(7, phone);
			pstmt.setString(8, heardfrom);
			pstmt.setString(9, creatdate);
			pstmt.setString(10, chapter);
        	if(leftdate.equals(""))
        		pstmt.setNull(11, java.sql.Types.DATE);
        	else
        		pstmt.setString(11, leftdate);
			pstmt.setString(12, leftwhy);
			pstmt.setString(13, email);
			
			int i = pstmt.executeUpdate();
			
	        
	        String topsmember = new StringBuffer(firstname).append(" ").append(lastname).append(" (").append(email).append(")").toString();
	        switch(i) {
	        case 1:
	        	logger.info("User " + user + " updated: " + topsmember);
	        	break;
	        default:
	        	logger.error("Unable to update " + topsmember);
	        	break;
	        }
	        
	        pstmt.close(); 
    		pstmt = null;
    		conn.close();
    		conn = null;
	        
		}catch(SQLException se) {
			logger.error(se.getMessage());	
			se.printStackTrace();
			
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
		    if (stmt != null) {
		      try { stmt.close(); } catch (SQLException e) { ; }
		      stmt = null;
		    }
		    if (conn != null) {
		      try { conn.close(); } catch (SQLException e) { ; }
		      conn = null;
		    }
		  }
		
		// Back to display
		response.sendRedirect("/" + deployDir + "/");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
