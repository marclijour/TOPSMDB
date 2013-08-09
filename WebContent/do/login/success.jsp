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
private static Logger logger = Logger.getLogger(success_jsp.class);
%>
<%@page session="true"%>
<%
final String deployDir = application.getInitParameter("deploy-dir");
if(!deployDir.contains("TOPS"))
	logger.warn("The deploy directory (" + deployDir + " does not contain \"TOPS\"");
	
if(session.getAttribute("user")==null){
	logger.warn("bounced unlogged user to login page");
	response.sendRedirect("/" + deployDir + "/login.jsp");
	return;
}
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login Success</title>
</head>
<body>
<center>
<h1>You are successfully logged in</h1>
<h3><a href="/<%= deployDir %>/do/login/logout.jsp">Logout</a></h3>
</center>
</body>
</html>
