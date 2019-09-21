var app = new Vue({
    el:"#app",
    data: {
        //当前登录用户名
        username:"",
        //购物车列表
        cartList: []
    },
    methods : {
        //查询购物车列表
        findCartList: function(){
          axios.get("cart/findCartList.do").then(function (response) {
              app.cartList = response.data;
          });
        },
        getUsername: function () {
            axios.get("cart/getUsername.do").then(function (response) {
                app.username = response.data.username;
            });

        }
    },
    created(){
        this.getUsername();
        //加载购物车列表
        this.findCartList();
    }
});