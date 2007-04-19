<%@ taglib prefix="t" uri="http://jakarta.apache.org/struts/tags-tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if
	test="${empty sessionScope.LoginBean.mode or !sessionScope.LoginBean.mode}">
	<t:insert definition=".main" />
</c:if>
<c:if
	test="${sessionScope.LoginBean.mode && sessionScope.LoginBean.role}">
	<t:insert definition=".groupForm" />
</c:if>
