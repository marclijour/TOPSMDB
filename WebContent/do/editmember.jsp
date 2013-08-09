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
private static Logger logger = Logger.getLogger(editmember_jsp.class);
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
	logger.warn("Guest user " + user + " attempted to edit a member");
	response.sendRedirect("/" + deployDir + "/"); // could be made more painful by logging the user out (punishment for working around JS)
	return;
}
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.SimpleDateFormat,java.util.Calendar" %>
<% 
	Calendar calendar = Calendar.getInstance();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	String firstname = (request.getParameter("firstname") == null)?"":request.getParameter("firstname");
	String lastname	 = (request.getParameter("lastname") == null)?"":request.getParameter("lastname");
	String jobtitle = (request.getParameter("jobtitle") == null)?"":request.getParameter("jobtitle");
	String branch = (request.getParameter("branch") == null)?"":request.getParameter("branch");
	String ministry = (request.getParameter("ministry") == null)?"":request.getParameter("ministry");
	String city = (request.getParameter("city") == null)?"":request.getParameter("city");
	String phone = (request.getParameter("phone") == null)?"":request.getParameter("phone");
	String email = (request.getParameter("email") == null)?"":request.getParameter("email");
	String heardfrom = (request.getParameter("heardfrom") == null)?"":request.getParameter("heardfrom");
	String creatdate = (request.getParameter("creatdate") == null)?dateFormat.format(calendar.getTime()):request.getParameter("creatdate");
	String chapter = (request.getParameter("chapter") == null)?"":request.getParameter("chapter");
	String leftdate = (request.getParameter("leftdate") == null)?"":request.getParameter("leftdate");
	String leftwhy = (request.getParameter("leftwhy") == null)?"":request.getParameter("leftwhy");
	String newsflash = (request.getParameter("newsflash") == null)?"":request.getParameter("newsflash");
	String topspot = (request.getParameter("topspot") == null)?"":request.getParameter("topspot");
	
	String selected = " selected='selected'";
	String emailUpdate = (email.equals(""))?"yes":"no";
%>
<div id="formContainer">
<form name="editForm" method="get"  action="/<%= deployDir %>/EditMemberServlet" onsubmit="return validateForm()">
<p class="addforminfo"><span class="asterisk">(*)</span>Fields marked with an asterisk are mandatory</p>
<span class="label">First name</span><span class="asterisk">(*)</span><input id="inputfirstname" class="addform" type="text" name="firstname" value='<%= firstname %>'/>
<span class="label">Last name</span><span class="asterisk">(*)</span><input class="addform" type="text" name="lastname" value="<%= lastname %>"/>
<span class="label">Job title</span><span class="asterisk">&nbsp;</span><input class="addform" type="text" name="jobtitle" value="<%= jobtitle %>"/>
<span class="label">Branch</span><span class="asterisk">&nbsp;</span><input class="addform" type="text" name="branch" value="<%= branch %>"/>
<span class="label">Ministry</span><span class="asterisk">&nbsp;</span><input class="addform" type="text" name="ministry" value="<%= ministry %>"/>
<span class="label">City</span><span class="asterisk">&nbsp;</span><input class="addform" type="text" name="city" value="<%= city %>"/>
<span class="label">Phone</span><span class="asterisk">&nbsp;</span><input class="addform" type="text" name="phone" value="<%= phone %>"/>
<span class="label">Email</span><span class="asterisk">(*)</span><input class="addform<%=(email.equals(""))?"":" readonly"%>" type="text" name="email" value="<%= email %>"<%=(email.equals(""))?"":" readonly=\"readonly\""%>/>
<span class="label">Heard from</span><span class="asterisk">&nbsp;</span><!--  <input class="addform" type="text" name="heardfrom"/>  -->
<select class="addform" name="heardfrom">
	<option class="addform" value="TOPS Intranet"<%= heardfrom.equals("TOPS Intranet")?selected:""%>>TOPS Intranet</option>
	<option class="addform" value="MyOPS News"<%= heardfrom.equals("MyOPS News")?selected:""%>>MyOPS News</option>
	<option class="addform" value="Topical"<%= heardfrom.equals("Topical")?selected:""%>>Topical</option>
	<option class="addform" value="OPSpedia"<%= heardfrom.equals("OPSpedia")?selected:""%>>OPSpedia</option>
	<option class="addform" value="TOPS event"<%= heardfrom.equals("TOPS event")?selected:""%>>TOPS event</option>
	<option class="addform" value="Other OPS event"<%= heardfrom.equals("Other OPS event")?selected:""%>>Other OPS event</option>
	<option class="addform" value="Ontario Internship Program"<%= heardfrom.equals("Ontario Internship Program")?selected:""%>>Ontario Internship Program</option>
	<option class="addform" value="Manager/Director/Supervisor"<%= heardfrom.equals("Manager/Director/Supervisor")?selected:""%>>Manager/Director/Supervisor</option>
	<option class="addform" value="Colleague"<%= heardfrom.equals("Colleague")?selected:""%>>Colleague</option>
	<option class="addform" value="Other"<%= heardfrom.equals("Other")?selected:""%>>Other</option>
</select>
<span class="label">Registration date</span><span class="asterisk">&nbsp;</span><input class="addform" type="text" name="creatdate" value="<%= creatdate %>"/>
<span class="label">Chapter</span><span class="asterisk">&nbsp;</span><!--<input class="addform" type="text" name="chapter"/>  -->
<select class="addform" name="chapter">
	<option class="addform" value="Toronto"<%= chapter.equals("Toronto")?selected:""%>>Toronto</option> 
	<option class="addform" value="Guelph"<%= chapter.equals("Guelph")?selected:""%>>Guelph</option>
	<option class="addform" value="Hamilton"<%= chapter.equals("Hamilton")?selected:""%>>Hamilton</option>
	<option class="addform" value="Kingston"<%= chapter.equals("Kingston")?selected:""%>>Kingston</option>
	<option class="addform" value="London"<%= chapter.equals("London")?selected:""%>>London</option>
	<option class="addform" value="Mississauga"<%= chapter.equals("Mississauga")?selected:""%>>Mississauga</option>
	<option class="addform" value="Niagara"<%= chapter.equals("Niagara")?selected:""%>>Niagara</option>
	<option class="addform" value="North Bay"<%= chapter.equals("North Bay")?selected:""%>>North Bay</option>
	<option class="addform" value="North Toronto"<%= chapter.equals("North Toronto")?selected:""%>>North Toronto</option>
	<option class="addform" value="Oshawa"<%= chapter.equals("Oshawa")?selected:""%>>Oshawa</option>
	<option class="addform" value="Ottawa"<%= chapter.equals("Ottawa")?selected:""%>>Ottawa</option>
	<option class="addform" value="Peterborough"<%= chapter.equals("Peterborough")?selected:""%>>Peterborough</option>
	<option class="addform" value="Pickering"<%= chapter.equals("Pickering")?selected:""%>>Pickering</option>
	<option class="addform" value="Sault Ste Marie"<%= chapter.equals("Sault Ste Marie")?selected:""%>>Sault Ste Marie</option>
	<option class="addform" value="South Porcupine"<%= chapter.equals("South Porcupine")?selected:""%>>South Porcupine</option>
	<option class="addform" value="Sudbury"<%= chapter.equals("Sudbury")?selected:""%>>Sudbury</option>
	<option class="addform" value="Thunder Bay"<%= chapter.equals("Thunder Bay")?selected:""%>>Thunder Bay</option><%= chapter.equals("")?"\n<option class=\"addform\" value=\"\" selected='selected'></option>":""
	
%>
</select>
<% if(!leftdate.equals("")) { %>
<span class="label">Left TOPS on</span><span class="asterisk">&nbsp;</span><input id="leftdatefield" class="addform" type="text" name="leftdate" value="<%= leftdate %>"/><input id="editformregisterback" class="raggedright" type="button" value="Register Back" onclick="reinstantiate()"/>
<span class="label">Rationale</span><span class="asterisk">&nbsp;</span><input class="addform" type="text" name="leftwhy" value="<%= leftwhy %>"/>
<% } %>
<%-- TODO add subscription (newsflash / TOPSpot) config here or open another page --%>
<input id="editformcancel" type="button" value="Cancel" onclick="window.location.href='/<%= deployDir %>/'"/>
<input id="editformsubmit" type="submit" value="Submit"/>
<input type="hidden" name="emailUpdate" value="<%= emailUpdate %>"/>
</form>
</div>

<script type="text/javascript">
window.onload = document.getElementById("inputfirstname").focus();

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

function reinstantiate() {
	document.editForm.leftdatefield.value='';
	document.editForm.leftdatefield.setAttribute('readonly', 'readonly');
	document.editForm.leftdatefield.className += " readonly";
}

</script>

