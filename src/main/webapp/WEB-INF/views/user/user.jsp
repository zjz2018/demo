<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="${ctx}/css/style.css" type="text/css" rel="stylesheet"/>
<title>Insert title here</title>
</head>
<body>
<div id="content">
    <div class="searchbar">
	<form action="${ctx}/user" method="get">
	用户名:<input type="text" name="name" value="${user.name}">
	登录名:<input type="text" name="loginName" value="${user.loginName}">
	<input type="submit" value="查询">
	<input type="button" onclick="location.href='${ctx}/user/create'" value="新建">
	</form>
	</div>
<table class="gridtable">
		<thead><tr><th>登录名</th><th>用户名</th><th>注册时间</th><th>管理</th></tr></thead>
		<tbody>
		<c:forEach items="${page.result}" var="user">
			<tr>
				<td><a href="${ctx}/user/update/${user.id}">${user.loginName}</a></td>
				<td>${user.name}</td>
				<td>
					<fmt:formatDate value="${user.birthday}" pattern="yyyy年MM月dd日  HH时mm分ss秒" />
				</td>
				<td><a href="${ctx}/user/delete/${user.id}">删除</a>
				<a href="${ctx}/user/update/${user.id}">修改</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<tags:page page="${page}"/>
	</div>
</body>
</html>