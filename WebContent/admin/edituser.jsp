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
private static Logger logger = Logger.getLogger(edituser_jsp.class);
%>
<%@ page session="true" %>
<%
String user = (String)session.getAttribute("user");
if(user==null) {
	//logger.debug("addmember.jsp: user = " + session.getAttribute("user"));
	logger.warn("bounced unlogged user to login page");
	response.sendRedirect("/TOPS/login.jsp"); 
	return;
}

String role = (String)session.getAttribute("role");
if(role == null || role.equals("Guest")) {
	logger.warn("Guest user " + user + " attempted to edit a user");
	response.sendRedirect("/TOPS/"); // TODO revise ; could be made more painful by logging the user out (punishment for working around JS)
	return;
}
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<h2 class='title'>Authorized Users</h2>
<div id="formContainer">
<form name="addForm" method="get"  action="/TOPS/admin/AddUserServlet" onsubmit="return validateForm()">
<p class="addforminfo"><span class="asterisk">(*)</span>Fields marked with an asterisk are mandatory</p>
<span class="label">Login</span><span class="asterisk">(*)</span><input class="addform readonly" type="text" name="login" value="${param.login}" readonly="readonly"/><!--  Login is key -->
<span class="label">Email</span><span class="asterisk">(*)</span><input class="addform" type="text" name="email" value="${param.email}"/>
<span class="label">Password</span><span class="asterisk">&nbsp;</span><input class="addform" type="text" name="password" value="${param.password}"/>
<span class="label">Role</span><span class="asterisk">(*)</span><input class="addform" type="text" name="role" value="${param.role}"/>
<%-- This would not permit editing roles 
<select class="addform" name="role">
	<option class="addform" value="Guest" <c:if test="${param.role == 'Guest'}">selected="selected"</c:if>>Guest</option>
	<option class="addform" value="Admin" <c:if test="${param.role == 'Admin'}">selected="selected"</c:if>>Admin</option>
	<option class="addform" value="Newsflash" <c:if test="${param.role == 'Newsflash'}">selected="selected"</c:if>>Newsflash</option>
	<option class="addform" value="TOPSpot" <c:if test="${param.role == 'TOPSpot'}">selected="selected"</c:if>>TOPSpot</option>
	<option class="addform" value="Outreach" <c:if test="${param.role == 'Outreach'}">selected="selected"</c:if>>Outreach</option>
	<option class="addform" value="Provincial Exec" <c:if test="${param.role == 'Provincial Executive'}">selected="selected"</c:if>>Provincial Exec</option>
	<option class="addform" value="Provincial Chair" <c:if test="${param.role == 'Provincial Chair'}">selected="selected"</c:if>>Provincial Chair</option>
	<option class="addform" value="Chapter Chair" <c:if test="${param.role == 'Chapter Chair'}">selected="selected"</c:if>>Chapter Chair</option>
</select>
--%>
<input class="addform cancel" type="button" value="Cancel" onclick='$("#content").load("admin/users.jsp");'/>
<input class="addform raggedright" type="button" value="Remove" onclick='window.location.href="/TOPS/admin/RemoveUserServlet?login=" + encodeURI("${param.login}")'/><%-- TODO proper AJAX --%>
<input class="addform" type="submit" value="Submit"/>
</form>
</div>

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


