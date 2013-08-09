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
private static Logger logger = Logger.getLogger(adduser_jsp.class);
%>
<%@ page session="true" %>
<%
final String deployDir = application.getInitParameter("deploy-dir");
if(!deployDir.contains("TOPS"))
	logger.warn("The deploy directory (" + deployDir + " does not contain \"TOPS\"");
	
String user = (String)session.getAttribute("user");
if(user==null) {
	//logger.debug("addmember.jsp: user = " + session.getAttribute("user"));
	logger.warn("bounced unlogged user to login page");
	response.sendRedirect("/" + deployDir + "/login.jsp");
	return;
}

String role = (String)session.getAttribute("role");
if(role == null || role.equals("Guest")) {
	logger.warn("Guest user " + user + " attempted to add a user");
	response.sendRedirect("/" + deployDir + "/"); // could be made more painful by logging the user out (punishment for working around JS)
	return;
}
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<h2 class='title'>Authorized Users</h2>
<form name="addForm" method="get"  action="/<%= deployDir %>/admin/AddUserServlet" onsubmit="return validateForm()">
<p class="addforminfo"><span class="asterisk">(*)</span>Fields marked with an asterisk are mandatory</p>
<span class="label">Login</span><span class="asterisk">(*)</span><input class="addform" type="text" name="login"/>
<span class="label">Email</span><span class="asterisk">(*)</span><input class="addform" type="text" name="email"/>
<span class="label">Password</span><span class="asterisk">&nbsp;</span><input class="addform" type="text" name="password"/>
<span class="label">Role</span><span class="asterisk">(*)</span>
<select class="addform" name="role">
	<option class="addform" value="Guest">Guest</option>
	<option class="addform" value="Admin">Admin</option>
	<option class="addform" value="Newsflash">Newsflash</option>
	<option class="addform" value="TOPSpot">TOPSpot</option>
	<option class="addform" value="Outreach">Outreach</option>
	<option class="addform" value="Provincial Exec">Provincial Exec</option>
	<option class="addform" value="Provincial Chair">Provincial Chair</option>
	<option class="addform" value="Chapter Chair">Chapter Chair</option>
</select><input class="addform cancel" type="button" value="Cancel" onclick='$("#content").load("admin/users.jsp");'/>
<input class="addform" type="submit" value="Submit"/>
</form>

<script type="text/javascript">
function validateForm() {
	var login=document.forms["addForm"]["login"];
	var email=document.forms["addForm"]["email"];
	var role=document.forms["addForm"]["role"];
	
	// resetting styles	
	login.style.background = "";
   	email.style.color = "";
	role.style.background = "";
	
	if (login.value==null || login.value=="") {
		login.style.background = "#FF0033";
		login.style.color = "white";
		login.focus();
		alert("Login must be filled out");
    	return false;
	}else if (email.value==null || email.value=="") {
		email.style.background = "#FF0033";
		email.style.color = "white";
		email.focus();
		alert("Email must be filled out");
		return false;
	}else if (role.value==null || role.value=="") {
		role.style.background = "#FF0033";
		role.style.color = "white";
		role.focus();
		alert("Role must be filled out");
		return false;
	}
	
}
</script>

