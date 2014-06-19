<%@tag pageEncoding="UTF-8" description="分页"%>
<%@ attribute name="page" type="org.zjz.app.base.dao.util.Page"
	required="true" description="分页"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
int current =  page.getPageNo();
int pageSize=page.getPageSize();
int begin = Math.max(1, current - pageSize/2);
long end = Math.min(begin + (pageSize - 1), page.getTotalPages());

request.setAttribute("current", current);
request.setAttribute("begin", begin);
request.setAttribute("end", end);
request.setAttribute("pageSize", pageSize);

String url=request.getRequestURI();

%>
<script language="javascript">
function jumpPage(pageNo) {
	 var url=location.href;
	 var param="";
	 if(url.indexOf("pageNO")>-1){
		 param="&"+url.substring(url.indexOf("&")+1, url.length);
	 }
	 else if(url.indexOf("?")>-1){
		 param="&"+url.substring(url.indexOf("?")+1, url.length);
	 }
	 var nurl="?pageNO="+pageNo+param;
     location.href=nurl;
}
</script>

	<div class="page">
	<% if (page.getTotalCount()>0){%>
	 <% if (page.isHasPre()){%>
	            <a href="?pageNO=1&sortType=${sortType}&${searchParams}" title="首页">&lt;&lt;</a>
               	<a href="?pageNO=${current-1}" title="上一页">&lt;</a>
         <%}else{%>
                <span class="disabled">&lt;&lt;</span>
                <span class="disabled">&lt;</span>
         <%} %>
	
		<c:forEach var="i" begin="${begin}" end="${end}">
            <c:choose>
                <c:when test="${i == current}">
                   <span class="current">${i}</span>
                </c:when>
                <c:otherwise>
                    <a href="javascript:jumpPage(${i});">${i}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>
        
       <% if (page.isHasNext()){%>
               	<a href="?pageNO=${current+1}&sortType=${sortType}&${searchParams}" title="下一页">&gt;</a>
               	<a href="?pageNO=${page.totalPages}&sortType=${sortType}&${searchParams}" title="尾页">&gt;&gt;</a>
         <%}else{%>
               <span class="disabled">&gt;</span>
               <span class="disabled">&gt;&gt;</span>
         <%} %>
       <%} %>  
		<span class="page-input"> 第<input type="text"
			 value="${current}"
			onblur="turnPage('${pageSize}', $(this).val(), this);" />页
		</span> &nbsp; <select onchange="turnPage($(this).val(), ${current}, this);">
			<option value="10"
				<c:if test="${pageSize eq 10}">selected="selected" </c:if>>10</option>
			<option value="20"
				<c:if test="${pageSize eq 20}">selected="selected" </c:if>>20</option>
			<option value="30"
				<c:if test="${pageSize eq 30}">selected="selected" </c:if>>30</option>
			<option value="50"
				<c:if test="${pageSize eq 50}">selected="selected" </c:if>>50</option>
		</select> <span class="page-info">[共${page.totalPages}页/${page.totalCount}条]</span>
	</div>
