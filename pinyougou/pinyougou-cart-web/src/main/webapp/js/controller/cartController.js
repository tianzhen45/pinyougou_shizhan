var app = new Vue({
    el:"#app",
    data: {
        //当前登录用户名
        username:""
    },
    methods : {
        getUsername: function () {
            axios.get("cart/getUsername.do").then(function (response) {
                app.username = response.data.username;
            });

        }
    },
    created(){
        this.getUsername();
    }
});