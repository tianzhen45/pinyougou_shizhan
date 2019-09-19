<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>CAS测试-一品优购</title>
</head>
<body>
欢迎 <%=request.getRemoteUser()%> 访问一品优购。<br>
-------------------- <a href="http://cas.pinyougou.com/logout?service=http://www.itcast.cn">退出</a>
</body>
</html>
