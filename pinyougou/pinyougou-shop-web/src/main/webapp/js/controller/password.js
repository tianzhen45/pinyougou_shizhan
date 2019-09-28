function updatePassword() {

    var oldpassword = $("#oldpassword").val();
    var password1 = $("#password1").val();
    var password = $("#password").val();

    $.ajax({
        url:"/seller/UpdatePassword.do",  //请求的地址
        type:"post",  //请求的方式
        data:{oldpassword:oldpassword,password:password,password1:password1},  //请求的参数
        dataType:"json",  //预定于响应的类型(text,json等等)
        success:function (data) {//执行成功的回调函数

            if(data.success) {
                parent.window.location.href = "http://shop.pinyougou.com/shoplogin.html?logout";
            }
            alert(data.message);

        }
    });
}
