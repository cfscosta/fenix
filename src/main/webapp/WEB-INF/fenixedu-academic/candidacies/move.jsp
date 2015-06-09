<%--

    Copyright © 2002 Instituto Superior Técnico

    This file is part of FenixEdu Academic.

    FenixEdu Academic is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FenixEdu Academic is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

${portal.toolkit()}

<spring:url var="actionUrl" value="${action}${moveCandidacyBean.candidacy.externalId}"/>
<div class="page-header">
	<h1>
		<spring:message code="label.candidacy.move.candidate" />
	</h1>
</div>
<h4><spring:message code="label.candidacy.choose.new.candidacy"/></h4>

<form:form modelAttribute="moveCandidacyBean" role="form" method="post" action="${actionUrl}" enctype="multipart/form-data">
<form:input type="hidden" class="form-control" id="candidacy" path="candidacy" value="${moveCandidacyBean.candidacy.externalId}"/>
<form:select class="form-control" id="candidacyProcess" path="candidacyProcess">
	<c:forEach var="asd" items="${processes}">
	<form:option value="${asd.externalId}">
		<c:out value="${asd.candidacyPeriod.presentationName}"/>
	</form:option>
	</c:forEach>
</form:select>
<p>
<button type="submit" class="btn btn-primary"><spring:message code="label.submit"/></button>
</p>
</form:form>