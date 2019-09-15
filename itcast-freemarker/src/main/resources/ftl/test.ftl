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
可以使用size获取集合大小<br>
上述的goodsList的大小为：${goodsList?size}<br>
可以使用eval将字符串转换为一个对象<br>
<#assign jsonStr='{"name":"itcast","age":13}'/>
<#assign jsonObj=jsonStr?eval/>
${jsonObj.name}
<br><hr><br>
在freemarker模版中可以使用.now 获取当前日期时间：${.now}<br>
不格式化显示日期：${today?datetime}<br>
显示日期：${today?date}<br>
显示时间：${today?time}<br>
格式化显示：${today?string("yyyy年MM月dd日 HH:mm:ss")}<br>

<br><hr><br>
数值直接输出，会以千分位逗号分割，如：${number}，可以将数值以字符串方式输出：${number?c}
<br><hr><br>
空值的处理<br>
- 可以在插值中，变量后面添加一个 ! 表示值为空的时候什么都不显示。<br>
- 如果值为空但是要有默认值的话；那么可以在 !“要显示的内容”<br>
emp的值是不存在；如果要什么都不显示则直接加! emp=${emp!}；如果值为空要显示默认值，可以在!"要显示的值"；emp = ${emp!"emp默认值"}<br>
<br>
<br>

??? 前面两个??表示变量是否存在；如果存在则返回true，否则返回false，第3个?表示函数的调用。<br>

<#assign bool3=false/>
${bool3???string}

<#if str10??>
    str10存在
    <#else>
    str10不存在
</#if>


<br><br><br><br><br><br><br>
</body>
</html>