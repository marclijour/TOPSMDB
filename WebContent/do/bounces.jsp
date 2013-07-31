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
private static Logger logger = Logger.getLogger(bounces_jsp.class);
%>
<%@ page session="true" %>
<%
if(session.getAttribute("user")==null) {
	//logger.debug("chunks.jsp: user = " + session.getAttribute("user"));
	logger.warn("bounced unlogged user to login page");
	response.sendRedirect("/TOPS/login.jsp");
	return;
}
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- the following content is loaded through AJAX into index.jsp --%>
<h2 class='title'>Bounces (beta)</h2>
<form id="emlUploadForm" action='LoadBouncesServlet' method='post' enctype='multipart/form-data'> 
	<h3 class='title'>Load Bounce report email</h3>
	<div class="fileupload">
		<p>Submit a file ending with .eml</p>
		<input class='load' id='filename' name='filename' type='file' />
		<input class='load' type='submit'/>
	</div>
</form>
<div id="emailstatuswrapper"></div>

