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
<%@ page import="org.apache.log4j.Logger,java.sql.*" %>
<%!
	private static Logger logger = Logger.getLogger(chunks_jsp.class);
%>
<%@ page session="true" %>
<%
final String deployDir = application.getInitParameter("deploy-dir");
if(!deployDir.contains("TOPS"))
	logger.warn("The deploy directory (" + deployDir + " does not contain \"TOPS\"");
	
	if(session.getAttribute("user")==null) {
		//logger.debug("chunks.jsp: user = " + session.getAttribute("user"));
		logger.warn("bounced unlogged user to login page");
		response.sendRedirect("/" + deployDir + "/login.jsp");
		return;
	}		
%>
<%-- These libraries are required for the <c> and <sql> tags --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %> 
<sql:query var="result" dataSource="jdbc/topsDB">
        SELECT DISTINCT CHAPTER FROM city_chapter ORDER BY chapter
</sql:query>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<h2 class='title'>Email Chunks</h2>
<span class='chunks'>TOPS members:</span>
<select id='chapterlist' class='chunks' name="chapterlist" onchange="updateChunks(document.getElementById('input').value)">
	<option class='chunks' value="All" selected="selected">All chapters</option>
    <c:forEach var="row" items="${result.rowsByIndex}">
    	<c:forEach var="col" items="${row}">
	<option class='chunks' value='<c:out value="${col}"/>'><c:out value="${col}"/></option>
        </c:forEach>
    </c:forEach>
</select>
<span class='chunks'>Maximum size:</span><input id ="input" class="chunks" type="text" width="5" value="500" onchange="updateChunks(this.value)"/><input type="submit" onclick="updateChunks(document.getElementById('input').value)"/>
<div id="chunkswrapper"></div>
<script>
// on load
updateChunks(500);

function updateChunks(max) {
	if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
  		xmlhttp=new XMLHttpRequest();
  	}else{// code for IE6, IE5
  		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
  	}

	xmlhttp.onreadystatechange=function() {
  		if (xmlhttp.readyState==4 && xmlhttp.status==200) {
  			document.getElementById("chunkswrapper").innerHTML = xmlhttp.responseText;
    	}
  	}
	var chapter=document.getElementById("chapterlist").options[document.getElementById("chapterlist").selectedIndex].value;
	xmlhttp.open("GET","/<%= deployDir %>/ChunkServlet?chunksize=" + max + "&chapter=" + chapter,true); // chapter can be chapter or 'All'
	xmlhttp.send();
}
</script>
