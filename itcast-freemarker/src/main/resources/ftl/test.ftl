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
if...else 条件控制语句：<br>
<#assign bool=true/>
<#if bool>
    bool的值为真。
    <#else>
        bool的值为假。
</#if>

<br><hr><br>
list循环控制语句；可以对集合的内容进行迭代。<br>
<#list goodsList as goods>
    ${goods_index}---${goods.name}---${goods.price}<br>
</#list>

<br><hr><br>


<br><hr><br>

</body>
</html>