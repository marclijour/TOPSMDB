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
<%@page import="com.sun.xml.internal.bind.CycleRecoverable.Context"%>
<%@page import="java.io.InputStream,java.io.IOException,java.util.Properties"%>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="true" %>
<%!
private static Logger logger = Logger.getLogger(index_jsp.class);
private static String version = null;
private String user= null;
%>
<%
// user ID
String userParam = (String)session.getAttribute("user");
String userFirstname = (String)session.getAttribute("firstname");
String userRole = (String)session.getAttribute("role");
if(userParam==null) {
        response.sendRedirect("/TOPS/login.jsp");
        return;
}
user = userParam.toLowerCase();
//logger.debug("index.jsp: logged in for user=" + user);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="./css/tops.css">
	<link rel="stylesheet" type="text/css" href="./css/flexigrid.css">
	
	<%-- <script src="http://code.jquery.com/jquery-latest.js"></script> --%>
	<%--  TODO 	for prod switch to min.js version and 
				see https://developers.google.com/speed/libraries/devguide for CDN --%>
	<script type="text/javascript" src="./js/jquery.js"></script>
	<script type="text/javascript" src="./js/flexigrid.js"></script>
<title>TOPS membership database</title>

<%--
	=== This part supports bounces.jsp ===  
	   Need to use jQuery Form Plugin here because IE does not handle XR2 yet.
	   See function call below.
	   See http://stackoverflow.com/questions/9395911/sending-a-file-as-multipart-through-xmlhttprequest
	   
	   <script src="http://malsup.github.com/jquery.form.js"></script>
 --%>
 	<script type="text/javascript" src="./js/jquery.form.js"></script>
	
</head>
<body onload="showFlexigrid();">
<h1>TOPS membership database</h1>
<h5><%= application.getInitParameter("version") %></h5><h4 id="userid">[&nbsp;<%= userFirstname %>,&nbsp;<%= userRole %>&nbsp;]</h4>
<div id="menu">
<span id="members" class="button">Members</span>
<span id="chunks" class="button">Chunks</span>
<span id="bounces" class="button">Bounces</span>
<span id="reports" class="button">Reports</span>
<!-- <span class="spacer"></span> -->
<% if(user.equals("admin")) { %>
<span id="users" class="adminbutton">Users</span>
<span id="backup" class="adminbutton">Back-up</span>
<% } %>
<span id="logout">Logout</span>
</div>
<div id="flexigrid"><!-- Flexigrid here style="visibility:hidden" --></div>
<div id="content" style="visibility:hidden"></div>


<%-- logger.info("Page loaded"); --%>

<!-- 
============= jQuery scripts to load data in the '#content' div
 -->
<script>
function showContentDiv() {
	$('.flexigrid').hide(); 
	document.getElementById("content").style.visibility="visible";
	document.getElementById("content").innerHTML = "<h3>Loading...</h3>";
}
	
$(function() {
    $('#members').click(function() {
    	showFlexigrid()
        return false;
    });
    $('#chunks').click(function() { 
    	showContentDiv();
    	$("#content").load("do/chunks.jsp");	// to copy email addresses by small chunks to facilitate mass mailing
        return false;
    });
    $('#bounces').click(function() { // to clear up the database from bounced emails/members
    	showContentDiv();
    	$("#content").load("do/bounces.jsp", function(response, status, xhr) {
    		if (status == "error") {
    			    var msg = "Sorry but there was an error: ";
    			    $("#error").html(msg + xhr.status + " " + xhr.statusText);
    		}
    		setupBouncesForm(); // bind the form to jQuery form plugin for AJAX call
    	});	
        return false;
    });
    $('#reports').click(function() {
    	showContentDiv();
    	$("#content").load("do/reports.jsp");	// to provide reports such as by chapters
        return false;
    });
    $('#users').click(function() {
    	showContentDiv();						// load another flexigrid (for app users) in flexigrid div
    	$("#content").load("admin/users.jsp");
        return false;
    });
    $('#backup').click(function() {
    	showContentDiv();
    	$("#content").load("BackupServlet");	// dump SQL + back-up DB
        return false;
    });
    $('#logout').click(function() {
    	window.location="do/login/logout.jsp";	// log user out and back to login page
        return false;
    });
    
    $("#flexigrid").flexigrid({
		url: './FlexiGridServlet',
		dataType: 'json',
    	colModel : [
    		{display: 'First name', name : 'firstname', width : 100, sortable : true, align: 'left'},
    		{display: 'Last name', name : 'lastname', width : 100, sortable : true, align: 'left'},
    		{display: 'Job title', name : 'jobtitle', width : 100, sortable : true, align: 'left'},
    		{display: 'Branch', name : 'branch', width : 100, sortable : true, align: 'left'}, 
    		{display: 'Ministry', name : 'ministry', width : 100, sortable : true, align: 'left'},
    		{display: 'City', name : 'city', width : 50, sortable : true, align: 'left'},
    		{display: 'Phone', name : 'phone', width : 70, sortable : true, align: 'left'},
    		{display: 'Email', name : 'email', width : 150, sortable : true, align: 'left'},
    		{display: 'Heard from', name : 'heardfrom', width : 50, sortable : true, align: 'left'},
    		{display: 'Joined on', name : 'creatdate', width : 100, sortable : true, align: 'center'},
    		{display: 'Chapter', name : 'chapter', width : 70, sortable : true, align: 'left'},
    		{display: 'Newsflash', name : 'newsflash', width : 40, sortable : true, align: 'center'},
    		{display: 'TOPSpot', name : 'topspot', width : 40, sortable : true, align: 'center'},
    		{display: 'Left on', name : 'leftdate', width : 70, sortable : true, align: 'left'},
    		{display: 'Rationale', name : 'leftwhy', width : 70, sortable : true, align: 'left'}
    		],
    	buttons : [
    		{name: 'Add', bclass: 'add', onpress : test},
    		{name: 'Delete', bclass: 'delete', onpress : test},
    		{name: 'Edit', bclass: 'edit', onpress : test},
    		{name: 'Save', bclass: 'save', onpress : test},
    		{separator: true}
    		],
    	searchitems : [
    		{display: 'First name', name : 'firstname'},
    		{display: 'Last name', name : 'lastname', isdefault: true},
    		{display: 'Chapter', name : 'chapter'},
    		{display: 'Left on (YYYY-MM-DD)', name : 'leftdate'}
    		],
    	sortname: "lastname",
    	sortorder: "asc",
    	usepager: true,
    	title: 'TOPS members',
    	useRp: true,
    	rp: 30,
//    	showTableToggleBtn: true,
//    	width: 700,
		width: 'auto',
    	height: 450
//    	height: 'auto'
    });   
    
});


function showFlexigrid() {
	$("#flexigrid").flexReload();
	$('.flexigrid').show(); 
	document.getElementById("flexigrid").style.visibility="visible"; 
	document.getElementById("content").style.visibility="hidden";
	$("#flexigrid").flexReload();
}

var userRole = "<%= userRole %>";

function test(com, grid) {
	if (com == 'Delete') {
		if(userRole.indexOf("Guest") != -1) {
			alert("Sorry, guest users have read-only access!");
			return;
		}			
		//confirm('Delete ' + $('.trSelected', grid).length + ' items?')
		if($('.trSelected', grid).length == 0) {
			alert("Select row(s) for deletion");
		}
		 var items = $('.trSelected',grid); 
		 for(i=0;i<items.length;i++) { 
           remove(items[i]);
         } 
	} else if (com == 'Add') {
		if(userRole.indexOf("Guest") != -1) {
			alert("Sorry, guest users have read-only access!");
			return;
		}			
		showContentDiv();
    	$("#content").load("do/addmember.jsp");	// to add a new member
    	
	} else if (com == "Edit") {
		if(userRole.indexOf("Guest") != -1) {
			alert("Sorry, guest users have read-only access!");
			return;
		}			
		if($('.trSelected', grid).length == 0) {
			alert("Select 1 row for edition");
		}else{
			var row =  $('.trSelected',grid)[0];
			var firstname = $('td[abbr="firstname"] >div', row).html();
			var lastname = $('td[abbr="lastname"] >div', row).html();
			var jobtitle = $('td[abbr="jobtitle"] >div', row).html();
			var branch = $('td[abbr="branch"] >div', row).html();
			var ministry = $('td[abbr="ministry"] >div', row).html();
			var city = $('td[abbr="city"] >div', row).html();
			var phone = $('td[abbr="phone"] >div', row).html();
			var email = $('td[abbr="email"] >div', row).html();
			var heardfrom = $('td[abbr="heardfrom"] >div', row).html();
			var creatdate = $('td[abbr="creatdate"] >div', row).html();
			var chapter = $('td[abbr="chapter"] >div', row).html();
			var leftdate = $('td[abbr="leftdate"] >div', row).html();
			var leftwhy = $('td[abbr="leftwhy"] >div', row).html();
			var newsflash = $('td[abbr="newsflash"] >div', row).html();
			var topspot = $('td[abbr="topspot"] >div', row).html();

	    	showContentDiv();
	    	var queryStg = "?firstname=" + firstname + "&lastname=" + lastname
			+ "&jobtitle=" + jobtitle + "&branch=" + branch + "&ministry=" + ministry
			+ "&city=" + city + "&phone=" + phone + "&email=" + email + "&heardfrom=" + heardfrom
			+ "&creatdate=" + creatdate + "&chapter=" + chapter
			+ "&leftdate=" + leftdate + "&leftwhy=" + leftwhy
			+ "&newsflash=" + newsflash + "&topspot=" + topspot;
	    	//alert(queryStg);
			$("#content").load("do/editmember.jsp" + encodeURI(queryStg));
		}
		
	}else if (com = 'Save') {
		window.location = "SaveGridServlet";
	}
}
function remove(row) {	// TODO use jQuery instead
	if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
  		xmlhttp=new XMLHttpRequest();
  	}else{// code for IE6, IE5
  		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
  	}

	xmlhttp.onreadystatechange=function() {
  		if (xmlhttp.readyState==4 && xmlhttp.status==200) {
  			if(xmlhttp.responseText.indexOf("guest") != -1)
    			alert(xmlhttp.responseText); 
  			else
  				deleteRow(row);
    		//alert("User deleted");
    	}
  	};
	
	var email=$('td[abbr="email"] >div', row).html();
	if(email != "&nbsp;") { // too many users with blank emails: see other approach below for those
		//alert("remotely deleting " + email);
		xmlhttp.open("GET","/TOPS/RemoveMemberServlet?email=" + email,true);
		xmlhttp.send();
		
	}else { // for users with blank email: try by name
		var firstname = $('td[abbr="firstname"] >div', row).html();
		var lastname = $('td[abbr="lastname"] >div', row).html();
		//alert("remotely deleting " + firstname + " " + lastname);
		xmlhttp.open("GET","/TOPS/RemoveMemberServlet?firstname=" + firstname + "&lastname=" + lastname,true);
		xmlhttp.send();
	}
}
function deleteRow(row) {
	row.parentNode.removeChild(row);
}

function unregisterMember(email, cell) {
	alert("Please confirm: unregistering member with email '" + email + "'?");
	$.get("/TOPS/RemoveMemberServlet?email=" + email, function() {
		var row = cell.parentNode.parentNode;
		deleteRow(row);
		console.log("removed user with email: " + email);
	});
}

<%--Mechanism to support bounces.jsp ; see also header above 
    and http://malsup.com/jquery/form/#ajaxForm (plugin GPL-licensed https://github.com/malsup/form/)--%>
function setupBouncesForm() { 
	var options = { 
        target:        '#emailstatuswrapper',   // target element(s) to be updated with server response 
        //beforeSubmit:  showRequest,  // pre-submit callback 
        //success:       showResponse  // post-submit callback 
 
        // other available options: 
        //url:       url         // override for form's 'action' attribute 
        //type:      type        // 'get' or 'post', override for form's 'method' attribute 
        //dataType:  null        // 'xml', 'script', or 'json' (expected server response type) 
        //clearForm: true        // clear all form fields after successful submit 
        //resetForm: true        // reset the form after successful submit 
 
        // $.ajax options can be used here too, for example: 
        //timeout:   3000 
    };    
	
	// bind to the form's submit event 
    $('#emlUploadForm').submit(function() { 
    	// inside event callbacks 'this' is the DOM element so we first 
        // wrap it in a jQuery object and then invoke ajaxSubmit 
        $(this).ajaxSubmit(options); 
         
        // !!! Important !!! 
        // always return false to prevent standard browser submit and page navigation 
        return false; 
    });  
    //debugging: alert('Form bound: ' + document.getElementById("emlUploadForm") );
} 

// pre-submit callback (for debugging)
function showRequest(formData, jqForm, options) { 
    // formData is an array; here we use $.param to convert it to a string to display it 
    // but the form plugin does this for you automatically when it submits the data 
    var queryString = $.param(formData); 
 
    // jqForm is a jQuery object encapsulating the form element.  To access the 
    // DOM element for the form do this: 
    // var formElement = jqForm[0]; 
 
    alert('About to submit: \n\n' + queryString); 
 
    // here we could return false to prevent the form from being submitted; 
    // returning anything other than false will allow the form submit to continue 
    return true; 
} 

// post-submit callback 
function showResponse(responseText, statusText, xhr, $form)  { 
    // for normal html responses, the first argument to the success callback 
    // is the XMLHttpRequest object's responseText property 
 
    // if the ajaxForm method was passed an Options Object with the dataType 
    // property set to 'xml' then the first argument to the success callback 
    // is the XMLHttpRequest object's responseXML property 
 
    // if the ajaxForm method was passed an Options Object with the dataType 
    // property set to 'json' then the first argument to the success callback 
    // is the json data object returned by the server 
 
    alert('status: ' + statusText + '\n\nresponseText: \n' + responseText + 
        '\n\nThe output div should have already been updated with the responseText.'); 
} 
</script>
</body>
</html>