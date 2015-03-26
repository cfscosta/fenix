<%--

    Copyright © 2014 Instituto Superior Técnico

    This file is part of FenixEdu Spaces.

    FenixEdu Spaces is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FenixEdu Spaces is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu Spaces.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:url var="searchUrl" value="/spaces-view/search/"/>
<jsp:include page="/teacher/evaluation/evaluationMenu.jsp" />
<h2><spring:message code="title.evaluation.manage.marksListWithFile"/></h2>
<p>
	<h3>${executionCourse.nome}</h3>
</p>
<p>
<c:if test="${evaluation.getClass().name == 'org.fenixedu.academic.domain.WrittenTest'}">
	<spring:message code="label.written.test"/>:
	<b>${evaluation.description}</b>
	<fmt:formatDate pattern="dd/MM/yyyy" value="${evaluation.dayDate}"/>
	<spring:message code="label.at"/>
	<fmt:formatDate pattern="HH:mm" value="${evaluation.beginningDate}"/>
</c:if>
<c:if test="${evaluation.getClass().name == 'org.fenixedu.academic.domain.Exam'}">
	<spring:message code="label.exam"/>:
	<b>${evaluation.description}</b>
	<fmt:formatDate pattern="dd/MM/yyyy" value="${evaluation.dayDate}"/>
	<spring:message code="label.at"/>
	<fmt:formatDate pattern="HH:mm" value="${evaluation.beginningDate}"/>
</c:if>
</p>
<div class="alert alert-info" role="alert">
	<spring:message code="label.fileUpload.information"/>
	<c:if test="${!empty evaluation.gradeScale}">
		<c:set var="gradeScaleDescription" value="${evaluation.gradeScale.getPossibleValueDescription(evaluation.isFinal())}"/>
		<p><b><spring:message code="label.marksOnline.currentGradeScale"/></b> ${gradeScaleDescription}</p>
	</c:if>
</div>
<spring:url var="formActionUrl" value="${action}"/>

<div class="row">
	<div class="col-md-3">Username</div>
	<div class="col-md-3">Nota</div>
</div>
<form:form modelAttribute="gradeBean" role="form" method="post" action="${formActionUrl}" enctype="multipart/form-data">
	<c:forEach var="mark" items="${gradeBean.marks}">
		<div class="row">
			<div class="col-md-3">${mark.key}</div>
			<div class="col-md-3">${mark.value}</div>
			<form:input type="hidden" path="marks['${mark.key}']" value="${mark.value}"/>
		</div>
	</c:forEach>
	<button type="submit" class="btn btn-default">Submeter</button>
</form:form>
</div>
</div>
