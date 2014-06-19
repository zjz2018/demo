<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<form id="inputForm" action="${ctx}/user/update" method="post">
<input type="hidden" name="id" value="${user.id}"/>
登陆名<input type="text" name="loginName" value="${user.loginName}"><br/>
密码<input type="text" name="password" value="${user.password}"><br/>
<input type="submit" value="提交">
</form>
</body>
</html>