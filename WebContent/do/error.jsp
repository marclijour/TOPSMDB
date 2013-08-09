<%-- 
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
--%>
<%@ page import="org.apache.log4j.Logger" %>
<%!
private static Logger logger = Logger.getLogger(error_jsp.class);
%>
<%@ page session="true" %>
<%
final String deployDir = application.getInitParameter("deploy-dir");
if(!deployDir.contains("TOPS"))
	logger.warn("The deploy directory (" + deployDir + " does not contain \"TOPS\"");
	
if(session.getAttribute("user")==null) {
	logger.debug("error.jsp: user = " + session.getAttribute("user"));
	response.sendRedirect("/" + deployDir + "/login.jsp");
	return;
}
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<h1>Error</h1>
<% if(request.getParameter("dupemail") != null) { %>
<p class="errormsg">There is already a user with the same email address (presumably the same person). Check and try again!</p>
<% }else{ %>
<p class="errormsg">Sorry, unexpected error...</p>
<% } %>
<span class="backlink">&lt; <a href="/<%= deployDir %>/">Back</a></span>

