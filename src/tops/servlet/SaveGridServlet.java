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
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

/**
 * This Servlet produces the CSV file and offers it for download.
 * 
 * Servlet implementation class SaveGridServlet
 */
@WebServlet("/SaveGridServlet")
public class SaveGridServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static Logger logger = Logger.getLogger(SaveGridServlet.class);
    private DataSource ds = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveGridServlet() {
        super();
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
		
		// Set up response / Attachment
		response.setCharacterEncoding("UTF-8");	
		response.setContentType("application/x-download");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String today = df.format(new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
		String filename = "TOPS-database-snapshot-" + today + ".csv";
		response.setHeader( "Content-Disposition", "attachment; filename=" + filename);	
		ServletOutputStream out = response.getOutputStream();
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet results = null;
		String sqlQueryMembers = "select * from members order by lastname asc";
		
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			results = stmt.executeQuery(sqlQueryMembers);
			
			if(results == null) {
        		logger.error("Can't retrieve members from the DB!");
        		stmt.close(); 
          		stmt = null;
          		conn.close();
          		conn = null;
              	
        		return;
        	}
    		
			// header
			out.println("First name,Last name,Job title,Branch,Ministry,City,Phone,Email,Heard from,Joined on,Chapter, Date Left, Reason");
			
			while(results.next()) {
				//logger.debug("printing: " + rs.getString(3));
				
				// skip first col = ID
				String chapter = (results.getString(12) == null)?"":results.getString(12);
				String dateleft = (results.getString(13) == null)?"":results.getString(13);
				String reasonleft = (results.getString(14) == null)?"":results.getString(14);
				out.print("\"");
				out.print(results.getString(2));
				out.print("\",\"");
				out.print(results.getString(3));
				out.print("\",\"");
				out.print(results.getString(4));
				out.print("\",\"");
				out.print(results.getString(5));
				out.print("\",\"");
				out.print(results.getString(6));
				out.print("\",\"");
				out.print(results.getString(7));
				out.print("\",\"");
				out.print(results.getString(8));
				out.print("\",\"");
				out.print(results.getString(9));
				out.print("\",\"");
				out.print(results.getString(10));
				out.print("\",\"");
				out.print(results.getString(11));
				out.print("\",\"");
				out.print(chapter);
				out.print("\",\"");
				out.print(dateleft);
				out.print("\",\"");
				out.print(reasonleft);
				out.println("\"");
				
			}
			
			results.close();
	        results = null;
	    	stmt.close(); 
	    	stmt = null;
	    	conn.close();
	    	conn = null;
	        	
		} catch (SQLException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			
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
