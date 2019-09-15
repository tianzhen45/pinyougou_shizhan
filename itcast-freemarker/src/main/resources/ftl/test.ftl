<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Freemarker测试</title>
</head>
<body>
你好！${msg}
<br><hr><br>
<#-- 这是注释；在输出的时候是看不见的 -->
assign 可以指定定义变量；可以在模版范围中使用。<br>
<#assign linkman="传智播客"/>
${linkman}
<br>
<#assign info={"mobile":"13400000000", "address":"吉山村"} />
${info.mobile} ---- ${info.address}

<br><hr><br>
include表示引入其它模版：<br>
<#include "header.ftl" />

<br><hr><br>

<br><hr><br>

<br><hr><br>


<br><hr><br>

</body>
</html>