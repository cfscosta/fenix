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
<spring:url var="reactjsUrl" value="/javaScript/react.js"/>
<spring:url var="jsxtransformUrl" value="/javaScript/JSXTransformer.js"/>
<script src="${reactjsUrl}"></script>
<script src="${jsxtransformUrl}"></script>
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

<script>
if (!Object.keys) {
	  Object.keys = (function() {
	    'use strict';
	    var hasOwnProperty = Object.prototype.hasOwnProperty,
	        hasDontEnumBug = !({ toString: null }).propertyIsEnumerable('toString'),
	        dontEnums = [
	          'toString',
	          'toLocaleString',
	          'valueOf',
	          'hasOwnProperty',
	          'isPrototypeOf',
	          'propertyIsEnumerable',
	          'constructor'
	        ],
	        dontEnumsLength = dontEnums.length;

	    return function(obj) {
	      if (typeof obj !== 'object' && (typeof obj !== 'function' || obj === null)) {
	        throw new TypeError('Object.keys called on non-object');
	      }

	      var result = [], prop, i;

	      for (prop in obj) {
	        if (hasOwnProperty.call(obj, prop)) {
	          result.push(prop);
	        }
	      }

	      if (hasDontEnumBug) {
	        for (i = 0; i < dontEnumsLength; i++) {
	          if (hasOwnProperty.call(obj, dontEnums[i])) {
	            result.push(dontEnums[i]);
	          }
	        }
	      }
	      return result;
	    };
	  }());
	}
</script>

<script type="text/jsx">

var Grade = React.createClass({
  render: function() {
    return (
	  <div className="row">
	  	<div className="col-md-1">{this.props.author}</div>
      	<div className="col-md-1">{this.props.info}</div>
	  </div>
    );
  }
});


var GradeBox = React.createClass({

  getInitialState: function () {
    return {
      app: app
    };
  },

  render: function() {
	return (
      <div className="gradeBox">
		<SelectTest data={testes}/>
        <GradeList data={this.props.data} />
      </div>
    );
  },

  componentWillMount: function () {
    this.state.app.listener = this;
  },
});

var GradeList = React.createClass({
  render: function() {

	var theData = [];
	var theMap = this.props.data;
	Object.keys(theMap).forEach(function (key) { 
    	var value = theMap[key];
    	theData.push({'author':key, 'text': value});
	})
	
    var gradeNodes = theData.map(function (grade) {
      return (
        <Grade author={grade.author} info={grade.text}/>
      );
    });
    return (
      <div className="gradeList">
        {gradeNodes}
      </div>
    );
  }
});





var onChange = function () {
	var selectedValue = document.getElementById('asd').value;
	var eval = datas[selectedValue];
	React.render(
	  <GradeBox data={eval} />,
	  document.getElementById('content')
	);
};

var app = {
	listener: null,
	
	changed: function(){
		onChange();
	},
	updateTable : function(sda){onChange();},

};

var SelectTest = React.createClass({
 
  getInitialState: function(){
	return {app: app};
  },

  valueChanged: function (event) {
    this.state.app.updateTable(event.target.value);
  },

  render: function() {
	var theData = this.props.data;
    var optionNodes = theData.map(function (grade) {
      return (
        <option key={grade.id} value={grade}>{grade}</option>
      );
    });
    return (
      <select id="asd" className="gradeList" onChange={this.valueChanged}>
        {optionNodes}
      </select>
    );
  },
  changed: function() {
	app.changed();
  } 
	
});

var datas = ${excelRepresent};
var testes = Object.keys(datas);

var t1 =  datas['t1'];
React.render(
  <SelectTest data={testes}/>,
  document.getElementById('content')
);

React.render(
  <GradeBox data={t1}/>,
  document.getElementById('content')
);

</script>

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
<div id="content">
<selectTest data="testes"/>
</div>
</div>
</div>


