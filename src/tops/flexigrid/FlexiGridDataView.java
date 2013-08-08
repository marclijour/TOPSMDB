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
package tops.flexigrid;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import tops.TOPSMember;

import com.google.gson.Gson;

/**
 * Throw away object for the immediate consumption of FlexiGridServlet: a view/page in the flexigrid interface
 * 
 * @author marc
 *
 */
public class FlexiGridDataView {
	private static Logger logger = Logger.getLogger(FlexiGridDataView.class);
	
	@SuppressWarnings("unused")
	private int page; // used for JSON output, initialized in constructor
	private int total;
	private ArrayList<Row> rows;
	
	
	public FlexiGridDataView(int page, int rp, String sortname, String sortorder, String query, String qtype) 
			throws Exception { // current page, results per page, sort key, sort order, search query, query field
		
		this.page = page;
    	rows = new ArrayList<Row>();
    	boolean searchEnabled = true;
    	boolean lookingForInActiveMembers = false;
    	

    	// setting up data source 
        Context ctx = null;
        DataSource ds = null;
		try {
			ctx = new InitialContext();
	        ds  = (DataSource) ctx.lookup("java:comp/env/jdbc/topsDB");
	        //logger.debug("hooked up embedded database though JNDI");
	        
		} catch (NamingException e) {
			logger.fatal(e.getMessage());
		}
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet results = null;

		try {
			conn = ds.getConnection();
			
			// Is search enabled? (filtering)
			if(query == null || query.equals(""))
				searchEnabled = false;

			if(qtype != null && qtype.equals("leftdate"))
				lookingForInActiveMembers = true;
			
			
	    	/*
	    	 * 1. Get number of members (rows)
	    	 */
	        stmt = conn.createStatement();
	        if(lookingForInActiveMembers) {	        	
	        	if(searchEnabled) {
	        		results = stmt.executeQuery("select count(*) from members WHERE leftdate <= CURRENT DATE AND CHAR(leftdate) LIKE '%" + query + "%'");
		        	if(results.next())
						total = results.getInt(1);
		        	logger.info("Connected to DB: " + total + " inactive/unregistered TOPS members WHERE leftdate <= CURRENT DATE AND CHAR(leftdate) LIKE '%" + query + "%'");
		        	
	        	}else {
	        		results = stmt.executeQuery("select count(*) from members WHERE leftdate <= CURRENT DATE");
		        	if(results.next())
						total = results.getInt(1);
		        	logger.info("Connected to DB: " + total + " inactive/unregistered TOPS members");
		        	
	        	}
	        	
	        	
	        }else { // dealing with registered/active members
	        	if(searchEnabled) {
	        		results = stmt.executeQuery("select count(*) from members WHERE (leftdate IS NULL OR leftdate > CURRENT DATE) AND LOWER(" + qtype + ") like LOWER('%" + query + "%')");
					
		        	if(results.next())
						total = results.getInt(1);
		        	logger.info("Connected to DB: " + total + " TOPS members WHERE (leftdate IS NULL OR leftdate > CURRENT DATE) AND LOWER(" + qtype + ") like LOWER('%" + query + "%')");
					
				}else{
		        	results = stmt.executeQuery("select count(*) from members WHERE leftdate IS NULL OR leftdate > CURRENT DATE");
					if(results.next())
						total = results.getInt(1);
					logger.info("Connected to DB: " + total + " active TOPS members");
				}
	        }
	        
	        results.close();
            results = null;
    		stmt.close();
    		stmt = null;
			
			/*
			 * 2. Load members
			 */
			String sqlQuery;
	        stmt = conn.createStatement();
	        if(lookingForInActiveMembers) 
	        	if(searchEnabled) 
	        		sqlQuery = "select * from members WHERE leftdate <= CURRENT DATE AND CHAR(leftdate) LIKE '%" + query + "%'";
	        	else
	        		sqlQuery = "select * from members WHERE leftdate <= CURRENT DATE";
	        
	        else 	// active members
	        	if(searchEnabled)
	        		sqlQuery = "select * from members WHERE (leftdate IS NULL OR leftdate > CURRENT DATE) AND LOWER(" + qtype + ") like LOWER('%" + query + "%') order by " + sortname + " " + sortorder;
	        	else
	        		sqlQuery = "select * from members WHERE (leftdate IS NULL OR leftdate > CURRENT DATE) order by " + sortname + " " + sortorder;
	        
	        
	        results = stmt.executeQuery(sqlQuery); 
			if(results == null) {
        		logger.error("Can't retrieve members from the DB!");
        		stmt.close(); 
        		stmt = null;
        		conn.close();
        		conn = null;
        		throw new Exception("Unable to retrieve members from the DB!"); // to be caught by the servlet (caller)
        	}
    		
			// skip unwanted rows
			for(int i=0; i < (page-1) * rp ; i++)
				results.next();
			
			for(int i=0; i < rp && results.next(); i++) {
			//while(rs.next()) {
				// skip first col = ID
				String chapter = (results.getString(12) == null)?"":results.getString(12);
				String leftdate = (results.getString(13) == null)?"":results.getString(13);
				String leftwhy = (results.getString(14) == null)?"":results.getString(14);
				String newsflash = (results.getBoolean(15) == true)?"Y":"N";
				String topspot = (results.getBoolean(16) == true)?"Y":"N";
				TOPSMember cell = new TOPSMember(results.getString(2), results.getString(3), results.getString(4), 
						results.getString(5), results.getString(6), results.getString(7), results.getString(8), 
						results.getString(9), results.getString(10), results.getString(11), 
						chapter, leftdate, leftwhy, newsflash, topspot); 
				rows.add(new Row((String)results.getString(2), cell));
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
     * Main feature: calls DB to retrieve latest and outputs JSON
     * @return json-formatted list of TOPS members
     */
    public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
    }
	
}
