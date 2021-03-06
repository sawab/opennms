<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<jsp:include page="/includes/header.jsp" flush="false">
	<jsp:param name="title" value="Site Status" />
	<jsp:param name="headTitle" value="Site Status Error" />
	<jsp:param name="breadcrumb" value="Site Status" />
</jsp:include>

<h3>Site Status View Error: ${error.shortDescr}</h3>

<p>
${error.longDescr}
</p>

<jsp:include page="/includes/footer.jsp" flush="false" />
