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
private static Logger logger = Logger.getLogger(users_jsp.class);
%>
<%@ page session="true" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% // TODO revise this (embedded HTML code)
String user = (String)session.getAttribute("user");
if(user == null) {
	logger.debug("admin/users.jsp: user = null");
	response.sendRedirect("/TOPS/login.jsp");
	return;
}

String role = (String)session.getAttribute("role");
if(role == null || !role.toLowerCase().equals("admin")) { // this should not happen
	logger.warn("Non-admin user " + user + " attempted to manage users");
	// could be made more painful by logging the user out (punishment for working around JS)
%>	
<h1>ERROR</h1>
Please log in as Admin.
<% } else { %>	
<h2 class='title'>Authorized Users</h2>
<p class='intro'>
Click on selected row to edit/delete users.
</p>

<span class="users">New user:</span><input class="users" type="submit" value="Add" onclick="addUser()"/>

<%-- These libraries are required for the <c> and <sql> tags --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %> 

<sql:query var="result" dataSource="jdbc/topsDB">
        SELECT * FROM dbuser ORDER BY login
</sql:query>
    
<table id="users">
        <tr>
            <c:forEach var="columnName" items="${result.columnNames}">
                <th onclick="sort(this)"><c:out value="${columnName}"/></th>
            </c:forEach>
        </tr>

        <%-- Output each row of data --%>
        <c:forEach var="row" items="${result.rowsByIndex}">
            <tr onclick="manageUser(this);">
                <%-- Output each column of data --%>
                <c:forEach var="col" items="${row}">
                    <td><c:out value="${col}"/></td>
                </c:forEach>
            </tr>
        </c:forEach>
    </table>

<script>
function manageUser(row) {						// to edit a new member
	$("#content").load("admin/edituser.jsp?login=" + row.cells[0].innerHTML
				+ "&email=" + escape(row.cells[1].innerHTML)
				+ "&password=" + escape(row.cells[2].innerHTML)
				+ "&role=" + escape(row.cells[3].innerHTML));	
	return false;
}
function addUser() {
	$("#content").load("admin/adduser.jsp");	// to add a new member
}
function sort(col) {
	alert("Upcoming sorting feature, stay tuned!");
}
</script>
<% } %>
