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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import tops.TOPSMember;


/**
 * Servlet implementation class LoadBouncesServlet
 */
@WebServlet("/LoadBouncesServlet")
@MultipartConfig
public class LoadBouncesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(LoadBouncesServlet.class);
	private DataSource ds = null;
	private String deployDir = "ERROR";
	
	public static final int UNDEFINED = 0;
	public static final int NOT_EML = 1;
	public static final int UNDELIVERABLE = 2;
	public static final int OUT_OF_OFFICE = 3;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadBouncesServlet() {
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
		
		logger.debug("Oops should not receive a GET request??");
		
	}

	/**
	 * This method follows the process adopted elsewhere (e.g. ChunksServlet and chunks.jsp).
	 * AJAX is used. The JSP displays a form to submit eml files, the POST request is sent
	 * through and AJAX call to the servlet, which returns a two-part response to be inserted
	 * in the display div area: 1) list of members to be removed (Yes/No option), and 
	 * 2) an output of the actual bounce email (from the eml file).
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		if(session.getAttribute("user")==null) {
			//logger.debug("user = " + session.getAttribute("user"));
			logger.warn("bounced unlogged user to login page");
			response.sendRedirect("/" + deployDir + "/login.jsp");
			return;
		}		
				
		// getting file
		logger.debug("Nb of parts: " + request.getParts().size());	
		Part filePart = request.getPart("filename"); // Retrieves <input type="file" name="filename">
		if(filePart == null) {
			logger.fatal("Null filepart"); 
			response.sendRedirect("/" + deployDir + "/error.jsp");	// TODO add custom error message mechanism
			return;
		}

		// setup output stream
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		ServletOutputStream out = response.getOutputStream();
		
		// 1. Output to first div (action for user, i.e. member to remove etc)
		out.println("<div id='action'>");
		out.println("<h3 class='title'>Action Items</h3>");

		
		if(filePart.getName().endsWith(".eml")) {
			out.print("<b>Error: please submit an EML file (ending with .eml).</b>\n</div>");
			return;
		}
		
		// Using JavaMail to parse the eml file
        Properties props = System.getProperties();
        props.put("mail.host", "smtp.dummydomain.com");
        props.put("mail.transport.protocol", "smtp");

        Session mailSession = Session.getDefaultInstance(props, null);
        InputStream source = filePart.getInputStream();
        MimeMessage message;
		// start processing data
		int status = UNDEFINED;
		int matchedUsers = 0;
		try {
			message = new MimeMessage(mailSession, source);
			String body = getText((javax.mail.Part) message);
			
			if(body.contains("Delivery has failed to these recipients")) {
				status = UNDELIVERABLE;
				final String MAIL_REGEX = "([_A-Za-z0-9-]+)(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})";
				Pattern pattern = Pattern.compile(MAIL_REGEX);
				Matcher matcher = pattern.matcher(body);
				String emails = "";
				while(matcher.find()) {
					String potentialEmail = matcher.group();
					if(potentialEmail.split("@")[0].contains(".")) {
						String email = matcher.group();
						
						if(isMember(email) && !emails.contains(email)) {
							
							if(matchedUsers == 0) { // first occurence
								out.println("<p class='warning'>Please check the error message in the bounced email at the bottom at this page before unregistering members.</p>");
								out.println("<table style='border: 0px;'>");
							}							
							
							out.println("<tr>");
							out.print("<td style='width: 20em;padding: 1em;'>" + email 
									+ "</td><td><input type='button' value='Unregister' onclick='unregisterMember(\"" + email + "\", this);'/></td>");
							matchedUsers++;
							out.println("</tr>");
						}
						emails = emails + email + ", "; // TODO chg that
					}
				}
				
				
				if(matchedUsers == 0)
					out.print("Nothing to do. No matching member in the TOPS database (this member may have been removed already). </br>");
				else {
					out.println("</table>");
					out.println("</br><i>Note: Unregistering a member will remove the member from TOPS.</i>");
				}
				
			}else{
				out.print("Nothing to do: delivery not failed. See email below and advise.");
				logger.error("not match");
			}
			
			// close action div
			out.println("\n</div>\n");
			
			
			if(matchedUsers == 0) {
				out.println("<script>");
				out.println("$('p.warning').style.visibility = 'hidden';");
				out.println("</script>");
				return;
			}
			
			// 2. Email dump for information
			out.println("<!-- ============ Dump of the eml file ============== -->");
			out.println("<div id='emaildump'>\n");
			out.println("<h3 class='title'>Email Content (for information)</h3>");
			out.println("<hr><code>");
			out.print("Subject : " + message.getSubject());
			out.println("<br/>");
			out.print("From : " + message.getFrom()[0]);
			out.println("<br/>");
			out.print("--------------");
			out.println("<br/>");
			out.println(body);	    
			out.println("</code></br></div>");
				
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			
		} catch (SQLException e) {
			logger.error("DB issue while trying to check user membership: " + e.getMessage());
			e.printStackTrace();
			out.print("<div class='error'><b>Error:</b> unable to connect to the database. Please contact your friendly system administrator.</div>\n</div>");
			return;
		}

		
	}
	
	private boolean isMember(String email) throws SQLException {
		//String branch = null, chapter = null, city = null, creatdate = null, firstname = null, lastname = null, ministry = null, phone = null, jobtitle = null, heardfrom = null; // TODO add subscriptions
		String sqlQuery = "select leftdate from members where LOWER(email) = '" + email.toLowerCase() + "'"
				+ " AND (leftdate IS NULL OR leftdate > CURRENT DATE)";
		//logger.debug("(LoadBouncesSerlvet) SQL: " + sqlQuery);
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet results = null;
		
		conn = ds.getConnection();
        stmt = conn.createStatement();
        results = stmt.executeQuery(sqlQuery); 
    	
        if(results != null && results.next()) {
    		logger.info("found registered/active user with email: " + email);
    		results.close();
    		stmt.close();
    		conn.close();
    		return true; 
    	}
    	
  		logger.info("no user with email: " + email);
		stmt.close();
		conn.close();
  		return false;	
	}

    private boolean textIsHtml = false;

    /**
     * Return the primary text content of the message.
     * from http://www.oracle.com/technetwork/java/javamail/faq/index.html#readattach 
     */
	private String getText(javax.mail.Part p) throws MessagingException, IOException {

	       if (p.isMimeType("text/*")) {
	            String s = (String)p.getContent();
	            textIsHtml = p.isMimeType("text/html");
	            return s;
	        }

	        if (p.isMimeType("multipart/alternative")) {
	            // prefer html text over plain text
	            Multipart mp = (Multipart)p.getContent();
	            String text = null;
	            for (int i = 0; i < mp.getCount(); i++) {
	                javax.mail.Part bp = mp.getBodyPart(i);
	                if (bp.isMimeType("text/plain")) {
	                    if (text == null)
	                        text = getText(bp);
	                    continue;
	                } else if (bp.isMimeType("text/html")) {
	                    String s = getText(bp);
	                    if (s != null)
	                        return s;
	                } else {
	                    return getText(bp);
	                }
	            }
	            return text;
	        } else if (p.isMimeType("multipart/*")) {
	            Multipart mp = (Multipart)p.getContent();
	            for (int i = 0; i < mp.getCount(); i++) {
	                String s = getText(mp.getBodyPart(i));
	                if (s != null)
	                    return s;
	            }
	        }

		return null;
	}
	
	// See code @ http://stackoverflow.com/questions/2422468/how-to-upload-files-in-jsp-servlet
	private static String getFilename(Part part) {
	    for (String cd : part.getHeader("content-disposition").split(";")) {
	    	System.out.println("cd: " + cd);
	        if (cd.trim().startsWith("filename")) {
	            String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
	            return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
	        }
	    }
	    return null;
	}
	
	//  See code @ http://stackoverflow.com/questions/2422468/how-to-upload-files-in-jsp-servlet
	private static String getValue(Part part) throws IOException {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream(), "UTF-8"));
	    StringBuilder value = new StringBuilder();
	    char[] buffer = new char[1024];
	    for (int length = 0; (length = reader.read(buffer)) > 0;) {
	        value.append(buffer, 0, length);
	    }
	    return value.toString();
	}
}
