<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="ServidorApresentacao.Action.sop.utils.SessionConstants, Util.CurricularCourseType" %>
<%@ page import="Util.EnrollmentState, DataBeans.InfoEnrolmentInOptionalCurricularCourse" %>

  <span class="error"><html:errors/></span>

  <bean:define id="curriculum" name="<%= SessionConstants.CURRICULUM %>" scope="request" />
  <bean:size id="enrolmentNumber" name="<%= SessionConstants.CURRICULUM %>" scope="request" />
  <bean:define id="student" name="<%= SessionConstants.STUDENT_CURRICULAR_PLAN %>" scope="request" />
  
  
  <bean:message key="label.person.name" />:
  <bean:write name="student" property="infoStudent.infoPerson.nome"/>
  </br>
  
  <bean:message key="label.degree.name" />:
  <bean:write name="student" property="infoDegreeCurricularPlan.infoDegree.nome"/>
  </br>

  <bean:message key="label.number" />:
  <bean:write name="student" property="infoStudent.number"/>
  </br>
  </br>
  
  
  
  <logic:notEqual name="enrolmentNumber" value="0">
	  <table>
	  	<tr>
		  	<td class="listClasses-header">
		  		<bean:message key="label.executionYear" />
		  	</td >
		  	<td class="listClasses-header">
		  		<bean:message key="label.semester" />
		  	</td >
		  	<td class="listClasses-header">
		  		<bean:message key="label.degree.name" />
		  	</td>
		  	<td class="listClasses-header">
		  		<bean:message key="label.curricular.course.name" />
		  	</td>
		  	<td class="listClasses-header">
		  		<bean:message key="label.finalEvaluation" />
		  	</td>
	  	</tr>
	  
	  	<logic:iterate id="enrolment" name="curriculum">
	  		<tr>
			  <td class="listClasses">
			    <bean:write name="enrolment" property="infoExecutionPeriod.infoExecutionYear.year"/>
			  </td>
			 
			  <td class="listClasses">
			    <bean:write name="enrolment" property="infoExecutionPeriod.semester"/>
			  </td>
			  <td class="listClasses">
			    <bean:write name="enrolment" property="infoCurricularCourse.infoDegreeCurricularPlan.infoDegree.sigla"/>
			  </td>
			  <td class="listClasses" style="text-align:left">
			    <bean:write name="enrolment" property="infoCurricularCourse.name"/>
				<logic:equal name="enrolment" property="infoCurricularCourse.type" value="<%= CurricularCourseType.OPTIONAL_COURSE_OBJ.toString() %>">
					<% if (pageContext.findAttribute("enrolment") instanceof InfoEnrolmentInOptionalCurricularCourse)
					   {%>
						<logic:notEmpty name="enrolment" property="infoCurricularCourseForOption">
							-&nbsp;<bean:write name="enrolment" property="infoCurricularCourseForOption.name"/>
						</logic:notEmpty>
						<logic:empty name="enrolment" property="infoCurricularCourseForOption">
							-&nbsp;<bean:message key="message.not.regular.optional.enrollment"/>
						</logic:empty>
					   <%}
					%>
				</logic:equal>
			  </td>
			  <td class="listClasses">
				<logic:notEqual name="enrolment" property="enrollmentState" value="<%= EnrollmentState.APROVED.toString() %>">
					<bean:message name="enrolment" property="enrollmentState.name" bundle="ENUMERATION_RESOURCES" />
				</logic:notEqual>
				
				<logic:equal name="enrolment" property="enrollmentState" value="<%= EnrollmentState.APROVED.toString() %>">
					<bean:write name="enrolment" property="infoEnrolmentEvaluation.grade"/>
				</logic:equal>
			  </td>
	  		</tr>
	    </logic:iterate>
	  </table>    	
  </logic:notEqual>
  <logic:equal name="enrolmentNumber" value="0">
		<bean:message key="message.no.enrolments" />
  </logic:equal>
    	
    		
