// 窗口加载完
window.onload = function () {
    var app = new Vue({
        el: '#account', // 元素绑定
        data: { // 数据模型
            userEntity: {},
            user: {
                username: "",
                password:"",
                nickName: "",
                sex: "",
                birthday: "",
                headPic: "",
                phone:""
            },
            entity: {"username": "", "password": "","phone":""},
            password:"",
            msgCode:""
        },
        methods: { // 操作方法
            // 获取登录用户名
            getUserInfo: function () {
                axios.get("user/getUserInfo.do").then(function (response) {
                    app.user = response.data.user;
                });
            },
            updateLoginUserPassword:function () {
                this.user.username = this.entity.username;
                this.user.password = this.entity.password;
                //判断两次密码是否一致
                if (this.password != this.entity.password) {
                    alert("两次输入的密码不一致;请重新输入");
                    return;
                }

                axios.post("user/updatePassword.do?",this.user).then(function (response) {
                    if (response.data.success){

                        alert(response.data.message);
                    }else {
                        alert(response.data.message)
                    }
                })
            },
            sendSmsCode: function () {
                axios.get("user/sendSmsCode.do?phone=" + this.user.phone).then(function (response) {
                    alert(response.data.message);
                });
            },
            checkTwoCode:function () {
                axios.get("user/checkTwoCode.do?msgCode=" + this.msgCode +"&phone=" +this.user.phone).then(function (response) {
                    if (response.data.success){
                        location.href="home-setting-address-phone.html"
                    }else {
                        alert(response.data.message)
                    }
                })
            },
        },
        created: function () {
            // 获取登录用户名
            this.getUserInfo();

        }
    });
};