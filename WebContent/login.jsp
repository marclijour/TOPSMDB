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
private static Logger logger = Logger.getLogger(login_jsp.class);
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

	<link rel="stylesheet" type="text/css" href="./css/tops.css" />
	<link rel="stylesheet" type="text/css" href="./css/login.css" />
	<%-- <script src="http://code.jquery.com/jquery-latest.js"></script> --%>
	<script type="text/javascript" src="./js/jquery.js"></script>
<!-- ..//JavaScript Code for this page\\.. 
		inspired from http://wowjava.wordpress.com/2010/10/22/ajax-login-validation-for-jsp/ -->
<script type="text/javascript">
String.prototype.contains = function(it) { return this.indexOf(it) != -1; };
$(document).ready(function(){
$("#login_frm").submit(function(){

//remove previous class and add new "myinfo" class
$("#msgbox").removeClass().addClass('myinfo').text('Validating Your Login ').fadeIn(1000);

this.timer = setTimeout(function () {
$.ajax({
url: '/TOPS/do/login/CheckLoginCredentialsServlet',
data: encodeURI('un='+ $('#login_id').val() +'&pw=' + $('#password').val()),
type: 'post',
success: function(msg){
if(!msg.contains('ERROR')) // Message Sent, check and redirect
{                // and direct to the success page

$("#msgbox").html('Login Verified, Logging in.....').addClass('myinfo').fadeTo(900,1,
function()
{
//redirect to secure page
	//document.location='/TOPS/do/login/loggingin.jsp?user='+msg;
	document.location='/TOPS/';
});

} else {
	$("#msgbox").fadeTo(200,0.1,function() //start fading the messagebox
	{
			//add message and change the class of the box and start fading
			$(this).html('Sorry, Wrong Combination Of Username And Password.').removeClass().addClass('myerror').fadeTo(900,1);
	});
}
}

});
}, 200);
return false;
});

});

</script>

<title>Login to TOPS</title>
</head>
<body>

<form name="login_frm" id="login_frm" action="" method="post">
    <div id="login_box">
      <div id="login_header">
            Login
      </div>
      <div id="form_val">
        <div class="label">User Id :</div>
        <div class="control"><input type="text" name="login_id" id="login_id"/><span style="font-size: 10px;"></span></div>
        
        <div class="label">Password:</div>
        <div class="control"><input type="password" name="password" id="password"/><span style="font-size: 10px;"></span></div>
        <div style="clear:both;height:0px;"></div>
      
      	<div id="msgbox"></div>
      </div>
      <div id="login_footer">
        <label>
        <input type="submit" name="login" id="login" value="Login" class="send_button" />
        </label>
      </div>
    </div>

</form>

</body>
</html>
