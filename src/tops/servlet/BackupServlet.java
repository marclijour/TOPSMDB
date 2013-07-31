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

import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import tops.util.ZippedDirectory;

/**
 * Servlet implementation class BackupServlet
 */
@WebServlet("/BackupServlet")
public class BackupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static Logger logger = Logger.getLogger(BackupServlet.class);
	private static String backupDir = "/tmp/";	// fallback ; see init for config from file defined in WEB-INF/web.xml
	private static String dlDirName = "downloads/"; 	// default
	private static String dlDir = "/tmp/"; 		// to bet set up in init()
	private static String dbname = "nonameDB";	// fallback ; see init from config file defined in WEB-INF/web.xml
    private DataSource ds = null;
    
    private static File latestZippedBackup = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BackupServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);

    	// setting up back-up directory
        ServletContext context = getServletContext();
        BackupServlet.backupDir = context.getInitParameter("path-to-back-ups");	// defined in WEB-INF/web.xml
		logger.info("Back-up directory identified as: " + BackupServlet.backupDir);

		BackupServlet.dlDirName = context.getInitParameter("downloads");
		BackupServlet.dlDir = getServletContext().getRealPath("/") + BackupServlet.dlDirName;
		logger.info("Download directory identified as: " + BackupServlet.dlDir);
		
		// used for file management (e.g. renaming file)
		BackupServlet.dbname = context.getInitParameter("dbname");
    	
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
			logger.warn("unlogged user bounced to login page");
			response.sendRedirect("/TOPS/login.jsp");
			return;
		}
		
		// Set up response / HTML
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		ServletOutputStream out = response.getOutputStream();
		
		Connection conn = null;
		CallableStatement cstmt = null;
		try {        
			conn = ds.getConnection();
			String sqlstmt = "CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)"; // online/hot backup see http://www.ibm.com/developerworks/data/library/techarticle/dm-0502thalamati/index.html
			cstmt = (CallableStatement) conn.prepareCall(sqlstmt);
			
			String backupFile = backupDB(conn, cstmt);
			out.print("<div style='height:100px;'>");
			
			out.print("<p>The database has been backed-up as '<b>" + backupFile + "</b>'.</p>");
			
			out.print("<p><a href='/TOPS/"
						+ BackupServlet.dlDirName + latestZippedBackup.getName() 
						+ "'>Back-up (zip file)</a> is available for download.</p>");
			
			out.print("</div>");
			
			cstmt.close(); // should be picked up by gc
    		cstmt = null;
    		conn.close();
    		conn = null;
			
		} catch (SQLException e) {	// in admin interface it's fine to send an error message in
			logger.error(e.getMessage());
			// TODO create error page + CSS style for error (divs, etc)
			out.print("<div style='height:100px;color: red;'><b>Error:</b>&nbsp;<i>" + e.getMessage() + "</i></div>");
		
		} finally {
		    // Always make sure result sets and statements are closed,
		    // and the connection is returned to the pool
		    if (cstmt != null) {
		      try { cstmt.close(); } catch (SQLException e) { ; }
		      cstmt = null;
		    }
		    if (conn != null) {
		      try { conn.close(); } catch (SQLException e) { logger.error("Unable to close DB connection: " + e.getMessage()); }
		      conn = null;
		    }
		}
		
	}

	// create back-up (online) as a directory (e.g. 'topsDB')
	private String backupDB(Connection conn, CallableStatement cstmt) throws SQLException, IOException {	
	
	    cstmt.setString(1,backupDir);
	    cstmt.execute(); 
	    cstmt.close();
		conn.close();
		
		// rename file with timestamp
		File topsdb = new File(backupDir, dbname); 
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String today = df.format(new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
		File backupFile = new File(backupDir, dbname + "-" + today);
		if(backupFile.exists()) {
			//logger.debug("Attempting to delete old backup file '" + backupFile.getAbsolutePath() + "'");
			boolean success = deleteDir(backupFile);	// remove previous versions (same day)
			if(!success)
				logger.error("Unable to delete old backup file '" + backupFile.getAbsolutePath() + "'");
		}
		boolean success = topsdb.renameTo(backupFile);		
		if(!success)
			logger.error("Unable to rename '" + topsdb.getAbsolutePath() 
								+ "' as '" + backupFile.getAbsolutePath() + "'");
		
		
		// zip the backed-up db and make it available for download
		latestZippedBackup = new File(dlDir, backupFile.getName() + ".zip");
		ZippedDirectory zip = new ZippedDirectory(backupFile, 	// dir to zip
										latestZippedBackup); 	// target zip
		zip.create(); // can throw IOException
		
		
		return backupFile.getAbsolutePath(); // name of the 'directory" backed-up
	}
	
	public static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }

	    // The directory is now empty so delete it
	    return dir.delete();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
