<%--

//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2002-2003 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified 
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Modifications:
//
// 2003 Feb 07: Fixed URLEncoder issues.
// 2002 Nov 26: Fixed breadcrumbs issue.
// 
// Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//
// For more information contact:
//      OpenNMS Licensing       <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com/
//

--%>

<%@page language="java"
	contentType="text/html"
	session="true"
%>

<jsp:include page="/includes/header.jsp" flush="false" >
  <jsp:param name="title" value="Restart Pollers" />
  <jsp:param name="headTitle" value="Restart Pollers" />
  <jsp:param name="headTitle" value="Configure Pollers" />
  <jsp:param name="headTitle" value="Admin" />
  <jsp:param name="location" value="admin" />
  <jsp:param name="breadcrumb" value="<a href='admin/index.jsp'>Admin</a>" />
  <jsp:param name="breadcrumb" value="<a href='admin/pollerConfig/index.jsp'>Configure Pollers</a>" />
  <jsp:param name="breadcrumb" value="Restart Pollers" />
</jsp:include>

<h3>The Pollers Need to be Restarted for the Changes to Take Effect</h3>

<p>
  Please click the &quot;Restart Pollers&quot; button below to have the
  pollers read the new configuration. If you want to make more poller
  configuration changes, please revisit the
  <a href="admin/pollerConfig/index.jsp">Configure Pollers</a> page.
</p>

<form method="post" action="admin/pollerConfig/finishPollerConfig">
  <input type="submit" value="Restart Pollers"/>
</form>

<jsp:include page="/includes/footer.jsp" flush="true"/>
