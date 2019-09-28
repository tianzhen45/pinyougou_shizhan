var app = new Vue({
    el: "#app",
    data:{
        entity:{}
    },
    methods:{
        add:function(){
            axios.post("seller/add.do", this.entity).then(function (response) {
                if(response.data.success){
                    //注册成功，跳转到登录页面
                    alert(response.data.message);
                    location.href = "shoplogin.html";
                } else {
                    alert(response.data.message);
                }
            });
        },

        //修改商家基础信息
        updateUser:function () {
            axios.post("/seller/updateUser.do", this.entity).then(function (response) {
                if (response.data.success) {
                    //修改商家成功,跳转到登录页面
                    alert(response.data.message);
                    parent.window.location.href = "http://shop.pinyougou.com/admin/index.html";
                } else {
                    alert(response.data.message);
                }
            });
        },

        //查询商家基本信息
        selectUser:function () {
            axios.get("/seller/findUser.do").then(function (response) {
                app.entity = response.data;
            })
        }
    },

    created() {
        this.selectUser();
    }
});