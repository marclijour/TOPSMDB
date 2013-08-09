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

import tops.flexigrid.FlexiGridDataView;

/**
 * Servlet implementation class FlexiGridServlet
 */
@WebServlet("/FlexiGridServlet")
public class FlexiGridServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(FlexiGridServlet.class);
	private DataSource ds = null;
	private String deployDir = "ERROR";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FlexiGridServlet() {
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
		
		
		/*
		logger.info("hello");
		Enumeration<String> params = request.getParameterNames();
		while(params.hasMoreElements()) {
			logger.info("param: " + params.nextElement());
		}
		logger.info("rp: " + request.getParameter("rp"));
		logger.info("page: " + request.getParameter("page"));
		logger.info("qtype: " + request.getParameter("qtype"));
		logger.info("sortorder: " + request.getParameter("sortorder"));
		logger.info("query: " + request.getParameter("query"));
		logger.info("sortname: " + request.getParameter("sortname"));
		*/
		
		int rp = Integer.parseInt(request.getParameter("rp")); 		// results per page
		int page = Integer.parseInt(request.getParameter("page"));	// current page number 
		String qtype = request.getParameter("qtype");				// column sort key
		String query = request.getParameter("query");				// column sort key
		String sortname = request.getParameter("sortname");			// sort key
		String sortorder = request.getParameter("sortorder");		// sort order
		
		try{
			FlexiGridDataView grid = new FlexiGridDataView(page, rp, sortname, sortorder, query, qtype);

			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.print(grid.toJson());
			
		} catch(Exception e) {
			logger.error("Unable to send a grid back to the browser: )" + e.getMessage());
		}			
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
