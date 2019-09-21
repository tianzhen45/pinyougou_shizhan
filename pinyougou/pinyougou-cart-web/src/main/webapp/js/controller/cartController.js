var app = new Vue({
    el:"#app",
    data: {
        //当前登录用户名
        username:"",
        //购物车列表
        cartList: []
    },
    methods : {

        //加入购物车
        addItemToCartList: function (itemId, num) {
            axios.get("cart/addItemToCartList.do?itemId=" + itemId + "&num=" + num).then(function (response) {
                if(response.data.success){
                    //刷新购物车列表
                    app.findCartList();
                } else {
                    alert(response.data.message);
                }
            });
        },
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