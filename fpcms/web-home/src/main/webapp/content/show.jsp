<%@page import="com.fpcms.model.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp" %>

<duowan:override name="head">
	<title>${cmsContent.title} - ${keyword}</title>
	<meta name="keywords" content="${cmsContent.title} - ${keyword}" />
	<meta name="description" content="${fn:substring(item.content,0,80)}" /> 
</duowan:override>

<duowan:override name="content">
		<div>
			<h1>${cmsContent.title}</h1>
			${cmsContent.content}
		</div>
</duowan:override>

<%-- jsp模板继承,具体使用请查看: http://code.google.com/p/rapid-framework/wiki/rapid_jsp_extends --%>
<%@ include file="base.jsp" %>
