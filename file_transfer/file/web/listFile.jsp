<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
    <title>下载文件显示页面</title>
</head>
<body>
<!-- 遍历Map集合 -->
<c:forEach var="me" items="${fileNameMap}">     <!--items:要被循环的信息 var:代表当前条目的变量名称-->
    <c:url value="/download" var="downurl">                     <!--JSTL:标签将URL格式化为一个字符串，然后存储在一个变量中-->
        <c:param name="filename" value="${me.key}"></c:param>   <!--name:URL中要设置的参数的名称，value:参数的值-->
    </c:url>
    ${me.value}<a href="${downurl}">下载</a>
    <br />
</c:forEach>
</body>
</html>
