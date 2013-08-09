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
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
 * Servlet implementation class RemoveMemberServlet
 */
@WebServlet("/RemoveMemberServlet")
public class RemoveMemberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(RemoveMemberServlet.class);
	private DataSource ds = null;
	private String deployDir = "ERROR";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RemoveMemberServlet() {
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
	        ds   = (DataSource) ctx.lookup("java:comp/env/jdbc/topsDB");
	        //logger.debug("hooked up embedded database though JNDI");
	        
		} catch (NamingException e) {
			logger.fatal(e.getMessage());
		}
    }

	/**
	 * Delete a user given either/or its email/ID
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		if(session.getAttribute("user")==null) {
			logger.info("anonymous tried to delete a user, back to login page!");
			//logger.debug("user = " + session.getAttribute("user"));
			response.sendRedirect("/" + deployDir + "/login.jsp");
			return;
		}		
		
		// Continue with valid user logged in
		String user = (String)session.getAttribute("user");
		String role = (String)session.getAttribute("role");
		logger.debug("role = " + role);
		
		// parameters sent by the client
		String id = (String)request.getParameter("id");
		String email = (String)request.getParameter("email");
		String firstname = (String)request.getParameter("firstname");
		String lastname = (String)request.getParameter("lastname");
		
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8"); 
		PrintWriter out = response.getWriter();
		
		// do nothing if user is not authorized
		if(role.equals("Guest")) {
			String memberID = (firstname == null)?email:new StringBuffer(firstname).append(" ").append(lastname).toString();
			logger.warn("Guest user " + user + " attempted to delete member " + memberID + " from the record");
			out.print("Sorry, guest users have read-only access!");
			out.close();
			return;
		}
		
		
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String leftdate = dateFormat.format(calendar.getTime());
		
		Connection conn = null;
		Statement stmt = null;
		
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			
			if(id != null) { //use ID   -- TODO next iteration use a table in the database
				//stmt.executeUpdate("delete from members where id=" + id);
				stmt.executeUpdate("update members set leftdate='" + leftdate + "' where id=" + id);
				logger.info("User " + user + " removed user #" + id + " as of " + leftdate);
				
			}else if(email != null && !email.equals("&nbsp;") && !email.equals("")) { // use email
				//stmt.executeUpdate("delete from members where email='" + email + "'");
				int count = stmt.executeUpdate("update members set leftdate='" + leftdate + "' where LOWER(email)='" + email.toLowerCase() + "'");
				if(count > 0)
					logger.info("User " + user + " removed user with email=" + email + " as of " + leftdate);
				else
					logger.info("User " + user + " was unable to remove user with email=" + email + " as of " + leftdate);
				
			}else if(firstname != null && lastname != null){
				//stmt.executeUpdate("delete from members where firstname='" + firstname + "' and lastname='" + lastname + "'");
				stmt.executeUpdate("update members set leftdate='" + leftdate + "' where firstname='" + firstname + "' and lastname='" + lastname + "'");
				logger.info("User " + user + " removed user " + firstname + " " + lastname + " as of " + leftdate);
				
			}else{
				logger.error("both ID and email are null, or firstname and lastname are null: can not remove member");
				stmt.close();
				stmt = null;
				conn.close();
				conn = null;
				response.sendError(500);
				return;
			}
			
			stmt.close();
			stmt = null;
			conn.close();
			conn = null;
			response.sendError(200);	// OK
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			response.sendError(500);
			
		} finally {
		    // Always make sure result sets and statements are closed,
		    // and the connection is returned to the pool
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
