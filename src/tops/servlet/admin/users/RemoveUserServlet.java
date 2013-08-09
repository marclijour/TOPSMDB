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
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import tops.servlet.RemoveMemberServlet;

/**
 * Servlet implementation class RemoveUserServlet
 */
@WebServlet("/admin/RemoveUserServlet")
public class RemoveUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(RemoveUserServlet.class);
	private DataSource ds = null;
	private String deployDir = "ERROR";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RemoveUserServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	
    	// deployment directory
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
		String role = (String)session.getAttribute("role");
		logger.debug("role = " + role);

		
		// parameters sent by the client
		String login = (String)request.getParameter("login");
		

		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try{
			conn = ds.getConnection();

	        String sql = "DELETE FROM dbuser WHERE login = ?";
	        pstmt = conn.prepareStatement(sql);

        	pstmt.setString(1, login);

        	int i = pstmt.executeUpdate();
        	
	        switch(i) {
	        case 1:
	        	logger.info("User " + user + " deleted user: " + login);
	        	break;
	        default:
	        	logger.error("Unable to update " + login);
	        	break;
	        }
	        
			pstmt.close();
			pstmt = null;
			conn.close();
			conn = null;
//			response.sendError(200);	// OK
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
//			response.sendError(500);
			
		} finally {
		    // Always make sure result sets and statements are closed,
		    // and the connection is returned to the pool
		    if (pstmt != null) {
		      try { pstmt.close(); } catch (SQLException e) { ; }
		      pstmt = null;
		    }
		    if (conn != null) {
		      try { conn.close(); } catch (SQLException e) { logger.error("Unable to close DB connection: " + e.getMessage()); }
		      conn = null;
		    }
		}
		

		// Back to display
		response.sendRedirect("/" + deployDir + "/"); // TODO revise with proper AJAX

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
