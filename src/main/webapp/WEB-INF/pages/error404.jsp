<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><spring:message code="page.title" /></title>
    <link type="text/css" rel="stylesheet" href="<%= request.getContextPath() %>/resources/style/comment.css" />
    <script type="text/javascript" src="<%= request.getContextPath() %>/resources/javascript/jquery-1.8.3.min.js"></script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/resources/javascript/comment.js"></script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/resources/javascript/default.js"></script>
</head>
<body>
    <%@ include file="/WEB-INF/pages/common/header.jsp" %>
    
    <div id="container">
        <div id="content-div" style="margin: 50px 40px 10px 40px;">
            <h2><spring:message code="info.error404.title" /></h2>
            <p style="border-bottom: 1px solid #000000; margin-top: 10px;"></p>
       
            <div style="margin: 20px 0px; padding-left: 10px; height: 100px;">
               <spring:message code="info.error404.content" />
            </div>
        </div>
        
        <div style="text-align: center; font: 14px/20px 'Microsoft YaHei',微软雅黑,Arial,Lucida Grande,Tahoma,sans-serif;">
          Powered by <a href="http://www.heroku.com">Heroku</a>,Design by <a href="https://twitter.com/lzqwebsoft">Johnny</a>.
        </div>
    </div>
    
    <%@ include file="/WEB-INF/pages/common/footer.jsp" %>
</body>
</html>