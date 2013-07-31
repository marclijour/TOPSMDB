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
import java.io.PrintWriter;
import java.sql.Connection;
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

/**
 * Servlet implementation class CheckLoginCredentialsServlet
 */
@WebServlet("/do/login/CheckLoginCredentialsServlet")
public class CheckLoginCredentialsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CheckLoginCredentialsServlet.class);
	private DataSource ds = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckLoginCredentialsServlet() {
        super();
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
		String user=request.getParameter("un");
		String pass=request.getParameter("pw");

		
		/*
		 * 0.  Testing input
		 */
		if(user == null || pass == null || user.equals("") || pass.equals(""))  {
			response.sendRedirect("/TOPS/login.jsp");
			return;
		}
		
		/*
		 * 1. Fetching credentials from db
		 */
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8"); // TODO check across the process (and IE needs UTF-8)
		PrintWriter out = response.getWriter();
		
		String sqlQuery = "select password, role, email from dbuser where login = '" + user + "'";
		//logger.debug("SQL: " + sqlQuery);
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet results = null;
		String email = "<no email>";
		HttpSession session = request.getSession(true);
		session.setMaxInactiveInterval(24 * 60 * 60);;
		
        try {
			conn = ds.getConnection();
	        stmt = conn.createStatement();
	        results = stmt.executeQuery(sqlQuery); 
	    	
	    	if(results == null) {
	    		logger.warn("denied login to user " + user);
	    		return; // TODO redirect to error page?
	    	}
	    	
	    	
	    	results.next(); // fetch first (and should be only one)
	    	if(!pass.equals(results.getString("password"))) {
	    		out.print("ERROR");
	    		logger.info("denied login to user " + user);

	    		results.close();
	            results = null;
	    		stmt.close(); // should be picked up by gc
	    		stmt = null;
	    		// NB not closing connection here, using it below (next try-catch)

        	}else{ 
	    		out.print(user);
	    		logger.info("accepted login from user " + user);

	    		email = results.getString("email");
	    		//logger.debug("retrieved email=" + email);
	            
	    		// creating user session
	    		String role = results.getString("role");

	            results.close();
	            results = null;
	    		stmt.close();
	    		stmt = null;
	    		// NB not closing connection here, using it below (next try-catch)
	    		
	            // update session with user info
	    		session.setAttribute("user", user);	// login
	    		session.setAttribute("role", role);

	            if(role.equals("Guest")) {
	            	char[] guestFirstname = user.toCharArray();
	            	guestFirstname[0] = Character.toUpperCase(guestFirstname[0]);
	            	session.setAttribute("firstname", new String(guestFirstname)); // will be greeted with login
	            	return; // guests do not need to be registered TOPS members
	            }
	    	}	
	        	
		} catch (SQLException e) {
			logger.error("Something went wrong with info from authenticated user=" + user);
			logger.error(e.getMessage());
			
    		out.print("ERROR"); //let the browser know something went wrong
    		logger.debug("sent 'ERROR' message back to the browser");
    		
    		return; // give up now
    		
		} finally {
		    // Always make sure result sets and statements are closed,
		    // and the connection is returned to the pool
		    if (results != null) {
		      try { results.close(); } catch (SQLException e) { ; }
		      results = null;
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

        
        /*
         * MORE INFO about user for cosmetic purposes
         * For non-guests, look for first name
         */
        
        try {            
        	if(conn == null) // show never happen (initialized above)
        		conn = ds.getConnection();
        	
            stmt = conn.createStatement();
            results = stmt.executeQuery("select firstname from members where lower(email)='" + email.toLowerCase() + "'");
    		
    		if(results == null){
    			logger.fatal("Oops! Can't find email for user logged as " + user);
    			return;
    		}
    		
    		results.next();
    		String firstname = (results.getString("firstname")==null)?"":results.getString("firstname");
        	session.setAttribute("firstname", firstname);
    		
            results.close();
            results = null;
    		stmt.close(); 
    		stmt = null;
    		conn.close();
    		conn = null;
        	
		} catch (SQLException e) {
			logger.error(e.getMessage());
    		out.print("ERROR"); //
    		
		} finally {
		    // Always make sure result sets and statements are closed,
		    // and the connection is returned to the pool
		    if (results != null) {
		      try { results.close(); } catch (SQLException e) { ; }
		      results = null;
		    }
		    if (stmt != null) {
		      try { stmt.close(); } catch (SQLException e) { ; }
		      stmt = null;
		    }
		    if (conn != null) {
		      try { conn.close(); } catch (SQLException e) { logger.error("Unable to close DB connection: " + e.getMessage()); }
		      conn = null;
		    }
		}
        
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
