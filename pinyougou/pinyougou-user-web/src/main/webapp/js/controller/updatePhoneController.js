var app = new Vue({
    el: "#app",
    data: {
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

        picCode:"",
        msgCode:""
    },
    methods: {

        getUsername:function () {
            axios.get("user/getUserInfo.do").then(function (response) {
                app.user = response.data.user;
            })
        },
        updateLoginUserPassword:function () {
            //判断两次密码是否一致
            if (this.password != this.entity.password) {
                alert("两次输入的密码不一致;请重新输入");
                return;
            }
            axios.get("user/updatePassword.do?username="+this.entity.username+"&password="+this.entity.password).then(function (response) {
                if (response.data.success){
                    alert(response.data.message)
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
        bindNewPhoneNumber:function () {
            axios.get("user/bindNewPhoneNumber.do?msgCode=" + this.msgCode +"&phone=" +this.user.phone).then(function (response) {
                if (response.data.success){
                    alert(response.data.message);
                    window.location.href="home-setting-address-complete.html"
                }else {
                    alert(response.data.message)
                }
            })
        }


    },
    created() {
        this.getUserInfo();
    }
});