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
private static Logger logger = Logger.getLogger(addmember_jsp.class);
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
	logger.warn("Guest user " + user + " attempted to add a member");
	response.sendRedirect("/TOPS/"); // could be made more painful by logging the user out (punishment for working around JS)
	return;
}
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.SimpleDateFormat,java.util.Calendar" %>
<% 
	Calendar calendar = Calendar.getInstance();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
%>
<form name="addForm" method="get"  action="/TOPS/AddMemberServlet" onsubmit="return validateForm()">
<p class="addforminfo"><span class="asterisk">(*)</span>Fields marked with an asterisk are mandatory</p>
<span class="label">First name</span><span class="asterisk">(*)</span><input class="addform" type="text" name="firstname"/>
<span class="label">Last name</span><span class="asterisk">(*)</span><input class="addform" type="text" name="lastname"/>
<span class="label">Job title</span><span class="asterisk">&nbsp;</span><input class="addform" type="text" name="jobtitle"/>
<span class="label">Branch</span><span class="asterisk">&nbsp;</span><input class="addform" type="text" name="branch"/>
<span class="label">Ministry</span><span class="asterisk">&nbsp;</span><input class="addform" type="text" name="ministry"/>
<span class="label">City</span><span class="asterisk">&nbsp;</span><input class="addform" type="text" name="city"/>
<span class="label">Phone</span><span class="asterisk">&nbsp;</span><input class="addform" type="text" name="phone"/>
<span class="label">Email</span><span class="asterisk">(*)</span><input class="addform" type="text" name="email"/>
<span class="label">Heard from</span><span class="asterisk">&nbsp;</span><!--  <input class="addform" type="text" name="heardfrom"/>  -->
<select class="addform" name="heardfrom">
	<option class="addform" value="TOPS Intranet">TOPS Intranet</option>
	<option class="addform" value="MyOPS News">MyOPS News</option>
	<option class="addform" value="Topical">Topical</option>
	<option class="addform" value="OPSpedia">OPSpedia</option>
	<option class="addform" value="TOPS event">TOPS event</option>
	<option class="addform" value="Other OPS event">Other OPS event</option>
	<option class="addform" value="Ontario Internship Program">Ontario Internship Program</option>
	<option class="addform" value="Manager/Director/Supervisor">Manager/Director/Supervisor</option>
	<option class="addform" value="Colleague">Colleague</option>
	<option class="addform" value="Other">Other</option>
</select>
<span class="label">Registration date</span><span class="asterisk">&nbsp;</span><input class="addform" type="text" name="creatdate" value="<%= dateFormat.format(calendar.getTime())%>"/>
<span class="label">Chapter</span><span class="asterisk">&nbsp;</span><!--<input class="addform" type="text" name="chapter"/>  -->
<select class="addform" name="chapter">
	<option class="addform" value="Toronto">Toronto</option>
	<option class="addform" value="Guelph">Guelph</option>
	<option class="addform" value="Hamilton">Hamilton</option>
	<option class="addform" value="Kingston">Kingston</option>
	<option class="addform" value="London">London</option>
	<option class="addform" value="Mississauga">Mississauga</option>
	<option class="addform" value="Niagara">Niagara</option>
	<option class="addform" value="North Bay">North Bay</option>
	<option class="addform" value="North Toronto">North Toronto</option>
	<option class="addform" value="Oshawa">Oshawa</option>
	<option class="addform" value="Ottawa">Ottawa</option>
	<option class="addform" value="Peterborough">Peterborough</option>
	<option class="addform" value="Pickering">Pickering</option>
	<option class="addform" value="Sault Ste Marie">Sault Ste Marie</option>
	<option class="addform" value="South Porcupine">South Porcupine</option>
	<option class="addform" value="Sudbury">Sudbury</option>
	<option class="addform" value="Thunder Bay">Thunder Bay</option>
</select>
<input class="addform cancel" type="button" value="Cancel" onclick="window.location.href='/TOPS/'"/>
<input class="addform" type="submit" value="Submit"/>
</form>

<script type="text/javascript">
function validateForm() {
	var firstname=document.forms["addForm"]["firstname"];
	var lastname=document.forms["addForm"]["lastname"];
	var email=document.forms["addForm"]["email"];
	var creatdate=document.forms["addForm"]["creatdate"];
	
	// resetting styles	
	firstname.style.background = "";
   	firstname.style.color = "";
	lastname.style.background = "";
   	lastname.style.color = "";
	email.style.background = "";
   	email.style.color = "";
	creatdate.style.background = "";
	creatdate.style.color = "";
	
	if (firstname.value==null || firstname.value=="") {
		firstname.style.background = "#FF0033";
    	firstname.style.color = "white";
      	firstname.focus();
		alert("First name must be filled out");
    	return false;
	}else if (lastname.value==null || lastname.value=="") {
	    lastname.style.background = "#FF0033";
	    lastname.style.color = "white";
      	lastname.focus();
		alert("Last name must be filled out");
		return false;
	}else if (email.value==null || email.value=="") {
		email.style.background = "#FF0033";
    	email.style.color = "white";
      	email.focus();
		alert("Email must be filled out");
		return false;
	}
	
	// valid email
	var atpos=email.value.indexOf("@");
	var dotpos=email.value.lastIndexOf(".");
	if (atpos<1 || dotpos<atpos+2 || dotpos+2>=email.value.length) {
	  alert("Enter a valid e-mail address");
	  return false;
	}
	
	// valid date 
    re = /^\d{4}-\d{1,2}-\d{1,2}$/;
    if(creatdate.value != "" && !creatdate.value.match(re)) {
    	creatdate.style.background = "#FF0033";
        creatdate.style.color = "white";
      	creatdate.focus();
        alert("Invalid date format: " + creatdate.value);
      	return false;
    }
}
</script>

