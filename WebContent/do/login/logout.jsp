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
<%@ page import="org.apache.log4j.Logger,java.util.Date" session="true" %>
<%!
private static Logger logger = Logger.getLogger(logout_jsp.class);
%>
<%
logger.info("logout.jsp: logging out user " + session.getAttribute("user"));
session.setAttribute("user", null);
session.setAttribute("role", null);
session.setAttribute("firstname", null);
session.invalidate();
response.sendRedirect("/TOPS/login.jsp");
%>
