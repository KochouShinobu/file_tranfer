<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>文件上传</title>
</head>
<body>
<%--${pageContext.request.contextPath}获取服务器绝对路径--%>
<form action="${pageContext.request.contextPath}/upload?_m=poi_upload" enctype="multipart/form-data" method="post">
    <p><input type="file" name="file1"></p>
    <p><input type="submit" value="提交"></p>
</form>
<form action="${pageContext.request.contextPath}/upload?_m=poi_down" enctype="multipart/form-data" method="post">
    <p><input type="submit" value="上传写入的excel文件"></p>
</form>
</body>
</html>