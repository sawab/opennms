<%--

//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2002-2008 The OpenNMS Group, Inc.  All rights reserved.
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
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="org.opennms.netmgt.config.*"%>
<%@page import="org.opennms.netmgt.config.users.*"%>
<%

        HttpSession userSession = request.getSession(false);
        User user = null;
        String userid = "";
        UserManager userFactory;
        try {
            UserFactory.init();
            userFactory = UserFactory.getInstance();
        } catch (Exception e) {
            throw new ServletException("UserFactory:modify() " + e);
        }

        if (userSession != null) {
            user = (User) userSession.getAttribute("user.modifyUser.jsp");
            userid = user.getUserId();
        }

        %>

<jsp:include page="/includes/header.jsp" flush="false" >
  <jsp:param name="title" value="Modify User" />
  <jsp:param name="headTitle" value="Modify" />
  <jsp:param name="headTitle" value="Users" />
  <jsp:param name="headTitle" value="Admin" />
  <jsp:param name="breadcrumb" value="<a href='admin/index.jsp'>Admin</a>" />
  <jsp:param name="breadcrumb" value="<a href='admin/userGroupView/index.jsp'>Users and Groups</a>" />
  <jsp:param name="breadcrumb" value="<a href='admin/userGroupView/users/list.jsp'>User List</a>" />
  <jsp:param name="breadcrumb" value="Modify User" />
</jsp:include>

<script language="Javascript" type="text/javascript" >
    
    function validate()
    {
        for (var c = 0; c < document.modifyUser.dutySchedules.value; c++)
        {
            var beginName= "duty" + c + "Begin";
            var endName  = "duty" + c + "End";
            
            var beginValue = new Number(document.modifyUser.elements[beginName].value);
            var endValue = new Number(document.modifyUser.elements[endName].value);
            
            if (!document.modifyUser.elements["deleteDuty"+c].checked)
            {
            if (isNaN(beginValue))
            {
                alert("The begin time of duty schedule " + (c+1) + " must be expressed in military time with no other characters, such as 800, not 8:00");
                return false;
            }
            if (isNaN(endValue))
            {
                alert("The end time of duty schedule " + (c+1) + " must be expressed in military time with no other characters, such as 800, not 8:00");
                return false;
            }
            if (beginValue > endValue)
            {
                alert("The begin value for duty schedule " + (c+1) + " must be less than the end value.");
                return false;
            }
            if (beginValue < 0 || beginValue > 2359)
            {
                alert("The begin value for duty schedule " + (c+1) + " must be greater than 0 and less than 2400");
                return false;
            }
            if (endValue < 0 || endValue > 2359)
            {
                alert("The end value for duty schedule " + (c+1) + " must be greater than 0 and less than 2400");
                return false;
            }
            }
        }
        return true;
    }

    function resetPassword()
    {
        newUserWin = window.open("<%=org.opennms.web.Util.calculateUrlBase(request)%>/admin/userGroupView/users/newPassword.jsp", "", "fullscreen=no,toolbar=no,status=no,menubar=no,scrollbars=yes,resizable=yes,directories=no,location=no,width=500,height=300");
    }

    function addDutySchedules()
    {
        var ok = validate();

        if(ok)
        {
          document.modifyUser.redirect.value="/admin/userGroupView/users/addDutySchedules";
          document.modifyUser.action="admin/userGroupView/users/updateUser";
          document.modifyUser.submit();
        }
    }
    
    function removeDutySchedules()
    {
        var ok = validate();
        
        if(ok)
        {
          document.modifyUser.redirect.value="/admin/userGroupView/users/modifyUser.jsp";
          document.modifyUser.action="admin/userGroupView/users/updateUser";
          document.modifyUser.submit();
        }
    }
    
    function saveUser()
    {
        var ok = validate();

        if(ok)
        {
          document.modifyUser.redirect.value="/admin/userGroupView/users/saveUser";
          document.modifyUser.action="admin/userGroupView/users/updateUser";
          document.modifyUser.submit();
        }
    }
    
    function cancelUser()
    {
        document.modifyUser.action="admin/userGroupView/users/list.jsp";
        document.modifyUser.submit();
    }
    
</script>


<form id="modifyUser" method="post" name="modifyUser">
<input id="userID" type="hidden" name="userID" value="<%=user.getUserId()%>"/>
<input id="password" type="hidden" name="password"/>
<input id="redirect" type="hidden" name="redirect"/>

<h3>Modify User: <%=userid%></h3>

<div id="contentleft">
  <p>
    <input type="button" value="Reset Password" onClick="resetPassword()" />
  </p>

    <table width="100%" border="0" cellspacing="0" cellpadding="2">
            <tr>
              <td colspan="2">
                <p><b>User Information</b></p>
              </td>
            </tr>
	    <%
	    String email = null;
        String pagerEmail = null;
        String xmppAddress = null;
        String numericPage = null;
        String numericPin = null;
        String textPage = null;
        String textPin = null;
        String fullName = null;
        String comments = null;
        Boolean isReadOnly = false;
        try {
            User usertemp = userFactory.getUser(userid);
            if (usertemp != null) {
                    email = userFactory.getEmail(userid);
                    pagerEmail = userFactory.getPagerEmail(userid);
                    xmppAddress = userFactory.getXMPPAddress(userid);
                    numericPage = userFactory.getNumericPage(userid);
                    numericPin = userFactory.getNumericPin(userid);
                    textPage = userFactory.getTextPage(userid);
                    textPin = userFactory.getTextPin(userid);
            } else {
                    Contact[] contact = user.getContact();
                    for (int i = 0; i < contact.length; i++) {
                            Contact tempContact = contact[i];
                            if (contact[i].getType().equals("email")) {
                                    email = contact[i].getInfo();
                            } else if (contact[i].getType().equals("pagerEmail")) {
                                    pagerEmail = contact[i].getInfo();
                            } else if (contact[i].getType().equals("xmppAddress")) {
                                    xmppAddress = contact[i].getInfo();
                            } else if (contact[i].getType().equals("numericPage")) {
                                    numericPage = contact[i].getInfo();
                            } else if (contact[i].getType().equals("textPage")) {
                                    textPage = contact[i].getInfo();
                            }
                    }
            }
            fullName = user.getFullName();
            comments = user.getUserComments();
            isReadOnly = user.isReadOnly();
        } catch (org.exolab.castor.xml.MarshalException e) {
            throw new ServletException("An Error occurred reading the users file", e);
        } catch (org.exolab.castor.xml.ValidationException e) {
            throw new ServletException("An Error occurred reading the users file", e);
        }

        %>
            <tr>
              <td valign="top">
                <label id="readOnlyLabel" for="readOnly">Read-Only:</label>
              </td>
              <td align="left" valign="top">
                <input id="readOnly" type="checkbox" name="readOnly"<%=  isReadOnly? " checked=\"true\"":"" %> />
              </td>
            </tr>
            <tr>
              <td valign="top">
                <label id="fullNameLabel" for="fullName">Full Name:</label>
              </td>
              <td align="left" valign="top">
                <input id="fullName" type="text" size="35" name="fullName" value="<%=(fullName == null ? "":fullName) %>" />
              </td>
            </tr>
            <tr>
              <td valign="top">
                <label id="userCommentsLabel" for="userComments">Comments:</label>
              </td>
              <td align="left" valign="top">
                <textarea rows="5" cols="33" id="userComments" name="userComments"><%=(comments == null ? "" : comments)%></textarea>
              </td>
            </tr>
            <tr>
              <td colspan="2">
                &nbsp;
              </td>
            </tr>
            <tr>
              <td colspan="2">
                <p><b>Notification Information</b></p>
              </td>
            </tr>
            <tr>
              <td valign="top">
                <label id="emailLabel" for="email">Email:</label>
              </td>
              <td valign="top">
                <input id="email" type="text" size="35" name="email" value='<%= (email == null ? "":email) %>'/>
              </td>
            </tr>
            <tr>
              <td valign="top">
                <label id="pemailLabel" for="pemail">Pager Email:</label>
              </td>
              <td valign="top">
                <input type="text" size="35" id="pemail" name="pemail" value='<%=(pagerEmail == null ? "":pagerEmail)%>'/>
              </td>
            </tr>
            <tr>
              <td valign="top">
                <label id="xmppAddressLabel" for="xmppAddress">XMPP Address:</label>
              </td>
              <td valign="top">
                <input id="xmppAddress" type="text" size="35" name="xmppAddress" value='<%=(xmppAddress == null ? "":xmppAddress)%>'/>
              </td>
            </tr>
            <tr>
              <td valign="top">
                <label id="numericalServiceLabel" for="numericalService">Numeric Service:</label>
              </td>
              <td valign="top">
                <input type="text" size="35" id="numericalService" name="numericalService" value='<%=(numericPage == null ? "":numericPage) %>'/>
              </td>
            </tr>
            <tr>
              <td valign="top">
                <label id="numericalPinLabel" for="numericalPin">Numeric PIN:</label>
              </td>
              <td valign="top">
                <input type="text" size="35" id="numericalPin" name="numericalPin" value='<%= (numericPin == null ? "":numericPin)%>'/>
              </td>
            </tr>
            <tr>
              <td valign="top">
                <label id="textServiceLabel" for="textService">Text Service:</label>
              </td>
              <td valign="top">
                <input type="text" size="35" id="textService" name="textService" value='<%= (textPage == null ? "":textPage)%>'/>
              </td>
            </tr>
            <tr>
              <td valign="top">
                <label id="textPinLabel" for="textPin">Text PIN:</label>
              </td>
              <td valign="top">
                <input type="text" size="35" id="textPin" name="textPin" value='<%=(textPin == null ? "":textPin)%>'/>
              </td>
            </tr>
          </table>
</div>

<div id="contentright">
  <p>
    This panel allows you to modify information for each user, including
    their name, notification information, and duty schedules.
  </p>

  <p>
    <b>Notification Information</b> provides the ability for you to configure
    contact information for each user, including any of <em>email</em>
    address, <em>pager email</em> (in the case that the pager can be reached
    as an email destination), <em>XMPP address</em> (for instant messages
    using the Jabber XMPP protocol), <em>numeric service</em> (for pagers
    that cannot display text messages), and <em>text service</em> (for
    alphanumeric pagers).
  </p>

  <p>
    <b>Duty Schedules</b> allow you to flexibility to determine when users
    should receive notifications.  A duty schedule consists of a list of
    days for which the time will apply and a time range, presented in
    military time with no punctuation.  Using this standard, days run from
    <em>0000</em> to <em>2359</em>.
  </p>

  <p>
    If your duty schedules span midnight, or if your users work multiple,
    non-contiguous time periods, you will need to configure multiple duty
    schedules.  To do so, select the number of duty schedules to add from
    the drop-down box next to <b>[Add This Many Schedules]</b>, and click
    the button.  Then, using the duty schedule fields you've just added,
    create a duty schedule from the start time to 2359 on one day, and
    enter a second duty schedule which begins at 0000 and ends at the end
    of that users coverage.
  </p>

  <p>
    To remove configured duty schedules, put a check in the <em>Delete</em>
    column and click <b>[Remove Checked Schedules]</b>.
  </p>

  <p>
   To save your configuration, click on <b>[Finish]</b>.
  </p>
</div>

<div class="spacer"><!-- --></div>

<p>
  <b>Duty Schedules</b>
</p>
                                  <%
Collection dutySchedules = user.getDutyScheduleCollection();
        %>
				<input type="hidden" name="dutySchedules" value="<%=user.getDutyScheduleCount()%>"/>
          
          <table width="100%" border="1" cellspacing="0" cellpadding="2" >
            <tr bgcolor="#999999">
              <td>&nbsp;</td>
              <td><b>Delete</b></td>
              <td><b>Mo</b></td>
              <td><b>Tu</b></td>
              <td><b>We</b></td>
              <td><b>Th</b></td>
              <td><b>Fr</b></td>
              <td><b>Sa</b></td>
              <td><b>Su</b></td>
              <td><b>Begin Time</b></td>
              <td><b>End Time</b></td>
            </tr>
                        <%
int i = 0;
        Iterator iter = dutySchedules.iterator();
        while (iter.hasNext()) {
            DutySchedule tmp = new DutySchedule((String) iter.next());
            Vector curSched = tmp.getAsVector();

            %>
                        <tr>
                          <td width="1%"><%=(i + 1)%></td>
                          <td width="1%">
                            <input type="checkbox" name="deleteDuty<%=i%>"/>
                          </td>
                          <%ChoiceFormat days = new ChoiceFormat("0#Mo|1#Tu|2#We|3#Th|4#Fr|5#Sa|6#Su");
            for (int j = 0; j < 7; j++) {
                Boolean curDay = (Boolean) curSched.get(j);

                %>
                          <td width="5%">
                            <input type="checkbox" name="duty<%=i+days.format(j)%>" <%= (curDay.booleanValue() ? "checked=\"true\"" : "")%>/>
                          </td>
                          <%}
            %>
                          <td width="5%">
                            <input type="text" size="4" name="duty<%=i%>Begin" value="<%=curSched.get(7)%>"/>
                          </td>
                          <td width="5%">
                            <input type="text" size="4" name="duty<%=i%>End" value="<%=curSched.get(8)%>"/>
                          </td>
                        </tr>
                        <%i++;
        }
        %>
          </table>

<p>
  <input id="addSchedulesButton" type="button" name="addSchedule"
         value="Add This Many Schedules" onclick="addDutySchedules()"/>

  <select name="numSchedules" value="3" size="1">
        <option value="1">1</option>
        <option value="2">2</option>
        <option value="3">3</option>
        <option value="4">4</option>
        <option value="5">5</option>
        <option value="6">6</option>
        <option value="7">7</option>
  </select>
</p>

<p>
  <input id="removeSchedulesButton" type="button" name="addSchedule"
         value="Remove Checked Schedules" onclick="removeDutySchedules()"/>
</p>

<p>
  <input id="saveUserButton" type="submit" name="finish" value="Finish"
         onclick="saveUser()"/>
  &nbsp;&nbsp;&nbsp;
  <input id="cancelButton" type="button" name="cancel" value="Cancel"
         onclick="cancelUser()"/>
</p>

</form>

<jsp:include page="/includes/footer.jsp" flush="false" />
