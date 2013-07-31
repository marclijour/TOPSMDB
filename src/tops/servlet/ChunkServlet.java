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
 * Servlet implementation class ChunkServlet
 */
@WebServlet("/ChunkServlet")
public class ChunkServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(Character.class);

    private DataSource ds = null;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChunkServlet() {
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
		HttpSession session = request.getSession(true);
		if(session.getAttribute("user")==null) {
			//logger.debug("user = " + session.getAttribute("user"));
			logger.warn("bounced unlogged user to login page");
			response.sendRedirect("/TOPS/login.jsp");
			return;
		}		
		
		int chunksize = Integer.parseInt(request.getParameter("chunksize"));
		String chapter = request.getParameter("chapter"); // chapter can be 'All' or chapter
		
		// define query depending on chapter
		String sqlQuery = null;
		if(chapter.equals("All")) {
			sqlQuery = "select * from members WHERE (leftdate IS NULL OR leftdate > CURRENT DATE) order by lastname asc";
			
		}else {
			sqlQuery = "select * from members WHERE (leftdate IS NULL OR leftdate > CURRENT DATE) AND chapter='" + chapter + "' order by lastname asc";
		}
		
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8"); // TODO check across the process
		PrintWriter out = response.getWriter();
		

		
		Connection conn = null; 
		Statement stmt = null;
		ResultSet results = null;
		
		try{
	        conn = ds.getConnection();
	        stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
	                								ResultSet.CONCUR_READ_ONLY);
	        
	        results = stmt.executeQuery(sqlQuery);
			//logger.debug("SQL: " + sqlQuery);
			
			// Check ResultSet's scrollability
			//if (rs.getType() == ResultSet.TYPE_FORWARD_ONLY) {
			//	logger.debug("ResultSet non-scrollable.");
			//}else {
			//	logger.debug("ResultSet scrollable.");
			//}
		      
	        
			int counter = 1;
	        while(results.next()) {
        		out.print("<h4 class='chunk'>Chunk #");
        		out.print(counter);
        		out.print("</h4><textarea class='chunk' onclick='this.focus();this.select();'>");
	        	for(int i=0; i < chunksize ; i++) {	
	        		String email = results.getString("email");
	        		if(!email.trim().equals("")) { 
	        			out.print(email);
	        			out.print("; ");
	        		}
	        		if(!results.next())
	        			break;
	           	}
	        	results.previous();
        		out.println("</textarea>");
        		counter++;
	        }
	        
	        out.close();
	        	        
	        results.close();
            results = null;
    		stmt.close(); 
    		stmt = null;
    		conn.close();
    		conn = null;
	        
		}catch(SQLException se) {
			logger.error(se.getMessage());	/*		
		} catch (InstantiationException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();*/
			
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
