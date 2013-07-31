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
private static Logger logger = Logger.getLogger(reports_jsp.class);
%>
<%@ page session="true" %>
<%
if(session.getAttribute("user")==null) {
	//logger.debug("reports.jsp: user = " + session.getAttribute("user"));
	logger.warn("bounced unlogged user to login page");
	response.sendRedirect("/TOPS/login.jsp");
	return;
}
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<h2 class="title">Reports</h2>
<p class="smallwarning">(Upcoming feature... can include tables and diagrams... to be defined as needed...)</p>
<ul>
	<li>health check report on emails (e.g. invalid addressed, bounced emails)</li>
	<li>members out of sync (based on authoritative sources such as INFO-GO and/or Corporate Active Directory)
	<li>list of members by chapter</li>
	<li>etc</li>
</ul>
